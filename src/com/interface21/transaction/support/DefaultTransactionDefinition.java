package com.interface21.transaction.support;

import com.interface21.transaction.TransactionDefinition;
import com.interface21.util.Constants;

/**
 * Default implementation of the TransactionDefinition interface,
 * offering bean-style configuration and sensible default values
 * (PROPAGATION_REQUIRED, ISOLATION_DEFAULT, TIMEOUT_DEFAULT, readOnly=false).
 *
 * <p>Base class for both TransactionTemplate and DefaultTransactionAttribute.
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 * @see com.interface21.transaction.support.TransactionTemplate
 * @see com.interface21.transaction.interceptor.DefaultTransactionAttribute
 */
public class DefaultTransactionDefinition implements TransactionDefinition {

	/** Constants instance for TransactionDefinition */
	private static final Constants constants = new Constants(TransactionDefinition.class);

	private int propagationBehavior = PROPAGATION_REQUIRED;

	private int isolationLevel = ISOLATION_DEFAULT;

	private int timeout = TIMEOUT_DEFAULT;

	private boolean readOnly = false;

	public DefaultTransactionDefinition() {
	}

	public DefaultTransactionDefinition(int propagationBehavior) {
		this.propagationBehavior = propagationBehavior;
	}

	/**
	 * Set the propagation behavior by the name of the respective constant in
	 * TransactionDefinition, e.g. "PROPAGATION_REQUIRED".
	 * @param constantName name of the constant
	 * @throws java.lang.IllegalArgumentException if an invalid constant was specified
	 * @see com.interface21.transaction.TransactionDefinition#PROPAGATION_REQUIRED
	 */
	public void setPropagationBehaviorName(String constantName) throws IllegalArgumentException {
		if (constantName == null || !constantName.startsWith(PROPAGATION_CONSTANT_PREFIX)) {
			throw new IllegalArgumentException("Only propagation constants allowed");
		}
		setPropagationBehavior(constants.asInt(constantName));
	}

	public void setPropagationBehavior(int propagationBehavior) {
		this.propagationBehavior = propagationBehavior;
	}

	public int getPropagationBehavior() {
		return propagationBehavior;
	}

	/**
	 * Set the isolation level by the name of the respective constant in
	 * TransactionDefinition, e.g. "ISOLATION_DEFAULT".
	 * @param constantName name of the constant
	 * @throws java.lang.IllegalArgumentException if an invalid constant was specified
	 * @see com.interface21.transaction.TransactionDefinition#ISOLATION_DEFAULT
	 */
	public void setIsolationLevelName(String constantName) throws IllegalArgumentException {
		if (constantName == null || !constantName.startsWith(ISOLATION_CONSTANT_PREFIX)) {
			throw new IllegalArgumentException("Only isolation constants allowed");
		}
		setIsolationLevel(constants.asInt(constantName));
	}

	public void setIsolationLevel(int isolationLevel) {
		this.isolationLevel = isolationLevel;
	}

	public int getIsolationLevel() {
		return isolationLevel;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

}
