/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.dao;

import com.interface21.core.NestedRuntimeException;

/**
 * Root of the hierarchy of data access exceptions discussed in
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>.
 * Please see Chapter 9 of this book for detailed discussion of the
 * motivation for this package.
 *
 * <p>This exception hierarchy aims to let user code find and handle the
 * kind of error encountered without knowing the details of the particular
 * data access API in use (e.g. JDBC). Thus it is possible to react to an
 * optimistic locking failure without knowing that JDBC is being used.
 *
 * <p>As this class is a runtime exception, there is no need for user code
 * to catch it or subclasses if any error is to be considered fatal
 * (the usual case).
 *
 * @author Rod Johnson
 */
public abstract class DataAccessException extends NestedRuntimeException {

	/**
	 * Constructor for DataAccessException.
	 * @param msg message
	 */
	public DataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataAccessException.
	 * @param msg message
	 * @param ex root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public DataAccessException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
