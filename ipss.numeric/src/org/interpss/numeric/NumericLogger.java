/*
 * @(#)NumericLogger.java   
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
 * @Date 01/02/2011
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * InterPSS numeric logger
 * 
 * @author mzhou
 *
 */
public class NumericLogger {
	private static Logger logger = null;
	
	/**
	 * constructor
	 * 
	 * @return
	 */
	public static Logger getLogger() { 
		if (logger == null) {
			logger = Logger.getLogger("ipss.numeric");
			logger.setLevel(Level.WARNING);
		}
		return logger; 
	}
}
