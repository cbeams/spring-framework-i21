/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction;

/**
 * Exception thrown when an operation is attempted that
 * relies on an existing transaction (such as setting
 * rollback status) and there is no existing transaction.
 * This represents an illegal usage of the transaction API.
 * @author Rod Johnson
 * @since 17-Mar-2003
 * @version $Revision$
 */
public class NoTransactionException extends TransactionUsageException {

	public NoTransactionException(String msg) {
		super(msg);
	}

	public NoTransactionException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
