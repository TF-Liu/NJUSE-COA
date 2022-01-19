package cpu.instr;

import cpu.CPU_State;
import cpu.mmu.MMU;

public class Mov implements Instruction {

    private final MMU mmu = MMU.getMMU();
    private int len = 0;
    private String instr;

    @Override
    public int exec(int opcode) {
        if (opcode == 0xa1) {
            len = 1 + 4;
            instr = String.valueOf(mmu.read(CPU_State.cs.read() + CPU_State.eip.read(), len));
            String offset = MMU.ToBitStream(instr.substring(1, 5));
            String src = MMU.ToBitStream(String.valueOf(mmu.read(CPU_State.ds.read() + offset, 4)));
            CPU_State.eax.write(src);
        } else if (opcode == 0xa3) {
            len = 1 + 4;
            instr = String.valueOf(mmu.read(CPU_State.cs.read() + CPU_State.eip.read(), len));
            String offset = MMU.ToBitStream(instr.substring(1, 5));
            char[] src = MMU.ToByteStream(CPU_State.eax.read());
            mmu.write(CPU_State.ds.read() + offset, 4, src);
        }
        return len;
    }

}
