/*
 * @(#)SpraseEqnUtil.java   
 *
 * Copyright (C) 2006-2020 www.interpss.org
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
 * @Date 12/11/2020
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.util;

import org.apache.commons.math3.complex.Complex;

import edu.emory.mathcs.csparsej.tdcomplex.DZcs_common.DZcsa;

/**
 * Sparse Eqn utility funcitons
 * 
 * @author mzhou
 *
 */
public class SpraseEqnUtil {
	/**
	 * Convert a DZcsa array to a Complex array
	 * 
	 * @param bAry the DZcsa array
	 * @return the converted Complex array
	 */
	public static Complex[] DZcsa2ComplexAry(DZcsa bAry) {
		int n = bAry.x.length/2;
		Complex[] cAry = new Complex[n]; 
		for ( int i = 0; i < n; i++ ) {
			cAry[i] = new Complex(bAry.real(i), bAry.imag(i));
		}
		return cAry;
	}

	/**
	 * Convert a Complex array to a DZcsa array
	 * 
	 * @param bAry the Complex array
	 * @return the converted DZcsa array
	 */	
	public static DZcsa ComplexAry2DZcsa(Complex[] cAry) {
		DZcsa bAry = new DZcsa(cAry.length); 
		for ( int i = 0; i < cAry.length; i++ ) {
			Complex c = cAry[i];
			bAry.set(i, c.getReal(), c.getImaginary());
		}
		return bAry;		
	}	
}
