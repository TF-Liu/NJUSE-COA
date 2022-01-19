package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BinaryToFloatTest {

	Transformer t = new Transformer();

	@Test
	public void test1() {
		String result = t.binaryToFloat("00000000010000000000000000000000");
		assertEquals(String.valueOf(Math.pow(2, -127)), result);
	}

	@Test
	public void test2() {
		String result = t.binaryToFloat("00000000011000000000000000000000");
		assertEquals(String.valueOf(0.75 * Math.pow(2, -126)), result);
	}

	@Test
	public void test3() {
		String result = t.binaryToFloat("01001001110100010000000000000000");
		assertEquals("" + 1.6328125 * Math.pow(2, 20), result);
	}

	@Test
	public void test4() {
		String result = t.binaryToFloat("11001001110100010000000000000000");
		assertEquals("-" + 1.6328125 * Math.pow(2, 20), result);
	}

	@Test
	public void test5() {
		String result = t.binaryToFloat("00110101110100010000000000000000");
		assertEquals("" + 1.6328125 * Math.pow(2, -20), result);
	}

	@Test
	public void test6() {
		String result = t.binaryToFloat("10110101110100010000000000000000");
		assertEquals("-" + 1.6328125 * Math.pow(2, -20), result);
	}

	@Test
	public void test7() {
		String result = t.binaryToFloat("11111111100000000000000000000000");  //对于32位的表示来说溢出
		assertEquals("-Inf", result);
	}

	@Test
	public void test8() {
		String result = t.binaryToFloat("01111111100000000000000000000000");
		assertEquals("+Inf", result);
	}

	@Test
	public void test9() {
		String result = t.binaryToFloat("00000000000000000000000000000000");
		assertEquals("0.0", result);
	}

}
