package memory.disk;

import org.junit.Test;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateDiskTest {

    private final Disk disk = Disk.getDisk();
    private final Transformer transformer = new Transformer();

    private String calAddr(int track, int sector, int point) {
        return "000000000" +
                transformer.intToBinary(String.valueOf(track)).substring(24) +
                transformer.intToBinary(String.valueOf(sector)).substring(26) +
                transformer.intToBinary(String.valueOf(point)).substring(23);
    }

    @Test
    public void DiskTest3() {
        String addr = calAddr(0, 63, 0);
        disk.read(addr, Disk.BYTE_PER_SECTOR);
        assertEquals(0, disk.getCurrentPoint());
        assertEquals(0, disk.getCurrentSector());
        assertEquals(1, disk.getCurrentTrack());
    }

    @Test
    public void DiskTest4() {
        String addr = calAddr(255, 63, 511);
        disk.read(addr, 1);
        assertEquals(0, disk.getCurrentPoint());
        assertEquals(0, disk.getCurrentSector());
        assertEquals(0, disk.getCurrentTrack());
    }

    @Test
    public void DiskRandomTest() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int track = random.nextInt(Disk.TRACK_NUM);
            int sector = random.nextInt(Disk.SECTOR_PER_TRACK);
            int point = random.nextInt(Disk.BYTE_PER_SECTOR);
            String addr = calAddr(track, sector, point);
            disk.read(addr, Disk.BYTE_PER_SECTOR);
            assertEquals(point, disk.getCurrentPoint());
            assertEquals((sector + 1) % Disk.SECTOR_PER_TRACK, disk.getCurrentSector());
            assertEquals((track + (sector + 1) / Disk.SECTOR_PER_TRACK) % Disk.TRACK_NUM, disk.getCurrentTrack());
        }
    }

}
