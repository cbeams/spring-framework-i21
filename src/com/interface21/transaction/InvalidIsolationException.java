package com.interface21.transaction;

/**
 * Exception that gets thrown when an invalid isolation level is specified,
 * i.e. an isolation level that the transaction manager implementation
 * doesn't support.
 *
 * @author Juergen Hoeller
 * @since 12.05.2003
 */
public class InvalidIsolationException extends TransactionUsageException {

	public InvalidIsolationException(String s) {
		super(s);
	}

}
