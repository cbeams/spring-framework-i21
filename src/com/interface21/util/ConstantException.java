/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.util;

/**
 * Exception thrown when the Constants class is asked for an invalid
 * constant name.
 * @see com.interface21.util.Constants
 * @version $Id$
 * @author Rod Johnson
 * @since 28-Apr-2003
 */
public class ConstantException extends IllegalArgumentException {
	
	/**
	 * Thrown when an invalid constant name is requested
	 * @param field invalid constant name
	 * @param clazz class containing the constant definitions
	 * @param message description of the problem
	 */
	public ConstantException(String field, Class clazz, String message) {
		super("Field '" + field + "' " + message + " in " + clazz);
	}

}
