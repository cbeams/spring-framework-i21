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
 * Root for exceptions thrown when we use a data access
 * resource incorrectly. Thrown for example on specifying bad SQL
 * when using a RDBMS.
 * Resource-specific subclasses will probably be supplied by
 * data access packages.
 * @author Rod Johnson
 * @version $Id$
 */
public class InvalidDataAccessResourceUsageException extends DataAccessException {
	
	/**
	 * Constructor for InvalidDataAccessResourceUsageException.
	 * @param msg message
	 */
	public InvalidDataAccessResourceUsageException(String msg) {
		super(msg);
	}
	/**
	 * Constructor for InvalidDataAccessResourceUsageException.
	 * @param msg message
	 * @param ex root cause
	 */
	public InvalidDataAccessResourceUsageException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
