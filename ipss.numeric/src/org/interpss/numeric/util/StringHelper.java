 /*
  * @(#) StringHelper.java   
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
  * @Date 06/04/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.numeric.util;

/**
 * String utility functions
 * 
 * @author mzhou
 *
 */
public class StringHelper {
	/**
	 * convert an Object[] to a String[]
	 * 
	 * @param oAry
	 * @return
	 */
	public static String[] toStrArray(Object[] oAry) {
		String[] sAry = new String[oAry.length];
		int cnt = 0;
		for (Object o : oAry)
			sAry[cnt++] = (String)o;
		return sAry;
	}
}
