/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.dao;

/**
 * Generic exception thrown when the current process was
 * a deadlock loser, and its transaction rolled back.
 * @author Rod Johnson
 * @version $Id$
 */
public class DeadlockLoserDataAccessException extends DataAccessException {

	/**
	 * Constructor for DeadlockLoserDataAccessException.
	 * @param s mesg
	 * @param ex root cause
	 */
	public DeadlockLoserDataAccessException(String s, Throwable ex) {
		super(s, ex);
	}

}
