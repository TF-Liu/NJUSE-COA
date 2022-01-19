package cpu.nbcdu;

import util.DataType;

import java.util.Arrays;

public class NBCDU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    /**
     * @param src  A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest + src
     */
    DataType add(DataType src, DataType dest) {
        CF = "0";
        OF = "0";
        return new DataType(do_add(src.toString(), dest.toString()));
    }

    /***
     *
     * @param src A 32-bits NBCD String
     * @param dest A 32-bits NBCD String
     * @return dest - src
     */
    DataType sub(DataType src, DataType dest) {
        CF = "0";
        OF = "0";
        return new DataType(do_sub(src.toString(), dest.toString()));
    }

    private String do_add(String a, String b) {
        String result;

        if (isPositive(a) && !isPositive(b)) {
            result = do_sub("1100".concat(b.substring(4)), a);
        } else if (!isPositive(a) && isPositive(b)) {
            result = do_sub("1100".concat(a.substring(4)), b);
        } else {
            String sign = "";
            if (isPositive(a) && isPositive(b)) {
                sign = "1100";
            } else if (!isPositive(a) && !isPositive(b)) {
                sign = "1101";
            }
            result = "";
            for (int i = 28; i >= 4; i -= 4) {
                result = NBCD_add_4(a.substring(i, i + 4), b.substring(i, i + 4), CF.equals("1")).concat(result);
            }
            result = sign.concat(result);
            if (CF.equals("1")) {
                OF = "1";
            }
        }
        return result;
    }

    private String do_sub(String a, String b) {
        String result;

        if (!isPositive(a) && isPositive(b)) {
            result = do_add("1100".concat(a.substring(4)), b);
        } else if (isPositive(a) && !isPositive(b)) {
            result = do_add("1101".concat(a.substring(4)), b);
        } else if (!isPositive(a) && !isPositive(b)) {
            result = do_sub("1100".concat(b.substring(4)), "1100".concat(a.substring(4)));
        } else {
            result = do_add("1100".concat(getInversion(a.substring(4))), b);
            if (OF.equals("0")) {
                result = "1101".concat(getInversion(result.substring(4)));
            }
        }
        return result;
    }

    private String NBCD_add_4(String a, String b, boolean hasCF) {
        char[] aBits = a.toCharArray();
        char[] bBits = b.toCharArray();
        char[] resultBits = new char[4];
        int c = 0;
        CF = "0";

        // simply addition
        for (int i = 3; i >= 0; i--) {
            resultBits[i] = (char) ((aBits[i] - '0') + (bBits[i] - '0') + c + '0');
            c = 0;
            if (resultBits[i] >= '2') {
                c = 1;
                resultBits[i] -= 2;
            }
        }
        String result = new String(resultBits);
        if (hasCF) {
            String result_ext = add_withExtBit(result, "0001");
            if (result_ext.startsWith("1")) {
                c = 1;
            }
            result = result_ext.substring(1);
        }

        // modification with "0110"
        if (c == 1 || isLarger(result, "1001")) {
            CF = "1";
            result = add_withExtBit(result, "0110").substring(1);
        }

        return result;
    }

    /**
     * get mod-10 complement-code of a NBCD String
     * Method:
     * 1. invert each bit;
     * 2. plus "1010" for each 4 digits
     * 3. plus 1 on the last digit
     *
     * @param nbcd An unsigned NBCD binary code
     * @return
     */
    private String getInversion(String nbcd) {
        // Step1
        char[] mask = new char[nbcd.length()];
        char[] nbcdBits = nbcd.toCharArray();
        Arrays.fill(mask, '1');
        char[] resultBits = new char[nbcd.length()];
        for (int i = 0; i < nbcd.length(); i++) {
            resultBits[i] = (char) (mask[i] - nbcdBits[i] + '0');
        }
        //Step2
        String versed = new String(resultBits);
        String modified = "";
        for (int i = 0; i < versed.length(); i += 4) {
            modified = modified.concat(add_withExtBit(versed.substring(i, i + 4), "1010").substring(1));
        }
        //Step3
        resultBits = modified.toCharArray();
        String one_pluser = "";
        for (int i = modified.length() - 4; i >= 0; i -= 4) {
            one_pluser = add_withExtBit(modified.substring(i, i + 4), "0001");
            if (isLarger(one_pluser.substring(1), "1001")) {
                for (int j = i; j < i + 4; j++) {
                    resultBits[j] = '0';
                }
                continue;
            } else {
                for (int j = i; j < i + 4; j++) {
                    resultBits[j] = one_pluser.charAt(j - i + 1);
                }
                break;
            }
        }
        if (isLarger(one_pluser.substring(1), "1001")) {
            OF = "1";
        }
        return new String(resultBits);
    }

    private boolean isPositive(String nbcdString) {
        if (nbcdString.startsWith("1100")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param a An unsigned 0-1 binary code String
     * @param b An unsigned 0-1 binary code String
     * @return true if a is larger than b
     */
    private boolean isLarger(String a, String b) {
        for (int i = 0; i < a.length(); i++) {
            int diff = a.charAt(i) - b.charAt(i);
            if (diff > 0) {
                return true;
            } else if (diff < 0) {
                return false;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * plus a and b with a extended bit for sign
     *
     * @param a A 4-bits unsigned NBCD code String
     * @param b A 4-bits unsigned NBCD code String
     * @return A 5-bits unsigned (a + b)'s result
     */
    private String add_withExtBit(String a, String b) {
        char[] aBits = a.toCharArray();
        char[] bBits = b.toCharArray();
        char[] resultBits = new char[5];
        int c = 0;

        // simply addition
        for (int i = 3; i >= 0; i--) {
            resultBits[i + 1] = (char) ((aBits[i] - '0') + (bBits[i] - '0') + c + '0');
            c = 0;
            if (resultBits[i + 1] >= '2') {
                c = 1;
                resultBits[i + 1] -= 2;
            }
        }

        if (c == 1) {
            resultBits[0] = '1';
        } else {
            resultBits[0] = '0';
        }
        return new String(resultBits);
    }

}
