package com.interface21.transaction;

/**
 * Exception thrown when a general transaction system error is encountered.
 * @author Juergen Hoeller
 * @since 24.03.2003
 */
public class TransactionSystemException extends TransactionException {

	public TransactionSystemException(String s) {
		super(s);
	}

	public TransactionSystemException(String s, Throwable ex) {
		super(s, ex);
	}
}
