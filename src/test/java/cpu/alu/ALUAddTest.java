package cpu.alu;

import org.junit.Test;
import util.DataType;
import util.Transformer;

import static org.junit.Assert.assertEquals;

public class ALUAddTest {

    private final ALU alu = new ALU();
    private final Transformer transformer = new Transformer();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void AddTest1() {
        src = new DataType("00000000000000000000000000000100");
        dest = new DataType("00000000000000000000000000000100");
        result = alu.add(src, dest);
        assertEquals("00000000000000000000000000001000", result.toString());
    }

}
