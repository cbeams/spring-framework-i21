
package com.interface21.jdbc.core;

import java.sql.SQLWarning;

import com.interface21.dao.UncategorizedDataAccessException;

/**
 * Exception thrown when we're not ignoring warnings.
 * If such an exception is thrown, the operation completed,
 * so we will need to explicitly roll it back if we're not happy
 * on looking at the warning. We might choose to ignore (or merely log)
 * the warning and throw the exception away.
 * @author Rod Johnson
 */
public class SQLWarningException extends UncategorizedDataAccessException {

	/**
	 * Constructor for ConnectionFactoryException.
	 * @param s message
	 * @param ex JDBC warning
	 */
	public SQLWarningException(String mesg, SQLWarning ex) {
		super(mesg, ex);
	}
	
	/**
	 * Return the SQLWarning.
	 * @return the SQLWarning
	 */
	public SQLWarning SQLWarning() {
		return (SQLWarning) getRootCause();
	}


}
