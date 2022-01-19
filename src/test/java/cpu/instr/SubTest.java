package cpu.instr;

import cpu.CPU;
import cpu.CPU_State;
import cpu.alu.ALU;
import cpu.mmu.MMU;
import memory.disk.Disk;
import org.junit.Before;
import org.junit.Test;
import util.DataType;

import static org.junit.Assert.assertEquals;

public class SubTest {

    private final CPU cpu = new CPU();
    private final MMU mmu = MMU.getMMU();
    private final ALU alu = new ALU();
    private final String opcode = String.valueOf((char) 0x2d);
    private final String eip = "00000000000000000000000000000000";

    @Before
    public void init() {
        mmu.clear();
        // 重置eip寄存器状态(本次作业只要求能够解析单条指令即可)
        CPU_State.eip.write(eip);
    }

    @Test
    public void test1() {
        String a = "00000000000000000000000000000100";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode;
        builder.immediate = String.valueOf(new char[]{0x04, 0x00, 0x00, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());

        CPU_State.eax.write(a);
        assertEquals(len, cpu.execInstr());
        assertEquals(alu.sub(new DataType(a), new DataType(a)).toString(), CPU_State.eax.read());
    }

    @Test
    public void test2() {
        String a = "00000000111111111111111111111111";
        String b = "00000000000000000000000000000001";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode;
        builder.immediate = String.valueOf(new char[]{0x01, 0x00, 0x00, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());

        CPU_State.eax.write(a);
        assertEquals(len, cpu.execInstr());
        assertEquals(alu.sub(new DataType(b), new DataType(a)).toString(), CPU_State.eax.read());
    }

}
