/*
 * @(#)Matrix_xy.java   
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

/**
 * Data structure class for manipulating 2x2 matrix [xx, xy, yx, yy]
 * 
 */
public class Matrix_xy implements java.io.Serializable {
	private static final long serialVersionUID = 1;

	public double xx = 0.0, xy = 0.0, yx = 0.0, yy = 0.0;

	/**
	 * Default constructor.
	 *
	 */
	public Matrix_xy() {
	}

	/**
	 * Constructor.
	 *
	 * @param x obj.xx = x; obj.xy = 0.0; obj.yx = 0.0;  obj.yy = x;
	 */
	public Matrix_xy(final double x) {
		xx = x;
		xy = 0.0;
		yx = 0.0;
		yy = x;
	}

	/**
	 * Constructor.
	 *
	 * @param x obj.xx = a; obj.xy = b; obj.yx = c;  obj.yy = d;
	 */
	public Matrix_xy(final double a, final double b, final double c,
			final double d) {
		xx = a;
		xy = b;
		yx = c;
		yy = d;
	}

	/**
	 * Convert the obj to a string.
	 *
	 * @return the string representation of the obj
	 */
	@Override
	public String toString() {
		return "( " + String.valueOf(xx) + ", " + String.valueOf(xy) + ", "
				+ String.valueOf(yx) + ", " + String.valueOf(yy) + " )\n";
	}
}