package util;

import org.junit.Assert;
import org.junit.Test;

public class IntToBinaryTest {

	private Transformer transformer = new Transformer();

	@Test
	public void test1() {
		Assert.assertEquals("00000000000000000000000000000010",
				transformer.intToBinary("2"));
	}

	@Test
	public void test2() {
		Assert.assertEquals("00000000000000000000000000000001",
				transformer.intToBinary("1"));
	}

	@Test
	public void test3() {
		Assert.assertEquals("00000000000000000000000000000000",
				transformer.intToBinary("0"));
	}

	@Test
	public void test4() {
		Assert.assertEquals("00000000000000000000000000001010",
				transformer.intToBinary("10"));
	}

	@Test
	public void test5() {
		Assert.assertEquals("11111111111111111111111111110110",
				transformer.intToBinary("-10"));
	}

}
