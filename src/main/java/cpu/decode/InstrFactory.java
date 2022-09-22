package cpu.decode;

import cpu.instr.Instruction;

public class InstrFactory {

    private static final String PREFIX = "cpu.instr.";

    public static Instruction getInstr(int opcode) {
        try {
            String className = Opcode.opcodeEntry[opcode].split("_")[0];
            className = className.substring(0, 1).toUpperCase() + className.substring(1);
            System.out.println(opcode + " " + className);
            Class<?> clazz = Class.forName(PREFIX + className);
            return (Instruction) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("No Such instruction, please add it to the instruction table!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
