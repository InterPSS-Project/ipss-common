/*
 * @(#)Number2String.java   
 *
 * Copyright (C) 2006-2011 www.interpss.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @Author Mike Zhou
 * @Version 1.0
 * @Date 09/15/2006
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.util;

import java.text.DecimalFormat;

import org.apache.commons.math3.complex.Complex;

/**
 * Num2Str class is for formated output. It is based on the DicimalFormat class.
 */
public class Number2String {
	/*
	 *    int, long type
	 *    ==============
	 */
	/**
	 * Format a string to the specified length.
	 *
	 * @param length the length of the formatted string. white space will be patched at the begining 
	 *               if length > 0 or at the end if length < 0
	 * @param s string to be formatted
	 * @return formatted string
	 */
	public static String toStr(int length, final String s) {
		String str = s;
		final int l = length > 0 ? length : -length;
		while (str.length() < l) {
			if (length > 0) {
				str = " " + str;
			} else {
				str = str + " ";
			}
		}
		return str;
	}

	/**
	 * Format an int number
	 *
	 * @param n number to be formatted
	 * @return formatted string
	 */
	public static String toStr(final int n) {
		final DecimalFormat f = new DecimalFormat("#0");
		return f.format(n);
	}

	/**
	 * Format a long number
	 *
	 * @param n number to be formatted
	 * @return formatted string
	 */
	public static String toStr(final long n) {
		final DecimalFormat f = new DecimalFormat("#0");
		return f.format(n);
	}
	

	/**
	 * Format a int to the specified format pattern. White space will be patched at the begining to
	 * make formatted string length = pattern string length.
	 *
	 * @param pattern format pattern
	 * @param d number to be formatted
	 * @return formatted string
	 */
	public static String toFixLengthStr(final String pattern, final int d) {
		final DecimalFormat f = new DecimalFormat(pattern);
		String str = f.format(d);
		while (str.length() < pattern.length()) {
			str = " " + str;
		}
		return str;
	}


	/**
	 * Format a int to the specified format pattern. White space will be patched at the begining to
	 * make formatted string length = pattern string length.
	 *
	 * @param pattern format pattern
	 * @param d number to be formatted
	 * @return formatted string
	 */
	public static String toStr(final String pattern, final int d) {
		final DecimalFormat f = new DecimalFormat(pattern);
		String str = f.format(d);
		while (str.length() < pattern.length()) {
			str = " " + str;
		}
		return str;
	}
	
	/*
	 *    double type
	 *    ===========
	 */
	
	
	/**
	 * Format a double to the specified format "#0.0000#'.
	 *
	 * @param d number to be formatted
	 * @return formatted string
	 */
	public static String toStr(final double d) {
		return toStr(d, "#0.0000#");
	}

	/**
	 * Format a double to the specified format "#0.0000000#'.
	 * 
	 * @param d
	 * @return
	 */
	public static String toDebugStr(final double d) {
		return toStr(d, "#0.0000000#");
	}

	/**
	 * Format a double to the specified format pattern. 
	 *
	 * @param pattern format pattern
	 * @param d number to be formatted
	 * @return formatted string
	 */
	public static String toStr(final double d, final String pattern) {
		final DecimalFormat f = new DecimalFormat(pattern);
		return f.format(d);
	}

	/**
	 * Format a double to the specified format pattern. 
	 * 
	 * @param pattern
	 * @param d
	 * @return
	 */
	public static String toStr(final String pattern, final double d) {
		return toFixLengthStr(d, pattern);
	}

	/**
	 * Format a double to the specified format pattern. 
	 * 
	 * @param d
	 * @param pattern
	 * @return
	 */
	public static String toStr(final Double d, final String pattern) {
		final DecimalFormat f = new DecimalFormat(pattern);
		return f.format(d==null?0.0:d);
	}

	/**
	 * Format a double to the specified format pattern. White space will be patched at the begining to
	 * make formatted string length = pattern string length.
	 *
	 * @param pattern format pattern
	 * @param d number to be formatted
	 * @return formatted string
	 */
	public static String toFixLengthStr(final double d, final String pattern) {
		String str = toStr(d, pattern);
		while (str.length() < pattern.length()) {
			str = " " + str;
		}
		return str;
	}

	/*
	 *    complex type
	 *    ==============
	 */

	/**
	 * Format a complex number by using the default Num2Str.toStr(double) function in format (a + jb).
	 *
	 * @param c complex number to be formatted
	 * @return formatted string 
	 */
	public static String toStr(final Complex c) {
		return c == null? "null" : toStr(c.getReal()) + " + j" + toStr(c.getImaginary());
	}

	/**
	 * Format a complex number by using the default Num2Str.toStr(double) function in format (a + jb).
	 * 
	 * @param c
	 * @param format
	 * @return
	 */
	public static String toStr(final Complex c, String format) {
		return toStr(format, c.getReal()) + " + j" + toStr(format, c.getImaginary());
	}

	/*
	 *    String type
	 *    ===========
	 */

	/**
	 * Format a string to the specified length. White space will be patched 
	 * at the begining if length > 0 or at the end if length < 0
	 *
	 * @param length the length of the formatted string. 
	 * @param s string to be formatted
	 * @return formatted string
	 */
	public static String toFixLengthStr(int length, final String s) {
		String str = s;
		final int l = length > 0 ? length : -length;
		while (str.length() < l) {
			if (length > 0) {
				str = " " + str;
			} else {
				str = str + " ";
			}
		}
		return str;
	}
}