/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJBException;
import javax.servlet.ServletException;

import junit.framework.TestCase;

import com.interface21.transaction.TransactionDefinition;

/**
 * 
 * @author Rod Johnson
 * @since 09-Apr-2003
 * @version $Revision$
 */
public class RuleBasedTransactionAttributeTests extends TestCase {

	public void testDefaultRule() {
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute();
		assertTrue(rta.rollbackOn(new RuntimeException()));
		assertTrue(rta.rollbackOn(new EJBException()));
		assertTrue(!rta.rollbackOn(new Exception()));
		assertTrue(!rta.rollbackOn(new ServletException()));
	}
	
	/**
	 * Test one checked exception that should roll back
	 *
	 */
	public void testRuleForRollbackOnChecked() {
		List l = new LinkedList();
		l.add(new RollbackRuleAttribute("javax.servlet.ServletException"));
		
		// Add irrelevant object: should be ignored
		l.add(new Object());
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, l);
		
		assertTrue(rta.rollbackOn(new RuntimeException()));
		assertTrue(rta.rollbackOn(new EJBException()));
		assertTrue(!rta.rollbackOn(new Exception()));
		// Check that default behaviour is overridden
		assertTrue(rta.rollbackOn(new ServletException()));
	}
	
	public void testRuleForCommitOnUnchecked() {
		List l = new LinkedList();
		l.add(new NoRollbackRuleAttribute("javax.ejb.EJBException"));
		l.add(new RollbackRuleAttribute("javax.servlet.ServletException"));
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, l);
		
		assertTrue(rta.rollbackOn(new RuntimeException()));
		// Check default behaviour is overridden
		assertTrue(!rta.rollbackOn(new EJBException()));
		assertTrue(!rta.rollbackOn(new Exception()));
		// Check that default behaviour is overridden
		assertTrue(rta.rollbackOn(new ServletException()));
	}
	
	public void testRuleForSelectiveRollbackOnchecked() {
		List l = new LinkedList();
		l.add(new RollbackRuleAttribute("java.rmi.RemoteException"));
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, l);
	
		assertTrue(rta.rollbackOn(new RuntimeException()));
		// Check default behaviour is overridden
		assertTrue(!rta.rollbackOn(new Exception()));
		// Check that default behaviour is overridden
		assertTrue(rta.rollbackOn(new RemoteException()));
	}
	
	public void testRollbackNever() {
		List l = new LinkedList();
		l.add(new NoRollbackRuleAttribute("Throwable"));
		
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, l);
	
		assertTrue(!rta.rollbackOn(new Throwable()));
		assertTrue(!rta.rollbackOn(new RuntimeException()));
		
		assertTrue(!rta.rollbackOn(new EJBException()));
		assertTrue(!rta.rollbackOn(new Exception()));
	
		assertTrue(!rta.rollbackOn(new ServletException()));
	}

}
