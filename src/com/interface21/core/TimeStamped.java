
package com.interface21.core;

/**
 * This interface can be implemented by cacheable objects
 * or cache entries, to enable the freshness of objects
 * to be checked.
 * @author Rod Johnson
 */
public interface TimeStamped {
	
	/**
	 * Return the timestamp for this object
	 * @return long the timestamp for this object,
	 * as returned by System.currentTimeMillis()
	 */
	long getTimeStamp();

}
