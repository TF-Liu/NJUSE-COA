package cpu.instr;

import cpu.CPU_State;
import cpu.mmu.MMU;
import cpu.registers.EIP;
import util.Transformer;

public class Jmp implements Instruction {

    private final MMU mmu = MMU.getMMU();
    private int len = 0;
    private String instr;
    private final Transformer transformer = new Transformer();

    @Override
    public int exec(int opcode) {
        if (opcode == 0xe9) {
            len = 1 + 4;
            instr = String.valueOf(mmu.read(CPU_State.cs.read() + CPU_State.eip.read(), len));
            String imm = MMU.ToBitStream(instr.substring(1, 5));
            ((EIP)CPU_State.eip).plus(Integer.parseInt(transformer.binaryToInt(imm)));
        }
        return len;
    }

}
