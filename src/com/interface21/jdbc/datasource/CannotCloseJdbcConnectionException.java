/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.datasource;

import com.interface21.dao.CleanupFailureDataAccessException;

/**
 * Exception thrown when we successfully executed a SQL
 * statement, but then failed to close the JDBC connection.
 * This results in a java.sql.SQLException, but application code
 * can choose to catch the exception to avoid the transaction being
 * rolled back.
 * @author Rod Johnson
 */
public class CannotCloseJdbcConnectionException extends CleanupFailureDataAccessException {

	/**
	 * Constructor for CannotCloseJdbcConnectionException.
	 * @param s message
	 * @param ex root cause
	 */
	public CannotCloseJdbcConnectionException(String s, Throwable ex) {
		super(s, ex);
	}
	
}
