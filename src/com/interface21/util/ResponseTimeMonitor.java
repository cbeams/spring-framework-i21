

package com.interface21.util;

/**
 * Interface implemented by objects that
 * can provide performance information as well as a record of the number
 * of times they are accessed.
 * <br/>Implementing objects must ensure that implementing this interface
 * does <b>not</b> compromise thread safety. However, it may be acceptable
 * for slight innaccuracies in reported statistics to result from the
 * avoidance of synchronization: performance may be well be more important
 * than exact reporting, so long as the errors are not likely to be misleading.
 * @author Rod Johnson
 * @since November 21, 2000
 */
public interface ResponseTimeMonitor {
	
	/** Return the number of accesses to this resource
	 * @return the number of accesses to this resource
	 */
    int getAccessCount();
    
    /** Return the average response time in milliseconds
     * @return the average response time in milliseconds
     */
    int getAverageResponseTimeMillis();
    
    /** Return the best (quickest) response time in milliseconds
     * @return the best (quickest) response time in milliseconds
     */
    int getBestResponseTimeMillis();
    
    /** Return the worst (slowest) response time in milliseconds
     * @return the worst (slowest) response time in milliseconds
     */
    int getWorstResponseTimeMillis();

}
