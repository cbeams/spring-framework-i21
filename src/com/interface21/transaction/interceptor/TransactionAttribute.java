/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import com.interface21.transaction.TransactionDefinition;

/**
 * This interface adds a rollBackOn specification to TransactionDefinition.
 * As custom rollBackOn is only possible with AOP, this class resides
 * in the AOP transaction package.
 *
 * @author Rod Johnson
 * @since 16-Mar-2003
 * @version $Revision$
 * @see com.interface21.transaction.interceptor.DefaultTransactionAttribute
 */
public interface TransactionAttribute extends TransactionDefinition {
	
	/**
	 * Should we roll back on a checked exception?
	 * @param ex the exception to evaluate
	 * @return boolean rollback or not
	 */
	boolean rollbackOn(Throwable ex);
	
}
