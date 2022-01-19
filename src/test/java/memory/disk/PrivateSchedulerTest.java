package memory.disk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrivateSchedulerTest {

    Scheduler scheduler = new Scheduler();

    int start2 = 100;
    int[] request2 = {55, 58, 39, 18, 90, 160, 150, 38, 184};

    int start3 = 0;
    int[] request3 = {0};

    int start4 = 0;
    int[] request4 = {1, 3, 10};

    @Test
    public void FCFSTest2() {
        double result = scheduler.FCFS(start2, request2);
        assertEquals(498.0 / 9, result, 0.01);
    }

    @Test
    public void FCFSTest3() {
        double result = scheduler.FCFS(start3, request3);
        assertEquals(0, result, 0.01);
    }

    @Test
    public void SSTFTest2() {
        double result = scheduler.SSTF(start2, request2);
        assertEquals(248.0 / 9, result, 0.01);
    }

    @Test
    public void SSTFTest3() {
        double result = scheduler.SSTF(start3, request3);
        assertEquals(0, result, 0.01);
    }

    @Test
    public void SSTFTest4() {
        double result = scheduler.SSTF(start4, request4);
        assertEquals(10.0 / 3, result, 0.01);
    }

    @Test
    public void SCANTest3() {
        double result = scheduler.SCAN(start2, request2, true);
        assertEquals(392.0 / 9, result, 0.01);
    }

    @Test
    public void SCANTest4() {
        double result = scheduler.SCAN(start2, request2, false);
        assertEquals(284.0 / 9, result, 0.01);
    }

    @Test
    public void SCANTest5() {
        double result = scheduler.SCAN(start3, request3, true);
        assertEquals(0, result, 0.01);
    }

    @Test
    public void SCANTest6() {
        double result = scheduler.SCAN(start3, request3, false);
        assertEquals(0, result, 0.01);
    }

    @Test
    public void SCANTest7() {
        double result = scheduler.SCAN(start4, request4, true);
        assertEquals(10.0 / 3, result, 0.01);
    }

    @Test
    public void SCANTest8() {
        double result = scheduler.SCAN(start4, request4, false);
        assertEquals(10.0 / 3, result, 0.01);
    }
}
