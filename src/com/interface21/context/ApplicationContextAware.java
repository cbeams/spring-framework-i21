/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.context;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the application context it runs in.
 * @author Rod Johnson
 */
public interface ApplicationContextAware {
	
	/** 
	 * Set the application context used by this object.
	 * Normally this call will be used to initialize the object.
	 * <p>Note that this call can occur multiple times: The implementation
	 * must check itself that if it is already initialized resp. if it
	 * wants to perform reinitialization.
	 * @param ctx ApplicationContext object to be used by this object
	 * @throws ApplicationContextException if initialization attempted
	 * by this object fails
	 */
	void setApplicationContext(ApplicationContext ctx) throws ApplicationContextException;

}
