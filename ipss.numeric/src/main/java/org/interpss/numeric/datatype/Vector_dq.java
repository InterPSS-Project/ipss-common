/*
 * @(#)Vector_dq.java   
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

/**
 * Data structure class for manipulating 2x1 [d,q] vector
 * 
 *
 * @author  Mike Zhou
 * @version 1.00 06/01/05
 */
public class Vector_dq  implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	
	public double d = 0.0, q = 0.0;

  /**
   * Default constructor.
	*
   */
	public Vector_dq()	 {
	}

  /**
   * Constructor.
	*
	* @param a set obj.x=a and obj.y=a
   */
	public Vector_dq( double a )	{
		d = a;	q = a;
	}

  /**
   * Constructor.
	*
	* @param a set obj.d=a
	* @param b set obj.q=b
   */
	public Vector_dq( double a, double b ) 	{
		d = a;	q = b;
	}

  /**
   * Constructor.
	*
	* @param c set obj.x=c.re and obj.y=c.im
   */
	public Vector_dq( Complex c ) {
		d = c.getReal();	q = c.getImaginary();
	}

	/**
	 * Return the magnitude of the vector
	 * 
	 * @return the magnitude
	 */
	public double abs() {
		return Math.sqrt(this.d*this.d + this.q*this.q);
	}
	
  /**
   * Convert the obj to a string.
	*
	* @return the string representation of the obj
   */
	@Override public String toString()	{
		return "(" + String.valueOf(d) + " + j" +
			   String.valueOf(q) + ")\n";
	}
}