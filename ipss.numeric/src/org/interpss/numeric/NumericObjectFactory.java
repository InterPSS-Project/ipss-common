/*
 * @(#)NumericObjectFactory.java   
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

package org.interpss.numeric;

import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.interpss.numeric.sparse.ISparseEqnDouble;
import org.interpss.numeric.sparse.ISparseEqnInteger;
import org.interpss.numeric.sparse.ISparseEqnMatrix2x2;
import org.interpss.spring.NumericSpringFactory;

/**
 * for creating object using its default implementation
 * 
 * @author mzhou
 *
 */
public class NumericObjectFactory {
	/**
	 * create a SparseEqnInteger object
	 * 
	 * @return
	 */
	public static ISparseEqnInteger createSparseEqnInteger(int n) {
		ISparseEqnInteger e = NumericSpringFactory.getSparseEqnInteger();
		e.setDimension(n);
		return e;
	}

	/**
	 * create a SparseEqnDouble object
	 * 
	 * @return
	 */
	public static ISparseEqnDouble createSparseEqnDouble(int n) {
		ISparseEqnDouble e = NumericSpringFactory.getSparseEqnDouble();
		e.setDimension(n);
		return e;
	}

	/**
	 * create a SparseEqnMatrix2x2 object
	 * 
	 * @return
	 */
	public static ISparseEqnMatrix2x2 createSparseEqnMatrix2x2(int n) {
		ISparseEqnMatrix2x2 e = NumericSpringFactory.getSparseEqnMatrix2x2();
		e.setDimension(n);
		return e;
	}	
	
	/**
	 * create a SparseEqnComplex object
	 * 
	 * @return
	 */
	public static ISparseEqnComplex createSparseEqnComplex(int n) {
		ISparseEqnComplex e = NumericSpringFactory.getSparseEqnComplex();
		e.setDimension(n);
		return e;
	}	
}
