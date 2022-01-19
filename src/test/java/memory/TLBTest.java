package memory;

import cpu.mmu.MMU;
import memory.cache.Cache;
import memory.disk.Disk;
import memory.tlb.TLB;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class TLBTest {

    private final MMU mmu = MMU.getMMU();

    private final Memory memory = Memory.getMemory();

    private final TLB tlb = TLB.getTLB();

    private final Disk disk = Disk.getDisk();

    private final MemTestHelper helper = new MemTestHelper();

    @Before
    public void init() {
        Memory.PAGE = true;
        Memory.SEGMENT = true;
        Cache.isAvailable = true;
        TLB.isAvailable = true;
        helper.clearAll();
        Memory.timer = false;
    }

    // 第0个虚页放到第0个物理页框，第2个虚页放到第1个物理页框
    @Test
    public void test1() {
        int len = 128;
        char[] expect = helper.fillData('0', len);
        char[] actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        assertArrayEquals(expect, actual);
        assertTrue(memory.isValidPage(0));
        assertArrayEquals("00000000000000000000".toCharArray(), memory.getFrameOfPage(0));
        expect = helper.fillData('8', len);
        actual = mmu.read("000000000000000000000000000000000010000000000000", len);
        assertArrayEquals(expect, actual);
        assertTrue(memory.isValidPage(2));
        assertArrayEquals("00000000000000000001".toCharArray(), memory.getFrameOfPage(2));
        assertTrue(tlb.checkStatus(new int[]{0, 1}, new boolean[]{true, true}, new int[]{0, 2}, new char[][]{"00000000000000000000".toCharArray(), "00000000000000000001".toCharArray()}));
    }

    // 跨页访问，第4、5、6个虚页分别放到第0、1、2个物理页框
    @Test
    public void test2() {
        int len = 1024 * 4 * 3;
        char[] expect = new char[len];
        for (int i = 0; i < 4 * 3; i++) {
            Arrays.fill(expect, i * 1024, i * 1024 + 1024, (char) (0x40 + i));
        }
        char[] actual = mmu.read("000000000000000000000000000000000100000000000000", len);
        assertArrayEquals(expect, actual);
        assertTrue(memory.isValidPage(4));
        assertTrue(memory.isValidPage(5));
        assertTrue(memory.isValidPage(6));
        assertArrayEquals("00000000000000000000".toCharArray(), memory.getFrameOfPage(4));
        assertArrayEquals("00000000000000000001".toCharArray(), memory.getFrameOfPage(5));
        assertArrayEquals("00000000000000000010".toCharArray(), memory.getFrameOfPage(6));
        assertTrue(tlb.checkStatus(new int[]{0, 1, 2}, new boolean[]{true, true, true}, new int[]{4, 5, 6}, new char[][]{"00000000000000000000".toCharArray(), "00000000000000000001".toCharArray(), "00000000000000000010".toCharArray()}));
    }

    // 读取1MB+1KB数据，cache和TLB被填满并恰好被替换第0行
    @Test
    public void test3() {
        int len = 1024 * 1024;
        char[] expect = disk.read("00000000000000000000000000000000", len);
        char[] actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        assertArrayEquals(expect, actual);
        len = 1024;
        expect = disk.read("00000000000100000000000000000000", len);
        actual = mmu.read("000000000000000000000000000100000000000000000000", len);
        assertTrue(memory.isValidPage(256));
        assertArrayEquals("00000000000100000000".toCharArray(), memory.getFrameOfPage(256));
        assertTrue(tlb.checkStatus(new int[]{0, 1}, new boolean[]{true, true}, new int[]{256, 1}, new char[][]{"00000000000100000000".toCharArray(), "00000000000000000001".toCharArray()}));
    }

    // 读取1MB+1KB数据，TLB被填满并恰好被替换第0行，此时再次访问第0个虚页，TLB缺失，需要查页表，并将缺失的页表项加载到TLB第1行
    @Test
    public void test4() {
        mmu.read("000000000000000000000000000000000000000000000000", 1024 * 1024);
        mmu.read("000000000000000000000000000100000000000000000000", 1024);
        int len = 128;
        char[] expect = disk.read("00000000000000000000000000000000", len);
        char[] actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        assertArrayEquals(expect, actual);
        assertTrue(tlb.checkStatus(new int[]{0, 1}, new boolean[]{true, true}, new int[]{256, 0}, new char[][]{"00000000000100000000".toCharArray(), "00000000000000000000".toCharArray()}));
    }

}
