/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.sql.SQLException;

import com.interface21.dao.UncategorizedDataAccessException;

/**
 * Exception thrown when we can't classify a SQLException into 
 * one of our generic data access exceptions
 * @see com.interface21.dao
 * @author Rod Johnson
 * @version $Id$
 */
public class UncategorizedSQLException extends UncategorizedDataAccessException {
	
	/** SQL that led to the problem */
	private final String sql;

	/**
	 * Constructor for ConnectionFactoryException.
	 * @param mesg message
	 * @param sql SQL we were tring to execute
	 * @param ex SQLException
	 */
	public UncategorizedSQLException(String mesg, String sql, SQLException ex) {
		super(mesg, ex);
		this.sql = sql;
	}
	
	/**
	 * Return the underlying SQLException
	 * @return the underlying SQLException
	 */
	public SQLException getSQLException() {
		return (SQLException) getRootCause();
	}
	
	/**
	 * Return the SQL that led to the problem
	 * @return the SQL that led to the problem
	 */
	public String getSql() {
		return sql;
	}

}
