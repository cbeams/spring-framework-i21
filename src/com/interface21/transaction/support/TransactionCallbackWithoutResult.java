package com.interface21.transaction.support;

import com.interface21.transaction.TransactionStatus;

/**
 * Simple convenience class for TransactionCallback implementation.
 * Allows for implementing a doInTransaction version without result,
 * i.e. without the need for a return statement.
 *
 * @author Juergen Hoeller
 * @since 28.03.2003
 */
public abstract class TransactionCallbackWithoutResult implements TransactionCallback {

	public final Object doInTransaction(TransactionStatus status) throws RuntimeException {
		doInTransactionWithoutResult(status);
		return null;
	}

	/**
	 * Gets called by TransactionTemplate.execute within a transactional context.
	 * Does not need to care about transactions itself, although it can retrieve
	 * and influence the status of the current transaction via the given status
	 * object, e.g. setting rollback-only.
	 *
	 * Note when using JTA: JTA transactions only work with transactional
	 * JNDI resources, so implementations need to use such resources if
	 * they want transaction support.
	 *
	 * @param status  the associated transaction status
	 * @throws java.lang.RuntimeException if the transaction needs to be rolled back
	 */
	protected abstract void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException;
}
