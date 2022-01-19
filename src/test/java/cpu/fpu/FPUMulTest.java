package cpu.fpu;

import org.junit.Test;
import util.DataType;
import util.IEEE754Float;
import util.Transformer;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class FPUMulTest {

	private final FPU fpu = new FPU();
	private final Transformer transformer = new Transformer();
	private DataType src;
	private DataType dest;
	private DataType result;

	@Test
	public void fpuMulTest7(){
		src = new DataType(transformer.floatToBinary( "0.25" ));
		dest = new DataType(transformer.floatToBinary( "4" ));
		result = fpu.mul(src, dest);
		assertEquals( transformer.floatToBinary( "1.0" ), result.toString() );
	}

	@Test
	public void fpuMulTest9() {
		String deNorm1 = "00000000000000000000000000000001";
		String deNorm2 = "00000000000000000000000000000010";
		String deNorm3 = "10000000010000000000000000000000";
		String small1 = "00000000100000000000000000000000";
		String small2 = "00000000100000000000000000000001";
		String big1 = "01111111000000000000000000000001";
		String big2 = "11111111000000000000000000000001";
		String[] strings = {deNorm1, deNorm2, deNorm3, small1, small2, big1, big2};
		double[] doubles = {10000000, 1.2, 1.1, 1, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, -0.1, -0.2, -0.3, -0.4, -0.5, -0.6, -0.7, -0.8, -0.9, -1, -10000000};

		float[] input = new float[strings.length + doubles.length];
		for (int i = 0; i < strings.length; i++) {
			input[i] = Float.parseFloat(transformer.binaryToFloat(strings[i]));
		}
		for (int i = 0; i < doubles.length; i++) {
			input[i + strings.length] = (float) doubles[i];
		}

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				src = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(input[i]))));
				dest = new DataType(transformer.intToBinary(Integer.toString(Float.floatToIntBits(input[j]))));
				result = fpu.mul(src, dest);
				assertEquals(transformer.intToBinary(Integer.toString(Float.floatToIntBits(input[i] * input[j]))), result.toString());
			}
		}
	}

}
