package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import static org.junit.Assert.assertEquals;

public class ALUSubTest {

    private final ALU alu = new ALU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void SubTest1() {
        src = new DataType("00000000000000000000000000000100");
        dest = new DataType("00000000000000000000000000000100");
        result = alu.sub(src, dest);
        assertEquals("00000000000000000000000000000000", result.toString());
    }

}
