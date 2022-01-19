package memory;

import memory.tlb.TLB;
import memory.cache.Cache;

import java.util.Arrays;

public class MemTestHelper {

    /**
     * 此方法容易导致生成过大数组，谨慎使用，后期建议删除
     */
    public char[] fillData(char dataUnit, int len) {
        char[] data = new char[len];
        Arrays.fill(data, dataUnit);
        return data;
    }

    public void clearAll() {
        Cache.getCache().clear();
        Memory.getMemory().clear();
        TLB.getTLB().clear();
    }

}
