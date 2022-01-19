package util;

import org.junit.*;

import static org.junit.Assert.*;

public class CRCTest {

    @Test
    public void CRCTrueTest1() {
        char[] data = "11100110".toCharArray();
        String p = "1011";
        char[] originCRC = CRC.Calculate(data, p);
        assertArrayEquals(originCRC, "100".toCharArray());
        assertArrayEquals(CRC.Check(data, p, originCRC), "000".toCharArray());
    }

    @Test
    public void CRCTrueTest2() {
        char[] data = "100011".toCharArray();
        String p = "1001";
        char[] originCRC = CRC.Calculate(data, p);
        assertArrayEquals(originCRC, "111".toCharArray());
        assertArrayEquals(CRC.Check(data, p, originCRC), "000".toCharArray());
    }

    @Test
    public void CRCTrueTest3() {
        char[] data = "100011".toCharArray();
        String p = "1011";
        char[] originCRC = CRC.Calculate(data, p);
        assertArrayEquals(originCRC, "111".toCharArray());
        assertArrayEquals(CRC.Check(data, p, originCRC), "000".toCharArray());
    }

    @Test
    public void CRCFalseTest() {
        char[] data = "11100110".toCharArray();
        String p = "1001";
        char[] originCRC = CRC.Calculate(data, p);
        assertArrayEquals(originCRC, "001".toCharArray());
        originCRC[1] = '1';
        assertArrayEquals(CRC.Check(data, p, originCRC), "010".toCharArray());
    }

}
