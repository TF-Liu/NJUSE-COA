package cpu.instr;

public class InstrBuilder {

	String prefix = "";

	String opcode = "";

	String ModRM = "";

	String SIB = "";

	String displacement = "";

	String immediate = "";

	@Override
	public String toString() {
		return prefix + opcode + ModRM + SIB + displacement + immediate;
	}
}
