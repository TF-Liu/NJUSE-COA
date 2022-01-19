package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateALUDivTest {

    private final ALU alu = new ALU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    /**
     * -267 / 3711 = 0 (-267)
     */
    @Test
    public void DivTest2() {
        src = new DataType("00000000000000000000111001111111");
        dest = new DataType("11111111111111111111111011110101");
        result = alu.div(src, dest);
        String quotient = "00000000000000000000000000000000";
        String remainder = "11111111111111111111111011110101";
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }


    /**
     * -7510 / -347 = 21 (-223)
     */
    @Test
    public void DivTest3() {
        src = new DataType("11111111111111111111111010100101");
        dest = new DataType("11111111111111111110001010101010");
        result = alu.div(src, dest);
        String quotient = "00000000000000000000000000010101";
        String remainder = "11111111111111111111111100100001";
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }


    /**
     * 9859 / 8794 = 1 (1065)
     */
    @Test
    public void DivTest4() {
        src = new DataType("00000000000000000010001001011010");
        dest = new DataType("00000000000000000010011010000011");
        result = alu.div(src, dest);
        String quotient = "00000000000000000000000000000001";
        String remainder = "00000000000000000000010000101001";
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }


    /**
     * 5327 / -7229 = 0 (5327)
     */
    @Test
    public void DivTest5() {
        src = new DataType("11111111111111111110001111000011");
        dest = new DataType("00000000000000000001010011001111");
        result = alu.div(src, dest);
        String quotient = "00000000000000000000000000000000";
        String remainder = "00000000000000000001010011001111";
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }


    /**
     * 0 / -1 = 0 (0)
     */
    @Test
    public void DivTest6() {
        src = new DataType("11111111111111111111111111111111");
        dest = new DataType("00000000000000000000000000000000");
        result = alu.div(src, dest);
        String quotient = "00000000000000000000000000000000";
        String remainder = "00000000000000000000000000000000";
        assertEquals(quotient, result.toString());
        assertEquals(remainder, alu.remainderReg.toString());
    }

    /**
     * -2^31 / -1 = -2^31(0) 溢出
     */
    @Test
    public void DivTest7() {
        src = new DataType("11111111111111111111111111111111");
        dest = new DataType("10000000000000000000000000000000");
        result = alu.div(src, dest);
        assertEquals( "10000000000000000000000000000000", result.toString());
        assertEquals("00000000000000000000000000000000", alu.remainderReg.toString());
    }

    @Test
    public void DivTest8() {
        int[] input = {-6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6};
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                src = new DataType(transformer.intToBinary(String.valueOf(input[i])));
                dest = new DataType(transformer.intToBinary(String.valueOf(input[j])));
                result = alu.div(src, dest);
                assertEquals(transformer.intToBinary(String.valueOf(input[j] / input[i])), result.toString());
                assertEquals(transformer.intToBinary(String.valueOf(input[j] % input[i])), alu.remainderReg.toString());
            }
        }
    }


    @Test
    public void DivRandomTest() {
        Random random = new Random();
        int a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextInt();
            b = random.nextInt();
            src = new DataType(transformer.intToBinary(String.valueOf(a)));
            dest = new DataType(transformer.intToBinary(String.valueOf(b)));
            result = alu.div(src, dest);
            assertEquals(transformer.intToBinary(String.valueOf(b / a)), result.toString());
            assertEquals(transformer.intToBinary(String.valueOf(b % a)), alu.remainderReg.toString());
        }
    }


    /**
     * 1 / 0  除0异常
     */
    @Test(expected = ArithmeticException.class)
    public void DivExceptionTest2() {
        src = new DataType("00000000000000000000000000000000");
        dest = new DataType("00000000000000000000000000000001");
        result = alu.div(src, dest);
    }

}
