/*
 * @(#)LimitType.java   
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

import org.interpss.numeric.NumericLogger;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.util.Number2String;

/**
 * Limits class for (max, min) limits
 * 
 */
public class LimitType implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	
	protected double _max = 0.0, _min = 0.0;

	/**
	 * Default constructor.
	 *
	 */
	public LimitType() {
	}

	/**
	 * Constructor.
	 *
	 * @param max unper limit
	 * @param min lower limit
	 */
	public LimitType(final double max, final double min) {
		if (max < min) {
			NumericLogger.getLogger().severe("Limits.Limits, max < min, max, min: " + max + ", " + min);
		}
		_max = max;
		_min = min;
	}

	/**
	 * Constructor.
	 *
	 * @param max unper limit
	 * @param min lower limit
	 */
	public LimitType(final String str) throws IpssNumericException {
		if (str == null || str.trim().equals("")) {
			_max = 0.0;
			_min = 0.0;
		}
		else {
			try {
				String maxStr = str.trim().substring(str.indexOf('(')+1, str.indexOf(','));
				String minStr = str.trim().substring(str.indexOf(',')+1, str.indexOf(')'));
				//System.out.println("maxStr, minStr: " + maxStr + ", " + minStr);
				_max = new Double(maxStr).doubleValue();
				_min = new Double(minStr).doubleValue();
			} catch (Exception e) {
				_max = 0.0;
				_min = 0.0;
				throw new IpssNumericException(e.toString());
			}
		}
	}

	/**
	 * Copy constructor.
	 *
	 * @param lim limit obj to be copied
	 */
	public LimitType(final LimitType lim) {
		_max = lim.getMax();
		_min = lim.getMin();
	}

	/**
	 * Get the max limit.
	 *
	 * @return the max limit
	 */
	public double getMax() {
		return _max;
	}

	/**
	 * Get the min limit.
	 *
	 * @return the min limit
	 */
	public double getMin() {
		return _min;
	}

	/**
	 * Check if there is a limit violation, max >= x >= min.
	 *
	 * @param x a double number to be checked
	 * @return true or false
	 */
	public boolean isViolated(final double x) {
		if ((x > getMax()) || (x < getMin())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Perform limit action. 
	 *   if max >= x >= min return x, 
	 *   if max < x return max, 
	 *   if x < min return min.
	 *
	 * @return the resulting number
	 */
	public double limit(final double x) {
		if (x > getMax()) {
//			if (Debug) {
//				NumericLogger.getLogger().info("Limit violation, x, max: " + x + ", " + getMax());
//			}
			return getMax();
		} else if (x < getMin()) {
//			if (Debug) {
//				NumericLogger.getLogger().info("Limit violation, x, min: " + x + ", " + getMin());
//			}
			return getMin();
		} else {
			return x;
		}
	}

	/**
	 * Perform discrete limit action. 
	 *   if max >= x >= min return the closest discrete point, 
	 *   if max < x return max, 
	 *   if x < min return min.
	 *
	 * @return the resulting number
	 */
	public double limitDiscrete(final double x, final double stepSize) throws IpssNumericException {
		if ((stepSize <= 0.0) || (getMax() <= getMin())) {
			throw new IpssNumericException("Wrong parameters, steps, max, min:"
					+ stepSize + ", " + getMax() + ", " + getMin());
		}
		if (x > getMax()) {
			return getMax();
		} else if (x < getMin()) {
			return getMin();
		} else {
			double distance = getMax() - getMin();
			double retn = x;
			double y = getMin();
			while (y <= getMax()) {
				if (Math.abs(x - y) < distance) {
					distance = Math.abs(x - y);
					retn = y;
				}
				y += stepSize;
			}
			return retn;
		}
	}

	/**
	 * Convert the obj to a string.
	 *
	 * @return the string representation of the obj
	 */
	@Override public String toString() {
		return "( " + String.valueOf(getMax()) + ", "
				+ String.valueOf(getMin()) + " )";
	}

	/**
	 * Convert the obj to a string with an actual number as an input
	 *
	 * @return the string representation of the obj
	 */
	public String toString(final double actual) {
		return "max: " + Number2String.toStr(getMax()) + "   " + "actaul: "
				+ Number2String.toStr(actual) + "   " + "min: "
				+ Number2String.toStr(getMin());
	}
}