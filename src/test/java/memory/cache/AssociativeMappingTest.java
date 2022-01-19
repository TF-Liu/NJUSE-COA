package memory.cache;

import memory.Memory;
import memory.cache.cacheReplacementStrategy.FIFOReplacement;
import memory.cache.cacheReplacementStrategy.LFUReplacement;
import memory.cache.cacheReplacementStrategy.LRUReplacement;
import org.junit.Before;
import org.junit.Test;
import util.Transformer;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class AssociativeMappingTest {

    @Before
    public void init() {
        Cache.getCache().setSETS(1);
        Cache.getCache().setSetSize(1024);
    }

    @Test
    public void test01() {
        // 无替换发生
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
        Cache.getCache().setReplacementStrategy(new FIFOReplacement());
        char[] input1 = new char[1024 * 1024];
        char[] input2 = new char[1024];
        char[] input3 = new char[1024];
        Arrays.fill(input1, 'a');
        Arrays.fill(input2, 'b');
        Arrays.fill(input3, 'c');
        String pAddr1 = "00000000000000000000000000000000";
        String pAddr2 = "00000000000000000000010000000000";
        String pAddr3 = "00000000000000000001110000000000";

        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        char[] dataRead = cache.read(pAddr1, input1.length);
        assertArrayEquals(input1, dataRead);
        ;
        assertTrue(cache.checkStatus(new int[]{0, 1}, new boolean[]{true, true}, new char[][]{"0000000000000000000000".toCharArray(), "0000000000000000000001".toCharArray()}));

        // cache第1行invalid，未命中，应重新加载
        memory.write(pAddr2, input2.length, input2);
        dataRead = cache.read(pAddr2, input2.length);
        assertArrayEquals(input2, dataRead);

        // cache第7行invalid，未命中，应重新加载
        memory.write(pAddr3, input3.length, input3);
        dataRead = cache.read(pAddr3, input3.length);
        assertArrayEquals(input3, dataRead);
        assertTrue(cache.checkStatus(new int[]{1, 7}, new boolean[]{true, true}, new char[][]{"0000000000000000000001".toCharArray(), "0000000000000000000111".toCharArray()}));
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
        Arrays.fill(input1, 'd');
        Arrays.fill(input2, 'e');
        Arrays.fill(input3, 'f');
        String pAddr1 = "00000000000000000000000000000000";
        String pAddr2 = "00000000101000000000010000000000";
        String pAddr3 = "00000000011100000000010000000001";

        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        char[] dataRead = cache.read(pAddr1, 1024 * 1024);
        assertArrayEquals(input1, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()});

        // cache中第0行应该被替换
        memory.write(pAddr2, input2.length, input2);
        dataRead = cache.read(pAddr2, 1024);
        assertArrayEquals(input2, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000010100000000001".toCharArray()});

        // cache中的第1行和第2行应该被替换
        memory.write(pAddr3, input3.length, input3);
        dataRead = cache.read(pAddr3, 1024);
        assertArrayEquals(input3, dataRead);
        assertTrue(cache.checkStatus(new int[]{1, 2}, new boolean[]{true, true}, new char[][]{"0000000001110000000001".toCharArray(), "0000000001110000000010".toCharArray()}));

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
        String pAddr2 = "00000000101000000001010000000000";
        String pAddr3 = "00000000011100000000010000000000";

        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        char[] dataRead = cache.read(pAddr1, 1024 * 1024);
        assertArrayEquals(input1, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()});

        // 将除了第1行以外的所有行均访问一遍
        char[] dataHit = new char[1024];
        Arrays.fill(dataHit, 'g');
        Transformer transformer = new Transformer();
        for (int i = 0; i < 1024; i++) {
            if (i == 1) continue;
            String pAddrHit = transformer.intToBinary("" + i).substring(10, 32) + "0000000000";
            dataRead = cache.read(pAddrHit, 1024);
            assertArrayEquals(dataHit, dataRead);
        }

        // cache中第1行应该被替换
        memory.write(pAddr2, input2.length, input2);
        dataRead = cache.read(pAddr2, 1024);
        assertArrayEquals(input2, dataRead);
        cache.checkStatus(new int[]{1}, new boolean[]{true}, new char[][]{"0000000010100000000101".toCharArray()});

        // cache中的第1行应该被替换
        memory.write(pAddr3, input3.length, input3);
        dataRead = cache.read(pAddr3, 1024);
        assertArrayEquals(input3, dataRead);
        assertTrue(cache.checkStatus(new int[]{1}, new boolean[]{true}, new char[][]{"0000000001110000000001".toCharArray()}));

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
        String pAddr2 = "00000000101000000001010000000000";
        String pAddr3 = "00000000011100000000010000000001";

        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        char[] dataRead = cache.read(pAddr1, 1024 * 1024);
        assertArrayEquals(input1, dataRead);
        cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000000000000000000000".toCharArray()});

        // 将除了第2行以外的所有行均访问一遍
        char[] dataHit = new char[1024];
        Arrays.fill(dataHit, 'j');
        Transformer transformer = new Transformer();
        for (int i = 0; i < 1024; i++) {
            if (i == 2) continue;
            String pAddrHit = transformer.intToBinary("" + i).substring(10, 32) + "0000000000";
            dataRead = cache.read(pAddrHit, 1024);
            assertArrayEquals(dataHit, dataRead);
        }

        // cache中第2行应该被替换
        memory.write(pAddr2, input2.length, input2);
        dataRead = cache.read(pAddr2, 1024);
        assertArrayEquals(input2, dataRead);
        cache.checkStatus(new int[]{2}, new boolean[]{true}, new char[][]{"0000000010100000000101".toCharArray()});

        // cache中的第0行和第1行应该被替换
        memory.write(pAddr3, input3.length, input3);
        dataRead = cache.read(pAddr3, 1024);
        assertArrayEquals(input3, dataRead);
        assertTrue(cache.checkStatus(new int[]{0, 1}, new boolean[]{true, true}, new char[][]{"0000000001110000000001".toCharArray(), "0000000001110000000010".toCharArray()}));

        cache.clear();
    }

}
