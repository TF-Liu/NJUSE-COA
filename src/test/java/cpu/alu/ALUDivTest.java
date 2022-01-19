package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import static org.junit.Assert.assertEquals;

public class ALUDivTest {

    private final ALU alu = new ALU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    /**
     * 10 / 10 = 1 (0)
     */
    @Test
    public void DivTest1() {
        src = new DataType("00000000000000000000000000001010");
        dest = new DataType("00000000000000000000000000001010");
        result = alu.div(src, dest);
        String quotient = "00000000000000000000000000000001";
        String remainder = "00000000000000000000000000000000";
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }

    /**
     * -8 / 2 = -4 (0)
     * 除法算法固有的bug
     */
    @Test
    public void DivSpecialTest() {
        Transformer t = new Transformer();
        src = new DataType(t.intToBinary("2"));
        dest = new DataType(t.intToBinary("-8"));
        result = alu.div(src, dest);
        String quotient = t.intToBinary("-4");
        String remainder = t.intToBinary("0");
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }

    /**
     * 0 / 0  除0异常
     */
    @Test(expected = ArithmeticException.class)
    public void DivExceptionTest1() {
        src = new DataType("00000000000000000000000000000000");
        dest = new DataType("00000000000000000000000000000000");
        result = alu.div(src, dest);
    }

}
