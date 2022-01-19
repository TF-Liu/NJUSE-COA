package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateALUMulTest {

    private final ALU alu = new ALU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    /**
     * -267 * 3711 = -990837
     */
    public void MulTest2() {
        src = new DataType("11111111111111111111111011110101");
        dest = new DataType("00000000000000000000111001111111");
        result = alu.mul(src, dest);
        assertEquals("11111111111100001110000110001011", result.toString());
    }

    @Test
    /**
     * -7510 * -347 = 2605970
     */
    public void MulTest3() {
        src = new DataType("11111111111111111110001010101010");
        dest = new DataType("11111111111111111111111010100101");
        result = alu.mul(src, dest);
        assertEquals("00000000001001111100001110010010", result.toString());
    }

    @Test
    /**
     * 9859 * 8794 = 86700046
     */
    public void MulTest4() {
        src = new DataType("00000000000000000010011010000011");
        dest = new DataType("00000000000000000010001001011010");
        result = alu.mul(src, dest);
        assertEquals("00000101001010101111000000001110", result.toString());
    }

    @Test
    /**
     * 5327 * -7229 = -38508883
     */
    public void MulTest5() {
        src = new DataType("00000000000000000001010011001111");
        dest = new DataType("11111111111111111110001111000011");
        result = alu.mul(src, dest);
        assertEquals("11111101101101000110011010101101", result.toString());
    }

    @Test
    /**
     * 2^16 = 65536
     */
    public void MulTest6() {
        src = new DataType("00000000000000000000000000000010");
        result = new DataType("00000000000000000000000000000010");
        for (int i=0; i<15; i++) {
            result = alu.mul(src, result);
        }
        assertEquals("00000000000000010000000000000000", result.toString());
    }

    @Test
    /**
     * 2347823 * -1234212 = -2897711320476 = 1391604324‬‬-(2^32*675) -> 1391604324
     */
    public void MulTest7() {
        src = new DataType("00000000001000111101001100101111");
        dest = new DataType("11111111111011010010101011011100");
        result = alu.mul(src, dest);
        assertEquals("01010010111100100011001001100100", result.toString());
    }

    @Test
    public void MulTest8() {
        int[] input = {0xffffffff,-3, -2, -1, 0, 1, 2, 4, 5, 0xefffffff};
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                src = new DataType(transformer.intToBinary(String.valueOf(input[i])));
                dest = new DataType(transformer.intToBinary(String.valueOf(input[j])));
                result = alu.mul(src, dest);
                assertEquals(transformer.intToBinary(String.valueOf(input[i] * input[j])), result.toString());
            }
        }
    }

    @Test
    public void MulRandomTest() {
        Random random = new Random();
        int a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextInt();
            b = random.nextInt();
            src = new DataType(transformer.intToBinary(String.valueOf(a)));
            dest = new DataType(transformer.intToBinary(String.valueOf(b)));
            result = alu.mul(src, dest);
            assertEquals(transformer.intToBinary(String.valueOf(a * b)), result.toString());
        }
    }

}
