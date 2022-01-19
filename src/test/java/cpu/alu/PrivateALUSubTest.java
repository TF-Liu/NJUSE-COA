package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateALUSubTest {

    private final ALU alu = new ALU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void SubTest2() {
        src = new DataType("00000000000000000001010011001101");
        dest = new DataType("00000000000000111001001101010100");
        result = alu.sub(src, dest);
        assertEquals("00000000000000110111111010000111", result.toString());
    }

    @Test
    public void SubTest3() {
        src = new DataType("00000000001100010111100101100110");
        dest = new DataType("00000000000000111001001011111010");
        result = alu.sub(src, dest);
        assertEquals("11111111110100100001100110010100", result.toString());
    }

    @Test
    public void SubTest4() {
        src = new DataType("00000000001100010111100101100110");
        dest = new DataType("11111111111111000110110100000110");
        result = alu.sub(src, dest);
        assertEquals("11111111110010101111001110100000", result.toString());
    }

    @Test
    public void SubTest5() {
        src = new DataType("11111111111111111000000110101010");
        dest = new DataType("11111111111111000110110100000110");
        result = alu.sub(src, dest);
        assertEquals("11111111111111001110101101011100", result.toString());
    }

    @Test
    public void SubTest6() {
        int[] input = {0x10000000, -3, -2, -1, 0, 1, 2};
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                src = new DataType(transformer.intToBinary(String.valueOf(input[i])));
                dest = new DataType(transformer.intToBinary(String.valueOf(input[j])));
                result = alu.sub(src, dest);
                assertEquals(transformer.intToBinary(String.valueOf(input[j] - input[i])), result.toString());
            }
        }
    }

    @Test
    public void SubRandomTest() {
        Random random = new Random();
        int a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextInt();
            b = random.nextInt();
            src = new DataType(transformer.intToBinary(String.valueOf(a)));
            dest = new DataType(transformer.intToBinary(String.valueOf(b)));
            result = alu.sub(src, dest);
            assertEquals(transformer.intToBinary(String.valueOf(b - a)), result.toString());
        }
    }

}
