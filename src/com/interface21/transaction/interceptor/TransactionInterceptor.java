/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.transaction.interceptor;

import org.aopalliance.MethodInterceptor;
import org.aopalliance.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionException;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.interceptor.AttributeRegistryTransactionAttributeSource;
import com.interface21.transaction.interceptor.TransactionAttribute;
import com.interface21.transaction.interceptor.TransactionAttributeSource;
import com.interface21.transaction.jta.JtaTransactionManager;

/**
 * Interceptor providing declarative transaction management using
 * the common Spring transaction infrastructure.
 * TransactionInterceptors are threadsafe.
 * <br>
 * Uses the <b>Strategy</b> design pattern. Implementations of the
 * PlatformTransactionManager interface will associate an object
 * with a transaction.
 * <br>Uses JTA as the default transaction infrastructure.
 *  
 * @version $Id$
 * @author Rod Johnson
 */
public class TransactionInterceptor implements MethodInterceptor {
	
	/**
	 * Name of transaction attribute in Invocation.
	 * Target classes can use this to find TransactionStatus.
	 * <b>NB: the AOP proxy owning this TransactionInterceptor
	 * must be set to expose invocations for this to be accessible.</b>
	 */
	public static final String TRANSACTION_STATUS_ATTACHMENT_NAME = TransactionInterceptor.class.getName() + "TRANSACTION_STATUS";

	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Delegate used to create, commit and rollback transactions
	 */
	private PlatformTransactionManager transactionManager;
	
	/**
	 * Helper used to find transaction attributes.
	 */
	private TransactionAttributeSource transactionAttributeSource;

	public TransactionInterceptor() {
		// Set default properties, which may be changed later
		this.transactionAttributeSource = new AttributeRegistryTransactionAttributeSource();
		this.transactionManager = new JtaTransactionManager();
	}
	
	/**
	 * @return PlatformTransactionManager helper
	 */
	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * Sets the platformTxManager. This will perform actual
	 * transaction management: this class is just a way of
	 * invoking it.
	 * @param platformTxManager The platformTxManager to set
	 */
	public void setTransactionManager(PlatformTransactionManager platformTxManager) {
		this.transactionManager = platformTxManager;
	}
	
	/**
	 * @return TransactionAttributeSource helper 
	 */
	public TransactionAttributeSource getTransactionAttributeSource() {
		return transactionAttributeSource;
	}

	/**
	 * Sets the transactionAttributeSource helper, which is used to
	 * find transaction attributes. The default implementation looks
	 * at the metadata attributes associated with the current invocation.
	 * @param transactionAttributeSource The transactionAttributeSource to set
	 */
	public void setTransactionAttributeSource(TransactionAttributeSource transactionAttributeSource) {
		this.transactionAttributeSource = transactionAttributeSource;
	}

	/**
	 * @see org.aopalliance.MethodInterceptor#invoke(org.aopalliance.MethodInvocation)
	 */
	public final Object invoke(MethodInvocation invocation) throws Throwable {
		// If this is null, the method is non-transactional
		TransactionAttribute transAtt = this.transactionAttributeSource.getTransactionAttribute(invocation);
		TransactionStatus status = null;
		
		// Create transaction if necessary
		if (transAtt != null) {
			// We need a transaction for this method
			
			logger.info("Creating transaction for method '" + invocation.getMethod().getName() + "'");
			
			// The PlatformTransactionManager will flag an error if an incompatible tx already exists
			status = this.transactionManager.getTransaction(transAtt);
			
			// Make the TransactionStatus available to callees
			invocation.setResource(TRANSACTION_STATUS_ATTACHMENT_NAME, status);
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
		try {
			Object retVal = invocation.invokeNext();
			if (status != null) {
				logger.info("COMMITING transaction on method '" + invocation.getMethod().getName() + "'");
				this.transactionManager.commit(status);
			}
			return retVal;
		}
		catch (TransactionException ex) {
			// Our own infrastructure exception
			// Just bail out, as we can't handle it
			throw ex;
		}
		catch (Throwable t) {
			// Target invocation
			if (status != null) {
				onThrowable(invocation, transAtt, status, t);
			}
			else if (status != null && transAtt.rollBackOn(t)) {
				// Rollback existing transaction
				status.setRollbackOnly();
			}
			throw t;
		}
		finally {
			if (transAtt != null) {
				invocation.setResource(TRANSACTION_STATUS_ATTACHMENT_NAME, null);
			}
		}
	}

	/**
	 * Handle a throwable.
	 * We may commit or roll back, depending on our configuration.
	 */
	private void onThrowable(MethodInvocation invocation, TransactionAttribute txAtt, TransactionStatus status, Throwable t) {
		if (txAtt.rollBackOn(t)) {
			logger.error(
				"ROLLING BACK transaction on method '"
					+ invocation.getMethod().getName()
					+ "' due to throwable: "
					+ t.getMessage());
			this.transactionManager.rollback(status);
		}
		else {
			if (logger.isDebugEnabled())
				logger.debug(
				"Method '"
					+ invocation.getMethod().getName()
					+ "' threw throwable {"
					+ t.getMessage()
					+ "} but this does not force transaction rollback");
			// Will still roll back if rollbackOnly is true
			this.transactionManager.commit(status);
		}
	}

}
