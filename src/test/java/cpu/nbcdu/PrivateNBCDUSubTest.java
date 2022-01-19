package cpu.nbcdu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrivateNBCDUSubTest {

    private final NBCDU nbcdu = new NBCDU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void SubTest2() {
        src = new DataType("11000000000000000000001100001001");
        dest = new DataType("11000000000000000000000100100101");
        result = nbcdu.sub(src, dest);
        assertEquals("11010000000000000000000110000100", result.toString());
    }

    @Test
    public void SubTest3() {
        src = new DataType("11000000000000000000000000000000");
        dest = new DataType("11000000000000000000000000000000");
        result = nbcdu.sub(src, dest);
        assertEquals("11000000000000000000000000000000", result.toString());
    }

    @Test
    public void SubTest4() {
        src = new DataType("11010000000000000000100010011000");
        dest = new DataType("11010000000000000000100010011000");
        result = nbcdu.sub(src, dest);
        assertEquals("11000000000000000000000000000000", result.toString());
    }

    @Test
    public void SubTest5() {
        int[] input = {1000000, -3, -2, -1, 0, 1, 2, 3, -1000000};
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                src = new DataType(transformer.decimalToNBCD(String.valueOf(input[i])));
                dest = new DataType(transformer.decimalToNBCD(String.valueOf(input[j])));
                result = nbcdu.sub(src, dest);
                assertEquals(transformer.decimalToNBCD(String.valueOf(input[j] - input[i])), result.toString());
            }
        }
    }

    @Test
    public void SubRandomTest() {
        Random random = new Random();
        int a, b;
        for (int i = 0; i < 10000; i++) {
            a = random.nextInt(9999999);
            b = random.nextInt(9999999);
            src = new DataType(transformer.decimalToNBCD(String.valueOf(a)));
            dest = new DataType(transformer.decimalToNBCD(String.valueOf(b)));
            result = nbcdu.sub(src, dest);
            assertEquals(transformer.decimalToNBCD(String.valueOf(b - a)), result.toString());
        }
    }
}
