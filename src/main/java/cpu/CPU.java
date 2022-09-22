package cpu;

import cpu.decode.InstrFactory;
import cpu.instr.Instruction;
import cpu.mmu.MMU;
import cpu.registers.Register;

public class CPU {

    private final MMU mmu = MMU.getMMU();

    public CPU() {}

    /**
     * execInstr a single instruction according to eip value
     */
    public int execInstr() {
        String eip = CPU_State.eip.read();
        // Fetch the target executor class according to opcode
        int opcode = instrFetch(eip, 1);
        Instruction instruction = InstrFactory.getInstr(opcode);
        assert instruction != null;
        return instruction.exec(opcode);
    }

    /**
     * @param eip 指令指针寄存器
     * @param length opcode的字节数，本作业只使用单字节opcode
     * @return int表示的opcode
     */
    public int instrFetch(String eip, int length) {
        Register cs = CPU_State.cs;
        char opcode = mmu.read(cs.read() + eip, length)[0];
        return opcode & 0xFF;
    }

}

