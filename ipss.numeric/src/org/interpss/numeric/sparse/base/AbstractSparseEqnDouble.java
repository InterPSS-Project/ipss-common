/*
 * @(#)AbstractSparseEqnDouble.java   
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

package org.interpss.numeric.sparse.base;

import org.interpss.numeric.sparse.ISparseEqnDouble;

/**
 * A base class for double type sparse eqn implementation
 * 
 * @author mzhou
 *
 */
public class AbstractSparseEqnDouble extends AbstractSparseEquation implements ISparseEqnDouble {

	@Override public void addToAij(double x, int i, int j) {
		throw new UnsupportedOperationException();
	}

	@Override public void addToBi(double bi, int i) {
		throw new UnsupportedOperationException();
	}

	@Override public double getAij(int i, int j) {
		throw new UnsupportedOperationException();
	}

	@Override public double getXi(int i) {
		throw new UnsupportedOperationException();
	}

	@Override public void setAij(double x, int i, int j) {
		throw new UnsupportedOperationException();
	}

	@Override public void setBi(double bi, int i) {
		throw new UnsupportedOperationException();
	}
}