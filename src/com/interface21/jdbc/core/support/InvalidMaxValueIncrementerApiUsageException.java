/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core.support;

import com.interface21.dao.DataAccessException;

/**
 * Exception thrown on incorrect usage of the API, such as failing
 * to provide IncrementerName or ColumnName before usage.
 *
 * <p>This represents a problem in our Java data access framework,
 * not the underlying data access infrastructure.
 *
 * @author Thomas Risberg
 */
public class InvalidMaxValueIncrementerApiUsageException extends DataAccessException {

	/**
	 * Constructor for InvalidMaxValueIncrementerApiUsageException.
	 * @param msg message
	 */
	public InvalidMaxValueIncrementerApiUsageException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for InvalidMaxValueIncrementerApiUsageException.
	 * @param msg message
	 * @param ex root cause, from an underlying API such as JDBC
	 */
	public InvalidMaxValueIncrementerApiUsageException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
