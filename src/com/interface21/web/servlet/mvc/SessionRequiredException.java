/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet.mvc;

import javax.servlet.ServletException;


/**
 * Exception thrown when a Controller requires a session for the
 * current method. This exception is normally raised by framework
 * code, but may sometimes be handled by application code.
 * @author Rod Johnson
 */
public class SessionRequiredException extends ServletException {

	/**
	 * Constructor for SessionRequiredException.
	 * @param mesg message
	 */
	public SessionRequiredException(String mesg) {
		super(mesg);
	}

	/**
	 * Constructor for SessionRequiredException.
	 * @param mesg message
	 * @param t root cause
	 */
	public SessionRequiredException(String mesg, Throwable t) {
		super(mesg, t);
	}
}
