package com.interface21.transaction.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.transaction.CannotCreateTransactionException;
import com.interface21.transaction.NoTransactionException;
import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionException;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.UnexpectedRollbackException;

/**
 * Abstract class that allows for easy implementation of PlatformTransactionManager.
 * Provides the following case handling:
 * <ul>
 * <li>determines if there is an existing transaction;
 * <li>applies the appropriate propagation behavior;
 * <li>supports falling back to non-transactional execution
 * (if allowNonTransactionExecution is set);
 * <li>determines programmatic rollback on commit;
 * <li>applies the appropriate modification on rollback
 * (actual rollback or setting rollback only);
 * <li>triggers registered synchronization callbacks
 * (if transactionSynchronization is active).
 * </ul>
 *
 * @author Juergen Hoeller
 * @since 28.03.2003
 * @see #setAllowNonTransactionalExecution
 * @see #setTransactionSynchronization
 */
public abstract class AbstractPlatformTransactionManager implements PlatformTransactionManager {

	protected final Log logger = LogFactory.getLog(getClass());

	private boolean allowNonTransactionalExecution = false;

	private boolean transactionSynchronization = false;


	/**
	 * Set if transaction support does not need to be available,
	 * e.g. when JTA isn't available in the container.
	 * Non-transactional fallback behavior is enabled in this case.
	 */
	public final void setAllowNonTransactionalExecution(boolean allowNonTransactionalExecution) {
		this.allowNonTransactionalExecution = allowNonTransactionalExecution;
	}

	/**
	 * Return if transaction support does not need to be available.
	 */
	public final boolean getAllowNonTransactionalExecution() {
		return allowNonTransactionalExecution;
	}

	/**
	 * Set if this transaction manager should activate the thread-bound
	 * transaction synchronization support. The default can very between
	 * transaction manager implementations, this class specifies false.
	 * <p>Note that transaction synchronization isn't supported for
	 * multiple concurrent transactions. Only one transaction manager
	 * is allowed to activate it at any time.
	 * @see TransactionSynchronizationManager
	 */
	public final void setTransactionSynchronization(boolean transactionSynchronization) {
		this.transactionSynchronization = transactionSynchronization;
	}

	/**
	 * Return if this transaction manager should activate the thread-bound
	 * transaction synchronization support.
	 */
	public final boolean getTransactionSynchronization() {
		return transactionSynchronization;
	}


