package com.interface21.transaction;

import com.interface21.transaction.support.AbstractPlatformTransactionManager;

/**
 * @author Juergen Hoeller
 * @since 29.04.2003
 */
class TestTransactionManager extends AbstractPlatformTransactionManager {

	private static final Object TRANSACTION = "transaction";

	private final boolean existingTransaction;

	private final boolean canCreateTransaction;

	protected boolean begin = false;

	protected boolean commit = false;

	protected boolean rollback = false;

	protected boolean rollbackOnly = false;

	protected TestTransactionManager(boolean existingTransaction, boolean canCreateTransaction) {
		this.existingTransaction = existingTransaction;
		this.canCreateTransaction = canCreateTransaction;
	}

	protected Object doGetTransaction() {
		return TRANSACTION;
	}

	protected boolean isExistingTransaction(Object transaction) {
		return existingTransaction;
	}

	protected void doBegin(Object transaction, int isolationLevel, int timeout) {
		if (!TRANSACTION.equals(transaction)) {
			throw new IllegalArgumentException("Not the same transaction object");
		}
		if (!this.canCreateTransaction) {
			throw new CannotCreateTransactionException("cannot create transaction");
		}
		this.begin = true;
	}

	protected void doCommit(TransactionStatus status) {
		if (!TRANSACTION.equals(status.getTransaction())) {
			throw new IllegalArgumentException("Not the same transaction object");
		}
		this.commit = true;
	}

	protected void doRollback(TransactionStatus status) {
		if (!TRANSACTION.equals(status.getTransaction())) {
			throw new IllegalArgumentException("Not the same transaction object");
		}
		this.rollback = true;
	}

	protected void doSetRollbackOnly(TransactionStatus status) {
		if (!TRANSACTION.equals(status.getTransaction())) {
			throw new IllegalArgumentException("Not the same transaction object");
		}
		this.rollbackOnly = true;
	}

}
