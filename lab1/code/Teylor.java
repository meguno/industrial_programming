package code;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

class Teylor {
	public double Teylor(double x, double e) {
		double sum = 0;
		double term = x;
		int n = 1;

		while (Math.abs(term) > e) {
			sum += term;
			term = term * (-1) * x * x / ((2 * n) * (2 * n + 1));
			n++;

		}

		return sum;
	}

	public BigDecimal Teylor(BigDecimal x, BigDecimal e, int k) {
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal term = x;
		int n = 1;

		int calculationK = k + 20;

		while (term.abs().compareTo(e) > 0) {
			sum = sum.add(term).setScale(calculationK, RoundingMode.HALF_UP);

			BigDecimal xSquared = x.multiply(x).setScale(calculationK, RoundingMode.HALF_UP);
			BigDecimal numerator = term.multiply(xSquared)
					.multiply(new BigDecimal("-1"))
					.setScale(calculationK, RoundingMode.HALF_UP);

			BigInteger denominatorBigInt = BigInteger.valueOf(2L * n)
					.multiply(BigInteger.valueOf(2L * n + 1));
			BigDecimal denominator = new BigDecimal(denominatorBigInt);

			term = numerator.divide(denominator, calculationK, RoundingMode.HALF_UP);
			n++;
		}
		return sum.setScale(k, RoundingMode.HALF_UP);
	}
}