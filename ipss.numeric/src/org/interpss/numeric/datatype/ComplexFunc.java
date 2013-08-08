/*
 * @(#)ComplexFunc.java   
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

package org.interpss.numeric.datatype;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.util.Number2String;

/**
 * Complex util function to argument the Complex class
 */
public class ComplexFunc {
	/**
	 * Create a Complex object
	 * 
	 * @param a complex object
	 * @return the resulting complex object
	 */
	public static Complex createComplex(final Complex a) {
		return new Complex(a.getReal(), a.getImaginary());
	}

	/**
	 * Create a Complex object
	 * 
	 * @param str string of format a + jb
	 * @return the resulting complex object
	 */
	public static Complex createComplex(final String str) {
		String s = str.replace('+', ' ');
		String[] sAry = new String[2];
		if (s.indexOf("j") < 0) {
			sAry[0] = s;
			sAry[1] = "0.0";
		} else {
			sAry = s.split("j");
			if (sAry[0].equals(""))
				sAry[0] = "0.0";
		}
		return new Complex(new Double(sAry[0]).doubleValue(), new Double(
				sAry[1]).doubleValue());
	}

	/**
	 * Make a copy of the Vector_xy object and Create a Complex object
	 * 
	 * @param a Vector_xy object
	 * @return the resulting complex object
	 */
	public static Complex createComplex(final Vector_xy a) {
		return new Complex(a.x, a.y);
	}

	/**
	 * Construct a complex number by using mag<ang(rad)
	 * 
	 * @param mag magnitude
	 * @param ang angle in rad
	 * @return the resulting complex number
	 */
	public static Complex polar(final double mag, final double angle) {
		return new Complex(mag * Math.cos(angle), mag * Math.sin(angle));
	}

	/**
	 * Check if this obj is equal to c
	 * 
	 * @param c a complex number
	 * @return true/false
	 */
	public static boolean sameValue(final Complex a, final Complex b) {
		return ((a.getReal() == b.getReal()) && (a.getImaginary() == b
				.getImaginary()));
	}

	/**
	 * Add two complex numbers togather
	 * 
	 * @param a complex number
	 * @param b complex number
	 * @return result
	 */
	public static Complex add(final Complex a, final Complex b) {
		return new Complex(a.getReal() + b.getReal(), a.getImaginary()
				+ b.getImaginary());
	}

	/**
	 * Add three complex numbers togather
	 * 
	 * @param a complex number
	 * @param b complex number
	 * @param c complex number
	 * @return result
	 */
	public static Complex add(final Complex a, final Complex b, final Complex c) {
		return new Complex(a.getReal() + b.getReal() + c.getReal(), 
				           a.getImaginary()	+ b.getImaginary() + c.getImaginary());
	}

	/**
	 * Subtract a - b
	 * 
	 * @param a complex number
	 * @param b complex number
	 * @return result
	 */
	public static Complex sub(final Complex a, final Complex b) {
		return new Complex(a.getReal() - b.getReal(), a.getImaginary()
				- b.getImaginary());
	}

	/**
	 * Division function, return d/c
	 * 
	 * @param d double divider
	 * @param c complex number
	 * @return the resulting complex number
	 */
	public static Complex div(final double d, final Complex c) {
		final double x = d
				/ (c.getReal() * c.getReal() + c.getImaginary()
						* c.getImaginary());
		return new Complex(c.getReal() * x, -c.getImaginary() * x);
	}

	/**
	 * Division function, return d/c
	 * 
	 * @param d double divider
	 * @param c complex number
	 * @return the resulting complex number
	 */
	public static Complex div(final Complex d, final Complex c) {
		Complex x = new Complex(d.getReal(), d.getImaginary());
		return x.divide(c);
	}

	/**
	 * Division function, return c/d
	 * 
	 * @param c complex number
	 * @param d double divider
	 * @return the resulting complex number
	 */
	public static Complex div(final Complex c, final double d) {
		return new Complex(c.getReal() / d, c.getImaginary() / d);
	}

	/**
	 * Multiplying function, return d*c
	 * 
	 * @param d double number
	 * @param c complex number
	 * @return the complex number
	 */
	public static Complex mul(final double d, final Complex c) {
		return new Complex(c.getReal() * d, c.getImaginary() * d);
	}

	/**
	 * Multiplying function, return d*c
	 * 
	 * @param d complex number
	 * @param c complex number
	 * @return the complex number
	 */
	public static Complex mul(final Complex d, final Complex c) {
		Complex x = new Complex(d.getReal(), d.getImaginary());
		return x.multiply(c);
	}

	/**
	 * Angle function, return the angle in rad of this obj
	 * 
	 * @return the resulting angle in rad
	 */
	public static double arg(final Complex c) {
		// the angle in the plane in rad
		if (c.getReal() == 0.0) {
			if (c.getImaginary() > 0.0) {
				return Math.toRadians(90.0);
			} else if (c.getImaginary() < 0.0) {
				return Math.toRadians(-90.0);
			} else {
				return 0.0;
			}
		} else {
			double ang = Math.atan(c.getImaginary() / c.getReal());
			if (c.getReal() < 0.0)
				ang += Math.PI;
			return ang;
		}
	}

	/**
	 * Convert the obj to a string.
	 * 
	 * @return the string representation of the obj
	 */
	public static String toMagAng(final Complex c) {
		return Number2String.toStr(c.abs()) + "("
				+ Number2String.toStr( Math.toDegrees(arg(c)), "###0.#") + ")";
	}

	/**
	 * Convert the obj to a string a + jb.
	 * 
	 * @return the string representation of the obj
	 */
	public static String toString(final Complex c) {
		// note: this method is used to persist Complex number in the 
		// Ipss Model
		return String.valueOf(c.getReal()) + " + j"
				+ String.valueOf(c.getImaginary());
	}
}