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

import com.interface21.dao.DataAccessException;

/**
 * Interface to be implemented by classes that can translate
 * between SQLExceptions and our data access strategy-agnostic
 * com.interface21.dao.DataAccessException.
 * <br/>Implementations can be generic (for example,
 * using SQLState codes for JDBC) or proprietary (for example,
 * using Oracle error codes) for greater precision.
 * @author Rod Johnson
 * @see com.interface21.dao.DataAccessException
 * @version $Id$
 */
public interface SQLExceptionTranslater {

	/** 
	 * Translate the given SQL exception into a generic
	 * data access exception.
	 * @param task readable text describing the task being attempted
	 * @param sql SQL query or update that caused the problem.
	 * May be null.
	 * @param sqlex SQLException encountered by JDBC implementation
	 */
	DataAccessException translate(String task, String sql, SQLException sqlex);
}
