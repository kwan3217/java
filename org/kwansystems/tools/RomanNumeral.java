/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kwansystems.tools;

/**
 * This class represents a roman numeral. Look up the <a
 * href="http://www.moxlotus.alternatifs.eu/programmation-converter.html">algorithm
 * for conversion of numbers arabic to roman</a>.
 *
 * @author MoxLotus
 *
 */
public class RomanNumeral {

	/**
	 * Array of <i>elementary roman numerals</i>.
	 */
	private final static String[] BASIC_ROMAN_NUMBERS = { "M", "CM", "D", "CD",
			"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

	/**
	 * Array of corresponding machine numbers
	 */
	private final static int[] BASIC_VALUES = { 1000, 900, 500, 400, 100, 90,
			50, 40, 10, 9, 5, 4, 1 };

	/**
	 * Machine number.
	 */
	private int value;

	/**
	 * Roman representation of the number.
	 */
	private String romanString;

	/**
	 * Construct a roman numeral given a machine number
	 *
	 * @param value
	 *           Machine number to construct a roman numeral for
	 * @throws IllegalArgumentException
	 *           Value outside of 1-3999
	 */
	public RomanNumeral(int value) throws IllegalArgumentException {
		if (1 <= value && value <= 3999) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Value out of range" + value);
		}
	}

	/**
	 * Construct a roman numeral from a roman numeral.
	 *
	 * @param s
	 *           String containing roman numeral to construct.
	 * @throws IllegalArgumentException
	 *            if not passed a valid roman numeral.
	 */
	public RomanNumeral(String s) throws IllegalArgumentException {
		String r = s.toUpperCase();
		int index = 0;
		for (int i = 0; i < BASIC_ROMAN_NUMBERS.length; i++) {
			while (r.startsWith(BASIC_ROMAN_NUMBERS[i], index)) {
				this.value += BASIC_VALUES[i];
				index += BASIC_ROMAN_NUMBERS[i].length();
			}
		}
		// Verify the input string is a valid roman number.
		RomanNumeral tempVerify;
		String verifyString;
    	tempVerify = new RomanNumeral(this.value);
		if ((verifyString = tempVerify.toRomanValue()).equals(r)) {
			this.romanString = r;
		} else {
			throw new IllegalArgumentException("The string '"+s+"' does not appear to be a valid roman numeral (round trips to "+verifyString+")");
		}

	}

	/**
	 * Return the roman numeral value form of this number
	 *
	 * @return A string containing the roman numeral for this value.
	 */
	public String toRomanValue() {
		if (this.romanString == null) {
			this.romanString = "";
			int remainder = this.value;
			for (int i = 0; i < BASIC_VALUES.length; i++) {
				while (remainder >= BASIC_VALUES[i]) {
					this.romanString += BASIC_ROMAN_NUMBERS[i];
					remainder -= BASIC_VALUES[i];
				}
			}
		}
		return this.romanString;
	}

	/**
	 * Return the machine value of this number.
	 *
	 * @return The machine value of this number.
	 */
	public int getValue() {
		return this.value;
	}
    public static void main(String[] args)  {
      for(int i=1;i<=12;i++) {
        System.out.println(new RomanNumeral(i).toRomanValue());
      }
      System.out.println(new RomanNumeral(1776).toRomanValue());
    }
}