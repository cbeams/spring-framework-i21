/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.transaction.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Implementation of TransactionAttributeSource that uses
 * attributes from the AttributeRegistry.
 * @author Rod Johnson
 * @since 15-Apr-2003
 * @version $Revision$
 */
public class AttributeRegistryTransactionAttributeSource implements TransactionAttributeSource {

	/**
	 * @see com.interface21.transaction.interceptor.TransactionAttributeSource#getTransactionAttribute(java.lang.Class, java.lang.reflect.Method)
	 */
	public TransactionAttribute getTransactionAttribute(MethodInvocation mi) {
		Class targetClass = mi.getMethod().getDeclaringClass();
		if (mi.getThis() != null) {
			targetClass = mi.getThis().getClass();
		}

		// TODO add knowledge about target class
		return findTransactionAttribute(mi);
	}

	/**
	 * Return the transaction attribute for this invocation.
	 * Protected rather than private as subclasses may want to customize
	 * how this is done: for example, returning a TransactionAttribute
	 * affected by the values of other attributes.
	 * Return null if its not transactional. 
	 * TODO refactor into Attrib4j-specific class
	 * @param invocation
	 * @return TransactionAttribute
	 */
	protected TransactionAttribute findTransactionAttribute(MethodInvocation invocation) {

		// TODO: get from target, might need AOPAlliance interface changes

		Object[] atts = invocation.getAttributeRegistry().getAttributes(invocation.getMethod());
		System.err.println("Atts=" + atts);
		if (atts == null)
			return null;
		boolean found = false;

		// Check there is a transaction attribute
		for (int i = 0; i < atts.length && !found; i++) {
			if (atts[i] instanceof TransactionAttribute)
				return (TransactionAttribute) atts[i];
			if (atts[i] instanceof RollbackRuleAttribute)
				found = true;
		}
		if (!found) {
			System.err.println("NOT FOUND");
			return null;
		}

		// We really want value: bit of a hack
		List l = new ArrayList(atts.length);
		for (int i = 0; i < atts.length; i++) {
			l.add(atts[i]);
		}
		TransactionAttribute txatt = new RuleBasedTransactionAttribute(l);
		System.err.println("txatt=" + txatt);
		return txatt;
		//return null;
	}

}
