package cpu.fpu;

import util.DataType;
import util.IEEE754Float;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用3位保护位进行计算
 */
public class FPU {

    private final String[][] addCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN}
    };

    private final String[][] subCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN}
    };

    private final String[][] mulCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN}
    };

    private final String[][] divCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
    };

    /**
     * compute the float add of (dest + src)
     */
    public DataType add(DataType src, DataType dest) {
        String a = src.toString();
        String b = dest.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        String cornerCondition = cornerCheck(addCorner, a, b);
        if (null != cornerCondition) return new DataType(cornerCondition);
        String total = new FPUHelper().floatAddition(a, b, 8, 23, 3);
        return new DataType(total.substring(1));
    }

    /**
     * compute the float add of (dest - src)
     */
    public DataType sub(DataType src, DataType dest) {
        String a = dest.toString();
        String b = src.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        String cornerCondition = cornerCheck(subCorner, a, b);
        if (null != cornerCondition) return new DataType(cornerCondition);
        b = (b.charAt(0) == '0' ? "1" : "0") + b.substring(1);
        String total = new FPUHelper().floatAddition(a, b, 8, 23, 3);
        return new DataType(total.substring(1));
    }

    /**
     * compute the float mul of dest * src
     */
    public DataType mul(DataType src, DataType dest) {
        String a = src.toString();
        String b = dest.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        String cornerCondition = cornerCheck(mulCorner, a, b);
        if (null != cornerCondition) return new DataType(cornerCondition);
        return new DataType(new FPUHelper().floatMultiplication(a, b).substring(1));
    }

    /**
     * compute the float mul of dest / src
     */
    public DataType div(DataType src, DataType dest) {
        String a = dest.toString();
        String b = src.toString();
        if (a.matches(IEEE754Float.NaN_Regular) || b.matches(IEEE754Float.NaN_Regular)) {
            return new DataType(IEEE754Float.NaN);
        }
        if (IEEE754Float.P_ZERO.equals(b) || IEEE754Float.N_ZERO.equals(b)) {
            if ((!IEEE754Float.P_ZERO.equals(a)) && (!IEEE754Float.N_ZERO.equals(a))) {
                throw new ArithmeticException();
            }
        }
        String cornerCondition = cornerCheck(divCorner, a, b);
        if (null != cornerCondition) return new DataType(cornerCondition);
        if (IEEE754Float.P_ZERO.equals(a) || IEEE754Float.N_ZERO.equals(a)) {
            if (a.charAt(0) == b.charAt(0)) {
                return new DataType(IEEE754Float.P_ZERO);
            } else {
                return new DataType(IEEE754Float.N_ZERO);
            }
        }
        return new DataType(new FPUHelper().floatDivision(a, b).substring(1));
    }

    private String cornerCheck(String[][] cornerMatrix, String oprA, String oprB) {
        for (String[] matrix : cornerMatrix) {
            if (oprA.equals(matrix[0]) &&
                    oprB.equals(matrix[1])) {
                return matrix[2];
            }
        }
        return null;
    }

    private class FPUHelper {

        /**
         * add two float number
         *
         * @param operand1 first in binary format
         * @param operand2 second in binary format
         * @param eLength  exponent's length
         * @param sLength  significand's length
         * @param gLength  guard's bits
         * @return exponent overflow + sign + eLength + sLength
         */
        public String floatAddition(String operand1, String operand2, int eLength, int sLength, int gLength) {
            if (allZero(operand1.substring(1))) {
                return "0" + operand2;
            }
            if (allZero(operand2.substring(1))) {
                return "0" + operand1;
            }
            int Xexponent = Integer.valueOf(operand1.substring(1, 1 + eLength), 2);  //注意这里要加上第二个参数2，不然默认十进制转化
            int Yexponent = Integer.valueOf(operand2.substring(1, 1 + eLength), 2);
            if (Xexponent == 0) Xexponent++;  //如果指数是全0，那么指数的真实值为1，因为阶码已经考虑了隐藏位
            if (Yexponent == 0) Yexponent++;
            StringBuilder Xsig = new StringBuilder(getSignificand(operand1, eLength, sLength));  //get the two significands including the implicit bit
            StringBuilder Ysig = new StringBuilder(getSignificand(operand2, eLength, sLength));
            for (int i = 0; i < gLength; i++) {  //add the guard bits
                Xsig.append("0");
                Ysig.append("0");
            }
            //现在Xsig组成为 隐藏位+sLength+保护位
            int exponent = Math.max(Xexponent, Yexponent);  //choose the bigger exponent and right shift the smaller one
            if (Xexponent != Yexponent) {
                if (Xexponent > Yexponent) {
                    Ysig = new StringBuilder(rightShift(Ysig.toString(), Xexponent - Yexponent));
                } else {
                    Xsig = new StringBuilder(rightShift(Xsig.toString(), Yexponent - Xexponent));
                }
            }
            if (allZero(Xsig.toString())) {
                return "0" + operand2;
            }
            if (allZero(Ysig.toString())) {
                return "0" + operand1;
            }
            String temp = signedAddition(operand1.charAt(0) + Xsig.toString(), operand2.charAt(0) + Ysig.toString(), Xsig.length());  //需要传入符号。这里直接设置寄存器长度为 Xsig.length() 就可以检测是否溢出，传入参数结构为 符号位+隐藏位+sLength+保护位
            //temp结构为 溢出位+符号位+隐藏位+sLength+保护位
            boolean overflow = temp.charAt(0) != '0';
            char sign = temp.charAt(1);
            String sig = temp.substring(2);  //sig结构为 隐藏位+sLength+保护位
            StringBuilder answer = new StringBuilder();
            if (overflow) {
                sig = "1" + sig.substring(0, sig.length() - 1);  //右移一位（不可能要移动两次）
                exponent++;
                if (exponent == maxValue(eLength)) {  //指数上溢
                    answer.append("1").append(sign);
                    answer.append(Integer.toBinaryString(exponent));
                    for (int i = 0; i < sLength; i++) answer.append("0");  //无穷要求阶码全为0
                    return answer.toString();
                }
            }
            if (allZero(sig)) {
                for (int i = 0; i < eLength + sLength + 2; i++) answer.append("0");
                return answer.toString();
            }
            while (sig.charAt(0) != '1' && exponent > 0) {  //规格化
                sig = leftShift(sig, "1");
                exponent--;
            }
            if (exponent == 0) {  //非规格化数
                sig = rightShift(sig, 1);
            }
            StringBuilder ans_exponent = new StringBuilder(Integer.toBinaryString(exponent));
            int len = ans_exponent.length();  //注意要先把长度单独写出来，不能写在循环里面
            for (int i = 0; i < eLength - len; i++) ans_exponent.insert(0, "0");  //补齐到eLength长度
            return "0" + round(sign, ans_exponent.toString(), sig);
        }

        /**
         * multiply two float number
         *
         * @param operand1 first in binary format
         * @param operand2 second in binary format
         * @return exponent overflow + sign + sLength + eLength
         */
        public String floatMultiplication(String operand1, String operand2) {
            int eLength = 8;
            int sLength = 23;
            int bias = (int) ((maxValue(eLength) + 1) / 2 - 1);  //bias
            int Xexponent = Integer.valueOf(operand1.substring(1, 1 + eLength), 2);  //注意这里要加上第二个参数2，不然默认十进制转化
            int Yexponent = Integer.valueOf(operand2.substring(1, 1 + eLength), 2);
            if (Xexponent == 0) Xexponent++;  //如果指数是全0，那么指数的真实值为1，因为阶码已经考虑了隐藏位
            if (Yexponent == 0) Yexponent++;
            int exponent = Xexponent + Yexponent - bias;
            String Xsig = getSignificand(operand1, eLength, sLength);  //get the two significands including the implicit bit
            String Ysig = getSignificand(operand2, eLength, sLength);
            int sign = (operand1.charAt(0) - '0') ^ (operand2.charAt(0) - '0');
            String temp = unsignedMultiplication(Xsig, Ysig, Xsig.length() * 2);  // 无符号数的乘法
            StringBuilder answer = new StringBuilder();
            exponent++;  //前两位都是隐藏位，因此要移动一位小数点
            while (temp.charAt(0) == '0' && exponent > 0) {
                temp = leftShift(temp, "1");
                exponent--;
            }
            while (!allZero(temp.substring(0, 1 + sLength)) && exponent < 0) {  //右移规格化
                temp = rightShift(temp, 1);
                exponent++;
            }
            if (exponent >= bias * 2 + 1) {  //infinite
                answer.append("1");
                answer.append(sign);
                for (int i = 0; i < eLength; i++) answer.append(1);
                answer.append(getAllZeros(sLength));
                return answer.toString();
            }
            if (exponent == 0) {  //非规格化数
                temp = rightShift(temp, 1);
            }
            if (exponent < 0) {  //乘法和除法有可能指数小于0，指数下溢，处理成0
                answer.append("0" + sign);
                answer.append(getAllZeros(sLength + eLength));
                return answer.toString();
            }
            String ans_exponent = Integer.toBinaryString(exponent);
            int len = ans_exponent.length();
            for (int i = 0; i < eLength - len; i++) ans_exponent = "0" + ans_exponent;  //补齐到eLength长度
            return "0" + round((char) (sign + 48), ans_exponent, temp);
        }

        /**
         * unsigned integer multiplication
         *
         * @param operand1 first
         * @param operand2 second
         * @param length   operand1.length()*2
         * @return length*2
         */
        public String unsignedMultiplication(String operand1, String operand2, int length) {
            String X = impleDigits(operand1, length / 2);  //length为寄存器长度，因此操作数长度只能为length的一半
            operand2 = impleDigits(operand2, length / 2);
            StringBuffer productBuffer = new StringBuffer();
            for (int i = 0; i < length / 2; i++) productBuffer = productBuffer.append("0");
            String product = productBuffer.toString() + operand2;
            for (int i = 0; i < length / 2; i++) {
                int Y = product.charAt(length - 1) - '0';
                char carry = '0';
                if (Y == 1) {
                    String temp = carry_adder(product.substring(0, length / 2), X, '0', length / 2);  //这里不能用adder，因为length长度不一定为4的倍数
                    carry = temp.charAt(0);  //carry_adder的溢出即为有进位
                    product = temp.substring(1) + product.substring(length / 2);
                }
                product = carry + product.substring(0, product.length() - 1);  //carry为隐藏进位
            }
            return product;
        }

        /**
         * divide two float number: oprand1 / oprand2
         *
         * @param operand1 first in binary format
         * @param operand2 second in binary format
         * @return exponent overflow + sign + sLength + eLength
         */
        public String floatDivision(String operand1, String operand2) {
            int eLength = 8;
            int sLength = 23;
            int gLength = 3;
            int sign = (operand1.charAt(0) - '0') ^ (operand2.charAt(0) - '0');
            int bias = (int) ((maxValue(eLength) + 1) / 2 - 1);  //bias
            int Xexponent = Integer.valueOf(operand1.substring(1, 1 + eLength), 2);  //注意这里要加上第二个参数2，不然默认十进制转化
            int Yexponent = Integer.valueOf(operand2.substring(1, 1 + eLength), 2);
            if (Xexponent == 0) Xexponent++;  //如果指数是全0，那么指数的真实值为1，因为阶码已经考虑了隐藏位
            if (Yexponent == 0) Yexponent++;
            String Xsig = getSignificand(operand1, eLength, sLength);  //get the two significands including the implicit bit
            String Ysig = getSignificand(operand2, eLength, sLength);
            for (int i = 0; i < gLength; i++) {  //add the guard bits
                Xsig += "0";
                Ysig += "0";
            }
            int exponent = Xexponent - Yexponent + bias;
            String temp = unsignedDivision(Xsig, Ysig);  // 无符号数的除法
            StringBuilder answer = new StringBuilder();
            //特殊除法，第一位就是隐藏位
            while (temp.charAt(0) == '0' && exponent > 0) {
                temp = leftShift(temp, "1");
                exponent--;
            }
            while (!allZero(temp.substring(0, 1 + sLength)) && exponent < 0) {  //右移规格化
                temp = rightShift(temp, 1);
                exponent++;
            }
            if (exponent >= bias * 2 + 1) {  //infinite
                answer.append("1");
                answer.append(sign);
                for (int i = 0; i < eLength; i++) answer.append(1);
                answer.append(getAllZeros(sLength));
                return answer.toString();
            }
            if (exponent == 0) {  //非规格化数
                temp = rightShift(temp, 1);
            }
            if (exponent < 0) {  //乘法和除法有可能指数小于0，指数下溢，处理成0
                answer.append("0" + sign);
                answer.append(getAllZeros(sLength + eLength));
                return answer.toString();
            }
            String ans_exponent = Integer.toBinaryString(exponent);
            int len = ans_exponent.length();
            for (int i = 0; i < eLength - len; i++) ans_exponent = "0" + ans_exponent;  //补齐到eLength长度
            return "0" + round((char) (sign + 48), ans_exponent, temp);
        }

        /**
         * unsigned integer division
         *
         * @param operand1 first
         * @param operand2 second
         * @return length*2
         */
        public String unsignedDivision(String operand1, String operand2) {
            String quotient = "";
            String product = operand1;  //0扩展
            for (int i = 0; i < operand1.length(); i++) product += "0";  //直接在后面加0
            for (int i = 0; i < operand1.length(); i++) {
                String temp = carry_adder(product.substring(0, operand1.length()), negation(operand2), '1', operand2.length()).substring(1);
                if (temp.charAt(0) == '0') product = temp.substring(1) + product.substring(operand1.length()) + "1";
                else product = leftShift(product, "1");
            }
            quotient = product.substring(operand1.length());
            return quotient;
        }

        /**
         * get a string of all '0'
         *
         * @param length
         * @return all '0' of length
         */
        public String getAllZeros(int length) {
            StringBuffer res = new StringBuffer();
            for (int i = 0; i < length; i++) res.append('0');
            return res.toString();
        }

        /**
         * left shift a num using its string format
         * e.g. "00001001" left shift 2 bits -> "00100100"
         *
         * @param operand to be moved
         * @param n       moving nums of bits
         * @return after moving
         */
        public String leftShift(String operand, String n) {
            StringBuffer result = new StringBuffer(operand.substring(Integer.parseInt(n)));  //保证位数不变
            for (int i = 0; i < Integer.parseInt(n); i++) {
                result = result.append("0");
            }
            return result.toString();
        }

        /**
         * right shitf a num without considering its sign using its string format
         *
         * @param operand to be moved
         * @param n       moving nums of bits
         * @return after moving
         */
        public String rightShift(String operand, int n) {
            StringBuilder result = new StringBuilder(operand);  //保证位数不变
            boolean sticky = false;
            for (int i = 0; i < n; i++) {
                sticky = sticky || result.toString().endsWith("1");
                result.insert(0, "0");
                result.deleteCharAt(result.length() - 1);
            }
            if (sticky) {
                result.replace(operand.length() - 1, operand.length(), "1");
            }
            return result.substring(0, operand.length());
        }

        /**
         * check if the given string is full of '0'
         *
         * @param string given string
         * @return result
         */
        public boolean allZero(String string) {
            for (char c : string.toCharArray()) {
                if (c != '0') return false;
            }
            return true;
        }

        /**
         * calculate the max value (true value) with the given length of bits
         *
         * @param length given length
         * @return result
         */
        public double maxValue(int length) {
            //不能使用移位操作
            return Math.pow(2, length) - 1;
        }

        /**
         * get the significand bits includes the implicit bit considering the subnormal number
         *
         * @param operand number string in the binary format
         * @param eLength exponent's length
         * @param sLength significand's length
         * @return result string including the implicit bit
         */
        public String getSignificand(String operand, int eLength, int sLength) {
            String exponent = operand.substring(1, 1 + eLength);
            if (Integer.valueOf(exponent) == 0) return "0" + operand.substring(1 + eLength);  //subnormal number
            else return "1" + operand.substring(1 + eLength);
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
         * sign-magnitude representation add
         *
         * @param operand1 sign-magnitude
         * @param operand2 sign-magnitude
         * @param length   length of the rigister(larger than the number's length without the sign)
         * @return 2+length, first presents overflow, second presents the sign and remain means result(length equal to the given length)
         */
        public String signedAddition(String operand1, String operand2, int length) {  //溢出规则和补码不一样，因此这里用了另外一种adder，可以判断是否有进位
            String a = operand1.substring(1);  //取出绝对值
            if (a.length() < length) a = impleDigits("0" + operand1.substring(1), length);  //扩展到寄存器长度
            String b = operand2.substring(1);
            if (b.length() < length) b = impleDigits("0" + operand2.substring(1), length);
            if (allZero(a)) return operand2;  //0的情况单独判断，不然下面减法会出错
            if (allZero(b)) return operand1;
            if (operand1.charAt(0) == operand2.charAt(0)) {  //同号做加法
                String temp = carry_adder(a, b, '0', length);
                return "" + temp.charAt(0) + operand1.charAt(0) + temp.substring(1);  //有进位即溢出
            } else {  //异号做减法，此时不可能溢出
                b = oneAdder(negation(b)).substring(1);  //取反加一，0的补没有意义，先前已经被排除
                String temp = carry_adder(a, b, '0', length);
                if (temp.charAt(0) == '1') return "0" + operand1.charAt(0) + temp.substring(1);  //如果有进位则正常，符号和被减数一样
                return "0" + negation("" + operand1.charAt(0)) + oneAdder(negation(temp.substring(1))).substring(1);  //没有进位就取反加一，并且符号和被减数相反
            }
        }

        /**
         * add two nums with the length of given length which could be divided by 4, and the result's first bit presents the overflow
         * different from the {@code adder} method, the result's first bit presents whether it generates the carry
         *
         * @param operand1 first
         * @param operand2 second
         * @param c        original carray
         * @param length   given length
         * @return result, and the result's first bit presents the carry
         */
        public String carry_adder(String operand1, String operand2, char c, int length) {
            operand1 = impleDigits(operand1, length);
            operand2 = impleDigits(operand2, length);
            String res = "";
            char carry = c;
            for (int i = length - 1; i >= 0; i--) {  //这里length不一定是4的倍数，采用更加通用的加法算法
                String temp = fullAdder(operand1.charAt(i), operand2.charAt(i), carry);
                carry = temp.charAt(0);
                res = temp.charAt(1) + res;
//            res = (char)('0'+((carry-'0')^(operand1.charAt(i)-'0')^(operand2.charAt(i)-'0')))+res;
//            carry = (char)('0'+((carry-'0')&(operand1.charAt(i)-'0') | (carry-'0')&(operand2.charAt(i)-'0') | (operand1.charAt(i)-'0')&(operand2.charAt(i)-'0')));
            }
            return carry + res;  //注意这个方法里面溢出即有进位
        }

        /**
         * add one to the operand
         *
         * @param operand the operand
         * @return result after adding, the first position means overflow (not equal to the carray to the next) and the remains means the result
         */
        public String oneAdder(String operand) {
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
                res[i] = (char) ('0' + bit);  //显示转化为char
            }
            String result = new StringBuffer(new String(res)).reverse().toString();
            return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;  //注意有进位不等于溢出，溢出要另外判断
        }

        /**
         * the 2 bits' full adder
         *
         * @param x first char
         * @param y second char
         * @param c carry from the former bit
         * @return result after adding, the first position means the carry to the next and second means the result in this position
         */
        public String fullAdder(char x, char y, char c) {
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
         * 对GRS保护位进行舍入
         *
         * @param sign    符号位
         * @param exp     阶码
         * @param sig_grs 带隐藏位和保护位的尾数
         * @return 舍入后的结果
         */
        public String round(char sign, String exp, String sig_grs) {
            int grs = Integer.parseInt(sig_grs.substring(24, 27), 2);
            if ((sig_grs.substring(27).contains("1")) && (grs % 2 == 0)) {
                grs++;
            }
            String sig = sig_grs.substring(0, 24); // 隐藏位+23位
            if (grs > 4) {
                sig = oneAdder(sig);
            } else if (grs == 4 && sig.endsWith("1")) {
                sig = oneAdder(sig);
            }

            if (Integer.parseInt(sig.substring(0, sig.length() - 23), 2) > 1) {
                sig = rightShift(sig, 1);
                exp = oneAdder(exp).substring(1);
            }
            if (exp.equals("11111111")) {
                return sign == '0' ? IEEE754Float.P_INF : IEEE754Float.N_INF;
            }

            return sign + exp + sig.substring(sig.length() - 23);
        }
    }

}
