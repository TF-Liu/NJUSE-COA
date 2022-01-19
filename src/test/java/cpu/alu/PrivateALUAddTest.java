package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateALUAddTest {

    private final ALU alu = new ALU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void AddTest2() {
        src = new DataType("11111000100110000101011000000110");
        dest = new DataType("00000010000011000001000110000001");
        result = alu.add(src, dest);
        assertEquals("11111010101001000110011110000111", result.toString());
    }

    @Test
    public void AddTest3() {
        src = new DataType("00000000000000111001001101010100");
        dest = new DataType("00000000000000000001010011001101");
        result = alu.add(src, dest);
        assertEquals("00000000000000111010100000100001", result.toString());
    }

    @Test
    public void AddTest4() {
        src = new DataType("11111111111111000110110010101100");
        dest = new DataType("11111111111111111110101100110011");
        result = alu.add(src, dest);
        assertEquals("11111111111111000101011111011111", result.toString());
    }

    @Test
    public void AddTest5() {
        src = new DataType("11111111111111000110110100000110");
        dest = new DataType("00000000001100010111100101100110");
        result = alu.add(src, dest);
        assertEquals("00000000001011011110011001101100", result.toString());
    }

    @Test
    public void AddTest6() {
        int[] input = {0x10000000, -3, -2, -1, 0, 1, 2};
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                src = new DataType(transformer.intToBinary(String.valueOf(input[i])));
                dest = new DataType(transformer.intToBinary(String.valueOf(input[j])));
                result = alu.add(src, dest);
                assertEquals(transformer.intToBinary(String.valueOf(input[i] + input[j])), result.toString());
            }
        }
    }

    @Test
    public void AddRandomTest() {
        Random random = new Random();
        int a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextInt();
            b = random.nextInt();
            src = new DataType(transformer.intToBinary(String.valueOf(a)));
            dest = new DataType(transformer.intToBinary(String.valueOf(b)));
            result = alu.add(src, dest);
            assertEquals(transformer.intToBinary(String.valueOf(a + b)), result.toString());
        }
    }

}
