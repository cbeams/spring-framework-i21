package com.interface21.transaction;

import java.sql.Connection;

/**
 * Interface for classes that define transaction properties.
 * Base interface for TransactionAttribute.
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 * @see com.interface21.transaction.support.DefaultTransactionDefinition
 * @see com.interface21.transaction.interceptor.TransactionAttribute
 */
public interface TransactionDefinition {

	String PROPAGATION_CONSTANT_PREFIX = "PROPAGATION";

	String ISOLATION_CONSTANT_PREFIX = "ISOLATION";

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

	/** Default transaction timeout */
	int TIMEOUT_DEFAULT = -1;

	/**
	 * Return the propagation behavior.
	 * Must return of the constants in PlatformTransactionManager.
	 * @see PlatformTransactionManager
	 */
	int getPropagationBehavior();

	/**
	 * Return the isolation level.
	 * Must return of the constants in PlatformTransactionManager.
	 * @see PlatformTransactionManager
	 */
	int getIsolationLevel();

	/**
	 * Return the transaction timeout.
	 * Must return a number of seconds, or TIMEOUT_DEFAULT.
	 * @see #TIMEOUT_DEFAULT
	 */
	public int getTimeout();

}
