/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import org.aopalliance.MethodInvocation;

import com.interface21.transaction.interceptor.TransactionAttribute;

/**
 * Interface used by TransactionInterceptor. Implementations
 * know how to source transaction attributes, whether from 
 * metadata attributes at source level (the default) or anywhere else.
 * @author Rod Johnson
 * @since 15-Apr-2003
 * @version $Id$
 */
public interface TransactionAttributeSource {

	/**
	 * Return the transaction attribute for this method.
	 * Return null if the method is non-transactional.
	 * @param clazz class we're interested in. May not be the same
	 * as the declaring class of the method. May not be null.
	 * @param m Method we're interested in. May not be null.
	 * @return TransactionAttribute transaction attribute or null.
	 */
	TransactionAttribute getTransactionAttribute(MethodInvocation invocation);
}
