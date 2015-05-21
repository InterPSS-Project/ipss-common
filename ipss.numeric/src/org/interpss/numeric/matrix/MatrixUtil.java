package org.interpss.numeric.matrix;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.datatype.Complex3x3;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.ISparseEqnComplexMatrix3x3;

public class MatrixUtil {
	/**
	 * complex matrix multiplication [A] x [b]
	 * 
	 * @param a matrix A
	 * @param b vector b
	 * @return return vector
	 * @throws IpssNumericException
	 */
	public static Complex[] mul(Complex[][] a, Complex[] b) throws IpssNumericException {
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
	
	public static String complex3x32DAry2String(Complex3x3[][] ary){
		StringBuffer sb = new StringBuffer();
		int row = ary.length;
		int col = ary[0].length;
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				sb.append(ary[i][j].toString()+", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	public static String complex2DAry2String(Complex[][] ary){
		StringBuffer sb = new StringBuffer();
		int row = ary.length;
		int col = ary[0].length;
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				sb.append(ary[i][j].toString()+", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static Complex[][] createComplex2DArray(int row,int col){
		Complex[][] complexs = new Complex[row][col];
		
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
			  complexs[i][j] = new Complex(0,0);
			  
			}
		}
		return complexs;
	}
	
	public static Complex3x3[][] createComplex3x32DArray(int row,int col){
		Complex3x3[][] complexs = new Complex3x3[row][col];
		
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
			  complexs[i][j] = new Complex3x3();
			}
		}
		return complexs;
	}
	
	/**
	 *  multiply the matrix by a transformation matrix. 
	 *  result = matrix*transMatrix
	 *  
	 *  A typical application is multiplying the ISparseEqnComplexMatrix3x3 matrix by the corresponding incidence matrix
	 *  
	 * @param matrix
	 * @param transMatrix
	 * @return matrix * transMatrix
	 */
	public static  Complex3x3[][] multiply( Complex3x3[][] matrix, int[][] transMatrix){
		
		if(matrix==null || transMatrix==null){
			return null;
		}
		else{
		
			// Check dimension consistency
			// the column size of the matrix must be equal to the row size of the transMatrix
			final int  rowCount = matrix.length;
			final int mCol = matrix[0].length;
			final int tRow = transMatrix.length;
			final int columnCount = transMatrix[0].length;
			
			Complex3x3[][]  result =  createComplex3x32DArray(rowCount,columnCount);
			
			if(mCol!=tRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				 Complex3x3[] mRowData = matrix[row];
				 
				 for (int col = 0; col < columnCount; col++) {
					 Complex3x3 sum = new Complex3x3();
					 for(int j = 0;j<mCol;j++){
						 
						 // Bik = sum  {Aij*Tjk} over j
						 sum = sum.add(mRowData[j].mulitply(transMatrix[j][col]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
		
		
	}
	
	
    public static  Complex3x3[][] preMultiply( int[][] transMatrix, Complex3x3[][] matrix){
    	if(matrix==null || transMatrix==null){
			return null;
		}
		else{
		
			// Check dimension consistency
			// the column size of the matrix must be equal to the row size of the transMatrix
			final int  rowCount = transMatrix.length;
			final int tCol =transMatrix[0].length;
			final int mRow = matrix.length;
			final int columnCount =  matrix[0].length;
			
			Complex3x3[][]  result =  createComplex3x32DArray(rowCount,columnCount);
			
			if(tCol!=mRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				 int[] tRowData = transMatrix[row];
				 
				 for (int col = 0; col < columnCount; col++) {
					 
					 Complex3x3 sum = new Complex3x3();
					 
					 for(int j = 0;j<tCol;j++){
						 
						 // Bik = sum  {T_ij*Matrix_jk} over j
						 sum = sum.add(matrix[j][col].mulitply(tRowData[j]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
	}

}
