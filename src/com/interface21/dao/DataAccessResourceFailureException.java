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
 * Data access exception thrown when a resource fails
 * completely: for example, if we can't connect to a database
 * using JDBC.
 * @author Rod Johnson
 * @version $Id$
 */
public class DataAccessResourceFailureException extends DataAccessException {

	/**
	 * Constructor for ResourceFailureDataAccessException.
	 * @param msg message
	 * @param ex root cause from data access API in use
	 */
	public DataAccessResourceFailureException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
