package com.interface21.transaction;

/**
 * Representation of the status of a transaction,
 * consisting of a transaction object and some status flags.
 *
 * <p>Transactional code can use this to retrieve status information,
 * and to programmatically request a rollback (instead of throwing
 * an exception that causes an implicit rollback).
 *
 * @author Juergen Hoeller
 * @since 27.03.2003
 * @see PlatformTransactionManager
 * @see com.interface21.transaction.support.TransactionCallback#doInTransaction
 * @see com.interface21.transaction.interceptor.TransactionInterceptor#currentTransactionStatus
 * @see #setRollbackOnly
 */
public class TransactionStatus {

	private Object transaction = null;

	private boolean newTransaction = false;

	private boolean rollbackOnly = false;

	/**
	 * Create a new TransactionStatus instance.
	 * @param transaction underlying transaction object,
	 * e.g. a JTA UserTransaction
	 * @param newTransaction if the transaction is new,
	 * else participating in an existing transaction
	 */
	public TransactionStatus(Object transaction, boolean newTransaction) {
		this.transaction = transaction;
		this.newTransaction = newTransaction;
	}

	/**
	 * Return the underlying transaction object, e.g. a JTA UserTransaction.
	 */
	public Object getTransaction() {
		return transaction;
	}

	/**
	 * Return if the transaction is new,
	 * else participating in an existing transaction.
	 */
	public boolean isNewTransaction() {
		return (transaction != null && newTransaction);
	}

	/**
	 * Set the transaction rollback-only. This instructs the transaction manager
	 * that the only possible outcome of the transaction may be a rollback,
	 * proceeding with the normal applicaiton workflow though (i.e. no exception). 
	 * <p>For transactions managed by TransactionTemplate or TransactionInterceptor.
	 * An alternative way to trigger a rollback is throwing an application exception.
	 * @see com.interface21.transaction.support.TransactionCallback#doInTransaction
	 * @see com.interface21.transaction.interceptor.TransactionAttribute#rollbackOn
	 */
	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	/**
	 * Return if the transaction has been set rollback-only.
	 */
	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

}
