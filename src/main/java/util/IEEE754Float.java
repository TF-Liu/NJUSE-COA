package util;

/**
 * 存放符合IEEE-754标准的特殊浮点数
 */
public class IEEE754Float {

	public static final String P_ZERO = "00000000000000000000000000000000";  // 0X0           positive zero

	public static final String N_ZERO = "10000000000000000000000000000000";  // 0X80000000    negative zero

	public static final String P_INF = "01111111100000000000000000000000";  // 0X7f800000    positive infinity

	public static final String N_INF = "11111111100000000000000000000000";  // 0Xff800000    negative infinity

	public static final String NaN_Regular = "(0|1){1}1{8}(0+1+|1+0+)(0|1)*";  // Not_A_Number    regular expression

	public static final String NaN = "01111111110000000000000000000000";  // Not_A_Number

}
