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
 * Exception thrown when an attempt to insert or update data
 * results in violation of an integrity constraint. Note that this
 * is not purely a relational concept; unique primary keys are
 * required by most database types.
 * @author Rod Johnson
 * @version $Id$
 */
public class DataIntegrityViolationException extends DataAccessException {

	/**
	 * Constructor for DataIntegrityViolationException.
	 * @param msg mesg
	 * @param ex root cause
	 */
	public DataIntegrityViolationException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
