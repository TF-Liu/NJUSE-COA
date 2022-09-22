package cpu.registers;

import util.Transformer;

/**
 * 指令指针寄存器
 */
public class EIP extends Register32 {

    public void plus(int len) {
        Transformer t = new Transformer();
        write(t.intToBinary(String.valueOf(Integer.parseInt(t.binaryToInt(read())) + len)));
    }

}
