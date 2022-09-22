package cpu;

import cpu.registers.*;
import cpu.registers.Register;

public class CPU_State {

    // 标志寄存器
    public static Register eflags = new EFLAGS();

    // 指令指针寄存器
    public static Register eip = new EIP();

    // 通用寄存器
    public static Register eax = new Register32();
    public static Register ecx = new Register32();
    public static Register edx = new Register32();
    public static Register ebx = new Register32();
    public static Register esp = new Register32();
    public static Register ebp = new Register32();

    // 段寄存器
    public static Register cs = new Register16();
    public static Register ds = new Register16();
    public static Register ss = new Register16();

}
