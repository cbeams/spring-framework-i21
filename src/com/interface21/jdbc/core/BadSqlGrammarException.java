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

import com.interface21.dao.InvalidDataAccessResourceUsageException;

/**
 * Exception thrown when SQL specified is invalid. Such exceptions
 * always have a java.sql.SQLException root cause.
 * <br>It would be possible to have subclasses for no such table, no such column etc.
 * A custom SQLExceptionTranslater could create such
 * more specific exceptions, without affecting code using this class.
 * @version $Id$
 */
public class BadSqlGrammarException extends InvalidDataAccessResourceUsageException {
	
	/** Root cause: underlying JDBC exception. */ 
	private final SQLException ex;
	
	/** The offending SQL. */
	private final String sql;

	/**
	 * Constructor for BadSqlGrammerException.
	 * @param mesg detailed message
	 * @param sql the offending SQL statement
	 * @param ex the root cause
	 */
	public BadSqlGrammarException(String mesg, String sql, SQLException ex) {
		super("Bad SQL grammar [" + sql + "]", ex);
		this.ex = ex;
		this.sql = sql;
	}
	
	/**
	 * Return the wrapped SQLException.
	 * @return the wrapped SQLException
	 */
	public SQLException getSQLException() {
		return ex;
	}
	
	/**
	 * Return the SQL that caused the problem.
	 * @return the offdending SQL
	 */
	public String getSql() {
		return sql;
	}

}
