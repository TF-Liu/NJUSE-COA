package cpu.fpu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateFPUSubTest {

    private final FPU fpu = new FPU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;


    @Test
    public void fpuSubTest2(){
        src = new DataType("01000001110101100000000000000000");
        dest = new DataType("01000000111011011100000000000000");
        result = fpu.sub(src, dest);
        assertEquals("11000001100110101001000000000000", result.toString());
    }

    @Test
    public void fpuSubTest3(){
        src = new DataType("11001001110100010000000000000000");
        dest = new DataType("01001001110100010000000000000000");
        result = fpu.sub(src, dest);
        assertEquals("01001010010100010000000000000000", result.toString());
    }

    @Test
    public void fpuSubTest4(){
        src = new DataType("01001001110100010000000000000000");
        dest = new DataType("01001001110100010000000000000000");
        result = fpu.sub(src, dest);
        assertEquals("00000000000000000000000000000000", result.toString());
    }

    @Test
    public void fpuSubTest5(){
        src = new DataType("11001001110100010000000000000000");
        dest = new DataType("00110101110100010000000000000000");
        result = fpu.sub(src, dest);
        assertEquals("01001001110100010000000000000000", result.toString());
    }

    @Test
    public void fpuSubTest6(){
        src = new DataType("01001001110100010000000000000000");
        dest = new DataType("00110101110100010000000000000000");
        result = fpu.sub(src, dest);
        assertEquals("11001001110100010000000000000000", result.toString());
    }

    @Test
    public void fpuSubTest7(){
        src = new DataType("10111110100000000000000000000000");
        dest = new DataType("00111111000000000000000000000000");
        result = fpu.sub(src, dest);
        assertEquals("00111111010000000000000000000000", result.toString());
    }

    @Test
    public void fpuSubTest8(){
        src = new DataType("00111110100000000000000000000000");
        dest = new DataType("00111111000000000000000000000000");
        result = fpu.sub(src, dest);
        assertEquals("00111110100000000000000000000000", result.toString());
    }

    @Test
    public void fpuSubRandomTest() {
        Random random = new Random();
        float a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextFloat();
            b = random.nextFloat();
            src = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(a))));
            dest = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(b))));
            result = fpu.sub(src, dest);
            assertEquals(transformer.intToBinary(Integer.toString(Float.floatToIntBits(b - a))), result.toString());
        }
    }

}
