/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */


package com.interface21.context;

import java.util.EventListener;

/**
 * Interface to be implemented by event listeners.
 * Based on standard java.util base class for Observer
 * design pattern.
 * @author  Rod Johnson
 */
public interface ApplicationListener extends EventListener {

	/**
	* Handle an application event
	* @param e event to respond to
	*/
    void onApplicationEvent(ApplicationEvent e);

}

