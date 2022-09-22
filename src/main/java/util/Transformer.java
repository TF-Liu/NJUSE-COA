package util;

public class Transformer {
    /**
     * Integer to BinaryString
     *
     * @param numStr to be converted
     * @return result
     */
    public String intToBinary(String numStr) {
        int num = Integer.parseInt(numStr);
        if (num == 0) return "00000000000000000000000000000000";  //0单独判读
        if (num == 0x80000000) return "10000000000000000000000000000000";
        boolean isNeg = false;
        if (num < 0) {  //负数转正数
            num = -num;
            isNeg = true;
        }
        StringBuilder temp = new StringBuilder();
        while (num > 0) {  //转为二进制
            if (num % 2 == 1) temp.append("1");
            else temp.append("0");
            num /= 2;
        }
        String ans = temp.reverse().toString();  //反转
        int len = ans.length();
        for (int i = 0; i < 32 - len; i++) ans = "0" + ans;
        if (isNeg) {  //如果是负数那么取反加一
            ans = oneAdder(negation(ans)).substring(1);
        }
        return ans;
    }

    public String binaryToInt(String binStr) {
        return String.valueOf(valueOf(binStr, 2));
    }

    public String floatToBinary(String floatStr) {
        int eLength = 8;
        int sLength = 23;

        double d = Double.valueOf(floatStr);
        boolean isNeg = d < 0;

        if (Double.isNaN(d)) {
            return "Nan";
        }
        if (!isFinite(d, eLength, sLength)) {
            return isNeg ? "-Inf" : "+Inf";  //NaN暂时不考虑
        }

        StringBuilder answer = new StringBuilder(1 + eLength + sLength);

        if (isNeg) answer.append("1");  //value is negative, so append sign info
        else answer.append("0");

        if (d == 0.0) {
            for (int i = 0; i < eLength + sLength; i++) {  //zero representation
                answer.append("0");
            }
            return answer.toString();
        } else {
            d = Math.abs(d);
            int bias = (int) ((maxValue(eLength) + 1) / 2 - 1);  //bias
            boolean subnormal = (d < minNormal(eLength, sLength));

            if (subnormal) {
                for (int i = 0; i < eLength; i++) {
                    answer.append("0");
                }
                d = d * Math.pow(2, bias - 1);  //将指数消去
                // d = 0.xxxxx
                answer.append(fixPoint(d, sLength));
            } else {
                // double exponent = Math.getExponent(d);  // 0.5 -> -1
                int exponent = (int) getExponent(d);
                answer.append(integerRepresentation(String.valueOf((int) (exponent + bias)), eLength));  //add the bias
                d = d / Math.pow(2, exponent);
                // d = 1.xxxxx
                answer.append(fixPoint(d - 1, sLength));  //fixPoint传入的参数要求小于1，自动忽略了隐藏位
            }
        }
        return answer.toString();
    }

    public String binaryToFloat(String binStr) {
        boolean isNeg = (binStr.charAt(0) == '1');
        String exp = binStr.substring(1, 9);
        String frag = binStr.substring(9);

        if (exp.equals("11111111")) {
            if (frag.contains("1")) {
                return "NaN";
            } else {
                return isNeg ? "-Inf" : "+Inf";
            }
        } else if (exp.equals("00000000")) {
            if (frag.contains("1")) {
                double f = 0.0;
                int fe = 1;
                for (char fc : frag.toCharArray()) {
                    f += Integer.parseInt(String.valueOf(fc)) / Math.pow(2, fe);
                    fe++;
                }
                f = (f) * Math.pow(2, -126);
                f = isNeg ? -f : f;
                return String.valueOf(f);
            } else {
                return "0.0";
            }
        }

        double f = 0.0;
        int fe = 1;
        for (char fc : frag.toCharArray()) {
            f += Integer.parseInt(String.valueOf(fc)) / Math.pow(2, fe);
            fe++;
        }

        int e = valueOf(exp, 2) - 127;
        f = (1 + f) * Math.pow(2, e);
        f = isNeg ? -f : f;

        return String.valueOf(f);
    }

    public String decimalToNBCD(String decimal) {
        return getBCDString(Integer.parseInt(decimal));
    }

    public String NBCDToDecimal(String NBCDStr) {
        return String.valueOf(NBCDTrueValue(NBCDStr));
    }

