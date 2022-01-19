package cpu.fpu;

import org.junit.Test;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

import static org.junit.Assert.assertEquals;

public class PrivateFPUDivTest {

    private final FPU fpu = new FPU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void fpuDivTest6(){
        dest = new DataType(IEEE754Float.P_INF);
        src = new DataType(transformer.floatToBinary( "1.0" ));
        result = fpu.div(src, dest);
        assertEquals(IEEE754Float.P_INF, result.toString());
    }

    @Test
    public void fpuDivTest7(){
        dest = new DataType(IEEE754Float.N_ZERO);
        src = new DataType(transformer.floatToBinary( "1.0" ));
        result = fpu.div(src, dest);
        assertEquals(IEEE754Float.N_ZERO, result.toString());
    }

    @Test
    public void fpuDivTest8(){
        dest = new DataType(transformer.floatToBinary( IEEE754Float.P_ZERO ));
        src = new DataType(transformer.floatToBinary( "5.0" ));
        result = fpu.div(src, dest);
        assertEquals(IEEE754Float.P_ZERO, result.toString());
    }

    @Test
    public void fpuDivTest9(){
        dest = new DataType(IEEE754Float.P_ZERO);
        src = new DataType(IEEE754Float.N_ZERO);
        result = fpu.div(src, dest);
        assertEquals(IEEE754Float.NaN, result.toString());
    }

}
