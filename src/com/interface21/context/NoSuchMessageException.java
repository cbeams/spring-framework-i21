package com.interface21.context;

/** 
 * Exception thrown when a message can't be resolved
 * @author Rod Johnson
 */
public class NoSuchMessageException extends Exception {
	
	/**
	 * Create a new exception.
	 * @param code code that could not be resolved.
	 */
	public NoSuchMessageException(String code) {
		super("No message found under code '" + code + "'");
	}

}

