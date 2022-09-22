package memory.cache;

import memory.Memory;
import memory.cache.cacheReplacementStrategy.FIFOReplacement;
import memory.cache.cacheReplacementStrategy.ReplacementStrategy;
import util.Transformer;

import java.util.Arrays;

/**
 * 高速缓存抽象类
 */
public class Cache {

    public static boolean isAvailable = true; // 默认启用Cache

    public static final int CACHE_SIZE_B = 1024 * 1024; // 1 MB 总大小

    public static final int LINE_SIZE_B = 1024; // 1 KB 行大小

    private final CacheLinePool cache = new CacheLinePool(CACHE_SIZE_B / LINE_SIZE_B);  // 总大小1MB / 行大小1KB = 1024个行

    private int SETS = 256; // 组数，默认为256组

    private int setSize = 4;    // 每组行数，默认为每组4行

    // 单例模式
    private static final Cache cacheInstance = new Cache();

    private Cache() {
    }

    public static Cache getCache() {
        return cacheInstance;
    }

    private ReplacementStrategy replacementStrategy = new FIFOReplacement();    // 替换策略

    public static boolean isWriteBack;   // 写策略

    private final Transformer transformer = new Transformer();


    /**
     * 读取[pAddr, pAddr + len)范围内的连续数据，可能包含多个数据块的内容
     *
     * @param pAddr 数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
     * @param len   待读数据的字节数
     * @return 读取出的数据，以char数组的形式返回
     */
    public char[] read(String pAddr, int len) {
        char[] data = new char[len];
        int addr = Integer.parseInt(transformer.binaryToInt("0" + pAddr));
        int upperBound = addr + len;
        int index = 0;
        while (addr < upperBound) {
            int nextSegLen = LINE_SIZE_B - (addr % LINE_SIZE_B);
            if (addr + nextSegLen >= upperBound) {
                nextSegLen = upperBound - addr;
            }
            int rowNO = fetch(transformer.intToBinary(String.valueOf(addr)));
            char[] cache_data = cache.get(rowNO).getData();
            int i = 0;
            while (i < nextSegLen) {
                data[index] = cache_data[addr % LINE_SIZE_B + i];
                index++;
                i++;
            }
            addr += nextSegLen;
        }
        return data;
    }

    /**
     * 向cache中写入[pAddr, pAddr + len)范围内的连续数据，可能包含多个数据块的内容
     *
     * @param pAddr 数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
     * @param len   待写数据的字节数
     * @param data  待写数据
     */
    public void write(String pAddr, int len, char[] data) {
        int addr = Integer.parseInt(transformer.binaryToInt("0" + pAddr));
        int upperBound = addr + len;
        int index = 0;
        while (addr < upperBound) {
            int nextSegLen = LINE_SIZE_B - (addr % LINE_SIZE_B);
            if (addr + nextSegLen >= upperBound) {
                nextSegLen = upperBound - addr;
            }
            int rowNO = fetch(transformer.intToBinary(String.valueOf(addr)));
            char[] cache_data = cache.get(rowNO).getData();
            int i = 0;
            while (i < nextSegLen) {
                cache_data[addr % LINE_SIZE_B + i] = data[index];
                index++;
                i++;
            }
            if (isWriteBack) {
                cache.get(rowNO).dirty = true;
            } else {
                Memory.getMemory().write(calculatePAddr(rowNO), Cache.LINE_SIZE_B, cache_data);
                cache.get(rowNO).validBit = true;
            }
            addr += nextSegLen;
        }
    }

    /**
     * 查询{@link Cache#cache}表以确认包含pAddr的数据块是否在cache内
     * 如果目标数据块不在Cache内，则将其从内存加载到Cache
     *
     * @param pAddr 数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
     * @return 数据块在Cache中的对应行号
     */
    private int fetch(String pAddr) {
        int blockNO = getBlockNO(pAddr);    // 地址前22位int形式
        int rowNO = map(blockNO);  // 返回内存地址blockNO所对应的cache中的行，返回-1则表示没有命中

        if (rowNO == -1) {    // 未命中
            rowNO = loadBlock(blockNO);
        }
        return rowNO;
    }

    /**
     * 根据目标数据内存地址前22位的int表示，进行映射
     *
     * @param blockNO 数据在内存中的块号
     * @return 返回cache中所对应的行，-1表示未命中
     */
    private int map(int blockNO) {
        int setNO = blockNO % SETS;           // 获得内存地址blockNO所对应的组号setNO
        char[] addrTag = calculateTag(blockNO);   // 获得内存地址blockNO所对应的tag
        int start = setNO * setSize;
        int end = (setNO + 1) * setSize - 1;
        for (int i = start; i <= end; i++) {
            if (cacheInstance.isMatch(i, addrTag)) {   // 命中该行
                replacementStrategy.hit(i);
                return i; // 返回该行
            }
        }
        return -1;
    }

