package com.interface21.transaction.support;

import org.apache.log4j.Logger;

import com.interface21.transaction.CannotCreateTransactionException;
import com.interface21.transaction.NoTransactionException;
import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionStatus;

/**
 * Abstract class that allows for easy implementation of PlatformTransactionManager.
 * Provides the following case handling:
 * <br>- determines if there is an existing transaction;
 * <br>- applies the appropriate propagation behavior;
 * <br>- supports falling back to non-transaction execution;
 * <br>- determines programmatic rollback on commit;
 * <br>- applies the appropriate modification on rollback
 * (actual rollback or setting rollback only).
 *
 * @author Juergen Hoeller
 * @since 28.03.2003
 */
public abstract class AbstractPlatformTransactionManager implements PlatformTransactionManager {

	protected final Logger logger = Logger.getLogger(getClass());

	/**
	 * if transaction support needs to be available (else fallback behavior is enabled)
	 */
	private boolean allowNonTransactionalExecution = false;

	/**
	 * Set if transaction support needs to be available (else fallback behavior is enabled).
	 */
	public void setAllowNonTransactionalExecution(boolean allowNonTransactionalExecution) {
		this.allowNonTransactionalExecution = allowNonTransactionalExecution;
	}

	/**
	 * Return if transaction support needs to be available (else fallback behavior is enabled).
	 */
	public boolean getAllowNonTransactionalExecution() {
		return allowNonTransactionalExecution;
	}

	/**
	 * This implementation of getTransaction handles propagation behavior and
	 * checks non-transactional execution. Delegates to doGetTransaction,
	 * isExistingTransaction, doBegin.
	 */
	public final TransactionStatus getTransaction(int propagationBehavior, int isolationLevel) {
		try {
			Object transaction = doGetTransaction();
			if (isExistingTransaction(transaction)) {
				logger.debug("Taking part in existing transaction");
				return new TransactionStatus(transaction, false);
			}
			if (propagationBehavior == PROPAGATION_MANDATORY) {
				throw new NoTransactionException("no existing transaction context");
			}
			if (propagationBehavior == PROPAGATION_REQUIRED) {
				// create new transaction
				doBegin(transaction, isolationLevel);
				return new TransactionStatus(transaction, true);
			}
		} catch (CannotCreateTransactionException ex) {
			// throw exception if transactional execution required
			if (!this.allowNonTransactionalExecution)
				throw ex;
			// else non-transactional execution
			logger.info("Transaction support is not available: falling back to non-transactional execution");
		}
		// empty (-> "no") transaction
		return new TransactionStatus(null, false);
	}

	/**
	 * This implementation of commit handles programmatic rollback requests,
	 * i.e. status.isRollbackOnly(), and non-transactional execution.
	 * Delegates to doCommit and rollback.
	 */
	public final void commit(TransactionStatus status) {
		if (status.isRollbackOnly()) {
			logger.debug("Transactional code has requested rollback");
			rollback(status);
		}
		else if (status.isNewTransaction()) {
			doCommit(status);
		}
	}

	/**
	 * This implementation of rollback handles taking part in existing transactions
	 * and non-transactional execution. Delegates to doRollback and doSetRollbackOnly.
	 */
	public final void rollback(TransactionStatus status) {
		if (status.isNewTransaction()) {
			doRollback(status);
		} else if (status.getTransaction() != null) {
			doSetRollbackOnly(status);
		} else {
			// no transaction support available
			logger.info("Should roll back transaction but cannot - no transaction support available");
		}
	}

	/**
	 * Return a current transaction object, i.e. a JTA UserTransaction.
	 */
	protected abstract Object doGetTransaction();

	/**
	 * Check if the given transaction object indicates an existing,
	 * i.e. already begun, transaction.
	 * @param transaction  the transaction object returned by doGetTransaction()
	 * @return if there is an existing transaction
	 */
	protected abstract boolean isExistingTransaction(Object transaction);

	/**
	 * Begin a new transaction with the given isolation level
	 * @param transaction  the transaction object returned by doGetTransaction()
	 * @param isolationLevel  the desired isolation level
	 */
	protected abstract void doBegin(Object transaction, int isolationLevel);

	/**
	 * Perform an actual commit on the given transaction.
	 * An implementation does not need to check the rollback-only flag.
	 * @param status  the status representation of the transaction
	 */
	protected abstract void doCommit(TransactionStatus status);

	/**
	 * Perform an actual rollback on the given transaction.
	 * An implementation does not need to check the new transaction flag.
	 * @param status  the status representation of the transaction
	 */
	protected abstract void doRollback(TransactionStatus status);

	/**
	 * Set the given transaction rollback-only.
	 * @param status  the status representation of the transaction
	 */
	protected abstract void doSetRollbackOnly(TransactionStatus status);
}

