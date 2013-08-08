/*
 * @(#)SparseEqnDoubleCommonMathImpl.java   
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

package org.interpss.numeric.sparse.impl;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.interpss.numeric.sparse.base.AbstractSparseEqnDouble;

/**
 * Sparse Equation of data type double for solving the [A]X=B problem. 
   using the apache common math lib. It is for illustrating extension implementation.
 */
public class SparseEqnDoubleCommonMathImpl extends AbstractSparseEqnDouble {
	RealMatrix A = null;
	RealVector B = null;
	RealVector X = null;
	
	public SparseEqnDoubleCommonMathImpl() {
	}

	public SparseEqnDoubleCommonMathImpl(int n) {
		this.setDimension(n);
	}
	
	@Override
	public void setDimension(int n) {
		super.setDimension(n);
		this.A = new Array2DRowRealMatrix( n, n );
		this.B = new ArrayRealVector(n);
	}
	
	@Override
	public void addToAij( final double x, final int i, final int j ) {
		double x0 = this.A.getEntry(i, j);
		this.A.setEntry(i, j, x0+x);
	}

	@Override
	public void setAij( final double x, final int i, final int j ) {
		this.A.setEntry(i, j, x);
	}
		
	@Override
	public double getAij( final int i, final int j ) {
		return this.A.getEntry(i, j);
	}

	@Override
	public double getXi( final int i ) {
		return this.X.getEntry(i);
	}
	
	@Override
	public void setBi( final double bi, final int i ) {
		this.B.setEntry(i, bi);
	}

	@Override
	public void addToBi( final double bi, final int i ) {
		this.B.setEntry(i, bi+this.B.getEntry(i));
	}
	
	@Override
	public void solveEqn() {
		LUDecomposition lu = new LUDecomposition(A);
		this.X = lu.getSolver().solve(B);
	}
}