package memory;

import cpu.mmu.MMU;
import memory.cache.Cache;
import memory.disk.Disk;
import memory.tlb.TLB;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class EasterEgg {

    private final MMU mmu = MMU.getMMU();

    private final Disk disk = Disk.getDisk();

    private final MemTestHelper helper = new MemTestHelper();

    @Before
    public void init() {
        Memory.PAGE = true;
        Memory.SEGMENT = true;
        helper.clearAll();
        Memory.timer = true;

        // TODO: 修改以下两个属性
        Cache.isAvailable = true;
        TLB.isAvailable = true;
    }

    @Test
    public void EasterEgg1() {
        int len = 32;
        char[] expect = disk.read("00000000000000000000000000000000", len);
        char[] actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        for (int i = 0; i < 300; i++) {
            actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        }
        assertArrayEquals(expect, actual);
    }

}
