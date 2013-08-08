/*
 * @(#)Point.java   
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
 * @Date 012/03/2011
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.datatype;

/**
 * data structure to represent a point on x-y plan
 * 
 * @author mzhou
 *
 */
public class Point {
	public double x, y;

	/**
	 * constructor
	 */
	public Point() {
		this(0.0, 0.0);
	}

	/**
	 * constructor
	 * 
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override public String toString() {
		return "Point: (" + x + "," + y + ")";
	}
}
