package com.interface21.transaction.support;

import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionException;
import com.interface21.transaction.TransactionStatus;
import com.interface21.util.Constants;

/**
 * Helper class that simplifies programmatic transaction demarcation
 * (via the "execute" method) and exception handling.
 *
 * <p>The central method is "execute", supporting transactional code implementing
 * the TransactionCallback interface. It handles the transaction lifecycle and
 * possible exceptions such that neither the TransactionCallback implementation
 * nor the calling code needs to explicitly handle transactions.
 *
 * <p>Typical usage: Allows for writing low-level application services that use
 * (JNDI) resources but are not transaction-aware themselves. Instead, they
 * can implicitly take part in (JTA) transactions handled by higher-level
 * application services utilizing this class, making calls to the low-level
 * services via an inner-class callback object.
 *
 * <p>Can be used within a service implementation via direct instantiation with
 * a transaction manager reference, or get prepared in an application context
 * and given to services as bean reference. Note: The transaction manager should
 * always be configured as bean in the application context, in the first case
 * given to the service directly, in the second case to the prepared template.
 *
 * <p>Supports setting the propagation behavior and the isolation level by name,
 * for convenient configuration in context definitions.
 *
 * @author Juergen Hoeller
 * @since 17.03.2003
 * @see com.interface21.transaction.support.TransactionCallback
 * @see com.interface21.transaction.PlatformTransactionManager
 * @see com.interface21.transaction.jta.JtaTransactionManager
 */
public class TransactionTemplate extends DefaultTransactionDefinition {

	/** Constants instance for TransactionDefinition */
	private static final Constants constants = new Constants(TransactionDefinition.class);

	protected PlatformTransactionManager transactionManager = null;

	/**
	 * Create a new TransactionTemplate instance.
	 * Mainly targetted at configuration by a bean factory.
	 * Note: Transaction manager property needs to be set before any execute calls.
	 * @see #setTransactionManager
	 */
	public TransactionTemplate() {
	}

	/**
	 * Create a new TransactionTemplate instance.
	 * @param transactionManager transaction manager to be used
	 * @see PlatformTransactionManager
	 */
	public TransactionTemplate(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * Set the transaction manager to be used.
	 */
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * Return the transaction manager to be used.
	 */
	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * Set the propagation behavior by the name of the respective constant in
	 * PlatformTransactionManager (e.g. "PROPAGATION_REQUIRED");
	 * @param constantName name of the constant
	 * @throws IllegalArgumentException if an invalid constant was specified
	 * @see PlatformTransactionManager
	 */
	public void setPropagationBehaviorName(String constantName) throws IllegalArgumentException {
		if (constantName == null || !constantName.startsWith(PROPAGATION_CONSTANT_PREFIX)) {
			throw new IllegalArgumentException("Only propagation constants allowed");
		}
		setPropagationBehavior(constants.asInt(constantName));
	}

	/**
	 * Set the isolation level by the name of the respective constant in
	 * PlatformTransactionManager (e.g. "ISOLATION_DEFAULT");
	 * @param constantName name of the constant
	 * @throws IllegalArgumentException if an invalid constant was specified
	 * @see PlatformTransactionManager
	 */
	public void setIsolationLevelName(String constantName) throws IllegalArgumentException {
		if (constantName == null || !constantName.startsWith(ISOLATION_CONSTANT_PREFIX)) {
			throw new IllegalArgumentException("Only isolation constants allowed");
		}
		setIsolationLevel(constants.asInt(constantName));
	}

	/**
	 * Executes the action specified by the given callback object within a transaction.
	 * Application exceptions thrown by the callback object get propagated to the caller.
	 * Allows for returning a result object created within the transaction,
	 * i.e. a business object or a collection of business objects.
	 *
	 * @param action callback object that specifies the transactional action
	 * @return a result object returned by the callback, or null
	 * @throws TransactionException in case of initialization, rollback,
	 * or system errors
	 * @throws RuntimeException in case of application exceptions thrown by
	 * the callback object
	 */
	public Object execute(TransactionCallback action) throws TransactionException, RuntimeException {
		TransactionStatus status = this.transactionManager.getTransaction(this);
		try {
			Object result = action.doInTransaction(status);
			this.transactionManager.commit(status);
			return result;
		}
		catch (TransactionException tse) {
			throw tse;
		}
		catch (RuntimeException ex) {
			// transactional code threw exception
			this.transactionManager.rollback(status);
			throw ex;
		}
	}
	
}
