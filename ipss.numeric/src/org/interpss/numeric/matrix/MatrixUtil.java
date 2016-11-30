package org.interpss.numeric.matrix;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.datatype.Complex3x1;
import org.interpss.numeric.datatype.Complex3x3;
import org.interpss.numeric.datatype.ComplexFunc;
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
	
	/**
	 * create a Complex matrix with all fields initialized to (0.0,0.0)
	 * 
	 * @param row row count
	 * @param col column cound
	 * @return
	 */
	public static Complex[][] createComplex2DArray(int row,int col){
		Complex[][] complexs = new Complex[row][col];
		
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
			  complexs[i][j] = new Complex(0,0);
			}
		}
		return complexs;
	}
	
	/**
	 * create a Complex3x3 block matrix with all fields initialized
	 * 
	 * @param row row count
	 * @param col column cound
	 * @return
	 */
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
	 * create a Complex vector with all fields initialized to (0.0,0.0)
	 * 
	 * @param row row count
	 * @return
	 */
	public static Complex[] createComplex1DArray(int row){
		Complex[] complexs = new Complex[row];
		for(int i=0;i<row;i++){
			  complexs[i]= new Complex(0.0,0.0);
			}
		return complexs;
	}
	
	/**
	 * create a Complex3x1 vector with all fields initialized
	 * 
	 * @param row row count
	 * @return
	 */
	public static Complex3x1[] createComplex3x1DArray(int row){
		Complex3x1[] complexs = new Complex3x1[row];
		for(int i=0;i<row;i++){
			  complexs[i]= new Complex3x1();
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
						 sum = sum.add(mRowData[j].multiply(transMatrix[j][col]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
		
		
	}
	

	/**
	 *  multiply the matrix by a transformation matrix. 
	 *  result = matrix*transMatrix
	 *  
	 * @param matrix
	 * @param transMatrix
	 * @return matrix * transMatrix
	 */
	public static  Complex[][] multiply( Complex[][] matrix, int[][] transMatrix){
		
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
			
			Complex[][] result =  new Complex[rowCount][columnCount];
			
			if(mCol!=tRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				 Complex[] mRowData = matrix[row];
				 
				 for (int col = 0; col < columnCount; col++) {
					 Complex sum = new Complex(0.0,0.0);
					 for(int j = 0;j<mCol;j++){
						 
						 // Bik = sum  {Aij*Tjk} over j
						 sum = sum.add(mRowData[j].multiply(transMatrix[j][col]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
	}

	/**
	 *  multiply the matrix by a transformation matrix. 
	 *  result = matrix*transMatrix
	 *  
	 * @param matrix
	 * @param transMatrix
	 * @return matrix * transMatrix
	 */
	public static  Complex3x1[][] multiply( Complex3x1[][] matrix, int[][] transMatrix){
		
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
			
			Complex3x1[][] result =  new Complex3x1[rowCount][columnCount];
			
			if(mCol!=tRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				 Complex3x1[] mRowData = matrix[row];
				 
				 for (int col = 0; col < columnCount; col++) {
					 Complex3x1 sum = new Complex3x1();
					 for(int j = 0;j<mCol;j++){
						 
						 // Bik = sum  {Aij*Tjk} over j
						 sum = sum.add(mRowData[j].multiply(transMatrix[j][col]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		} // end of else
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
	public static  Complex3x3[][] multiply( Complex3x3[][] matrix, double[][] transMatrix){
		
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
						 sum = sum.add(mRowData[j].multiply(transMatrix[j][col]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
		
		
	}
	
	
	/**
	 *  pre-multiply the matrix by a transformation matrix. 
	 *  result = transMatrix*matrix
	 *  
	 * @param transMatrix
	 * @param matrix
	 * @return transMatrix * matrix
	 */
	public static  Complex3x3[][] preMultiply( double[][] transMatrix, Complex3x3[][] matrix){
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
					 double[] tRowData = transMatrix[row];
					 
					 for (int col = 0; col < columnCount; col++) {
						 
						 Complex3x3 sum = new Complex3x3();
						 
						 for(int j = 0;j<tCol;j++){
							 
							 // Bik = sum  {T_ij*Matrix_jk} over j
							 sum = sum.add(matrix[j][col].multiply(tRowData[j]));
						 } // end of for-j
						 result[row][col] = sum;
					
					 } // for-col
					 
				 } // for-row
				 
				 return result;
			
			} // end of else
		}
	
	
	/**
	 *  pre-multiply the matrix by a transformation matrix. 
	 *  result = transMatrix*matrix
	 *  
	 * @param transMatrix
	 * @param matrix
	 * @return transMatrix * matrix
	 */
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
						 sum = sum.add(matrix[j][col].multiply(tRowData[j]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
	}

    /**
     * transpose[M] x [A] x [M]
     * 
     * @param matrix matrix [A]
     * @param transM transformation matrix
     * @return
     */
    public static  Complex[][] prePostMultiply( Complex[][] matrix,  int[][] transM) {
    	Complex[][] m = multiply(matrix, transM);
    	return preMultiply(transpose(transM), m);
    }
    
    /**
     * transpose[M] x [A] x [M]
     * 
     * @param matrix matrix [A]
     * @param transM transformation matrix
     * @return
     */
    public static  Complex3x1[][] prePostMultiply( Complex3x1[][] matrix,  int[][] transM) {
    	Complex3x1[][] m = multiply(matrix, transM);
    	return preMultiply(transpose(transM), m);
    }
    
    
	/**
	 *  pre-multiply the matrix by a transformation matrix. 
	 *  result = transMatrix*matrix
	 *  
	 * @param transMatrix
	 * @param matrix
	 * @return transMatrix * matrix
	 */
    public static  Complex[][] preMultiply( int[][] transMatrix, Complex[][] matrix){
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
			
			Complex[][]  result =  new Complex[rowCount][columnCount];
			
			if(tCol!=mRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				 int[] tRowData = transMatrix[row];
				 
				 for (int col = 0; col < columnCount; col++) {
					 
					 Complex sum = new Complex(0.0,0.0);
					 
					 for(int j = 0;j<tCol;j++){
						 
						 // Bik = sum  {T_ij*Matrix_jk} over j
						 sum = sum.add(matrix[j][col].multiply(tRowData[j]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
	}    

	/**
	 *  pre-multiply the matrix by a transformation matrix. 
	 *  result = transMatrix*matrix
	 *  
	 * @param transMatrix
	 * @param matrix
	 * @return transMatrix * matrix
	 */
    public static  Complex3x1[][] preMultiply( int[][] transMatrix, Complex3x1[][] matrix){
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
			
			Complex3x1[][]  result =  new Complex3x1[rowCount][columnCount];
			
			if(tCol!=mRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				 int[] tRowData = transMatrix[row];
				 
				 for (int col = 0; col < columnCount; col++) {
					 
					 Complex3x1 sum = new Complex3x1();
					 
					 for(int j = 0;j<tCol;j++){
						 
						 // Bik = sum  {T_ij*Matrix_jk} over j
						 sum = sum.add(matrix[j][col].multiply(tRowData[j]));
					 } // end of for-j
					 result[row][col] = sum;
				
				 } // for-col
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
	}    
    
    
	/**
	 *  pre-multiply the vector by a transformation matrix. 
	 *  result = transMatrix*vector
	 *  
	 * @param transMatrix
	 * @param vector
	 * @return transMatrix * vector
	 */
    public static  Complex3x1[] preMultiply( int[][] transMatrix, Complex3x1[] vector){
    	if(transMatrix==null || vector==null){
			return null;
		}
		else{
		
			// Check dimension consistency
			// the column size of the matrix must be equal to the row size of the transMatrix
			final int  rowCount = transMatrix.length;
			final int tCol =transMatrix[0].length;
			final int vRow = vector.length;
			
			if(tCol!=vRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			Complex3x1[]  result =  new Complex3x1[rowCount];
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				     
				     int[] tRowData = transMatrix[row];
				     Complex3x1 sum = new Complex3x1();
				    
					 for(int j = 0;j<vRow;j++){
						 
						 // Bik = sum  {T_ij*Matrix_jk} over j
						 sum = sum.add(vector[j].multiply(tRowData[j]));
					 } // end of for-j
					 result[row] = sum;
				
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
    	
    	
    }
    
	/**
	 *  pre-multiply the vector by a transformation matrix. 
	 *  result = transMatrix*vector
	 *  
	 * @param transMatrix
	 * @param vector
	 * @return transMatrix * vector
	 */
    public static  Complex3x1[] preMultiply( double[][] transMatrix, Complex3x1[] vector){
    	if(transMatrix==null || vector==null){
			return null;
		}
		else{
		
			// Check dimension consistency
			// the column size of the matrix must be equal to the row size of the transMatrix
			final int  rowCount = transMatrix.length;
			final int tCol =transMatrix[0].length;
			final int vRow = vector.length;
			
			if(tCol!=vRow){
				throw new Error("The dimensions of the two matrice are not consistent!");
			}
			
			Complex3x1[]  result =  new Complex3x1[rowCount];
			
			// implementation based on the basic matrix multiplication rule
			 for (int row = 0; row < rowCount; row++) {
				     
				     double[] tRowData = transMatrix[row];
				     Complex3x1 sum = new Complex3x1();
				    
					 for(int j = 0;j<vRow;j++){
						 
						 // Bik = sum  {T_ij*Matrix_jk} over j
						 sum = sum.add(vector[j].multiply(tRowData[j]));
					 } // end of for-j
					 result[row] = sum;
				
				 
			 } // for-row
			 
			 return result;
		
		} // end of else
    	
    	
    }
    
    /**
     * add the two matrix together
     * 
     * @param A
     * @param B
     * @return
     */
    public static  Complex3x3[][] add( Complex3x3[][] A,Complex3x3[][] B ){
    	if(A==null || B==null){
			return null;
		}
    	else{
    		
    		//first, check dimension consistency
    		if(A.length !=B.length ||A[0].length!=B[0].length){
    			throw new Error("The dimensions of the two matrice are not the same!");
    		}
    		
    		final int  rowCount = A.length;
			final int columnCount =  A[0].length;
			
			Complex3x3[][]  result = createComplex3x32DArray(rowCount,columnCount );
			
			 for (int row = 0; row < rowCount; row++) {
				 for (int col = 0; col < columnCount; col++) {
					 result[row][col] = A[row][col].add(B[row][col]);
				 }
			 }
			 
			 return result;
    	}
    }
    
    /**
     * add the two matrix together
     * 
     * @param A
     * @param B
     * @return
     */
    public static  Complex[][] add( Complex[][] A, Complex[][] B ){
    	if(A==null || B==null){
			return null;
		}
    	else{
    		
    		//first, check dimension consistency
    		if(A.length !=B.length ||A[0].length!=B[0].length){
    			throw new Error("The dimensions of the two matrice are not the same!");
    		}
    		
    		final int  rowCount = A.length;
			final int columnCount =  A[0].length;
			
			Complex[][]  result = new Complex[rowCount][columnCount];
			
			 for (int row = 0; row < rowCount; row++) {
				 for (int col = 0; col < columnCount; col++) {
					 result[row][col] = A[row][col].add(B[row][col]);
				 }
			 }
			 
			 return result;
    	}
    }

    /**
     * add the two matrix together
     * 
     * @param A
     * @param B
     * @return
     */
    public static  Complex3x1[][] add( Complex3x1[][] A, Complex3x1[][] B ){
    	if(A==null || B==null){
			return null;
		}
    	else{
    		
    		//first, check dimension consistency
    		if(A.length !=B.length ||A[0].length!=B[0].length){
    			throw new Error("The dimensions of the two matrice are not the same!");
    		}
    		
    		final int  rowCount = A.length;
			final int columnCount =  A[0].length;
			
			Complex3x1[][]  result = new Complex3x1[rowCount][columnCount];
			
			 for (int row = 0; row < rowCount; row++) {
				 for (int col = 0; col < columnCount; col++) {
					 result[row][col] = A[row][col].add(B[row][col]);
				 }
			 }
			 
			 return result;
    	}
    }    
    /**
     * add the two vector together
     * 
     * @param A
     * @param B
     * @return
     */
    public static  Complex3x1[] add( Complex3x1[] A,Complex3x1[] B ){
    	if(A==null || B==null){
			return null;
		}
    	else{
    		
    		//first, check dimension consistency
    		if(A.length !=B.length ){
    			throw new Error("The dimensions of the two matrice are not the same!");
    		}
    		
    		final int  rowCount = A.length;
			
			Complex3x1[]  result = new Complex3x1[rowCount];
			
			 for (int row = 0; row < rowCount; row++) {
					 result[row] = A[row].add(B[row]); 
			 }

			 return result;
    	}
    	
    	
    }
    
    /**
     * transpose the matrix, result = [matrix]T
     * 
     * @param t
     * @return
     */
    public static int[][] transpose(int[][] t){
    	
    	if(t==null){
    		return null;
    	}
    	else{
    		int tRow = t.length;
    		int tCol = t[0].length;
    		int[][] result = new int[tCol][tRow];
    		
    		for(int i=0;i<tRow;i++){
    			for(int j=0;j<tCol;j++){
    				result[j][i] = t[i][j];
    			}
    		}
    		return result;
    	}
    		
    }
    
    /**
     * transpose the matrix, result = [matrix]T
     * 
     * @param t
     * @return
     */
    public static double[][] transpose(double[][] t){
    	if(t==null){
    		return null;
    	}
    	else{
    		int tRow = t.length;
    		int tCol = t[0].length;
    		double[][] result = new double[tCol][tRow];
    		
    		for(int i=0;i<tRow;i++){
    			for(int j=0;j<tCol;j++){
    				result[j][i] = t[i][j];
    			}
    		}
    		return result;
    	}
    		
    }

    /**
     * Convert three vectors to a Complex3x1 vector
     * 
     * @param vector0
     * @param vector1
     * @param vector2
     * @return
     */
	public static Complex3x1[] toComplex3x1Ary(Complex[] vector0, Complex[] vector1, Complex[] vector2) {
		Complex3x1[] ary = new Complex3x1[vector0.length];
		for (int i = 0; i < vector0.length; i++) {
			ary[i] = new Complex3x1(
					vector0[i],
					vector1[i],
					vector2[i]);
		}
		return ary;
	}
	
	/**
	 * convert a Complex3x1 vector to a Complex[] vector
	 * 
	 * @param vector the Complex3x1 vector
	 * @return the Complex[] vector
	 */
	public static  Complex[][] toComplexAry(Complex3x1[] vector) {
		Complex[][] ary = new Complex[3][vector.length];
		for (int i = 0; i < vector.length; i++) {
			Complex3x1 c = vector[i];
			ary[Complex3x1.Index_1][i] = c.b_1;
			ary[Complex3x1.Index_2][i] = c.c_2;
			ary[Complex3x1.Index_0][i] = c.a_0;
		}
		return ary;
	}

	/**
	 * Convert a Complex3x1 matrix to a Complex[3] matrix
	 * 
	 * @param matrix the Complex3x1 matrix
	 * @return the Complex[3] matrix
	 */
	public static  Complex[][][] toComplexMatrix(Complex3x1[][] matrix) {
		// assume matrix is a square matrix
		Complex[][][] ary = new Complex[3][matrix.length][matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				Complex3x1 c = matrix[i][j];
				ary[Complex3x1.Index_1][i][j] = c.b_1;
				ary[Complex3x1.Index_2][i][j] = c.c_2;
				ary[Complex3x1.Index_0][i][j] = c.a_0;
			}
		}
		return ary;
	}    
     /*
      * output utility functions
      */
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
	
	
	public static String complex3x1Ary2String(Complex3x1[] ary){
		StringBuffer sb = new StringBuffer();
		int row = ary.length;

		for(int i=0;i<row;i++){
		    sb.append(ary[i].toString()+", ");
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
	
     
     public static String toString(Complex[][] m) {
    	 StringBuffer buffer = new StringBuffer("\n\n");
    	 for (int i = 0; i < m.length; i++)
    		 for (int j = 0; j < m[0].length; j++)
    			 buffer.append(i + "," + j + ": (" + ComplexFunc.toStr(m[i][j]) + ")\n");
    	 return buffer.toString();
     }

}
