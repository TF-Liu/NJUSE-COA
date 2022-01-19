package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FloatToBinaryTest {

    private Transformer t = new Transformer();

    @Test
    public void test1() {
        String result = t.floatToBinary(String.valueOf(Math.pow(2, -127)));
        assertEquals("00000000010000000000000000000000", result);
    }

    @Test
    public void test2() {
        String result = t.floatToBinary(String.valueOf(0.75 * Math.pow(2, -126)));
        assertEquals("00000000011000000000000000000000", result);
    }

    @Test
    public void test3() {
        String result = t.floatToBinary("" + 1.6328125 * Math.pow(2, 20));
        assertEquals("01001001110100010000000000000000", result);
    }

    @Test
    public void test4() {
        String result = t.floatToBinary("-" + 1.6328125 * Math.pow(2, 20));
        assertEquals("11001001110100010000000000000000", result);
    }

    @Test
    public void test5() {
        String result = t.floatToBinary("" + 1.6328125 * Math.pow(2, -20));
        assertEquals("00110101110100010000000000000000", result);
    }

    @Test
    public void test6() {
        String result = t.floatToBinary("-" + 1.6328125 * Math.pow(2, -20));
        assertEquals("10110101110100010000000000000000", result);
    }

    @Test
    public void test7() {
        String result = t.floatToBinary("-" + Double.MAX_VALUE);  //对于32位的表示来说溢出
        assertEquals("-Inf", result);
    }

    @Test
    public void test8() {
        String result = t.floatToBinary("" + Double.MAX_VALUE);
        assertEquals("+Inf", result);
    }

}
