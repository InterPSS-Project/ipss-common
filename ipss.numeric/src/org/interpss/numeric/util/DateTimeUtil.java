/*
 * @(#)NumericUtil.java   
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
 * @Date 12/10/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date time utility funcitons
 * 
 * @author mzhou
 *
 */
public class DateTimeUtil {
    static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	
	/**
	 * check if the date is within
	 * 
	 * @param date
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean within(Date date, Date begin, Date end) {
		return date.after(begin) && date.before(end);
	}

	/**
	 * check if the date is within
	 * 
	 * @param date
	 * @param beginStr format "8/18/2007"
	 * @param endStr   format "8/18/2007"
	 * @return
	 * @throws ParseException 
	 */
	public static boolean within(Date date, String beginStr, String endStr) throws ParseException {
		return within(date, df.parse(beginStr), df.parse(endStr));
	}

	/**
	 * check if the date is within
	 * 
	 * @param dateStr
	 * @param beginStr format "8/18/2007"
	 * @param endStr   format "8/18/2007"
	 * @return
	 * @throws ParseException 
	 */
	public static boolean within(String dateStr, String beginStr, String endStr) throws ParseException {
		return within(df.parse(dateStr), df.parse(beginStr), df.parse(endStr));
	}
	
	/**
	 * format date to the format "8/18/2011"
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return df.format(date);
	}	
}
