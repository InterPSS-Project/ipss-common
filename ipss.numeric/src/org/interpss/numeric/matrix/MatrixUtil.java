package org.interpss.numeric.matrix;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.exp.IpssNumericException;

public class MatrixUtil {
	/**
	 * complex matrix multiplication [A] x [b]
	 * 
	 * @param a matrix A
	 * @param b vector b
	 * @return return vector
	 * @throws IpssNumericException
	 */
	public Complex[] mul(Complex[][] a, Complex[] b) throws IpssNumericException {
		if (a.length <= 0 || 
				a.length != a[0].length ||
				a.length != b.length) {
			throw new IpssNumericException("matrix dimension error");
		}
		Complex[] rnt = new Complex[b.length];
		for (int i = 0; i < b.length; i++) {
			rnt[i] = new Complex(0.0, 0.0);
			for (int j = 0; j < b.length; j++) {
				rnt[i] = rnt[i].add(a[i][j].multiply(b[j]));
			}
		}
		return rnt;
	}

}
