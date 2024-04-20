/*
 * @(#)Complex3x1.java   
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
 * @Date 09/15/2006
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.datatype;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.NumericConstant;
import org.interpss.numeric.util.Number2String;

/**
 * Data structure class for manipulating 3x1 [a,b,c] or [0,1,2] complex number vector
 */
public class Complex3x1 implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	
	// index to transfer the object to a complex[]
	public final static int Index_0 = 0;
	public final static int Index_1 = 1;
	public final static int Index_2 = 2;
	
	public final static int Index_a = 0;
	public final static int Index_b = 1;
	public final static int Index_c = 2;

	public Complex 
		a_0 = new Complex(0.0, 0.0), 
		b_1 = new Complex(0.0, 0.0),
		c_2 = new Complex(0.0, 0.0);

	/**
	 * Default constructor.
	 *
	 */
	public Complex3x1() {
	}

	/**
	 * Constructor. this obj = [a0, b1, c2]
	 *
	 * @param a0 a complex number 
	 * @param b1 a complex number 
	 * @param c2 a complex number 
	 */
	public Complex3x1(final Complex a0, final Complex b1, final Complex c2) {
		a_0 = a0;
		b_1 = b1;
		c_2 = c2;
	}
	
	/**
	 * Constructor. this obj = [a0, b1, c2]
	 *
	 * @param a0 a complex number 
	 * @param b1 a complex number 
	 * @param c2 a complex number 
	 */
	public Complex3x1(final double a0, final double b1, final double c2) {
		a_0 = new Complex(a0,0);
		b_1 = new Complex(b1,0);
		c_2 = new Complex(c2,0);
	}

	/**
	 * Constructor. this obj = [a0, b1, c2]
	 *
	 * @param a0 a complex number 
	 * @param b1 a complex number 
	 * @param c2 a complex number 
	 */
	public Complex3x1(final Complex[] c) {
		a_0 = c[Index_0];
		b_1 = c[Index_1];
		c_2 = c[Index_2];
	}
	
	/**
	 * Copy Constructor
	 *
	 * @param c obj to be copied 
	 */
	public Complex3x1(final Complex3x1 c) {
		a_0 = c.a_0;
		b_1 = c.b_1;
		c_2 = c.c_2;
	}

	/**
	 * Transformation from abc to 012 system
	 *
	 * @param abc a 3x1 complex vector in abc system
	 * @return resulting 3x1 complex vector in 012 system
	 */
	public static Complex3x1 abc_to_z12(final Complex3x1 abc) {
		Complex z = abc.a_0.add(abc.b_1.add(abc.c_2)), p = abc.a_0.add(
				NumericConstant.a.multiply(abc.b_1)).add(
				NumericConstant.a2.multiply(abc.c_2)), n = abc.a_0.add(
				NumericConstant.a2.multiply(abc.b_1)).add(
				NumericConstant.a.multiply(abc.c_2));
		z = ComplexFunc.div(z, 3.0);
		p = ComplexFunc.div(p, 3.0);
		n = ComplexFunc.div(n, 3.0);
		return new Complex3x1(z, p, n);
	}
	
    /*
		Operator methods
     */

	public static Complex3x1 valueOf(Complex3x1 c) {
		return new Complex3x1(c.a_0, c.b_1, c.c_2);
	}

	// immutable
	public Complex3x1 negate() {
		return new Complex3x1(this.a_0.negate(), this.b_1.negate(), this.c_2.negate());
	}	
	
	// immutable
	public Complex3x1 clone(){
		return new Complex3x1(this.a_0,this.b_1,this.c_2);
	}
	
	/**
	 * convert the complex object to its conjugate
	 * 
	 * @return the converted object (a new Complex3x1 object)
	 */
	// immutable
	public Complex3x1 conjugate(){
		return new Complex3x1(this.a_0.conjugate(),this.b_1.conjugate(),this.c_2.conjugate());
	}
	
	// immutable
	public Complex[] toArray() {
		return new Complex[] { this.a_0, this.b_1, this.c_2};
	}	
	
	/*
		Math methods
	 */	
	
	/**
	 * add this object with a Complex3x1 x+y = [ x.a_0+y.a_0, x.b_1+y.b_1, x.c_2+y.c_2 ] 
	 * 
	 * @param y the add value
	 * @return the resulting object (a new Complex3x1 object)
	 */
	// immutable
	public Complex3x1 add(Complex3x1 y) {
		return new Complex3x1(this.a_0.add(y.a_0),this.b_1.add(y.b_1),this.c_2.add(y.c_2));
	}

	/**
	 * subtract this object with a Complex3x1 x - y = [ x.a_0-y.a_0, x.b_1-y.b_1, x.c_2-y.c_2 ] 
	 * 
	 * @param y the subtract value
	 * @return the resulting object (a new Complex3x1 object)
	 */
	// immutable
	public Complex3x1 subtract(Complex3x1 y) {
		return new Complex3x1(this.a_0.subtract(y.a_0),this.b_1.subtract(y.b_1),this.c_2.subtract(y.c_2));
	}

	/**
	 * multiply the object with a double value 
	 * 
	 * @param d the double value
	 * @return
	 */
	// immutable
	public Complex3x1 multiply(double d) {
		return new Complex3x1(this.a_0.multiply(d),this.b_1.multiply(d),this.c_2.multiply(d));
	}
	
	/**
	 * multiply the object with three double values, as follows 
	 * this.a_0.multiply(d0), this.b_1.multiply(d1), this.c_2.multiply(d2)
	 * 
	 * @param d the double value
	 * @return
	 */
	public Complex3x1 multiply(double d0, double d1,double d2) {
		return new Complex3x1(this.a_0.multiply(d0),this.b_1.multiply(d1),this.c_2.multiply(d2));
	}

	/**
	 * multiply the object with a complex3x1 object => [x.a*y.a, x.b*y.b, x.c*y.c]
	 * 
	 * @param y the complex3x1 value
	 * @return
	 *
	*/
	// immutable
	public Complex3x1 multiply(Complex3x1 y) {
		return new Complex3x1(this.a_0.multiply(y.a_0),this.b_1.multiply(y.b_1),this.c_2.multiply(y.c_2));
	}

	/**
	 * multiply the object with a complex3x1 object, and get the sum. i.e., x.a*y.a + x.b*y.b + x.c*y.c
	 * 
	 * @param y the complex value
	 * @return
	 *
	*/
	// immutable
	public Complex dotProduct(Complex3x1 value) {
		return this.a_0.multiply(value.a_0).add( this.b_1.multiply(value.b_1)).add( this.c_2.multiply(value.c_2));
	}

	/**
	 * devide this object with a Complex3x1 x/y = [ x.a_0/y.a_0, x.b_1/y.b_1, x.c_2/y.c_2 ] 
	 * 
	 * @param y the devider
	 * @return the resulting object (a new Complex3x1 object)
	 */
	// immutable
	public Complex3x1 divide(Complex3x1 y) {
		return new Complex3x1(this.a_0.divide(y.a_0),this.b_1.divide(y.b_1),this.c_2.divide(y.c_2));
	}
	
	/**
	 * get the absolute value of this object
	 * 
	 * @return the absolute value
	 */
	// immutable
	public double abs(){
		return this.a_0.abs()+this.b_1.abs()+this.c_2.abs();
	}

	/**
	 * get max[abs(a_0), abs(b_1), abs(c_2)]
	 * 
	 * @return the max value
	 */
	// immutable
	public double absMax(){
		double max =this.a_0.abs();
		if(this.b_1.abs()>max) max = this.b_1.abs();
		if(this.c_2.abs()>max) max = this.c_2.abs();
		
		return max;
	}

	/*
	 * 
	 */
	
	/**
	 * convert this object from abc to 012
	 * 
	 * @return the converted object
	 */
	public Complex3x1 to012(){
		Complex z = a_0.add(b_1.add(c_2)), 
				
				p = a_0.add(
				          NumericConstant.a.multiply(b_1)).add(
				          NumericConstant.a2.multiply(c_2)),
				
				n = a_0.add(
				            NumericConstant.a2.multiply(b_1)).add(
				            NumericConstant.a.multiply(c_2));
		z = ComplexFunc.div(z, 3.0);
		p = ComplexFunc.div(p, 3.0);
		n = ComplexFunc.div(n, 3.0);
		return new Complex3x1(z, p, n);
	}
	
	/**
	 * convert this object from 012 to abc
	 * 
	 * @return the converted object
	 */
	public Complex3x1 toABC(){
        Complex a = a_0.add(b_1.add(c_2)), 
				
				b = a_0.add(
				NumericConstant.a2.multiply(b_1)).add(
				NumericConstant.a.multiply(c_2)), 
				
				c = a_0.add(
				NumericConstant.a.multiply(b_1)).add(
				NumericConstant.a2.multiply(c_2));
        
		return new Complex3x1(a, b, c);
		
	}

	/**
	 * Transformation from 012 to abc system
	 *
	 * @param abc a 3x1 complex vector in 012 system
	 * @return resulting 3x1 complex vector in abc system
	 */
	public static Complex3x1 z12_to_abc(final Complex3x1 z12) {
		final Complex a = z12.a_0.add(z12.b_1.add(z12.c_2)), 
				
				b = z12.a_0.add(
				NumericConstant.a2.multiply(z12.b_1)).add(
				NumericConstant.a.multiply(z12.c_2)), 
				
				c = z12.a_0.add(
				NumericConstant.a.multiply(z12.b_1)).add(
				NumericConstant.a2.multiply(z12.c_2));
		return new Complex3x1(a, b, c);
	}

	/**
	 * Convert the obj to a string.
	 *
	 * @return the string representation of the obj
	 */
	@Override
	public String toString() {
		return Number2String.toStr(a_0) + "  "
				+ Number2String.toStr(b_1) + "  "
				+ Number2String.toStr(c_2);
	}
}