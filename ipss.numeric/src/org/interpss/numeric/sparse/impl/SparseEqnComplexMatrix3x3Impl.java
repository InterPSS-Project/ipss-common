package org.interpss.numeric.sparse.impl;

import org.interpss.numeric.datatype.Complex3x1;
import org.interpss.numeric.datatype.Complex3x3;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.ISparseEqnComplexMatrix3x3;

public class SparseEqnComplexMatrix3x3Impl implements ISparseEqnComplexMatrix3x3{

	public SparseEqnComplexMatrix3x3Impl(int noBus) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addToA(Complex3x3 x, int i, int j) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Complex3x1 getX(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setA(Complex3x3 x, int i, int j) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Complex3x3 getA(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBi(Complex3x1 bi, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToB(Complex3x1 bi, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getZeroA_row() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDimension() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDimension(int n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseDimension() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTotalElements() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean luMatrix(double tolerance) throws IpssNumericException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean luMatrixAndSolveEqn(double tolerance)
			throws IpssNumericException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void solveEqn() throws IpssNumericException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setB2Unity(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setB2Zero() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setToZero() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getZeroAii_row() {
		// TODO Auto-generated method stub
		return 0;
	}

}