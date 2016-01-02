package org.interpss.numeric.matrix;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.NumericLogger;
import org.interpss.numeric.datatype.Complex3x1;
import org.interpss.numeric.datatype.Complex3x3;

/**
 * class for defining a full matrix eqn [A][x] = [b] of type Complex [A] and Complex [b]
 * 
 * @author Mike
 *
 */
public class ComplexMatrixEqn extends AbstractMatrixEqn<Complex, Complex>{
	/**
	 * constructor
	 * 
	 * @param d the dimension of the matrix
	 */
	public ComplexMatrixEqn(int d) {
		super(d);
		this.a = MatrixUtil.createComplex2DArray(d,d);
		this.b = MatrixUtil.createComplex1DArray(d);
	}
	
	/**
	 * transform the [A] matrix to a 3x3 block matrix
	 * 
	 * @return the 3x3 block matrix
	 */
	public Complex3x3[][] to3x3BlockMatrix(){
        //check the dimension if it is valid
        if((this.getDimension() % 3)!=0){
        	NumericLogger.getLogger().severe("The dimension of the matrix is not multiple times of 3!");
        	return null;
        }

        int k = this.getDimension()/3;
        Complex3x3[][] blockMatrix = new Complex3x3[k][k] ;
                	
		// perform matrix transformation in a 3x3 block-wise manner
		for(int i=0; i<k; i++){
			for(int j=0; j<k; j++){
				Complex3x3 m3x3 =  new Complex3x3();
				
				m3x3.aa=this.getAij(3*i,3*j);
				m3x3.ab=this.getAij(3*i,3*j+1);
				m3x3.ac=this.getAij(3*i,3*j+2);
				
				m3x3.ba=this.getAij(3*i+1,3*j);
				m3x3.bb=this.getAij(3*i+1,3*j+1);
				m3x3.bc=this.getAij(3*i+1,3*j+2);
				
				m3x3.ca=this.getAij(3*i+2,3*j);
				m3x3.cb=this.getAij(3*i+2,3*j+1);
				m3x3.cc=this.getAij(3*i+2,3*j+2);
				
				blockMatrix[i][j] = m3x3;
			}
		}
			
		return blockMatrix;
	}

	/**
	 * transform the [b] vector to a 3x1 block vector
	 * 
	 * @return the 3x1 block vector
	 */
	public Complex3x1[] to3x1BlockVector(){
		//check the dimension if it is valid
		if((this.getDimension() % 3)!=0){
			NumericLogger.getLogger().severe("The dimension of the matrix is not multiple times of 3!");
			return null;
		}

		int k = this.getDimension()/3;
		Complex3x1[] blockSource = new Complex3x1[k] ;
		
		// perform matrix transformation in a 3x3 block-wise manner
		for(int i=0; i<k; i++){
			Complex3x1 v3X1 =  new Complex3x1();
			
			v3X1.a_0 = this.getBi(3*i);
			v3X1.b_1 = this.getBi(3*i+1);
			v3X1.c_2 = this.getBi(3*i+2);
			
			blockSource[i] = v3X1;
		}
		return blockSource;
	}	
}
