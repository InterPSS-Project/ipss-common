package org.interpss.numeric.datatype;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldLUDecomposition;

/**
 * A class to represent 3x3 complex elements for ABC or 012 coordinate Y/Z representation. 
 * 
 * @author tonyh, mikez
 *
 */
public class Complex3x3 {
	private static final Complex ConstA = new Complex(-0.5, Math.sqrt(3)/2);
	private static final double Const1_3 = 1.0/3.0;
	
	// transformation matrix from 012 coordinates to abc coordinates
	public static final Complex[][] T120_abc = new Complex[][]{
			{new Complex(1.0,0.0),     new Complex(1.0,0.0),    new Complex(1.0,0.0)},
			{ConstA.multiply(ConstA),  ConstA,                  new Complex(1.0,0.0)},
			{ConstA,                   ConstA.multiply(ConstA), new Complex(1.0,0.0)}
		};
	
	// transformation matrix from abc coordinates to 012 coordinates
	 public static final Complex[][] Tabc_120 = new Complex[][]{
			{new Complex(Const1_3,0.0), ConstA.divide(3.0),                  ConstA.multiply(ConstA).divide(3.0)},
			{new Complex(Const1_3,0.0), ConstA.multiply(ConstA).divide(3.0), ConstA.divide(3.0)                 },
			{new Complex(Const1_3,0.0), new Complex(Const1_3,0.0),             new Complex(Const1_3,0.0)             }
		};
	 

	public  Complex 
		aa = new Complex(0.0, 0.0), 
		ab = new Complex(0.0, 0.0),
		ac = new Complex(0.0, 0.0),
		
		ba = new Complex(0.0, 0.0), 
		bb = new Complex(0.0, 0.0),
		bc = new Complex(0.0, 0.0),
		
		ca = new Complex(0.0, 0.0), 
		cb = new Complex(0.0, 0.0),
		cc = new Complex(0.0, 0.0);
	
