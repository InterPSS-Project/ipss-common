 /*
  * @(#)PerformanceTimer.java   
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

import java.util.logging.Logger;

/**
 * A utility class to measure time
 */
public class PerformanceTimer {
    private long starttime = 0;
    private long endtime = 0;
    private Logger logger = null;

    /**
     * constructor
     * 
     * @param log
     */
    public PerformanceTimer(Logger log) {
    	this();
    	this.logger = log;
    }

    /**
     * constructor
     */
    public PerformanceTimer() {
    	this.start();
    }

    /**
     * start the timer for the measurement
     */
    public void start() {
        this.starttime = System.currentTimeMillis() ;
    }
    
    /**
     * end the timer for the measurement
     * 
     * @return
     */
    public long end() {
        this.endtime = System.currentTimeMillis() ;
        return endtime - starttime;
    }
    
    /**
     * set the duration
     * 
     * @return
     */
    public long getDuration() {
        return endtime - starttime;
    }
    
    /**
     * Log the duration to log file and print on the Console
     * 
     * @param str log message
     */
    public String log(String str) {
    	end();
        String s = str + " (sec) = " + getDuration()/1000.0;
        if (this.logger != null)
        	this.logger.info(s);
        else
            System.out.println(s);
        return s;
    }

    /**
     * log the time duration to the std output
     * 
     * @param str
     */
    public void logStd(String str) {
    	end();
        System.out.println(str + " (sec) = " + getDuration()/1000.0 );
    }
    
    /**
     * convert the duration to a string
     * 
     * @param str
     * @return
     */
    public String toString(String str) {
    	end();
        return str + " (sec) = " + getDuration()/1000.0;
    }
}
