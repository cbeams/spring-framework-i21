
package com.interface21.util;

import java.util.Date;

/** 
 * Implementation of ResponseTimeMonitor for use via delegation by
 * objects that implement this interface.
 * <br>Uses no synchronization, so is suitable for use in a web application.
 * @author Rod Johnson
 * @since November 21, 2000
 */
public class ResponseTimeMonitorImpl implements ResponseTimeMonitor {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/**
	 * The number of operations recorded by this object.
	 */
	private int      accessCount;	
	
	/** The system time at which this object was initialized. */
    private long     initedMillis;
    
    /** The sum of the response times for all operations. */
    private int      totalResponseTimeMillis = 0;
    
    /** The best response time this object has recorded. */
    private int      bestResponseTimeMillis = Integer.MAX_VALUE;
    
    /** The worst response time this object has recorded. */
    private int      worstResponseTimeMillis = Integer.MIN_VALUE;
    

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/** 
	 * Creates a new ResponseTimeMonitorImpl.
     */
	public ResponseTimeMonitorImpl() {        
		initedMillis = System.currentTimeMillis();
	}
    
   
    //---------------------------------------------------------------------
	// Methods from ResponseTimeMonitor interface
	//---------------------------------------------------------------------
    /** 
     * Return the number of hits this object has handled
     * @return the number of hits this object has handled
     */
    public final int getAccessCount() {
        return accessCount;
    }

    /** 
     * Return the number of milliseconds since this object was loaded
     * @return the number of milliseconds since this object was loaded
     */
    public final long getUptime() {
        return System.currentTimeMillis() - initedMillis;
    }

    /** 
     * Return the date when this object was loaded
     * @return the date when this object was loaded
     */
    public final Date getLoadDate() {
        return new Date(initedMillis);
    }   

    
    /** 
     * Return the average response time achieved by this object
     * @return the average response time achieved by this object
     */
    public final int getAverageResponseTimeMillis() {
    	// Avoid division by 0
    	if (getAccessCount() == 0)
    		return 0;
        return totalResponseTimeMillis / getAccessCount();
    }
       
    
    /** 
     * Return the best (lowest) response time achieved by this object
     * @return the best (lowest) response time achieved by this object
     */
    public final int getBestResponseTimeMillis() {
        return bestResponseTimeMillis;
    }
    
    /** 
     * Return the worst (slowest) response time achieved by this object
     * @return  the worst (slowest) response time achieved by this object
     */
    public final int getWorstResponseTimeMillis() {
        return worstResponseTimeMillis;
    }
	
	 //---------------------------------------------------------------------
	// Public methods enabling the recording of performance information
	//---------------------------------------------------------------------   
	/** 
	 * Utility method to record this response time, updating
	 * the best and worst response times if necessary
	 * @param responseTime the response time of this request
	 */
	public final void recordResponseTime(long responseTime) {
		++accessCount;
		int iResponseTime = (int) responseTime;
		totalResponseTimeMillis += iResponseTime;
		if (iResponseTime < bestResponseTimeMillis)
			bestResponseTimeMillis = iResponseTime;
		if (iResponseTime > worstResponseTimeMillis)
			worstResponseTimeMillis = iResponseTime;
    }  
	
	/**
	 * @return a human-readable string showing the performance
	 * data recorded by this object.
	 */
	public String toString() {
		return "hits=" + getAccessCount() + "; avg=" + getAverageResponseTimeMillis() + "; best=" + getBestResponseTimeMillis() + "; worst=" + getWorstResponseTimeMillis();
	}
    
}	// class ResponseTimeMonitorImpl
