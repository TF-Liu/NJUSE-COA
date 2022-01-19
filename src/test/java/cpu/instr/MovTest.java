package cpu.instr;

import cpu.CPU;
import cpu.CPU_State;
import cpu.mmu.MMU;
import memory.Memory;
import memory.disk.Disk;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MovTest {

    private final CPU cpu = new CPU();
    private final MMU mmu = MMU.getMMU();
    private final Memory memory = Memory.getMemory();
    private final String opcode1 = String.valueOf((char) 0xa1);
    private final String opcode2 = String.valueOf((char) 0xa3);
    private final String eip = "00000000000000000000000000000000";

    @Before
    public void init() {
        mmu.clear();
        // 重置eip寄存器状态(本次作业只要求能够解析单条指令即可)
        CPU_State.eip.write(eip);
    }

    @Test
    public void test1() {
        String a = "11111111111111111111111111111111";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode1;
        builder.immediate = String.valueOf(new char[]{0x10, 0x00, 0x00, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());
        Disk.getDisk().write("00000000000000000000000000010000", 4, new char[]{0xff, 0xff, 0xff, 0xff});

        assertEquals(len, cpu.execInstr());
        assertEquals(a, CPU_State.eax.read());
    }

    @Test
    public void test2() {
        String a = "00000000111111111111111111111111";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode1;
        builder.immediate = String.valueOf(new char[]{0x10, 0x00, 0x00, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());
        Disk.getDisk().write("00000000000000000000000000010000", 4, new char[]{0xff, 0xff, 0xff, 0x00});

        assertEquals(len, cpu.execInstr());
        assertEquals(a, CPU_State.eax.read());
    }

    @Test
    public void test3() {
        String a = "11111111111111111111111111111111";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode2;
        builder.immediate = String.valueOf(new char[]{0x10, 0x00, 0x00, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());

        CPU_State.eax.write(a);
        assertEquals(len, cpu.execInstr());
        assertArrayEquals(new char[]{0xff, 0xff, 0xff, 0xff}, memory.read("00000000000000000000000000010000", 4));
    }

    @Test
    public void test4() {
        String a = "00000000111111111111111111111111";
        int len = 1 + 4;

        InstrBuilder builder = new InstrBuilder();
        builder.opcode = opcode2;
        builder.immediate = String.valueOf(new char[]{0x10, 0x00, 0x00, 0x00});

        // 将指令写入磁盘
        Disk.getDisk().write(eip, len, builder.toString().toCharArray());

        CPU_State.eax.write(a);
        assertEquals(len, cpu.execInstr());
        assertArrayEquals(new char[]{0xff, 0xff, 0xff, 0x00}, memory.read("00000000000000000000000000010000", 4));
    }

}
