/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.transaction;

/**
 * This is the central interface in Spring's transaction support.
 * Applications can use this directly, but it is not primarily meant as API.
 * Typically, applications will work with either TransactionTemplate or the
 * AOP transaction interceptor.
 *
 * <p>For implementers, AbstractPlatformTransactionManager is a good starting
 * point. The default implementations are DataSourceTransactionManager and
 * JtaTransactionManager.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16-Mar-2003
 * @version $Revision$
 * @see com.interface21.transaction.support.TransactionTemplate
 * @see com.interface21.transaction.interceptor.TransactionInterceptor
 * @see com.interface21.transaction.support.AbstractPlatformTransactionManager
 * @see com.interface21.jdbc.datasource.DataSourceTransactionManager
 * @see com.interface21.transaction.jta.JtaTransactionManager
 */
public interface PlatformTransactionManager {

	/**
	 * Return a currently active transaction or create a new one.
	 * Note that parameters like isolation level or timeout will only be applied
	 * to new transactions, and thus be ignored when participating in active ones.
	 * Furthermore, they aren't supported by every transaction manager:
	 * A proper implementation should thrown an exception when custom values
	 * that it doesn't support are specified.
	 * @param definition TransactionDefinition instance (can be null for defaults),
	 * describing propagation behavior, isolation level, timeout etc.
	 * @return transaction status object representing the new or current transaction
	 * @throws TransactionException in case of lookup, creation, or system errors
	 */
	TransactionStatus getTransaction(TransactionDefinition definition)
	    throws TransactionException;

	/**
	 * Commit the given transaction, with regard to its status.
	 * If the transaction has been marked rollback-only programmatically,
	 * perform a rollback.
	 * If the transaction wasn't a new one, omit the commit
	 * to take part in the surrounding transaction properly.
	 * @param status object returned by the getTransaction() method.
	 * @throws TransactionException in case of commit or system errors
	 */
	void commit(TransactionStatus status) throws TransactionException;

	/**
	 * Roll back the given transaction, with regard to its status.
	 * If the transaction wasn't a new one, just set it rollback-only
	 * to take part in the surrounding transaction properly.
	 * @param status object returned by the getTransaction() method.
	 * @throws TransactionException in case of system errors
	 */
	void rollback(TransactionStatus status) throws TransactionException;

}
