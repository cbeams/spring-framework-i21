/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.transaction.interceptor;

import org.aopalliance.intercept.AspectException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.aop.framework.AopContext;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.TransactionException;

/**
 * Interceptor providing declarative transaction management using
 * the common Spring transaction infrastructure.
 * TransactionInterceptors are threadsafe.
 *
 * <p>Uses the <b>Strategy</b> design pattern. Implementations of
 * the PlatformTransactionManager interface will associate an
 * object with a transaction.
 *
 * <p>This class could set JTA as default transaction manager as that
 * implementation does not need any specific configuration. JTA is
 * <i>not</i> the default though to avoid unnecessary dependencies.
 *  
 * @version $Id$
 * @author Rod Johnson
 */
public class TransactionInterceptor implements MethodInterceptor, InitializingBean {
	
	/**
	 * Name of transaction attribute in Invocation.
	 * Target classes can use this to find TransactionStatus.
	 * <b>NB: The AOP proxy owning this TransactionInterceptor
	 * must be set to expose invocations for this to be accessible.</b>
	 */
	public static final String TRANSACTION_STATUS_ATTACHMENT_NAME = TransactionInterceptor.class.getName() + "TRANSACTION_STATUS";

	/**
	 * Return the transaction status of the current method invocation.
	 * Mainly intended for code that wants to set the current transaction
	 * rollback-only but not throw an application exception.
	 * @throws org.aopalliance.intercept.AspectException
	 * if the invocation cannot be found, because the method was invoked
	 * outside an AOP invocation context or because the AOP framework
	 * has not been configured to expose the invocation context
	 */
	public static TransactionStatus currentTransactionStatus() throws AspectException {
		return (TransactionStatus) AopContext.currentInvocation().getAttachment(TRANSACTION_STATUS_ATTACHMENT_NAME);
	}


	protected final Log logger = LogFactory.getLog(getClass());

	/** Delegate used to create, commit and rollback transactions */
	private PlatformTransactionManager transactionManager;
	
	/** Helper used to find transaction attributes */
	private TransactionAttributeSource transactionAttributeSource;

	/**
	 * Create a new TransactionInterceptor.
	 * Sets AttributeRegistryTransactionAttributeSource as default source.
	 * Does not set a default transaction manager!
	 * @see #setTransactionManager
	 * @see #setTransactionAttributeSource
	 * @see AttributeRegistryTransactionAttributeSource
	 */
	public TransactionInterceptor() {
		// Set default properties, which may be changed later
		this.transactionAttributeSource = new AttributeRegistryTransactionAttributeSource();
	}

	/**
	 * Set the transaction manager. This will perform actual
	 * transaction management: This class is just a way of invoking it.
	 */
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	/**
	 * Return the transaction manager.
	 */
	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * Set the transaction attribute source which is used to
	 * find transaction attributes. The default implementation looks
	 * at the metadata attributes associated with the current invocation.
	 */
	public void setTransactionAttributeSource(TransactionAttributeSource transactionAttributeSource) {
		this.transactionAttributeSource = transactionAttributeSource;
	}

	/**
	 * Return the transaction attribute source.
	 */
	public TransactionAttributeSource getTransactionAttributeSource() {
		return transactionAttributeSource;
	}

	public void afterPropertiesSet() {
		if (this.transactionManager == null) {
			throw new IllegalArgumentException("transactionManager is required");
		}
		if (this.transactionAttributeSource == null) {
			throw new IllegalArgumentException("transactionAttributeSource is required");
		}
	}

	/**
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public final Object invoke(MethodInvocation invocation) throws Throwable {
		// If this is null, the method is non-transactional
		TransactionAttribute transAtt = this.transactionAttributeSource.getTransactionAttribute(invocation);
		TransactionStatus status = null;
		
		// Create transaction if necessary
		if (transAtt != null) {
			// We need a transaction for this method
			logger.info("Creating transaction for method '" + invocation.getMethod().getName() + "'");
			
			// The transaction manager will flag an error if an incompatible tx already exists
			status = this.transactionManager.getTransaction(transAtt);
			
			// Make the TransactionStatus available to callees
			invocation.addAttachment(TRANSACTION_STATUS_ATTACHMENT_NAME, status);
		}
		else {
			// It isn't a transactional method
			if (logger.isDebugEnabled())
				logger.debug(
				"Don't need to create transaction for method '"
					+ invocation.getMethod().getName()
					+ "': this method isn't transactional");
		}

		// Invoke the next interceptor in the chain.
		// This will normally result in a target object being invoked.
		Object retVal = null;
		try {
			retVal = invocation.proceed();
		}
		catch (Throwable ex) {
			// Target invocation exception
			if (status != null) {
				onThrowable(invocation, transAtt, status, ex);
			}
			throw ex;
		}
		finally {
			if (transAtt != null) {
				invocation.addAttachment(TRANSACTION_STATUS_ATTACHMENT_NAME, null);
			}
		}
		if (status != null) {
			logger.info("Committing transaction on method '" + invocation.getMethod().getName() + "'");
			this.transactionManager.commit(status);
		}
		return retVal;
	}

	/**
	 * Handle a throwable.
	 * We may commit or roll back, depending on our configuration.
	 */
	private void onThrowable(MethodInvocation invocation, TransactionAttribute txAtt, TransactionStatus status, Throwable ex) {
		if (txAtt.rollbackOn(ex)) {
			logger.error(
				"Rolling back transaction on method '"
					+ invocation.getMethod().getName()
					+ "' due to throwable: "
					+ ex.getMessage());
			try {
				this.transactionManager.rollback(status);
			}
			catch (TransactionException tex) {
				logger.error("Application exception overridden by rollback exception", ex);
				throw tex;
			}
		}
		else {
			if (logger.isDebugEnabled())
				logger.debug(
				"Method '"
					+ invocation.getMethod().getName()
					+ "' threw throwable {"
					+ ex.getMessage()
					+ "} but this does not force transaction rollback");
			// Will still roll back if rollbackOnly is true
			this.transactionManager.commit(status);
		}
	}

}
