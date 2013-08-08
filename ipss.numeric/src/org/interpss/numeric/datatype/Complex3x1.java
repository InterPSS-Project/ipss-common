/*
 * @(#)Complex3x1.java   
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
import org.interpss.numeric.NumericConstant;
import org.interpss.numeric.util.Number2String;

/**
 * Data structure class for manipulating 3x1 [a,b,c] or [0,1,2] complex number vector
 */
public class Complex3x1 implements java.io.Serializable {
	private static final long serialVersionUID = 1;

	public Complex 
		a_0 = new Complex(0.0, 0.0), 
		b_1 = new Complex(0.0, 0.0),
		c_2 = new Complex(0.0, 0.0);

	/**
	 * Default constructor.
	 *
	 */
	public Complex3x1() {
	}

	/**
	 * Constructor. this obj = [a0, b1, c2]
	 *
	 * @param a0 a complex number 
	 * @param b1 a complex number 
	 * @param c2 a complex number 
	 */
	public Complex3x1(final Complex a0, final Complex b1, final Complex c2) {
		a_0 = a0;
		b_1 = b1;
		c_2 = c2;
	}

	/**
	 * Copy Constructor
	 *
	 * @param c obj to be copied 
	 */
	public Complex3x1(final Complex3x1 c) {
		a_0 = c.a_0;
		b_1 = c.b_1;
		c_2 = c.c_2;
	}

	/**
	 * Transformation from abc to 012 system
	 *
	 * @param abc a 3x1 complex vector in abc system
	 * @return resulting 3x1 complex vector in 012 system
	 */
	public static Complex3x1 abc_to_z12(final Complex3x1 abc) {
		Complex z = abc.a_0.add(abc.b_1.add(abc.c_2)), p = abc.a_0.add(
				NumericConstant.a.multiply(abc.b_1)).add(
				NumericConstant.a2.multiply(abc.c_2)), n = abc.a_0.add(
				NumericConstant.a2.multiply(abc.b_1)).add(
				NumericConstant.a.multiply(abc.c_2));
		z = ComplexFunc.div(z, 3.0);
		p = ComplexFunc.div(p, 3.0);
		n = ComplexFunc.div(n, 3.0);
		return new Complex3x1(z, p, n);
	}

	/**
	 * Transformation from 012 to abc system
	 *
	 * @param abc a 3x1 complex vector in 012 system
	 * @return resulting 3x1 complex vector in abc system
	 */
	public static Complex3x1 z12_to_abc(final Complex3x1 z12) {
		final Complex a = z12.a_0.add(z12.b_1.add(z12.c_2)), b = z12.a_0.add(
				NumericConstant.a2.multiply(z12.b_1)).add(
				NumericConstant.a.multiply(z12.c_2)), c = z12.a_0.add(
				NumericConstant.a.multiply(z12.b_1)).add(
				NumericConstant.a2.multiply(z12.c_2));
		return new Complex3x1(a, b, c);
	}

	/**
	 * Convert the obj to a string.
	 *
	 * @return the string representation of the obj
	 */
	@Override
	public String toString() {
		return Number2String.toStr(a_0) + "  "
				+ Number2String.toStr(b_1) + "  "
				+ Number2String.toStr(c_2);
	}
}