package org.interpss.numeric.matrix;

/**
 * Abstract base class for defining a full matrix eqn [A][x] = [b] 
 * 
 * @author Mike
 *
 * @param <TMatrix> template for matrix [A] element
 * @param <TVector> template for vector [b] element
 */

public class AbstractMatrixEqn<TMatrix, TVector> {
	/* the vector [b] */
	protected TVector[] b = null;

	/* the matrix [A] */
	protected TMatrix[][] a = null;
	
	/* dimension */
	private int dimension = 0;
	
	/**
	 * default constructor
	 */
	public AbstractMatrixEqn() {
	}

	/**
	 * constructor
	 * 
	 * @param d the dimension of the matrix
	 */	
	protected AbstractMatrixEqn(int d) {
		this.dimension = d;
	}

	/**
	 * @return the [b] vector
	 */
	public TVector[] getB() {
		return b;
	}

	/**
	 * @return the [A] matrix
	 */
	public TMatrix[][] getA() {
		return a;
	}

	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}
	
	/**
	 * set the [A]ij element
	 * 
	 * @param a the matrix element
	 * @param i row number
	 * @param j column number
	 */
	public void setAij(TMatrix a, int i, int j) {
		this.a[i][j] = a;
	}

	/**
	 * set the [b]i element
	 * 
	 * @param b the vector element
	 * @param i row number
	 */
	public void setBi(TVector b, int i) {
		this.b[i] = b;
	}

	/**
	 * set the [b] vector
	 * 
	 * @param bAry the [b] vector
	 */
	public void setB(TVector[] bAry) {
		this.b = bAry;
	}
	
	/**
	 * get the [A]ij element
	 * 
	 * @param i row number
	 * @param j column number
	 */
	public TMatrix getAij(int i, int j) {
		return this.a[i][j];
	}

	/**
	 * get the [b]i element
	 * 
	 * @param i row number
	 */
	public TVector getBi(int i) {
		return this.b[i];
	}
}
