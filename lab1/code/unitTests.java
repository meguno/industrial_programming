package code;

import java.math.BigDecimal;

public class unitTests {

	public static void main(String[] args) {
		testSinZero();
		testCompareWithMathSin();
		testSinNegativeAngle();
		System.out.println("All tests completed!");
	}

	public static void testSinZero() {
		Teylor teylor = new Teylor();

		double result = teylor.Teylor(0.0, 1e-10);
		if (Math.abs(result) > 1e-8) {
			throw new AssertionError("sin(0) is 0, we got: " + result);
		}

		BigDecimal zero = BigDecimal.ZERO;
		BigDecimal precision = new BigDecimal("1e-10");
		BigDecimal bigResult = teylor.Teylor(zero, precision, 10);

		BigDecimal allowedError = new BigDecimal("1e-8");
		if (bigResult.abs().compareTo(allowedError) > 0) {
			throw new AssertionError("sin(0) for BigDecimal is 0, we got: " + bigResult);
		}

		System.out.println("sin(0)=0 is completed");
	}

	public static void testCompareWithMathSin() {
		Teylor teylor = new Teylor();
		double degrees = 45;
		double radians = Math.toRadians(degrees);
		double expected = Math.sin(radians);

		double myResult = teylor.Teylor(radians, 1e-12);
		double error = Math.abs(myResult - expected);

		if (error >= 1e-6) {
			throw new AssertionError("Error is too big: " + error);
		}

		System.out.println("comparison with Math.sin: error = " + error);
	}

	public static void testSinNegativeAngle() {
		Teylor teylor = new Teylor();
		double x = 0.5;

		double sinPositive = teylor.Teylor(x, 1e-10);
		double sinNegative = teylor.Teylor(-x, 1e-10);

		if (Math.abs(-sinPositive - sinNegative) > 1e-8) {
			throw new AssertionError("sin(-x) must be equal -sin(x)");
		}

		System.out.println("sin(-x) = -sin(x) is right");
	}
}