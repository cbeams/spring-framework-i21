package com.interface21.transaction;

/**
 * Exception that gets thrown when an invalid timeout is specified,
 * i.e. the transaction manager implementation doesn't support timeouts.
 * @author Juergen Hoeller
 * @since 12.05.2003
 */
public class InvalidTimeoutException extends TransactionUsageException {

	private int timeout = TransactionDefinition.TIMEOUT_DEFAULT;

	public InvalidTimeoutException(String msg, int timeout) {
		super(msg);
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

}
