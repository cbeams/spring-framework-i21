package com.interface21.transaction.support;

import com.interface21.transaction.TransactionStatus;

/**
 * Callback interface for transactional code. To be used with TransactionTemplate's
 * execute method, assumably often as anonymous class within a method implementation.
 *
 * <p>Typically used to gather various calls to transaction-unaware low-level
 * services into a higher-level method implementation with transaction demarcation.
 *
 * @author Juergen Hoeller
 * @since 17.03.2003
 * @see com.interface21.transaction.support.TransactionTemplate
 */
public interface TransactionCallback {

	/**
	 * Gets called by TransactionTemplate.execute within a transactional context.
	 * Does not need to care about transactions itself, although it can retrieve
	 * and influence the status of the current transaction via the given status
	 * object, e.g. setting rollback-only.
	 *
	 * <p>Allows for returning a result object created within the transaction,
	 * i.e. a business object or a collection of business objects. A thrown
	 * RuntimeException is treated as application exception that enforces a
	 * rollback. An exception gets propagated to the caller of the template.
	 * 
	 * <p>Note when using JTA: JTA transactions only work with transactional
	 * JNDI resources, so implementations need to use such resources if
	 * they want transaction support.
	 *
	 * @param status associated transaction status
	 * @return a result object, or null
	 * @throws java.lang.RuntimeException if the transaction needs to be rolled back,
	 * propagating the application exception to the caller
	 * @see com.interface21.transaction.support.TransactionTemplate#execute
	 */
	Object doInTransaction(TransactionStatus status) throws RuntimeException;

}