	/**
	 * Constructor
	 */
	public Complex3x3(){
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param selfZorY
	 * @param mutualZorY
	 */
	public Complex3x3(Complex selfZorY, Complex mutualZorY){
		this.aa = selfZorY; 
		this.bb = selfZorY; 
		this.cc = selfZorY;
		this.ab= mutualZorY; 
		this.ac= mutualZorY;
		this.ba= mutualZorY; 
		this.bc= mutualZorY;
		this.ca= mutualZorY; 
		this.cb= mutualZorY;
	}
	
	/**
	 * Constructor. create a diagonal 3x3 matrix, all non-diagonal elements are zero 
	 * 
	 * @param pos  - diagonal element aa
	 * @param neg  - diagonal element bb
	 * @param zero - diagonal element cc
	 */
	public Complex3x3(Complex pos,Complex neg, Complex zero){
		this.aa = pos;
		this.bb = neg;
		this.cc = zero;
	}
	
	/**
	 * Constructor
	 * 
	 * @param cplxAry3x3
	 */
	public Complex3x3(Complex[][] cplxAry3x3) {
		if(cplxAry3x3.length!=3 || cplxAry3x3[0].length!=3)
			throw new RuntimeException("Input Complex array cplxAry3x3 is not a 3 by 3 matrix");

		this.aa = cplxAry3x3[0][0];
		this.ab = cplxAry3x3[0][1];
		this.ac = cplxAry3x3[0][2];
		
		this.ba = cplxAry3x3[1][0];
		this.bb = cplxAry3x3[1][1];
		this.bc = cplxAry3x3[1][2];
		
		this.ca = cplxAry3x3[2][0];
		this.cb = cplxAry3x3[2][1];
		this.cc = cplxAry3x3[2][2];
	}
	
	/*
		Operator methods
     */	
	
	/**
	 * for implementing symbolic notation = operator
	 * 
	 * @param c
	 * @return
	 */
	public static Complex3x1 valueOf(Complex3x1 c) {
		return new Complex3x1(c);
	}
	
	/**
	 * for implementing symbolic notation - sign operator
	 * 
	 * @return
	 */
    public  Complex3x3 negate() {
    	return new Complex3x3(
    			new Complex[][] {
    				{ this.aa.negate(), this.ab.negate(), this.ac.negate() }, 
    				{ this.ba.negate(), this.bb.negate(), this.bc.negate() }, 
					{ this.ca.negate(), this.cb.negate(), this.cc.negate() }
    			}
    		);
    }
	
	/*
		Math methods
	 */	
	
    /**
     * for implementing symbolic notation + operator
     * 
     * @param m3x3
     * @return
     */
    // immutable
    public  Complex3x3 add(final Complex3x3 m3x3){
		 Complex3x3 cplxM3x3 = new  Complex3x3();
		 cplxM3x3.aa = this.aa.add(m3x3.aa);
		 cplxM3x3.ab = this.ab.add(m3x3.ab);
		 cplxM3x3.ac = this.ac.add(m3x3.ac);
		 
		 cplxM3x3.ba = this.ba.add(m3x3.ba);
		 cplxM3x3.bb = this.bb.add(m3x3.bb);
		 cplxM3x3.bc = this.bc.add(m3x3.bc);
		 
		 cplxM3x3.ca = this.ca.add(m3x3.ca);
		 cplxM3x3.cb = this.cb.add(m3x3.cb);
		 cplxM3x3.cc = this.cc.add(m3x3.cc);
				 
		return cplxM3x3;
	}

    /**
     * for implementing symbolic notation - operator
     * 
     * @param m3x3
     * @return
     */
    // immutable
	public  Complex3x3 subtract(final Complex3x3 m3x3){
		 Complex3x3 cplxM3x3 = new  Complex3x3();
		 cplxM3x3.aa = this.aa.subtract(m3x3.aa);
		 cplxM3x3.ab = this.ab.subtract(m3x3.ab);
		 cplxM3x3.ac = this.ac.subtract(m3x3.ac);
		 
		 cplxM3x3.ba = this.ba.subtract(m3x3.ba);
		 cplxM3x3.bb = this.bb.subtract(m3x3.bb);
		 cplxM3x3.bc = this.bc.subtract(m3x3.bc);
		 
		 cplxM3x3.ca = this.ca.subtract(m3x3.ca);
		 cplxM3x3.cb = this.cb.subtract(m3x3.cb);
		 cplxM3x3.cc = this.cc.subtract(m3x3.cc);
		return cplxM3x3;
	}

	/**
	 * for implementing symbolic notation * operator
	 * 
	 * @param m3x3
	 * @return
	 */
	// immutable
	public  Complex3x3 multiply(final Complex3x3 m3x3){
    	 Complex3x3 cplxM3x3 = new  Complex3x3();
    	 cplxM3x3.aa = this.aa.multiply(m3x3.aa).add(this.ab.multiply(m3x3.ba)).add(this.ac.multiply(m3x3.ca));
    	 cplxM3x3.ab = this.aa.multiply(m3x3.ab).add(this.ab.multiply(m3x3.bb)).add(this.ac.multiply(m3x3.cb));
    	 cplxM3x3.ac = this.aa.multiply(m3x3.ac).add(this.ab.multiply(m3x3.bc)).add(this.ac.multiply(m3x3.cc));
    	 
    	 
    	 cplxM3x3.ba = this.ba.multiply(m3x3.aa).add(this.bb.multiply(m3x3.ba)).add(this.bc.multiply(m3x3.ca));
    	 cplxM3x3.bb = this.ba.multiply(m3x3.ab).add(this.bb.multiply(m3x3.bb)).add(this.bc.multiply(m3x3.cb));
    	 cplxM3x3.bc = this.ba.multiply(m3x3.ac).add(this.bb.multiply(m3x3.bc)).add(this.bc.multiply(m3x3.cc));
    	 
    	 
    	 cplxM3x3.ca = this.ca.multiply(m3x3.aa).add(this.cb.multiply(m3x3.ba)).add(this.cc.multiply(m3x3.ca));
    	 cplxM3x3.cb = this.ca.multiply(m3x3.ab).add(this.cb.multiply(m3x3.bb)).add(this.cc.multiply(m3x3.cb));
    	 cplxM3x3.cc = this.ca.multiply(m3x3.ac).add(this.cb.multiply(m3x3.bc)).add(this.cc.multiply(m3x3.cc));
    	 
		return cplxM3x3;
   	}
    
	/**
	 * for implementing symbolic notation * operator
	 * 
	 * @param m3x1
	 * @return
	 */
	// immutable
    public  Complex3x1 multiply(final Complex3x1 m3x1){
    	 Complex3x1 cplxM3x1 = new  Complex3x1();
    	 cplxM3x1.a_0 = this.aa.multiply(m3x1.a_0).add(this.ab.multiply(m3x1.b_1)).add(this.ac.multiply(m3x1.c_2));
    	 cplxM3x1.b_1 = this.ba.multiply(m3x1.a_0).add(this.bb.multiply(m3x1.b_1)).add(this.bc.multiply(m3x1.c_2));
    	 cplxM3x1.c_2 = this.ca.multiply(m3x1.a_0).add(this.cb.multiply(m3x1.b_1)).add(this.cc.multiply(m3x1.c_2));
    	 return cplxM3x1;
    }
    
    /**
     * for implementing symbolic notation * operator
     * 
     * @param factor
     * @return
     */
    // immutable
    public  Complex3x3 multiply (double factor){
    	 Complex3x3 cplxM3x3 = new  Complex3x3();
    	 cplxM3x3.aa = aa.multiply(factor);
    	 cplxM3x3.ab = ab.multiply(factor);
    	 cplxM3x3.ac = ac.multiply(factor);
    	 
    	 cplxM3x3.ba = ba.multiply(factor);
    	 cplxM3x3.bb = bb.multiply(factor);
    	 cplxM3x3.bc = bc.multiply(factor);
    	 
    	 cplxM3x3.ca = ca.multiply(factor);
    	 cplxM3x3.cb = cb.multiply(factor);
    	 cplxM3x3.cc = cc.multiply(factor);
    	 
    	 return cplxM3x3;
    }
    
    /**
     * for implementing symbolic notation * operator
     * 
     * @param factor
     * @return
     */
    // immutable
    public Complex3x3 multiply(Complex factor) {
    	 Complex3x3 cplxM3x3 = new  Complex3x3();
    	 cplxM3x3.aa = aa.multiply(factor);
    	 cplxM3x3.ab = ab.multiply(factor);
    	 cplxM3x3.ac = ac.multiply(factor);
    	 
    	 cplxM3x3.ba = ba.multiply(factor);
    	 cplxM3x3.bb = bb.multiply(factor);
    	 cplxM3x3.bc = bc.multiply(factor);
    	 
    	 cplxM3x3.ca = ca.multiply(factor);
    	 cplxM3x3.cb = cb.multiply(factor);
    	 cplxM3x3.cc = cc.multiply(factor);
    	 
    	 return cplxM3x3;

	}
    
    /**
	 * calculate the absolute value
	 * 
	 * @return
	 */
 // immutable
	public double abs(){
		return (this.aa.abs()+this.ba.abs()+this.ca.abs() + 
				this.ab.abs()+this.bb.abs()+this.cb.abs() +
				this.ac.abs()+this.bc.abs()+this.cc.abs());
	}

	/**
	 * calculate the max abs() of this object elements
	 * 
	 * @return
	 */
	// immutable
	public double absMax(){
		Double[] array = new Double[]{
				this.aa.abs(),this.ba.abs(),this.ca.abs(),
				this.ab.abs(),this.bb.abs(),this.cb.abs(),
				this.ac.abs(),this.bc.abs(),this.cc.abs()};
		return Collections.max(Arrays.asList(array));
		
	}

	/**
     * compute the inverse of the matrix(this)
     * 
     * @return
     */
    // immutable
    public Complex3x3 inv(){
    	Array2DRowFieldMatrix<Complex> fm = new Array2DRowFieldMatrix<>(this.toComplex2DAry());
    	FieldLUDecomposition<Complex> lu = new FieldLUDecomposition<>(fm);
		Complex[][] inv = lu.getSolver().getInverse().getData();
		return new Complex3x3(inv);
    }
    
    /**
     * transpose the matrix(this) 
     * 
     * @return
     */
    // immutable
    public Complex3x3 transpose(){
    	Complex3x3 cplxM3x3 = new  Complex3x3();
    	 cplxM3x3.aa = this.aa;
		 cplxM3x3.ab = this.ba;
		 cplxM3x3.ac = this.ca;
		 
		 
		 cplxM3x3.ba = this.ab;
		 cplxM3x3.bb = this.bb;
		 cplxM3x3.bc = this.cb;
		 
		 
		 cplxM3x3.ca = this.ac;
		 cplxM3x3.cb = this.bc;
		 cplxM3x3.cc = this.cc;
		 
		 return cplxM3x3;
    }
    
    /**
     * create a unit matrix
     * 
     * @return
     */
    public  static Complex3x3 createUnitMatrix(){
    	 Complex3x3 cplxM3x3 = new  Complex3x3();
    	 cplxM3x3.aa = new Complex(1,0);
    	 cplxM3x3.bb = new Complex(1,0);
    	 cplxM3x3.cc = new Complex(1,0);
    	 
    	 return cplxM3x3;
    }
    
    /**
     * Transform this object from ABC to 012
     * 
     * @return
     */
    public  Complex3x3 To120(){
    	return new Complex3x3( Tabc_120).multiply(this).multiply(new Complex3x3( T120_abc));
		
   	}
    
    /**
     * Transform this object from 012 to ABC
     * 
     * @return
     */
    public Complex3x3 ToAbc(){
    	return new Complex3x3(T120_abc).multiply(this).multiply(new Complex3x3(Tabc_120 ));
    }
    
    /**
     * ABC to 012 transformation, Zs = T^-1*Zp*T
     * 
     * @param abc
     * @return
     */
	public  static Complex3x3 abc_to_120(final Complex3x3 Zabc) {
		return new Complex3x3( Tabc_120).multiply(Zabc).multiply(new Complex3x3( T120_abc));
	}
	
	/**
	 * 012 to ABC transformation, Zs = T^-1*Zp*T
	 * 
	 * @param z12
	 * @return
	 */
	public static Complex3x3 z12_to_abc(final Complex3x3 Z012) {
		return new Complex3x3( T120_abc).multiply( Z012).multiply(new Complex3x3(Tabc_120 ));
	}
    
	/**
	 * Transform this object to a Complex[]3[3] matrix
	 * 
	 * @return
	 */
    public Complex[][] toComplex2DAry(){
    	Complex[][] cplx2D = new Complex[3][3];
    	
    	cplx2D[0][0] = this.aa;
    	cplx2D[0][1] = this.ab;
    	cplx2D[0][2] = this.ac;
    	
    	
    	cplx2D[1][0] = this.ba;
    	cplx2D[1][1] = this.bb;
    	cplx2D[1][2] = this.bc;
    	
    	
    	cplx2D[2][0] = this.ca;
    	cplx2D[2][1] = this.cb;
    	cplx2D[2][2] = this.cc;
    	
    	return cplx2D;
    }
    
    @Override
    public String toString(){
    	String s = "";
    	
    	s += "aa = "+this.aa.toString()+",";
    	s += "ab = "+this.ab.toString()+",";
    	s += "ac = "+this.ac.toString()+"\n";
    	
    	s += "ba = "+this.ba.toString()+",";
    	s += "bb = "+this.bb.toString()+",";
    	s += "bc = "+this.bc.toString()+"\n";
    	
    	s += "ca = "+this.ca.toString()+",";
    	s += "cb = "+this.cb.toString()+",";
    	s += "cc = "+this.cc.toString()+"\n";
    	
    	return s;
    }
}