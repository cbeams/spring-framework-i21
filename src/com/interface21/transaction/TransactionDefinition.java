package com.interface21.transaction;

import java.sql.Connection;

/**
 * Interface for classes that define transaction properties.
 * Base interface for TransactionAttribute.
 *
 * <p>Note that isolation level and timeout settings will only get
 * applied when starting a new transaction. As only propagation behavior
 * "required" can actually cause that, it doesn't make sense to specify
 * the isolation level or timeout else.
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
	 * <p>This is typically the default setting of a transaction definition.
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
	 * Use default isolation level of the underlying database.
	 * All other levels correspond to java.sql.Connection.
	 * @see java.sql.Connection
	 */
	int ISOLATION_DEFAULT          = -1;

	int ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;

	int ISOLATION_READ_COMMITTED   = Connection.TRANSACTION_READ_COMMITTED;

	int ISOLATION_REPEATABLE_READ  = Connection.TRANSACTION_REPEATABLE_READ;

	int ISOLATION_SERIALIZABLE     = Connection.TRANSACTION_SERIALIZABLE;

	/** Use default timeout of the underlying transaction system */
	int TIMEOUT_DEFAULT = -1;

	/**
	 * Return the propagation behavior.
	 * Must return one of the PROPAGATION constants.
	 * @see #PROPAGATION_REQUIRED
	 */
	int getPropagationBehavior();

	/**
	 * Return the isolation level.
	 * Must return one of the ISOLATION constants.
	 * <p>Only makes sense in combination with PROPAGATION_REQUIRED.
	 * @see #ISOLATION_DEFAULT
	 */
	int getIsolationLevel();

	/**
	 * Return the transaction timeout.
	 * Must return a number of seconds, or TIMEOUT_DEFAULT.
	 * <p>Only makes sense in combination with PROPAGATION_REQUIRED.
	 * @see #TIMEOUT_DEFAULT
	 */
	public int getTimeout();

	public boolean isReadOnly();

}
