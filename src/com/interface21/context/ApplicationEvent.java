/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.context;

import java.util.EventObject;

/**
 * Class to be extended by all application events.
 * Abstract as it doesn't make sense for generic events
 * to be published directly.
 * @author  Rod Johnson
 */
public abstract class ApplicationEvent extends EventObject {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** System time when the event happened */
	private long	timestamp;

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
    /** Creates a new ApplicationEvent
     * @param source component that published the event
     */
	public ApplicationEvent(Object source) {
        super(source);
		timestamp = System.currentTimeMillis();
	}

	//---------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------
	/** Return the system time in milliseconds when the event happened
	 * @return the system time in milliseconds when the event happened
	 */
	public long getTimestamp() {
		return timestamp;
	}

}	// class ApplicationEvent
