package cpu.fpu;

import org.junit.Test;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateFPUAddTest {

    private final FPU fpu = new FPU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void fpuAddTest2() {
        src = new DataType("11000000010010001111010111000010");
        dest = new DataType("01000001001101010000000000000000");
        result = fpu.add(src, dest);
        assertEquals("01000001000000101100001010010000", result.toString());
    }

    @Test
    public void fpuAddTest3() {
        src = new DataType(IEEE754Float.P_INF);
        dest = new DataType("01000001001101010000000000000000");
        result = fpu.add(src, dest);
        assertEquals(IEEE754Float.P_INF, result.toString());
    }

    @Test
    public void fpuAddTest4() {
        src = new DataType("01111111110000000000000000000000");
        dest = new DataType("11000000010010111111010111000010");
        result = fpu.add(src, dest);
        assertEquals(IEEE754Float.NaN, result.toString());
    }

    @Test
    public void fpuAddTest5() {
        src = new DataType("01111111110000000000000000000000");
        dest = new DataType(IEEE754Float.N_ZERO);
        result = fpu.add(src, dest);
        assertEquals(IEEE754Float.NaN, result.toString());
    }

    @Test
    public void fpuAddTest6() {
        src = new DataType("01111111011111111111111111111111");
        dest = new DataType("01111111011111111111111111111111");
        result = fpu.add(src, dest);
        assertEquals(IEEE754Float.P_INF, result.toString());
    }

    @Test
    public void fpuAddTest7() {
        src = new DataType("11111111011111111111111001101111");
        dest = new DataType("01110001011111111111111111111111");
        result = fpu.add(src, dest);
        assertEquals("11111111011111111111111001101111", result.toString());
    }

    @Test
    public void fpuAddTest8() {
        src = new DataType(IEEE754Float.P_ZERO);
        dest = new DataType(IEEE754Float.P_ZERO);
        result = fpu.add(src, dest);
        assertEquals(IEEE754Float.P_ZERO, result.toString());
    }

    @Test
    public void fpuAddRandomTest() {
        Random random = new Random();
        float a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextFloat();
            b = random.nextFloat();
            src = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(a))));
            dest = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(b))));
            result = fpu.add(src, dest);
            assertEquals(transformer.intToBinary(Integer.toString(Float.floatToIntBits(a + b))), result.toString());
        }
    }

}
