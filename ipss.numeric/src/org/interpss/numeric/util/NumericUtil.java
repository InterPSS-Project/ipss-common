/*
 * @(#)NumericUtil.java   
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
 * @Date 12/10/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.util;

import org.apache.commons.math3.complex.Complex;

/**
 * numeric utility funcitons
 * 
 * @author mzhou
 *
 */
public class NumericUtil {
	public static boolean OUT_Alert = false;
	
	private static final double ERR = 1.0e-10;
	
	/**
	 * Check if the two ints are equal
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equals(int x, int y) {
		if (x != y && OUT_Alert)
			System.out.println("x != y, x: " + x + ", y: " + y);
		return x == y;
	}

	/**
	 * Check if the two doubles are equal regarding to standard err
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equals(double x, double y) {
		return equals(x, y, ERR);
	}

	/**
	 * Check if the two doubles are equal regarding to the err
	 * 
	 * @param x
	 * @param y
	 * @param err
	 * @return
	 */
	public static boolean equals(double x, double y, double err) {
		double a  = Math.abs(x - y);
		if (a >= err && OUT_Alert) {
			System.out.println("x != y, x: " + x + ", y: " + y);
		}
		return a < err;
	}

	/**
	 * Check if the two doubles are equal regarding to standard err
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equals(Complex x, Complex y) {
		return equals(x, y, ERR);
	}

	/**
	 * Check if the two doubles are equal regarding to the err
	 * 
	 * @param x
	 * @param y
	 * @param err
	 * @return
	 */
	public static boolean equals(Complex x, Complex y, double err) {
		return equals(x.getReal(), y.getReal(), err) && equals(x.getImaginary(), y.getImaginary(), err);
	}
	
	/**
	 * check if x and y are with the same sign
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean sameSign(double x, double y) {
		return x > 0.0 && y > 0.0 ||
			   x < 0.0 && y < 0.0;
	}
}
