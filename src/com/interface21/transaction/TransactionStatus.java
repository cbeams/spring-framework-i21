package com.interface21.transaction;

/**
 * @author Juergen Hoeller
 * @since 27.03.2003
 */
public class TransactionStatus {

	private Object transaction = null;
	private boolean newTransaction = false;
	private boolean rollbackOnly = false;

	public TransactionStatus(Object transaction, boolean newTransaction) {
		this.transaction = transaction;
		this.newTransaction = newTransaction;
	}

	public Object getTransaction() {
		return transaction;
	}

	public boolean isNewTransaction() {
		return (transaction != null && newTransaction);
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}
}
