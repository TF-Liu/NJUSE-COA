package cpu.fpu;

import org.junit.Test;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

import static org.junit.Assert.assertEquals;

public class FPUDivTest {

    private final FPU fpu = new FPU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void fpuDivTest1(){
        dest = new DataType(transformer.floatToBinary( "0.4375" ));
        src = new DataType(transformer.floatToBinary( "0.5" ));
        result = fpu.div(src, dest);
        assertEquals(transformer.floatToBinary( "0.875" ), result.toString());
    }

    @Test
    public void fpuDivTest2(){
        dest = new DataType(transformer.floatToBinary( "1.0" ));
        src = new DataType(transformer.floatToBinary( "4.0" ));
        result = fpu.div(src, dest);
        assertEquals(transformer.floatToBinary( "0.25" ), result.toString());
    }

    @Test
    public void fpuDivTest3(){
        dest = new DataType(transformer.floatToBinary( "-2.0" ));
        src = new DataType(transformer.floatToBinary( "1.0" ));
        result = fpu.div(src, dest);
        assertEquals(transformer.floatToBinary( "-2.0" ), result.toString());
    }

    @Test
    public void fpuDivTest4(){
        dest = new DataType(transformer.floatToBinary( "1" ));
        src = new DataType(transformer.floatToBinary( "-2.0" ));
        result = fpu.div(src, dest);
        assertEquals(transformer.floatToBinary( "-0.5" ), result.toString());
    }

    @Test
    public void fpuDivTest5(){
        dest = new DataType(transformer.floatToBinary( "0.4375" ));
        src = new DataType(transformer.floatToBinary( "0.625" ));
        result = fpu.div(src, dest);
        assertEquals(transformer.floatToBinary("0.7"), result.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void fpuDivExceptionTest(){
        dest = new DataType(transformer.floatToBinary( "2.2" ));
        src = new DataType(transformer.floatToBinary( "0.0" ));
        result = fpu.div(src, dest);
    }

}
