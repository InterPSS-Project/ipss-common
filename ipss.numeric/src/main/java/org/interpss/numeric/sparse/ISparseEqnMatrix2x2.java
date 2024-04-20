/*
 * @(#)SparseEqnMatrix2x2.java   
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

package org.interpss.numeric.sparse;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.datatype.Matrix_xy;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.sparse.base.ISparseEqnObject;

/**
 * Sparse Equation of data type 2x2 matrix for solving the [A]X=B problem. 
 * To outside, the index number is from 0 to n-1
 *
 */
public interface ISparseEqnMatrix2x2 extends ISparseEqnObject<Matrix_xy, Vector_xy> {
	/**
	* Set b[i] object.
	* 
	* @param c the b[i] object
	* @param i row number 
	*/
	public void setB( final Complex c, final int i );
		
  /**
   * add to b[i] object.
	* 
	* @param c the b[i] object
	* @param i row number 
   */
	public void addToB( final Complex c, final int i );
}