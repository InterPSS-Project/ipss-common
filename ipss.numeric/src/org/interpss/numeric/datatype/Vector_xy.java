/*
 * @(#)Vector_xy.java   
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
 * Data structure class for manipulating 2x1 [x,y] vector
 * 
 */
public class Vector_xy extends Point implements java.io.Serializable {
	private static final long serialVersionUID = 1;

	/**
	 * Default constructor.
	 *
	 */
	public Vector_xy() {
	}

	/**
	 * Constructor.
	 *
	 * @param a set obj.x=a and obj.y=a
	 */
	public Vector_xy(final double a) {
		super(a, a);
	}

	/**
	 * Constructor.
	 *
	 * @param a set obj.x=a
	 * @param b set obj.y=b
	 */
	public Vector_xy(final double a, final double b) {
		super(a, b);
	}

	/**
	 * Constructor.
	 *
	 * @param c set obj.x=c.re and obj.y=c.im
	 */
	public Vector_xy(final Complex c) {
		super(c.getReal(), c.getImaginary());
	}

	/**
	 * Return the magnitude of the vector
	 * 
	 * @return the magnitude
	 */
	public double abs() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	/**
	 * Convert the obj to a string.
	 *
	 * @return the string representation of the obj
	 */
	@Override
	public String toString() {
		return "(" + Number2String.toStr(x) + " + j" + Number2String.toStr(y) + ")";
	}
}