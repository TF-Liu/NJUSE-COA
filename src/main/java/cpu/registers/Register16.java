package cpu.registers;

import java.util.Arrays;

/**
 * 16-bits寄存器
 */
public class Register16 extends Register{

    public Register16() {
        reg = new char[16];
        Arrays.fill(reg, '0');
    }

}
