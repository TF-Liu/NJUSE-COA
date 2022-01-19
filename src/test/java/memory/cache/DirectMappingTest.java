package memory.cache;

import memory.Memory;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import util.Transformer;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// 直接映射无替换策略
public class DirectMappingTest {

    @Before
    public void init() {
        Cache.getCache().setSETS(1024);
        Cache.getCache().setSetSize(1);
    }

    @Test
    public void test01() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        char[] data = {0b00000001, 0b00000010, 0b000000011, 0b00000100};
        String pAddr = "00000000000000000000000000000001";
        memory.write(pAddr, data.length, data);
        char[] dataRead = cache.read(pAddr, data.length);
        // 判断是否能够正确读出数据
        assertArrayEquals(data, dataRead);
        // 判断Cache状态是否符合预期
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test02() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        char[] data = {0b00000001, 0b00000010, 0b000000011, 0b00000100};
        String pAddr = "00000000000100000000000000000001";
        memory.write(pAddr, data.length, data);
        // 判断是否能够正确读出数据
        assertArrayEquals(data, cache.read(pAddr, data.length));
        // 判断Cache状态是否符合预期
        assertTrue(cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000010000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test03() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        Transformer t = new Transformer();
        char[] data = {0b00000001, 0b00000010, 0b000000011, 0b00000100};
        String pAddr = "00000000000000000000010000000001";
        memory.write(pAddr, data.length, data);
        // 判断是否能够正确读出数据
        assertArrayEquals(data, cache.read(pAddr, data.length));
        // 判断Cache状态是否符合预期
        assertTrue(cache.checkStatus(new int[]{Integer.parseInt(t.binaryToInt("0000000001"))}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));
        cache.clear();
    }

    @Test
    public void test04() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        Transformer t = new Transformer();
        char[] data = {0b00000001, 0b00000010, 0b000000011, 0b00000100};
        String pAddr = "00000000000100000000010000000001";
        memory.write(pAddr, data.length, data);
        // 判断是否能够正确读出数据
        assertArrayEquals(data, cache.read(pAddr, data.length));
        // 判断Cache状态是否符合预期
        assertTrue(cache.checkStatus(new int[]{Integer.parseInt(t.binaryToInt("0000000001"))}, new boolean[]{true}, new char[][]{"0000000000010000000000".toCharArray()}));
        cache.clear();
    }
}
