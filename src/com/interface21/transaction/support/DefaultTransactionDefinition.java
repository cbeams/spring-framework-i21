package com.interface21.transaction.support;

import com.interface21.transaction.TransactionDefinition;

/**
 * Default implementation of the TransactionDefinition interface,
 * offering bean-style configuration and sensible default values
 * (PROPAGATION_REQUIRED, ISOLATION_DEFAULT, TIMEOUT_DEFAULT).
 * Base class for both TransactionTemplate and DefaultTransactionAttribute.
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 * @see com.interface21.transaction.support.TransactionTemplate
 * @see com.interface21.transaction.interceptor.DefaultTransactionAttribute
 */
public class DefaultTransactionDefinition implements TransactionDefinition {

	private int propagationBehavior = PROPAGATION_REQUIRED;

	private int isolationLevel = ISOLATION_DEFAULT;

	private int timeout = TIMEOUT_DEFAULT;

	public DefaultTransactionDefinition() {
	}

	public DefaultTransactionDefinition(int propagationBehavior) {
		this.propagationBehavior = propagationBehavior;
	}

	public DefaultTransactionDefinition(int propagationBehavior, int isolationLevel) {
		this.propagationBehavior = propagationBehavior;
		this.isolationLevel = isolationLevel;
	}

	public int getPropagationBehavior() {
		return propagationBehavior;
	}

	public void setPropagationBehavior(int propagationBehavior) {
		this.propagationBehavior = propagationBehavior;
	}

	public int getIsolationLevel() {
		return isolationLevel;
	}

	public void setIsolationLevel(int isolationLevel) {
		this.isolationLevel = isolationLevel;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
