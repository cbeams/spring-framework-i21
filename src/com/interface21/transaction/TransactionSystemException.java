package com.interface21.transaction;

/**
 * @author jho
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
