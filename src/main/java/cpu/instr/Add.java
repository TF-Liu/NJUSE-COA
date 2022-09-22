package cpu.instr;

import cpu.CPU_State;
import cpu.alu.ALU;
import cpu.mmu.MMU;
import util.DataType;

public class Add implements Instruction {

    private final MMU mmu = MMU.getMMU();
    private int len = 0;
    private String instr;
    private final ALU alu = new ALU();

    @Override
    public int exec(int opcode) {
        if (opcode == 0x05) {
            len = 1 + 4;
            instr = String.valueOf(mmu.read(CPU_State.cs.read() + CPU_State.eip.read(), len));
            String imm = MMU.ToBitStream(instr.substring(1, 5));
            DataType result = alu.add(new DataType(CPU_State.eax.read()), new DataType(imm));
            CPU_State.eax.write(result.toString());
        }
        return len;
    }

}
