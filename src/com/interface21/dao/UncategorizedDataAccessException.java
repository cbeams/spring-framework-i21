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
 * Normal superclass when we can't distinguish anything
 * more specific than "something went wrong with the
 * underlying resource": for example, a SQLException from JDBC we
 * can't pinpoint more precisely.
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class UncategorizedDataAccessException extends DataAccessException {

	/**
	 * Constructor for UncategorizedDataAccessException.
	 * @param msg description of failure
	 * @param ex exception thrown by underlying data access API
	 */
	public UncategorizedDataAccessException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