    /**
     * 根据块号，结合具体的映射策略，计算数据块在Cache行中的Tag
     *
     * @param blockNO 数据在内存中的块号
     * @return 长度为22，结合具体的映射策略判断前多少位有效
     */
    private char[] calculateTag(int blockNO) {
        int tag = blockNO / SETS;
        int offset = 0;
        for (int i = 1; i < SETS; i *= 2) {
            offset++;
        }
        StringBuilder tagStr = new StringBuilder(transformer.intToBinary("" + tag).substring(10 + offset, 32));
        int diff = 22 - tagStr.length();
        for (int i = 0; i < diff; i++) {
            tagStr.append("0");
        }
        return tagStr.toString().toCharArray();
    }

    /**
     * 未命中的情况下，将内存读取出该内存块的数据写入cache
     *
     * @param blockNO 数据在内存中的块号
     * @return 返回cache中所对应的行
     */
    private int loadBlock(int blockNO) {
        int setNO = blockNO % SETS;
        char[] addrTag = calculateTag(blockNO);
        if (SETS == 1024) {
            cacheInstance.update(setNO, addrTag, Memory.getMemory().read(transformer.intToBinary(String.valueOf(Cache.LINE_SIZE_B * blockNO)), Cache.LINE_SIZE_B));
            return setNO;
        }
        return this.replacementStrategy.replace(setNO * setSize, (setNO + 1) * setSize - 1, addrTag, Memory.getMemory().read(transformer.intToBinary(String.valueOf(Cache.LINE_SIZE_B * blockNO)), Cache.LINE_SIZE_B));
    }

    /**
     * 更新cache
     *
     * @param rowNO 需要更新的cache行号
     * @param tag   待更新数据的Tag
     * @param input 待更新的数据
     */
    public void update(int rowNO, char[] tag, char[] input) {
        CacheLine cacheLine = cache.get(rowNO);
        cacheLine.update(tag, input);
    }

    /**
     * 从32位物理地址(22位块号 + 10位块内地址)获取目标数据在内存中对应的块号
     *
     * @param pAddr 32位物理地址
     * @return 数据在内存中的块号
     */
    private int getBlockNO(String pAddr) {
        return Integer.parseInt(transformer.binaryToInt("0" + pAddr.substring(0, 22)));
    }

    /**
     * 判断该行是否命中
     *
     * @param rowNO 内存地址对应的cache行
     * @param tag   内存地址对应的tag
     * @return 判断结果
     */
    public boolean isMatch(int rowNO, char[] tag) {
        if (this.cache.get(rowNO) == null) {
            return false;
        }
        if (!this.cache.get(rowNO).validBit) {
            return false;
        }
        return Arrays.equals(this.cache.get(rowNO).tag, tag);
    }

    /**
     * 根据行号计算该行首地址对应的物理地址
     *
     * @param rowNO 行号
     * @return 对应的物理地址
     */
    public String calculatePAddr(int rowNO) {
        int offset = 0;
        for (int i = 1; i < SETS; i *= 2) {
            offset++;
        }
        String setNo = transformer.intToBinary("" + rowNO / setSize).substring(32 - offset, 32);
        char[] tag = cache.get(rowNO).tag;
        return new String(tag).substring(0, tag.length - offset) + setNo + "0000000000";
    }


    // 获取有效位
    public boolean isValid(int rowNO) {
        return cache.get(rowNO).validBit;
    }

    // 获取脏位
    public boolean isDirty(int rowNO) {
        return cache.get(rowNO).dirty;
    }

    // LFU算法增加访问次数
    public void addVisited(int rowNO) {
        CacheLine cacheLine = cache.get(rowNO);
        cacheLine.visited = cacheLine.visited + 1;
    }

    // 获取访问次数
    public int getVisited(int rowNO) {
        CacheLine cacheLine = cache.get(rowNO);
        if (cacheLine.validBit) {
            return cacheLine.visited;
        }
        return -1;
    }

    // 用于LRU算法，重置时间戳
    public void setTimeStamp(int rowNO) {
        CacheLine cacheLine = cache.get(rowNO);
        cacheLine.timeStamp = System.currentTimeMillis();
    }

