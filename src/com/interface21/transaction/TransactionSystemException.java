package com.interface21.transaction;

/**
 * Exception thrown when a general transaction system error is encountered,
 * like on commit or rollback.
 * @author Juergen Hoeller
 * @since 24.03.2003
 */
public class TransactionSystemException extends TransactionException {

	public TransactionSystemException(String msg) {
		super(msg);
	}

	public TransactionSystemException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
