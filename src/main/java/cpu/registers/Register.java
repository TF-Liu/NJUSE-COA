package cpu.registers;

/**
 * 寄存器抽象类
 */
public abstract class Register {

    protected char[] reg;

    public String read() {
        return new String(reg);
    }

    public void write(String v) {
        reg = v.toCharArray();
    }

}
