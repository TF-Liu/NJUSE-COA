package cpu.instr;

import cpu.CPU;
import cpu.CPU_State;
import cpu.mmu.MMU;
import memory.disk.Disk;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JmpTest {

    private final CPU cpu = new CPU();
    private final MMU mmu = MMU.getMMU();
    private final String opcode = String.valueOf((char) 0xe9);
    private final String eip = "00000000000000000000000000000000";

    @Before
    public void init() {
        mmu.clear();
        // 重置eip寄存器状态(本次作业只要求能够解析单条指令即可)
        CPU_State.eip.write(eip);
    }

    @Test
    public void test1() {
        String a = "00000000000000000000000000010000";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode;
        builder.immediate = String.valueOf(new char[]{0x10, 0x00, 0x00, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());

        assertEquals(len, cpu.execInstr());
        assertEquals(a, CPU_State.eip.read());
    }

    @Test
    public void test2() {
        String a = "00000000111111111111111111111111";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode;
        builder.immediate = String.valueOf(new char[]{0xff, 0xff, 0xff, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());

        assertEquals(len, cpu.execInstr());
        assertEquals(a, CPU_State.eip.read());
    }

}