	/**
	 * This implementation of getTransaction handles propagation behavior and
	 * checks non-transactional execution (on CannotCreateTransactionException).
	 * Delegates to doGetTransaction, isExistingTransaction, doBegin.
	 */
	public final TransactionStatus getTransaction(TransactionDefinition definition)
	    throws TransactionException {
		try {
			Object transaction = doGetTransaction();
			logger.debug("Using transaction object [" + transaction + "]");
			if (isExistingTransaction(transaction)) {
				logger.debug("Participating in existing transaction");
				return new TransactionStatus(transaction, false);
			}
			if (definition == null) {
				// use defaults
				definition = new DefaultTransactionDefinition();
			}
			if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
				throw new NoTransactionException("Transaction propagation mandatory but no existing transaction context");
			}
			if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED) {
				// create new transaction
				doBegin(transaction, definition.getIsolationLevel(), definition.getTimeout());
				if (this.transactionSynchronization) {
					TransactionSynchronizationManager.init();
				}
				return new TransactionStatus(transaction, true);
			}
		}
		catch (CannotCreateTransactionException ex) {
			// throw exception if transactional execution required
			if (!this.allowNonTransactionalExecution) {
				logger.error(ex.getMessage());
				throw ex;
			}
			// else non-transactional execution
			logger.warn("Transaction support is not available: falling back to non-transactional execution", ex);
		}
		catch (TransactionException ex) {
			logger.error(ex.getMessage());
			throw ex;
		}
		// empty (-> "no") transaction
		return new TransactionStatus(null, false);
	}

	/**
	 * This implementation of commit handles programmatic rollback requests,
	 * i.e. status.isRollbackOnly(), and non-transactional execution.
	 * Delegates to doCommit and rollback.
	 */
	public final void commit(TransactionStatus status) throws TransactionException {
		if (status.isRollbackOnly()) {
			logger.debug("Transactional code has requested rollback");
			rollback(status);
		}
		else if (status.isNewTransaction()) {
			try {
				doCommit(status);
				triggerAfterCompletion(TransactionSynchronization.STATUS_COMMITTED);
			}
			catch (UnexpectedRollbackException ex) {
				triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
				logger.error(ex.getMessage());
				throw ex;

			}
			catch (TransactionException ex) {
				triggerAfterCompletion(TransactionSynchronization.STATUS_UNKNOWN);
				logger.error(ex.getMessage());
				throw ex;
			}
			finally {
				TransactionSynchronizationManager.clear();
			}
		}
	}

	/**
	 * This implementation of rollback handles participating in
	 * existing transactions and non-transactional execution.
	 * Delegates to doRollback and doSetRollbackOnly.
	 */
	public final void rollback(TransactionStatus status) throws TransactionException {
		if (status.isNewTransaction()) {
			try {
				doRollback(status);
				triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
			}
			catch (TransactionException ex) {
				triggerAfterCompletion(TransactionSynchronization.STATUS_UNKNOWN);
				logger.error(ex.getMessage());
				throw ex;
			}
			finally {
				TransactionSynchronizationManager.clear();
			}
		}
		else if (status.getTransaction() != null) {
			try {
				doSetRollbackOnly(status);
			}
			catch (TransactionException ex) {
				logger.error(ex.getMessage());
				throw ex;
			}
		}
		else {
			// no transaction support available
			logger.info("Should roll back transaction but cannot - no transaction support available");
		}
	}

	/**
	 * Trigger afterCompletion callbacks on registered synchronizations,
	 * if transaction synchronization is active.
	 * @param status completion status according to TransactionSynchronization constants
	 * @see #setTransactionSynchronization
	 */
	private void triggerAfterCompletion(int status) {
		if (this.transactionSynchronization) {
			TransactionSynchronizationManager.triggerAfterCompletion(status);
		}
	}


	/**
	 * Return a current transaction object, i.e. a JTA UserTransaction.
	 * @return the current transaction object
	 * @throws CannotCreateTransactionException if transaction support is
	 * not available (e.g. no JTA UserTransaction retrievable from JNDI)
	 * @throws TransactionException in case of lookup or system errors
	 */
	protected abstract Object doGetTransaction() throws CannotCreateTransactionException, TransactionException;

	/**
	 * Check if the given transaction object indicates an existing,
	 * i.e. already begun, transaction.
	 * @param transaction transaction object returned by doGetTransaction()
	 * @return if there is an existing transaction
	 * @throws TransactionException in case of system errors
	 */
	protected abstract boolean isExistingTransaction(Object transaction) throws TransactionException;

	/**
	 * Begin a new transaction with the given isolation level.
	 * @param transaction transaction object returned by doGetTransaction()
	 * @param isolationLevel desired isolation level
	 * @param timeout transaction timeout (in seconds)
	 * @throws TransactionException in case of creation or system errors
	 */
	protected abstract void doBegin(Object transaction, int isolationLevel, int timeout) throws TransactionException;

	/**
	 * Perform an actual commit on the given transaction.
	 * An implementation does not need to check the rollback-only flag.
	 * @param status status representation of the transaction
	 * @throws TransactionException in case of commit or system errors
	 */
	protected abstract void doCommit(TransactionStatus status) throws TransactionException;

	/**
	 * Perform an actual rollback on the given transaction.
	 * An implementation does not need to check the new transaction flag.
	 * @param status status representation of the transaction
	 * @throws TransactionException in case of system errors
	 */
	protected abstract void doRollback(TransactionStatus status) throws TransactionException;

	/**
	 * Set the given transaction rollback-only. Only called on rollback
	 * if the current transaction takes part in an existing one.
	 * @param status status representation of the transaction
	 * @throws TransactionException in case of system errors
	 */
	protected abstract void doSetRollbackOnly(TransactionStatus status) throws TransactionException;

}
