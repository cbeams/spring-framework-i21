
package com.interface21.core;

/**
 * Interface that can be implemented by exceptions etc. that
 * are error coded. The error code is a String, rather than a number,
 * so it can be given user-readable values, such as "object.failureDescription".
 * These codes will be resolved by a com.interface21.context.MessageSource
 * object.
 * <br/>This interface is necessary because both runtime and checked
 * exceptions are useful, and they cannot share a common,
 * framework-specific, superclass.
 * @author  Rod Johnson
 * @version $Id$
 */
public interface ErrorCoded {
	
	/** Constant to indicate that this failure isn't coded */
	public static final String UNCODED = "uncoded";
	
	/** 
	 * Return the error code associated with this failure. 
	 * The GUI can render this anyway it pleases, allowing for Int8ln etc.
	 * @return a String error code associated with this failure
	 */
	String getErrorCode();

}

