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
 * Exception thrown when we couldn't cleanup after a data
 * access operation, but the actual operation went OK.
 * For example, this exception or a subclass might be thrown if a JDBC Connection
 * couldn't be closed after it had been used successfully.
 * @author Rod Johnson
 */
public class CleanupFailureDataAccessException extends DataAccessException {

	/**
	 * Constructor for CleanupFailureDataAccessException.
	 * @param s Message
	 * @param ex Root cause from the underlying data access API,
	 * such as JDBC
	 */
	public CleanupFailureDataAccessException(String s, Throwable ex) {
		super(s, ex);
	}

}
