package cs365Homework2;

/*
 * @author Kevin Nguyen
 */

public class Fp {
	public int add(int a, int b) {
		// smaller number exponent goes into a for simpler code
		if (((a >> 23) & 255) > ((b >> 23) & 255)) {
			int temp = a;
			a = b;
			b = temp;
		}

		// splitting all the numbers into the required formats of exponents, fractions
		// and sign
		int aSign = (a >> 31) & 1;
		int aExponent = (a >> 23) & 255;
		int aFraction = a & 0x7FFFFF;

		int bSign = (b >> 31) & 1;
		int bExponent = (b >> 23) & 255;
		int bFraction = b & 0x7FFFFF;

		int answerSign;
		int answerExponent;
		int answerFraction;

		// check to make sure that the exponent is not 0
		if (aExponent == 0) {
			return b;
		}
		if (bExponent == 0) {
			return a;
		}

		// uses float to check if the current value given is not a number and returns
		// that value
		if (aExponent == 0b11111111 || bExponent == 0b11111111) {
			return Float.floatToIntBits(Float.NaN);
		}

		// checking to make sure that there are no 0's
		if (a == Float.floatToIntBits(Float.POSITIVE_INFINITY) && b == Float.floatToIntBits(Float.NEGATIVE_INFINITY)) {
			return Float.floatToIntBits(Float.NaN);
		}
		if (b == Float.floatToIntBits(Float.POSITIVE_INFINITY) && a == Float.floatToIntBits(Float.NEGATIVE_INFINITY)) {
			return Float.floatToIntBits(Float.NaN);
		}
		if (a == Float.floatToIntBits(Float.POSITIVE_INFINITY) && b == Float.floatToIntBits(Float.POSITIVE_INFINITY)) {
			return Float.floatToIntBits(Float.POSITIVE_INFINITY);
		}
		if (a == Float.floatToIntBits(Float.NEGATIVE_INFINITY) && b == Float.floatToIntBits(Float.NEGATIVE_INFINITY)) {
			return Float.floatToIntBits(Float.NEGATIVE_INFINITY);
		}

		// adding the 1 back to the beggining of the decimal to put the numbers in the
		// correct form
		aFraction += 0x800000;
		bFraction += 0x800000;

		// because is the smaller number, shift the numbers until the numbers are the
		// correct factor
		aFraction = aFraction >> (bExponent - aExponent);
		aExponent = bExponent;

		// if the signs are the same a simple additon can be done, if there are
		// different then it is a subtraction
		if (aSign == bSign) {
			answerFraction = (aFraction + bFraction);
		} else {
			answerFraction = Integer.max(aFraction, bFraction) - Integer.min(aFraction, bFraction);

		}
		// set the sign bit equal to the sign bit of the larger number
		if (Integer.max(aFraction, bFraction) == aFraction) {
			answerSign = aSign;
		} else {

			answerSign = bSign;
		}

		// finding the shift amount to know what to pit as the correct value
		int shiftAmount = 8 - Integer.numberOfLeadingZeros(answerFraction);

		// shift so that "hidden 1 bit" is in the correct place
		if (shiftAmount >= 0) {
			answerFraction = answerFraction >> shiftAmount;
		} else {
			answerFraction = answerFraction << Math.abs(shiftAmount);
		}

		// removing the implied 1.xxxxx
		answerFraction -= 0x800000;

		// set the add the shift amount to the exponent
		answerExponent = aExponent + shiftAmount;

		// assemble the final answer to be returned to the user
		int answer = answerFraction;
		answer += answerExponent << 23;
		answer += answerSign << 31;

		return answer;
	}

	public int mul(int a, int b) {
		// split numbers into sign, exponent, and fraction
		int aSign = (a >> 31) & 1;
		int aExponent = (a >> 23) & 255;
		int aFraction = a & 0x7FFFFF;

		int bSign = (b >> 31) & 1;
		int bExponent = (b >> 23) & 255;
		int bFraction = b & 0x7FFFFF;

		int answerSign, answerExponent;
		long answerFraction;// store as a long to make it easier to do the math

		// check to make sure that the exponent is not 0
		if (aExponent == 0) {
			return a;
		}
		if (bExponent == 0) {
			return b;
		}

		// uses float to check if the current value given is not a number and returns
		// that value

		if (aExponent == 0b11111111 || bExponent == 0b11111111) {
			return Float.floatToIntBits(Float.NaN);
		}

		// check for all possible values for infinity
		if (a == Float.floatToIntBits(Float.POSITIVE_INFINITY) && b == Float.floatToIntBits(Float.NEGATIVE_INFINITY)) {
			return Float.floatToIntBits(Float.NaN);
		}
		if (b == Float.floatToIntBits(Float.POSITIVE_INFINITY) && a == Float.floatToIntBits(Float.NEGATIVE_INFINITY)) {
			return Float.floatToIntBits(Float.NaN);
		}
		if (a == Float.floatToIntBits(Float.POSITIVE_INFINITY) && b == Float.floatToIntBits(Float.POSITIVE_INFINITY)) {
			return Float.floatToIntBits(Float.POSITIVE_INFINITY);
		}
		if (a == Float.floatToIntBits(Float.NEGATIVE_INFINITY) && b == Float.floatToIntBits(Float.NEGATIVE_INFINITY)) {
			return Float.floatToIntBits(Float.NEGATIVE_INFINITY);
		}

		// finding what the final exponent value will be
		answerExponent = aExponent + bExponent - 127;

		// find finding the value of the sign bit
		answerSign = aSign ^ bSign;// xor operator ^

		// adding the 1 back to make it 1.xxxx
		aFraction += 0x800000;
		bFraction += 0x800000;

		// mulitply the fractions to make it correct
		answerFraction = (long) aFraction * (long) bFraction;

		// renormalizing the value to allow it to be returned in a format that will work
		if (Long.highestOneBit(answerFraction) == (long) Math.pow(2, 47)) {
			answerFraction = answerFraction >> 1;
			answerExponent += 1;
		}
		answerFraction = answerFraction << 1;

		// truncate all the values by 24 to re allow it to be put into a form that is
		// useable
		answerFraction = answerFraction >> 24;

		// removing the hidden 1.xxxx so that we can restore it

		// reassemble integer result
		int answer = ((int) answerFraction & 0x7FFFFF);
		answer += answerExponent << 23;
		answer += answerSign << 31;

		return answer;

	}
}