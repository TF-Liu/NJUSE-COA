package util;

import org.junit.Assert;
import org.junit.Test;

public class NBCDToDecimalTest {

	private Transformer transformer = new Transformer();

	@Test
	public void test1() {
		Assert.assertEquals("10",
				transformer.NBCDToDecimal("11000000000000000000000000010000"));
	}

	@Test
	public void test2() {
		Assert.assertEquals("306",
				transformer.NBCDToDecimal("11000000000000000000001100000110"));
	}

	@Test
	public void test3() {
		Assert.assertEquals("-451",
				transformer.NBCDToDecimal("11010000000000000000010001010001"));
	}

	@Test
	public void test4() {
		Assert.assertEquals("1997",
				transformer.NBCDToDecimal("11000000000000000001100110010111"));
	}

	@Test
	public void test5() {
		Assert.assertEquals("0",
				transformer.NBCDToDecimal("11000000000000000000000000000000"));
	}

}
