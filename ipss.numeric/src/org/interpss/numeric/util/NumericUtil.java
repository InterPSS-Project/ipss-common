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
import org.interpss.numeric.datatype.LimitType;
import org.interpss.numeric.datatype.Matrix_xy;

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
	 * Check if the two Complex objects are equal regarding to standard err
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equals(Complex x, Complex y) {
		return equals(x, y, ERR);
	}

	/**
	 * Check if the two Complex objects are equal regarding to the err
	 * 
	 * @param x
	 * @param y
	 * @param err
	 * @return
	 */
	public static boolean equals(Complex x, Complex y, double err) {
		if (x == null && y == null)
			return true;
		else if (x != null && y == null || x == null && y != null)
			return false;
		return equals(x.getReal(), y.getReal(), err) && equals(x.getImaginary(), y.getImaginary(), err);
	}

	/**
	 * Check if the two Matrix_xy objects are equal regarding to standard err
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equals(Matrix_xy x, Matrix_xy y) {
		return equals(x, y, ERR);
	}

	/**
	 * Check if the two Matrix_xy objects are equal regarding to the err
	 * 
	 * @param x
	 * @param y
	 * @param err
	 * @return
	 */
	public static boolean equals(Matrix_xy x, Matrix_xy y, double err) {
		if (x == null && y == null)
			return true;
		else if (x != null && y == null || x == null && y != null)
			return false;
		return equals(x.xx, y.xx, err) && 
			   equals(x.xy, y.xy, err) && 
			   equals(x.yx, y.yx, err) && 
			   equals(x.yy, y.yy, err);
	}
	
	/**
	 * Check if the two LimitType objects are equal regarding to standard err
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equals(LimitType x, LimitType y) {
		return equals(x, y, ERR);
	}

	/**
	 * Check if the two LimitType objects are equal regarding to the err
	 * 
	 * @param x
	 * @param y
	 * @param err
	 * @return
	 */
	public static boolean equals(LimitType x, LimitType y, double err) {
		if (x == null && y == null)
			return true;
		else if (x != null && y == null || x == null && y != null)
			return false;
		return equals(x.getMax(), y.getMax(), err) && 
			   equals(x.getMin(), y.getMin(), err);
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
	
	/**
	 * The resource variable is used to adjust the target variable. In the case of target variable violation, check if the resource has adjustment
	 * room to change the target variable. 
	 * 
	 * @param target target variable to be controlled
	 * @param targetLimit target variable limit
	 * @param resource resource variable to be adjusted to control the target variable 
	 * @param resourceLimit resource variable limit
	 * @param adjSen adjustment sensitivity. If adjSen > 0,  increase the resource variable will result in increasing target variable.
	 * @return
	 */
	public static boolean hasAdjustRoom(double target, LimitType targetLimit, double resource, LimitType resourceLimit, boolean positiveSen) {
		if (target <= targetLimit.getMin()) {
			// adjustment target below the limit
			return positiveSen? (resource < resourceLimit.getMax()) :  // action: increase the resource to increase the target. If adjustment resource less than the limit, there is a room  
								(resource > resourceLimit.getMin());
		}
		else if (target >= targetLimit.getMax()) {
			// adjustment target above the limit
			return positiveSen? (resource > resourceLimit.getMin()) : // action: decrease the resource to decrease the target. if adjustment resource large than the limit, there is a room
								(resource < resourceLimit.getMax());
		}
		return false;
	}
}
