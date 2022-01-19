package cpu.registers;

/**
 * The register for the flags
 */
public class EFLAGS extends Register32 {

    private static final int CF = 0;      //carry flag
    private static final int PF = 2;      //parity flag
    private static final int ZF = 6;      //zero flag
    private static final int SF = 7;      //sign flag
    private static final int OF = 11;      //overflow flag

    public boolean getCF() {
        return reg[CF] == '1';
    }

    public void setCF(boolean cf) {
        reg[CF] = b2c(cf);
    }

    public boolean getPF() {
        return reg[PF] == '1';
    }

    public void setPF(boolean pf) {
        reg[PF] = b2c(pf);
    }

    public boolean getZF() {
        return reg[ZF] == '1';
    }

    public void setZF(boolean zf) {
        reg[ZF] = b2c(zf);
    }

    public boolean getSF() {
        return reg[SF] == '1';
    }

    public void setSF(boolean sf) {
        reg[SF] = b2c(sf);
    }

    public boolean getOF() {
        return reg[OF] == '1';
    }

    public void setOF(boolean of) {
        reg[OF] = b2c(of);
    }

    private char b2c(boolean b) {
        if (b) {
            return '1';
        } else {
            return '0';
        }
    }
}
