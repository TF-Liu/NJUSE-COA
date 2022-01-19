package memory.disk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiskTest {

    private final Disk disk = Disk.getDisk();

    @Test
    public void DiskTest1() {
        disk.read("00000000000000000000000000000000", 1);
        assertEquals(1, disk.getCurrentPoint());
        assertEquals(0, disk.getCurrentSector());
        assertEquals(0, disk.getCurrentTrack());
    }

    @Test
    public void DiskTest2() {
        disk.read("00000000000000000000000000000000", Disk.BYTE_PER_SECTOR);
        assertEquals(0, disk.getCurrentPoint());
        assertEquals(1, disk.getCurrentSector());
        assertEquals(0, disk.getCurrentTrack());
    }

}