    // 获取时间戳
    public long getTimeStamp(int rowNO) {
        CacheLine cacheLine = cache.get(rowNO);
        if (cacheLine.validBit) {
            return cacheLine.timeStamp;
        }
        return -1;
    }

    // 获取该行数据
    public char[] getData(int rowNO) {
        return cache.get(rowNO).getData();
    }

    /**
     * 该方法会被用于测试，请勿修改
     * 使用策略模式，设置cache的替换策略
     *
     * @param replacementStrategy 替换策略
     */
    public void setReplacementStrategy(ReplacementStrategy replacementStrategy) {
        this.replacementStrategy = replacementStrategy;
    }

    /**
     * 该方法会被用于测试，请勿修改
     *
     * @param SETS 组数
     */
    public void setSETS(int SETS) {
        this.SETS = SETS;
    }

    /**
     * 该方法会被用于测试，请勿修改
     *
     * @param setSize 每组行数
     */
    public void setSetSize(int setSize) {
        this.setSize = setSize;
    }

    /**
     * 告知Cache某个连续地址范围内的数据发生了修改，缓存失效
     * 该方法仅在memory类中使用，请勿修改
     *
     * @param pAddr 发生变化的数据段的起始地址
     * @param len   数据段长度
     */
    public void invalid(String pAddr, int len) {
        int from = getBlockNO(pAddr);
        Transformer t = new Transformer();
        int to = getBlockNO(t.intToBinary(String.valueOf(Integer.parseInt(t.binaryToInt("0" + pAddr)) + len - 1)));

        for (int blockNO = from; blockNO <= to; blockNO++) {
            int rowNO = map(blockNO);
            if (rowNO != -1) {
                cache.get(rowNO).validBit = false;
            }
        }
    }

    /**
     * 清除Cache全部缓存
     * 该方法会被用于测试，请勿修改
     */
    public void clear() {
        for (CacheLine line : cache.clPool) {
            if (line != null) {
                line.validBit = false;
            }
        }
    }

    /**
     * 输入行号和对应的预期值，判断Cache当前状态是否符合预期
     * 这个方法仅用于测试，请勿修改
     *
     * @param lineNOs     行号
     * @param validations 有效值
     * @param tags        tag
     * @return 判断结果
     */
    public boolean checkStatus(int[] lineNOs, boolean[] validations, char[][] tags) {
        if (lineNOs.length != validations.length || validations.length != tags.length) {
            return false;
        }
        for (int i = 0; i < lineNOs.length; i++) {
            CacheLine line = cache.get(lineNOs[i]);
            if (line.validBit != validations[i]) {
                return false;
            }
            if (!Arrays.equals(line.getTag(), tags[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * 负责对CacheLine进行动态初始化
     */
    private static class CacheLinePool {

        private final CacheLine[] clPool;

        /**
         * @param lines Cache的总行数
         */
        CacheLinePool(int lines) {
            clPool = new CacheLine[lines];
        }

        private CacheLine get(int rowNO) {
            CacheLine l = clPool[rowNO];
            if (l == null) {
                clPool[rowNO] = new CacheLine();
                l = clPool[rowNO];
            }
            return l;
        }
    }


    /**
     * Cache行，每行长度为(1+22+{@link Cache#LINE_SIZE_B})
     */
    private static class CacheLine {

        // 有效位，标记该条数据是否有效
        boolean validBit = false;

        // 脏位，标记该条数据是否被修改
        boolean dirty = false;

        // 用于LFU算法，记录该条cache使用次数
        int visited = 0;

        // 用于LRU和FIFO算法，记录该条数据时间戳
        Long timeStamp = 0L;

        // 标记，占位长度为22位，有效长度取决于映射策略：
        // 直接映射: 12 位
        // 全关联映射: 22 位
        // (2^n)-路组关联映射: 22-(10-n) 位
        // 注意，tag在物理地址中用高位表示，如：直接映射(32位)=tag(12位)+行号(10位)+块内地址(10位)，
        // 那么对于值为0b1111的tag应该表示为0000000011110000000000，其中前12位为有效长度
        char[] tag = new char[22];

        // 数据
        char[] data = new char[LINE_SIZE_B];

        char[] getData() {
            return this.data;
        }

        char[] getTag() {
            return this.tag;
        }

        // 更新该CacheLine
        void update(char[] tag, char[] input) {
            validBit = true;
            visited = 1;
            timeStamp = System.currentTimeMillis();
            System.arraycopy(tag, 0, this.tag, 0, tag.length);
            // input.length <= this.data.length
            System.arraycopy(input, 0, this.data, 0, input.length);
        }

    }
}
