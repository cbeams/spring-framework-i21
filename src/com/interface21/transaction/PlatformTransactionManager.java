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
	 * Return a currently active transaction or create a new one.
	 * @param propagationBehavior  propagation behavior according to the constants in this interface
	 * @param isolationLevel  isolation level according to the constants in this interface
	 * @return transaction status object representing the new or current transaction
	 */
	TransactionStatus getTransaction(int propagationBehavior, int isolationLevel);

	/**
	 * Commit the given transaction, with regard to its status.
	 * If the transaction has been marked rollback-only programmatically,
	 * perform a rollback.
	 * If the transaction wasn't a new one, omit the commit
	 * to take part in the surrounding transaction properly.
	 * @param status object returned by the getTransaction() method.
	 */
	void commit(TransactionStatus status);

	/**
	 * Roll back the given transaction, with regard to its status.
	 * If the transaction wasn't a new one, just set it rollback-only
	 * to take part in the surrounding transaction properly.
	 * @param status object returned by the getTransaction() method.
	 */
	void rollback(TransactionStatus status);

}
