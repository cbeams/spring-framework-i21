/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionStatus;

/**
 * Used for testing only (for example, when we must replace the
 * behaviour of a PlatformTransactionManager bean we don't have access to).
 * Allows behaviour of an entire class to change with static delegate change.
 * Not multithreaded.
 * @author Rod Johnson
 * @since 26-Apr-2003
 * @version $Revision$
 */
public class PlatformTransactionManagerFacade implements PlatformTransactionManager {
	
	/**
	 * This member can be changed to change behaviour class-wide.
	 */
	public static PlatformTransactionManager delegate;

	/**
	 * @see com.interface21.transaction.PlatformTransactionManager#getTransaction(com.interface21.transaction.TransactionDefinition)
	 */
	public TransactionStatus getTransaction(TransactionDefinition definition) {
		return delegate.getTransaction(definition);
	}

	/**
	 * @see com.interface21.transaction.PlatformTransactionManager#commit(com.interface21.transaction.TransactionStatus)
	 */
	public void commit(TransactionStatus status) {
		delegate.commit(status);
	}

	/**
	 * @see com.interface21.transaction.PlatformTransactionManager#rollback(com.interface21.transaction.TransactionStatus)
	 */
	public void rollback(TransactionStatus status) {
		delegate.rollback(status);
	}

}