    /**
     * add one to the operand
     *
     * @param operand the operand
     * @return result after adding, the first position means overflow (not equal to the carray to the next) and the remains means the result
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
            res[i] = (char) ('0' + bit);  //显示转化为char
        }
        String result = new StringBuffer(new String(res)).reverse().toString();
        return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;  //注意有进位不等于溢出，溢出要另外判断
    }

    /**
     * convert the string's 0 and 1.
     * e.g 00000 to 11111
     *
     * @param operand string to convert (by default, it is 32 bits long)
     * @return string after converting
     */
    private String negation(String operand) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < operand.length(); i++) {
            result = operand.charAt(i) == '1' ? result.append("0") : result.append("1");
        }
        return result.toString();
    }

    /**
     * equal to the Integer.valueOf
     *
     * @param num   a string
     * @param radix radix
     * @return result
     */
    private int valueOf(String num, int radix) {
        int ans = 0;
        for (int i = 0; i < num.length(); i++) {
            int temp = 0;
            if (num.charAt(i) <= '9' && num.charAt(i) >= '0') temp = num.charAt(i) - '0';
            else temp = num.charAt(i) - 'a' + 10;
            ans = ans * radix + temp;
        }
        return ans;
    }

    /**
     * convert a string as a num's NBCD's representation to its true value
     *
     * @param operand to be converted
     * @return the string format of its true value
     */
    private int NBCDTrueValue(String operand) {
        StringBuilder ans = new StringBuilder();
        if (operand.startsWith("1101")) ans.append('-');
        operand = operand.substring(4);
        for (int i = 0; i < operand.length() && i < 28; i += 4) {
            ans.append(Integer.valueOf(operand.substring(i, i + 4), 2));
        }
        return Integer.parseInt(ans.toString());
    }

    /**
     * 十进制数转BCD码
     *
     * @param val 十进制整数
     * @return 1 +  4*(整数位数) BCD码
     */
    public String getBCDString(int val) {
//        if (val == 0) return "1100 0000";
        String sign = val < 0 ? "1101" : "1100"; //得到符号位
        String result = "";
        val = Math.abs(val);
        int i = 7;
        while (i > 0) {
            int tmpVal = val % 10;
            result = getBCDString_4(tmpVal).concat(result);
            val = val / 10;
            i--;
        }
        return sign.concat(result);
    }

    /**
     * @param val 0-9的整数
     * @return 4位二进制数 [0000 - 1001]
     */
    private String getBCDString_4(int val) {
        String result = "";
        for (int i = 0; i < 4; i++, val = val / 2) {
            if (val % 2 == 1)
                result = "1".concat(result);
            else
                result = "0".concat(result);
        }
        return result;
    }

    /**
     * calculate the max value (true value) with the given length of bits
     *
     * @param length given length
     * @return result
     */
    private double maxValue(int length) {
        //不能使用移位操作
        return Math.pow(2, length) - 1;
    }

    /**
     * check if the number could be present
     *
     * @param d       decimal
     * @param eLength exponent's length
     * @param sLength significand's length
     * @return result
     */
    private boolean isFinite(double d, int eLength, int sLength) {
        int bias = (int) ((maxValue(eLength) + 1) / 2 - 1);  //bias
        int exponent = (int) (maxValue(eLength) - 1 - bias - sLength);  //指数全1和全0是特殊情况，这里只要计算可以被正常表示的最大值，因此-1，且直接将significand转化的位数减去
        double significand = maxValue(sLength + 1);  //加上隐藏位
        double result = significand * Math.pow(2, exponent);
        return d >= -result && d <= result;
//		return result;  //用于测试最大值
    }

    /**
     * calculate the min normal with the given length
     *
     * @param eLength exponent's length
     * @param sLength significand's length
     * @return result
     */
    private double minNormal(int eLength, int sLength) {
        int bias = (int) ((maxValue(eLength) + 1) / 2 - 1);  //bias
        return Math.pow(2, 1 - bias);  //指数为1，阶码全0
    }

    /**
     * calculate the fix-point representation
     *
     * @param d       decimal num must be smaller than 1
     * @param sLength length of the result
     * @return fix-point string
     */
    private String fixPoint(double d, int sLength) {
        d = d < 1 ? d : d - (int) d;  //d = 0.xxxxx
        StringBuilder res = new StringBuilder();
        int count = 0;
        while (d != 0 && count < sLength) {
            d *= 2;
            if (d < 1) {
                res.append("0");
            } else {
                d -= 1;
                res.append("1");
            }
            count++;  //最长为sLength的长度
        }
        int len = res.length();  //不能直接用res.length()
        for (int i = 0; i < sLength - len; i++) res.append(0);
        return res.toString();
    }

    /**
     * make a num to format 1.xxxxx, return the exponent of 2
     *
     * @param d num
     * @return exponent
     */
    private double getExponent(double d) {
        if (d == 0) return 0;  //0不能得到正确结果，即-bias
        int exponent = 0;
        while (d >= 2) {
            d /= 2;
            exponent++;
        }
        while (d < 1) {
            d *= 2;
            exponent--;
        }
        return exponent;
    }

    /**
     * convert a number to its implement representation
     *
     * @param number a num to be converted
     * @param length the return string's length
     * @return its implement representation
     */
    private String integerRepresentation(String number, int length) {
        int num = Integer.valueOf(number);
        // num = number.charAt(0) == '-' ? -Integer.valueOf(number.substring(1)) : Integer.valueOf(number);
        if (num < 0) return Integer.toBinaryString(num).substring(32 - length);
        else {
            String result = Integer.toBinaryString(num);
            int len = length - result.length();  //这一步要先提取出来，不然下面会实时计算len
            for (int i = 0; i < len; i++) {
                result = "0" + result;
            }
            return result;
        }
    }

}
