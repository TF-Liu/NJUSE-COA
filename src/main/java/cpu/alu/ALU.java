package cpu.alu;


import util.BinaryIntegers;
import util.DataType;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    DataType remainderReg;

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    /**
     * 返回两个二进制整数的和
     * dest + src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType add(DataType src, DataType dest) {
        // add two integer in 2's complement code
        //注意有进位不等于溢出，溢出要另外判断。已经被封装在上一步
        return new DataType(adder(src.toString(), dest.toString(), '0', 32));
    }

    /**
     * 返回两个二进制整数的差
     * dest - src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType sub(DataType src, DataType dest) {
        //不能直接取反加一（有可能取反加一溢出），必须这一步改初始carry位‘0’为‘1’。这样写完全模拟ppt上面的做法，但是还是可能取反的时候就溢出，先加一反而不溢出，所以其实还是不完美。注意有进位不等于溢出，溢出要另外判断。
        return new DataType(adder(dest.toString(), negation(src.toString()), '1', 32));
    }

    /**
     * 返回两个二进制整数的乘积(结果低位截取后32位)
     * dest * src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType mul(DataType src, DataType dest) {
        int length = 32;  // length为数据长度
        String X = impleDigits(src.toString(), length);
        String destStr = impleDigits(dest.toString(), length);
        String negX = oneAdder(negation(X)).substring(1);  //取反加一,去掉第一位溢出位
        String product = BinaryIntegers.ZERO + destStr;
        int Y1 = 0;
        int Y2 = product.charAt(2 * length - 1) - '0';
        for (int i = 0; i < length; i++) {
            switch (Y1 - Y2) {
                case 1:
                    product = adder(product.substring(0, length), X, '0', length) + product.substring(length);
                    break;
                case -1:
                    product = adder(product.substring(0, length), negX, '0', length) + product.substring(length);
                    break;
            }
            product = product.substring(0, 1) + product.substring(0, product.length() - 1);  //算数右移
            Y1 = Y2;  //更新两个Y
            Y2 = product.charAt(2 * length - 1) - '0';
        }
        String higher = product.substring(0, length);
        String lower = product.substring(length);  // 直接截断高32位，取低32位作为结果返回
        OF = "0";
        for (char c : higher.toCharArray()) {
            if (c == '1') {
                OF = "1";  // 如果高32位有1判定为溢出
                break;
            }
        }
        return new DataType(lower);
    }

    /**
     * 返回两个二进制整数的除法结果
     * dest ÷ src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType div(DataType src, DataType dest) {
        String srcStr = src.toString();
        String destStr = dest.toString();
        int length = 64;
        String quotient = "";
        String remainder = "";
        boolean op1Zero = isZero(destStr);
        boolean op2Zero = isZero(srcStr);


        if (op1Zero && !op2Zero) {    // If X=0 and Y≠0: 0
            remainderReg = new DataType(BinaryIntegers.ZERO);
            return new DataType(BinaryIntegers.ZERO);
        }

        if (op2Zero) {    // If Y=0: exception
            throw new ArithmeticException();
        }

        if (isZero(destStr.substring(1)) && destStr.charAt(0) == '1' && isNegativeOne(srcStr)) {   //   -2^32 / -1  溢出
            remainderReg = new DataType(BinaryIntegers.ZERO);
            return new DataType(destStr);
        }

        String product = impleDigits(destStr, length);  //符号扩展
        if (product.charAt(0) == srcStr.charAt(0))
            product = adder(product.substring(0, length / 2), negation(srcStr), '1', length / 2) + product.substring(length / 2);
        else
            product = adder(product.substring(0, length / 2), srcStr, '0', length / 2) + product.substring(length / 2);
        for (int i = 0; i < length / 2; i++) {
            if (product.charAt(0) == srcStr.charAt(0)) {
                quotient += "1";
                product = leftShift(product, 1);
                product = adder(product.substring(0, length / 2), negation(srcStr), '1', length / 2) + product.substring(length / 2);
            } else {
                quotient += "0";
                product = leftShift(product, 1);
                product = adder(product.substring(0, length / 2), srcStr, '0', length / 2) + product.substring(length / 2);
            }
        }

        quotient = quotient.substring(1);
        if (product.charAt(0) == srcStr.charAt(0)) quotient = quotient + "1";
        else quotient = quotient + "0";
        if (quotient.charAt(0) == '1') quotient = oneAdder(quotient).substring(1);
        remainder = product.substring(0, length / 2);
        if (remainder.charAt(0) != destStr.charAt(0)) {
            if (destStr.charAt(0) == srcStr.charAt(0)) {
                remainder = adder(remainder, srcStr, '0', length / 2);
            } else {
                remainder = adder(remainder, negation(srcStr), '1', length / 2);
            }
        }

        if (isZero(adder(srcStr, impleDigits(remainder, length / 2), '0', length / 2))) {
            quotient = add(new DataType(impleDigits(quotient, length / 2)), new DataType(BinaryIntegers.NegativeOne)).toString();
            remainder = BinaryIntegers.ZERO;
            remainderReg = new DataType(remainder);
            return new DataType(quotient);
        }
        else if (remainder.equals(srcStr)) {
            quotient = oneAdder(quotient).substring(1);
            remainder = BinaryIntegers.ZERO;
            remainderReg = new DataType(remainder);
            return new DataType(quotient);
        }

        //溢出情况前面判断了，补齐位数
        remainderReg = new DataType(impleDigits(remainder, length / 2));
        return new DataType(impleDigits(quotient, length / 2));
    }


    /**
     * add one to the operand
     *
     * @param operand the operand
     * @return result after adding, the first position means overflow, and the remains means the result
     */
    private String oneAdder(String operand) {
        int len = operand.length();
        StringBuffer temp = new StringBuffer(operand);
        temp = temp.reverse();
        int[] num = new int[len];
        for (int i = 0; i < len; i++) num[i] = temp.charAt(i) - '0';  //先转化为反转后对应的int数组
        int bit = 0x0;
        int carry = 0x1;
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
            bit = num[i] ^ carry;
            carry = num[i] & carry;
            res[i] = (char) ('0' + bit);
        }
        String result = new StringBuffer(new String(res)).reverse().toString();
        return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;
    }


    private String adder(String operand1, String operand2, char c, int length) {
        operand1 = impleDigits(operand1, length);
        operand2 = impleDigits(operand2, length);
        String res = carry_adder(operand1, operand2, c, length);
        OF = addOverFlow(operand1, operand2, res);
        return res;  //注意有进位不等于溢出，溢出要另外判断
    }

    /**
     * given a length, make operand to that digits considering the sign
     *
     * @param operand given num
     * @param length  make complete
     * @return completed string
     */
    private String impleDigits(String operand, int length) {
        int len = length - operand.length();
        char imple = operand.charAt(0);
        StringBuffer res = new StringBuffer(new StringBuffer(operand).reverse());
        for (int i = 0; i < len; i++) {
            res = res.append(imple);
        }
        return res.reverse().toString();
    }

    /**
     * test if add given two nums overflow
     *
     * @param operand1 first
     * @param operand2 second
     * @param result   result after the adding
     * @return 1 means overflow, 0 means not
     */
    private String addOverFlow(String operand1, String operand2, String result) {
        int X = operand1.charAt(0) - '0';
        int Y = operand2.charAt(0) - '0';
        int S = result.charAt(0) - '0';
        return "" + ((~X & ~Y & S) | (X & Y & ~S));  //两个操作数符号相同，和结果符号不同，则溢出
    }

    /**
     * add two nums with the length of given length
     *
     * @param operand1 first
     * @param operand2 second
     * @param c        original carray
     * @param length   given length
     * @return result
     */
    private String carry_adder(String operand1, String operand2, char c, int length) {
        operand1 = impleDigits(operand1, length);
        operand2 = impleDigits(operand2, length);
        String res = "";
        char carry = c;
        for (int i = length - 1; i >= 0; i--) {  //这里length不一定是4的倍数，采用更加通用的加法算法
            String temp = fullAdder(operand1.charAt(i), operand2.charAt(i), carry);
            carry = temp.charAt(0);
            res = temp.charAt(1) + res;
        }
        CF = String.valueOf(carry);
        return res;  //注意这个方法里面溢出即有进位
    }

    /**
     * the 2 bits' full adder
     *
     * @param x first char
     * @param y second char
     * @param c carry from the former bit
     * @return result after adding, the first position means the carry to the next and second means the result in this position
     */
    private String fullAdder(char x, char y, char c) {
        int bit = (x - '0') ^ (y - '0') ^ (c - '0');  //三位异或
        int carry = ((x - '0') & (y - '0')) | ((y - '0') & (c - '0')) | ((x - '0') & (c - '0'));  //有两位为1则产生进位
        return "" + carry + bit;  //第一个空串让后面的加法都变为字符串加法
    }

    /**
     * convert the string's 0 and 1.
     * e.g 00000 to 11111
     *
     * @param operand string to convert (by default, it is 32 bits long)
     * @return string after converting
     */
    public String negation(String operand) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < operand.length(); i++) {
            result = operand.charAt(i) == '1' ? result.append("0") : result.append("1");
        }
        return result.toString();
    }


    /**
     * check if the given number is 0
     *
     * @param operand given number to be checked
     * @return 1 presents num == 0 and 0 presents num != 0
     */
    private boolean isZero(String operand) {
        for (char c : operand.toCharArray()) {
            if (c != '0') return false;
        }
        return true;
    }


    /**
     * check if the given number is -1
     *
     * @param operand given number to be checked
     * @return 1 presents num == -1 and 0 presents num != -1
     */
    private boolean isNegativeOne(String operand) {
        for (char c : operand.toCharArray()) {
            if (c != '1') return false;
        }
        return true;
    }


    /**
     * left shift a num using its string format
     * e.g. "00001001" left shift 2 bits -> "00100100"
     *
     * @param operand to be moved
     * @param n       moving nums of bits
     * @return after moving
     */
    public String leftShift(String operand, int n) {
        StringBuffer result = new StringBuffer(operand.substring(n));  //保证位数不变
        for (int i = 0; i < n; i++) {
            result = result.append("0");
        }
        return result.toString();
    }

}
