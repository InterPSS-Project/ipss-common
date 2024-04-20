/*
 * @(#)TestUtilFunc.java   
 *
 *  Copyright (C) 2006-2011 www.interpss.org
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
 * @Date 07/15/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.util;

import org.interpss.numeric.datatype.Complex3x1;

/**
 * utility function for testing
 * 
 * @author mzhou
 *
 */
public class TestUtilFunc {
	/**
	 * compare complex3x1 value 
	 * 
	 * @param iPU_012
	 * @param zeroRe
	 * @param zeroIm
	 * @param oneRe
	 * @param oneIm
	 * @param twoRe
	 * @param twoIm
	 * @param err
	 * @return
	 */
	public static boolean compare(Complex3x1 iPU_012, double zeroRe,
			double zeroIm, double oneRe, double oneIm, double twoRe,
			double twoIm, double err) {
		boolean ok = NumericUtil.equals(iPU_012.a_0.getReal(), zeroRe, err)
				&& NumericUtil.equals(iPU_012.a_0.getImaginary(), zeroIm, err)
				&& NumericUtil.equals(iPU_012.b_1.getReal(), oneRe, err)
				&& NumericUtil.equals(iPU_012.b_1.getImaginary(), oneIm, err)
				&& NumericUtil.equals(iPU_012.c_2.getReal(), twoRe, err) 
				&& NumericUtil.equals(iPU_012.c_2.getImaginary(), twoIm, err);
		if (!ok) 
			System.out.println("iPU_012 = " + iPU_012);
		return ok;
	}

	/**
	 * compare complex3x1 value
	 * 
	 * @param iPU_012
	 * @param zeroRe
	 * @param zeroIm
	 * @param oneRe
	 * @param oneIm
	 * @param twoRe
	 * @param twoIm
	 * @return
	 */
	public static boolean compare(Complex3x1 iPU_012, double zeroRe,
			double zeroIm, double oneRe, double oneIm, double twoRe,
			double twoIm) {
		return compare(iPU_012, zeroRe, zeroIm, oneRe, oneIm, twoRe, twoIm, 0.0001);
	}
}
