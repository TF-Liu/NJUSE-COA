package cpu.fpu;

import org.junit.Test;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateFPUMulTest {

    private final FPU fpu = new FPU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    //  +0 * +0 = +0
    @Test
    public void fpuMulTest1(){
        src = new DataType(IEEE754Float.P_ZERO);
        dest = new DataType(IEEE754Float.P_ZERO);
        result = fpu.mul(src, dest);
        assertEquals( IEEE754Float.P_ZERO, result.toString() );
    }

    //  +0 * -0 = -0
    @Test
    public void fpuMulTest2(){
        src = new DataType(IEEE754Float.P_ZERO);
        dest = new DataType(IEEE754Float.N_ZERO);
        result = fpu.mul(src, dest);
        assertEquals( IEEE754Float.N_ZERO, result.toString() );
    }

    //  +0 * +无穷 = NaN
    @Test
    public void fpuMulTest3(){
        src = new DataType(IEEE754Float.P_ZERO);
        dest = new DataType(IEEE754Float.P_INF);
        result = fpu.mul(src, dest);
        assertEquals(IEEE754Float.NaN, result.toString());
    }

    //   +0 * NaN = NaN
    @Test
    public void fpuMulTest4(){
        src = new DataType(IEEE754Float.P_ZERO);
        dest = new DataType("11111111101010101010101010101010");
        result = fpu.mul(src, dest);
        assertEquals(IEEE754Float.NaN, result.toString());
    }

    // +0 * -1 = -0
    @Test
    public void fpuMulTest5(){
        src = new DataType(IEEE754Float.P_ZERO);
        dest = new DataType(transformer.floatToBinary( "-1.0" ));
        result = fpu.mul(src, dest);
        assertEquals( IEEE754Float.N_ZERO, result.toString() );
    }

    // -0 * +1 = -0
    @Test
    public void fpuMulTest6(){
        src = new DataType(IEEE754Float.N_ZERO);
        dest = new DataType(transformer.floatToBinary( "1.0" ));
        result = fpu.mul(src, dest);
        assertEquals( IEEE754Float.N_ZERO, result.toString() );
    }

    @Test
    public void fpuMulTest8(){
        src = new DataType(transformer.floatToBinary( "1.25" ));
        dest = new DataType(transformer.floatToBinary( "1.375" ));
        result = fpu.mul(src, dest);
        assertEquals( transformer.floatToBinary( "1.71875" ), result.toString() );
    }

    @Test
    public void fpuMulRandomTest() {
        Random random = new Random();
        float a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextFloat();
            b = random.nextFloat();
            src = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(a))));
            dest = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(b))));
            result = fpu.mul(src, dest);
            assertEquals(transformer.intToBinary(Integer.toString(Float.floatToIntBits(a * b))), result.toString());
        }
    }

}
