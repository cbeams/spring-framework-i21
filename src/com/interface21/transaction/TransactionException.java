/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction;

import com.interface21.core.NestedRuntimeException;

/**
 * Superclass for all transaction exceptions.
 * @author Rod Johnson
 * @since 17-Mar-2003
 * @version $Revision$
 */
public abstract class TransactionException extends NestedRuntimeException {

	/**
	 * @param s
	 */
	public TransactionException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public TransactionException(String s, Throwable ex) {
		super(s, ex);
	}

}
