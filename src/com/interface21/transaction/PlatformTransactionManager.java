/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.transaction;

import java.sql.Connection;

import com.interface21.transaction.*;

/**
 * SPI interface.
 * @author Rod Johnson, Juergen Hoeller
 * @since 16-Mar-2003
 * @version $Revision$
 */
public interface PlatformTransactionManager {

	/**
	 * Support a current transaction, create a new one if none exists.
	 * Analogous to EJB transaction attribute of the same name.
	 */
	int PROPAGATION_REQUIRED = 0;

	/**
	 * Support a current transaction, execute non-transactional if none exists.
	 * Analogous to EJB transaction attribute of the same name.
	 */
	int PROPAGATION_SUPPORTS = 1;

	/**
	 * Support a current transaction, throw an exception if none exists.
	 * Analogous to EJB transaction attribute of the same name.
	 */
	int PROPAGATION_MANDATORY = 2;

	/**
	 * Default isolation level, all other according to java.sql.Connection levels.
	 * @see java.sql.Connection
	 */
	int ISOLATION_DEFAULT          = -1;
	int ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;
	int ISOLATION_READ_COMMITTED   = Connection.TRANSACTION_READ_COMMITTED;
	int ISOLATION_REPEATABLE_READ  = Connection.TRANSACTION_REPEATABLE_READ;
	int ISOLATION_SERIALIZABLE     = Connection.TRANSACTION_SERIALIZABLE;

	/**
	 * Create a new transaction or returns a currently active transaction.
	 * @param propagationBehavior  propagation behavior according to the constants in this interface
	 * @param isolationLevel  isolation level according to the constants in this interface
	 * @return transaction object representing the new or current transaction
	 */
	TransactionStatus getTransaction(int propagationBehavior, int isolationLevel);

	/**
	 * @param status object returned by the getTransaction() method.
	 */
	void commit(TransactionStatus status);

	/**
	 * @param status object returned by the getTransaction() method.
	 */
	void rollback(TransactionStatus status);

}