package util;

import org.junit.Assert;
import org.junit.Test;

public class DecimalToNBCDTest {

    private Transformer transformer = new Transformer();

    @Test
    public void test1() {
        Assert.assertEquals("11000000000000000000000000010000",
                transformer.decimalToNBCD("10"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("11000000000000000000001100000110",
                transformer.decimalToNBCD("306"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("11010000000000000000010001010001",
                transformer.decimalToNBCD("-451"));
    }

    @Test
    public void test4() {
        Assert.assertEquals("11000000000000000001100110010111",
                transformer.decimalToNBCD("1997"));
    }

    @Test
    public void test5() {
        Assert.assertEquals("11000000000000000000000000000000",
                transformer.decimalToNBCD("0"));
    }
}
