package com.interface21.transaction.support;

import org.apache.log4j.Logger;

import com.interface21.transaction.CannotCreateTransactionException;
import com.interface21.transaction.NoTransactionException;
import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionStatus;

/**
 * Abstract class that allows for easy implementation of PlatformTransactionManagers.
 * Provides the following case handling:
 * - determines if there is an existing transaction;
 * - applies the appropriate propagation behavior;
 * - supports falling back to non-transaction execution;
 * - determines programmatic rollback on commit;
 * - applies the appropriate modification on rollback
 *   (actual rollback or setting rollback only).
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

	public void setAllowNonTransactionalExecution(boolean allowNonTransactionalExecution) {
		this.allowNonTransactionalExecution = allowNonTransactionalExecution;
	}

	public boolean getAllowNonTransactionalExecution() {
		return allowNonTransactionalExecution;
	}

	public TransactionStatus getTransaction(int propagationBehavior, int isolationLevel) {
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

	public void commit(TransactionStatus status) {
		if (status.isRollbackOnly()) {
			logger.debug("Transactional code has requested rollback");
			rollback(status);
		}
		else if (status.isNewTransaction()) {
			doCommit(status);
		}
	}

	public void rollback(TransactionStatus status) {
		if (status.isNewTransaction()) {
			doRollback(status);
		} else if (status.getTransaction() != null) {
			doSetRollbackOnly(status);
		} else {
			// no transaction support available
			logger.info("Should roll back transaction but cannot - no transaction support available");
		}
	}

	protected abstract Object doGetTransaction();

	protected abstract boolean isExistingTransaction(Object transaction);

	protected abstract void doBegin(Object transaction, int isolationLevel);

	protected abstract void doCommit(TransactionStatus status);

	protected abstract void doRollback(TransactionStatus status);

	protected abstract void doSetRollbackOnly(TransactionStatus status);
}

