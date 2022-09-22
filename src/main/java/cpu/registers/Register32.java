package cpu.registers;

import java.util.Arrays;

/**
 * 32-bits寄存器
 */
public class Register32 extends Register{

    public Register32() {
        reg = new char[32];
        Arrays.fill(reg, '0');
    }

}
