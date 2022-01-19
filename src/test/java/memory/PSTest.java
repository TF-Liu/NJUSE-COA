package memory;

import cpu.mmu.MMU;
import memory.cache.Cache;
import memory.tlb.TLB;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * 段页式内存管理模式下读数据，需要逻辑地址转线性地址再转物理地址
 */
public class PSTest {

    private static MMU mmu;

    private static Memory memory;

    private static MemTestHelper helper;

    private final char[] base = "00000000000000000000000000000000".toCharArray();
    private final char[] limit = "11111111111111111111".toCharArray();

    @Before
    public void init() {
        mmu = MMU.getMMU();
        Memory.PAGE = true;
        Memory.SEGMENT = true;
        memory = Memory.getMemory();
        helper = new MemTestHelper();
        helper.clearAll();
        Cache.isAvailable = false;
        TLB.isAvailable = false;
        Memory.timer = false;
    }

    // 第0个虚页放到第0个物理页框
    @Test
    public void test1() {
        int len = 128;
        char[] expect = helper.fillData('0', len);
        char[] actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        assertArrayEquals(expect, actual);
        assertArrayEquals(base, memory.getBaseOfSegDes(0));
        assertArrayEquals(limit, memory.getLimitOfSegDes(0));
        assertTrue(memory.isValidSegDes(0));
        assertTrue(memory.isGranularitySegDes(0));
        assertTrue(memory.isValidPage(0));
        assertArrayEquals("00000000000000000000".toCharArray(), memory.getFrameOfPage(0));
    }

    // 第0个虚页放到第0个物理页框，第2个虚页放到第1个物理页框
    @Test
    public void test2() {
        int len = 128;
        mmu.read("000000000000000000000000000000000000000000000000", len);
        char[] expect = helper.fillData('8', len);
        char[] actual = mmu.read("000000000000000000000000000000000010000000000000", len);
        assertArrayEquals(expect, actual);
        assertTrue(memory.isValidPage(2));
        assertArrayEquals("00000000000000000001".toCharArray(), memory.getFrameOfPage(2));
    }

    // 跨页访问，第4、5、6个虚页分别放到第0、1、2个物理页框
    @Test
    public void test3() {
        int len = 1024 * 4 * 3;
        char[] expect = new char[len];
        for (int i = 0; i < 4 * 3; i++) {
            Arrays.fill(expect, i * 1024, i * 1024 + 1024, (char)(0x40 + i));
        }
        char[] actual = mmu.read("000000000000000000000000000000000100000000000000", len);
        assertArrayEquals(expect, actual);
        assertTrue(memory.isValidPage(4));
        assertTrue(memory.isValidPage(5));
        assertTrue(memory.isValidPage(6));
        assertArrayEquals("00000000000000000000".toCharArray(), memory.getFrameOfPage(4));
        assertArrayEquals("00000000000000000001".toCharArray(), memory.getFrameOfPage(5));
        assertArrayEquals("00000000000000000010".toCharArray(), memory.getFrameOfPage(6));
    }

    // 从一个页的中间开始跨页访问4KB内容，第24、25个虚页分别放到第0、1个物理页框
    @Test
    public void test4() {
        int len = 1024 * 4;
        char[] expect = new char[len];
        for (int i = 0; i < 4; i++) {
            Arrays.fill(expect, i * 1024, i * 1024 + 1024, (char)(0x52 + i));
        }
        char[] actual = mmu.read("000000000000000000000000000000011000100000000000", len);
        assertArrayEquals(expect, actual);
        assertTrue(memory.isValidPage(24));
        assertTrue(memory.isValidPage(25));
        assertArrayEquals("00000000000000000000".toCharArray(), memory.getFrameOfPage(24));
        assertArrayEquals("00000000000000000001".toCharArray(), memory.getFrameOfPage(25));
    }

}
