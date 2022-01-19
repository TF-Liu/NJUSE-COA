package util;

public class CRC {

    /**
     * CRC计算器
     *
     * @param data       数据流
     * @param polynomial 多项式
     * @return CheckCode
     */
    public static char[] Calculate(char[] data, String polynomial) {
        StringBuilder dividend = new StringBuilder(String.valueOf(data));
        for (int i = 0; i < polynomial.length() - 1; i++) {
            dividend.append("0");
        }
        return mod2div(dividend.toString(), polynomial).toCharArray();
    }

    /**
     * CRC校验器
     *
     * @param data       接收方接受的数据流
     * @param polynomial 多项式
     * @param CheckCode  CheckCode
     * @return 余数
     */
    public static char[] Check(char[] data, String polynomial, char[] CheckCode) {
        return mod2div(String.valueOf(data) + String.valueOf(CheckCode), polynomial).toCharArray();
    }

    /**
     * 实现模2除法
     *
     * @param dividend 除数
     * @param divisor  被除数
     * @return 余数
     */
    public static String mod2div(String dividend, String divisor) {
        StringBuilder result = new StringBuilder(dividend);
        for (int i = 0; i < dividend.length() - divisor.length() + 1; i++) {
            if (result.charAt(i) == '1') {
                for (int j = 0; j < divisor.length(); j++) {
                    if (result.charAt(i + j) == divisor.charAt(j)) {
                        result.replace(i + j, i + j + 1, "0");
                    } else {
                        result.replace(i + j, i + j + 1, "1");
                    }
                }
            }
        }
        return result.substring(result.length() - divisor.length() + 1);
    }
}

