/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import org.aopalliance.intercept.MethodInvocation;

import com.interface21.transaction.interceptor.TransactionAttribute;

/**
 * Interface used by TransactionInterceptor. Implementations
 * know how to source transaction attributes, whether from metadata
 * attributes at source level (the default) or anywhere else.
 * @author Rod Johnson
 * @since 15-Apr-2003
 * @version $Id$
 */
public interface TransactionAttributeSource {

	/**
	 * Return the transaction attribute for this method.
	 * Return null if the method is non-transactional.
	 * @param invocation method invocation descriptor
	 * @return TransactionAttribute transaction attribute or null.
	 */
	TransactionAttribute getTransactionAttribute(MethodInvocation invocation);

}
