package com.interface21.core;

/**
 * InternalErrorException denotes an internal error in the framework.
 * Assertions are useful while debugging/testing the code, but a
 * different mechanism is needed to catch internal framework errors in
 * production.
 * @author Isabelle Muszynski
 * @since 5 April 2003
 */

public class InternalErrorException extends NestedRuntimeException {

	/**
	 * Default constructor
	 **/
	public InternalErrorException() {
		super("Internal error");
	}

	/**
	 * Constructor
	 * @param msg the exception message
	 **/
	public InternalErrorException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param msg the exception message
	 * @param ex the nested exception
	 **/
	public InternalErrorException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
