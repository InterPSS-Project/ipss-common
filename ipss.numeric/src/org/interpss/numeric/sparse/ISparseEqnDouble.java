/*
 * @(#)SparseEqnDouble.java   
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

import org.interpss.numeric.sparse.base.ISparseEquation;


/**
 * Sparse Equation of data type double for solving the [A]X=B problem. 
 * To outside, the index number is from 0 to n-1
 *
 */

public interface ISparseEqnDouble extends ISparseEquation {

  /**
   * Add the aij element to the matrix. If aij exists, aij += x.
	* 
	* @param x the aij element
	* @param i the element row number
	* @param j the element column number
   */
	void addToAij( final double x, final int i, final int j );

  /**
   * Get the x[i] element.
	* 
	* @param i the element row number
	* @return the x[i] element
   */
	double getXi( final int i );

  /**
   * Set the aij element.
	* 
	* @param x the aij element
	* @param i the element row number 
	* @param j the element column number 
   */
	void setAij( final double x, final int i, final int j );
	
	/**
	* Get the aij element.
	* 
	* @param i the element row number 
	* @param j the element column number 
	*/
	double getAij(final int i, final int j );
		
	/**
	 * Set b[i] element.
	 * 
	 * @param bi the bi element
	 * @param i row number 
	 */
	void setBi( final double bi, final int i );
	
	/**
	 * add to the b[i] element.
	 * 
	 * @param bi the bi element
	 * @param i row number 
	 */
	void addToBi( final double bi, final int i );	
}