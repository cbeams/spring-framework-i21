package com.interface21.transaction.jta;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.support.AbstractPlatformTransactionManager;

/**
 * PlatformTransactionManager implementation for JTA.
 * Simply delegates to the appropriate JtaServices methods.
 *
 * @author Juergen Hoeller
 * @since 24.03.2003
 * @see JtaServices
 */
public class JtaTransactionManager extends AbstractPlatformTransactionManager {

	public JtaTransactionManager() {
	}

	public JtaTransactionManager(boolean allowNonTransactionalExecution) {
		setAllowNonTransactionalExecution(allowNonTransactionalExecution);
	}

	protected Object doGetTransaction() {
		return JtaServices.getTransaction();
	}

	protected boolean isExistingTransaction(Object transaction) {
		return (JtaServices.getStatus((UserTransaction) transaction) != Status.STATUS_NO_TRANSACTION);
	}

	/**
	 * This standard JTA implementation simply ignores the isolation
	 * level. To be overridden by server-specific subclasses that
	 * handle the isolation level.
	 * @param transaction current JTA transaction object
	 * @param isolationLevel desired isolation level
	 */
	protected void doBegin(Object transaction, int isolationLevel) {
		JtaServices.begin((UserTransaction) transaction);
	}

	protected void doCommit(TransactionStatus status) {
		JtaServices.commit((UserTransaction) status.getTransaction());
	}

	protected void doRollback(TransactionStatus status) {
		JtaServices.rollback((UserTransaction) status.getTransaction());
	}

	protected void doSetRollbackOnly(TransactionStatus status) {
		JtaServices.setRollbackOnly((UserTransaction) status.getTransaction());
	}
}
