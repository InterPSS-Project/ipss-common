/*
 * @(#)SparseEqnInteger.java   
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
 * Sparse Equation of data type integer. The purpose is for arranging network bus number
 * to minimize the non-zero fill-ins. 	To outside, the index number is from 0 to n-1
 */
public interface ISparseEqnInteger extends ISparseEquation {

  /**
   * Arrange bus bus according to the Tinney-2 rule
	*
   */
	void arrangeRowNoT2();

  /**
   * Arrange bus bus according to the Tinney-3 rule
	*
   */
	void arrangeRowNoT3();
	
  /**
   * After the bus number arrangement, get the new bus number from an old bus number.
	*
	* @param i old bus number
	* @return the new bus number
   */
	public int getOld2New( final int i );

  /**
   * Set the aij element
	* 
	* @param n the aij element
	* @param i the element row number (starts from 0)
	* @param j the element column number (starts from 0)
   */
	void setAij( final int n, final int i, final int j );
}


