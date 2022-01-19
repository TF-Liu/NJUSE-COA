package memory;

import cpu.mmu.MMU;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 分段模式下读取数据，需要将逻辑地址转线性地址，线性地址等效于物理地址
 */
public class SegTest {

    private static MMU mmu;

    private static Memory memory;

    private static MemTestHelper helper;

    private final char[] base = "00000000000000000000000000000000".toCharArray();
    private final char[] limit = "11111111111111111111".toCharArray();

    @Before
    public void init() {
        mmu = MMU.getMMU();
        Memory.PAGE = false;
        Memory.SEGMENT = true;
        memory = Memory.getMemory();
        helper = new MemTestHelper();
        helper.clearAll();
    }

    // 访问并创建第0段
    @Test
    public void test1() {
        int len = 128;
        char[] expect = helper.fillData('0', len);
        char[] actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        assertArrayEquals(expect, actual);
        assertArrayEquals(base, memory.getBaseOfSegDes(0));
        assertArrayEquals(limit, memory.getLimitOfSegDes(0));
        assertTrue(memory.isValidSegDes(0));
        assertFalse(memory.isGranularitySegDes(0));
    }

    // 创建并访问两次第1段
    @Test
    public void test2() {
        int len = 128;
        char[] expect = helper.fillData('0', len);
        char[] actual = mmu.read("000000000000100000000000000000000000000000000000", len);
        assertArrayEquals(expect, actual);
        assertArrayEquals(base, memory.getBaseOfSegDes(1));
        assertArrayEquals(limit, memory.getLimitOfSegDes(1));
        assertTrue(memory.isValidSegDes(1));
        assertFalse(memory.isGranularitySegDes(1));
        expect = helper.fillData('1', len);
        actual = mmu.read("000000000000100000000000000000000000010000000000", len);
        assertArrayEquals(expect, actual);
    }

    // 强制创建第2段，段基址不为全0，段界限不为全1
    @Test
    public void test3() {
        int len = 128;
        mmu.read("000000000000000000000000000000000000000000000000", len);
        String segBase = "00000000000000000001000000000000";
        memory.alloc_seg_force(2, segBase, 1024, false);
        char[] expect = helper.fillData('4', len);
        char[] actual = mmu.read("000000000001000000000000000000000000000000000000", len);
        assertArrayEquals(expect, actual);
        assertArrayEquals(segBase.toCharArray(), memory.getBaseOfSegDes(2));
        assertArrayEquals("0000000000000000000010000000000".toCharArray(), memory.getLimitOfSegDes(2));
        assertTrue(memory.isValidSegDes(2));
        assertFalse(memory.isGranularitySegDes(2));
    }

    // 强制创建第3段，越界访问，报段错误
    @Test(expected = SecurityException.class)
    public void test4() {
        int len = 128;
        String segBase = "00000000000100000000000000000000";
        memory.alloc_seg_force(3, segBase, 1024, false);
        mmu.read("000000000001100000000000000000000000001111000000", len);
    }

}
