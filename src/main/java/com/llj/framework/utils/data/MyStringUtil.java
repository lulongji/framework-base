/**
 * 
 */
package com.llj.framework.utils.data;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * 
 * @author lu
 *
 */
public class MyStringUtil {

	public static Boolean isEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 生成随机数字
	 *
	 * @param length
	 * @return
	 */
	public static String randomNumber(int length) {
		char[] numbersAndLetters = null;
		java.util.Random randGen = null;
		if (length < 1) {
			return null;
		}
		// Init of pseudo random number generator.
		if (randGen == null) {
			if (randGen == null) {
				randGen = new java.util.Random();
				// Also initialize the numbersAndLetters array
				numbersAndLetters = ("0123456789").toCharArray();
			}
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(9)];
		}
		return new String(randBuffer);
	}

	/**
	 * Gets the random string.
	 *
	 * @param randomNumberSize
	 *            the random number size
	 * @param ipAddress
	 *            the ip address
	 * @param port
	 *            the port
	 * @return the random string
	 */
	public static String getRandomString(int randomNumberSize, String ipAddress, int port) {
		long number = 0;
		number += ipToLong(ipAddress);
		number += port;
		Random randomGen = new Random();
		number += randomGen.nextLong();

		/**
		 * 取mod
		 */
		String defaultString = getZeroString(randomNumberSize);
		StringBuilder modStringBuilder = new StringBuilder();
		modStringBuilder.append("1").append(defaultString);
		long mod = Long.parseLong(modStringBuilder.toString());

		/**
		 * 算随机值
		 */
		number = number > 0 ? number % mod : Math.abs(number) % mod;

		/**
		 * 格式化返回值 为randomNumberSize位
		 */
		DecimalFormat df = new DecimalFormat(defaultString);
		return df.format(number);
	}

	/**
	 * Ip to long.
	 *
	 * @param ipAddress
	 *            the ip address
	 * @return the long
	 */
	public static long ipToLong(String ipAddress) {
		long result = 0;
		String[] atoms = ipAddress.split("\\.");

		for (int i = atoms.length - 1, j = 0; i >= j; i--) {
			result |= (Long.parseLong(atoms[atoms.length - 1 - i]) << (i * 8));
		}

		return result & 0xFFFFFFFF;
	}

	/**
	 * Gets the zero string.
	 *
	 * @param length
	 *            the length
	 * @return the zero string
	 */
	public static String getZeroString(int length) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append("0");
		}
		return buffer.toString();
	}

}
