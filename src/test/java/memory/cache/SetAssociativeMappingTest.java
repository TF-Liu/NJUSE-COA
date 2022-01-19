package memory.cache;

import memory.Memory;
import memory.cache.cacheReplacementStrategy.FIFOReplacement;
import memory.cache.cacheReplacementStrategy.LFUReplacement;
import memory.cache.cacheReplacementStrategy.LRUReplacement;
import org.junit.Before;
import org.junit.Test;
import util.Transformer;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class SetAssociativeMappingTest {

    @Before
    public void init() {
        Cache.getCache().setSETS(256);
        Cache.getCache().setSetSize(4);
    }

    @Test
    public void test01() {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        cache.setReplacementStrategy(new FIFOReplacement());
        String pAddr1 = "00000000000000000000010000000000";
        String pAddr2 = "00000000000000000000100000000000";
        String pAddr3 = "00000000010010001101100000000000";

        char[] input1 = new char[1024];
        Arrays.fill(input1, 'a');
        memory.write(pAddr1, 1024, input1);
        assertArrayEquals(input1, cache.read(pAddr1, 1024));
        assertTrue(cache.checkStatus(new int[]{1 * 4}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));

        char[] input2 = new char[1024];
        Arrays.fill(input2, 'b');
        memory.write(pAddr2, 1024, input2);
        assertArrayEquals(input2, cache.read(pAddr2, 1024));
        assertTrue(cache.checkStatus(new int[]{2 * 4}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()}));

        char[] input3 = {0b11110111, 0b01111110, 0b10010110, 0b01111110, 0b00110101, 0b10010100, 0b11010111, 0b10011101, 0b01111100, 0b11000000};
        memory.write(pAddr3, input3.length, input3);
        assertArrayEquals(input3, cache.read(pAddr3, input3.length));
        assertTrue(cache.checkStatus(new int[]{54 * 4}, new boolean[]{true}, new char[][]{"0000000001001000000000".toCharArray()}));

        cache.clear();
    }

    @Test
    public void test02() {
        // FIFO替换策略
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        Cache.getCache().setReplacementStrategy(new FIFOReplacement());
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        char[] input3 = new char[1024];
        char[] input4 = new char[1024];
        Arrays.fill(input1, 'c');
        Arrays.fill(input2, 'd');
        Arrays.fill(input3, 'e');
        Arrays.fill(input4, 'f');
        String pAddr1 = "00000000000000000000000000000000";
        String pAddr2 = "00000000101000000000000000000000";
        String pAddr3 = "00000000111000000000000000000000";
        String pAddr4 = "00000000011100000000010000000001";

        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        char[] dataRead = cache.read(pAddr1, 1024 * 1024);
        assertArrayEquals(input1, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()});

        // cache中第0行应该被替换
        memory.write(pAddr2, input2.length, input2);
        dataRead = cache.read(pAddr2, 1024);
        assertArrayEquals(input2, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000010100000000000".toCharArray()});

        // cache中第1行应该被替换
        memory.write(pAddr3, input3.length, input3);
        dataRead = cache.read(pAddr3, 1024);
        assertArrayEquals(input3, dataRead);
        cache.checkStatus(new int[]{1}, new boolean[]{true}, new char[][]{"0000000011100000000000".toCharArray()});

        // cache中的第4行和第8行应该被替换
        memory.write(pAddr4, input4.length, input4);
        dataRead = cache.read(pAddr4, 1024);
        assertArrayEquals(input4, dataRead);
        assertTrue(cache.checkStatus(new int[]{4, 8}, new boolean[]{true, true}, new char[][]{"0000000001110000000000".toCharArray(), "0000000001110000000000".toCharArray()}));

        cache.clear();
    }

    @Test
    public void test03() {
        // LFU替换策略
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        Cache.getCache().setReplacementStrategy(new LFUReplacement());
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        char[] input3 = new char[1024];
        Arrays.fill(input1, 'g');
        Arrays.fill(input2, 'h');
        Arrays.fill(input3, 'i');
        String pAddr1 = "00000000000000000000000000000000";
        String pAddr2 = "00000000101000000000000000000000";
        String pAddr3 = "00000000011100000000010000000000";

        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        char[] dataRead = cache.read(pAddr1, 1024 * 1024);
        assertArrayEquals(input1, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()});

        // 访问一遍cache的第0 2 3 5 6 7行
        char[] dataHit = new char[1024];
        Arrays.fill(dataHit, 'g');
        Transformer transformer = new Transformer();
        for (int i = 0; i < 4; i++) {
            if (i == 1) continue;
            String pAddrHit = transformer.intToBinary("" + i).substring(18, 32) + "000000000000000000";
            dataRead = cache.read(pAddrHit, 1024);
            assertArrayEquals(dataHit, dataRead);
        }
        for (int i = 0; i < 4; i++) {
            if (i == 0) continue;
            String pAddrHit = transformer.intToBinary("" + i).substring(18, 32) + "000000010000000000";
            dataRead = cache.read(pAddrHit, 1024);
            assertArrayEquals(dataHit, dataRead);
        }

        // cache中第1行应该被替换
        memory.write(pAddr2, input2.length, input2);
        dataRead = cache.read(pAddr2, 1024);
        assertArrayEquals(input2, dataRead);
        cache.checkStatus(new int[]{1}, new boolean[]{true}, new char[][]{"0000000010100000000000".toCharArray()});

        // cache中的第4行应该被替换
        memory.write(pAddr3, input3.length, input3);
        dataRead = cache.read(pAddr3, 1024);
        assertArrayEquals(input3, dataRead);
        assertTrue(cache.checkStatus(new int[]{4}, new boolean[]{true}, new char[][]{"0000000001110000000000".toCharArray()}));

        cache.clear();
    }

    @Test
    public void test04() {
        // LRU替换策略
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        Cache.getCache().setReplacementStrategy(new LRUReplacement());
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        char[] input3 = new char[1024];
        Arrays.fill(input1, 'j');
        Arrays.fill(input2, 'k');
        Arrays.fill(input3, 'l');
        String pAddr1 = "00000000000000000000000000000000";
        String pAddr2 = "00000000101000000000000000000000";
        String pAddr3 = "00000000011100000000000000000001";

        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        char[] dataRead = cache.read(pAddr1, 1024 * 1024);
        assertArrayEquals(input1, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()});

        // 访问一遍cache的第0 2 3 5 6 7行
        char[] dataHit = new char[1024];
        Arrays.fill(dataHit, 'j');
        Transformer transformer = new Transformer();
        for (int i = 0; i < 4; i++) {
            if (i == 1) continue;
            String pAddrHit = transformer.intToBinary("" + i).substring(18, 32) + "000000000000000000";
            dataRead = cache.read(pAddrHit, 1024);
            assertArrayEquals(dataHit, dataRead);
        }
        for (int i = 0; i < 4; i++) {
            if (i == 0) continue;
            String pAddrHit = transformer.intToBinary("" + i).substring(18, 32) + "000000010000000000";
            dataRead = cache.read(pAddrHit, 1024);
            assertArrayEquals(dataHit, dataRead);
        }

        // cache中第1行应该被替换
        memory.write(pAddr2, input2.length, input2);
        dataRead = cache.read(pAddr2, 1024);
        assertArrayEquals(input2, dataRead);
        cache.checkStatus(new int[]{1}, new boolean[]{true}, new char[][]{"0000000010100000000000".toCharArray()});

        // cache中的第0行和第4行应该被替换
        memory.write(pAddr3, input3.length, input3);
        dataRead = cache.read(pAddr3, 1024);
        assertArrayEquals(input3, dataRead);
        assertTrue(cache.checkStatus(new int[]{0, 4}, new boolean[]{true, true}, new char[][]{"0000000001110000000000".toCharArray(), "0000000001110000000000".toCharArray()}));

        cache.clear();
    }
}
