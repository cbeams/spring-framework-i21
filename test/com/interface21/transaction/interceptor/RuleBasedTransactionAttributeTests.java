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

import com.interface21.transaction.PlatformTransactionManager;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.interceptor.NoRollbackRuleAttribute;
import com.interface21.transaction.interceptor.RollbackRuleAttribute;
import com.interface21.transaction.interceptor.RuleBasedTransactionAttribute;

/**
 * 
 * @author Rod Johnson
 * @since 09-Apr-2003
 * @version $Revision$
 */
public class RuleBasedTransactionAttributeTests extends TestCase {

	/**
	 * Constructor for RuleBasedTransactionAttributeTests.
	 * @param arg0
	 */
	public RuleBasedTransactionAttributeTests(String arg0) {
		super(arg0);
	}
	
	public void testDefaultRule() {
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_READ_COMMITTED, new LinkedList());
		
		assertTrue(rta.rollBackOn(new RuntimeException()));
		assertTrue(rta.rollBackOn(new EJBException()));
		assertTrue(!rta.rollBackOn(new Exception()));
		assertTrue(!rta.rollBackOn(new ServletException()));
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
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_READ_COMMITTED, l);
		
		assertTrue(rta.rollBackOn(new RuntimeException()));
		assertTrue(rta.rollBackOn(new EJBException()));
		assertTrue(!rta.rollBackOn(new Exception()));
		// Check that default behaviour is overridden
		assertTrue(rta.rollBackOn(new ServletException()));
	}
	
	public void testRuleForCommitOnUnchecked() {
		List l = new LinkedList();
		l.add(new NoRollbackRuleAttribute("javax.ejb.EJBException"));
		l.add(new RollbackRuleAttribute("javax.servlet.ServletException"));
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_READ_COMMITTED, l);
		
		assertTrue(rta.rollBackOn(new RuntimeException()));
		// Check default behaviour is overridden
		assertTrue(!rta.rollBackOn(new EJBException()));
		assertTrue(!rta.rollBackOn(new Exception()));
		// Check that default behaviour is overridden
		assertTrue(rta.rollBackOn(new ServletException()));
	}
	
	public void testRuleForSelectiveRollbackOnchecked() {
		List l = new LinkedList();
		l.add(new RollbackRuleAttribute("java.rmi.RemoteException"));
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_READ_COMMITTED, l);
	
		assertTrue(rta.rollBackOn(new RuntimeException()));
		// Check default behaviour is overridden
		assertTrue(!rta.rollBackOn(new Exception()));
		// Check that default behaviour is overridden
		assertTrue(rta.rollBackOn(new RemoteException()));
	}
	
	public void testRollbackNever() {
		List l = new LinkedList();
		l.add(new NoRollbackRuleAttribute("Throwable"));
		
		RuleBasedTransactionAttribute rta = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_READ_COMMITTED, l);
	
		assertTrue(!rta.rollBackOn(new Throwable()));
		assertTrue(!rta.rollBackOn(new RuntimeException()));
		
		assertTrue(!rta.rollBackOn(new EJBException()));
		assertTrue(!rta.rollBackOn(new Exception()));
	
		assertTrue(!rta.rollBackOn(new ServletException()));
	}

}
