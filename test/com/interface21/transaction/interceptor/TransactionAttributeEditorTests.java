/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import javax.ejb.EJBException;
import javax.servlet.ServletException;

import junit.framework.TestCase;

import com.interface21.transaction.TransactionDefinition;

/**
 * Tests to check conversion from String to TransactionAttribute
 * @since 26-Apr-2003
 * @version $Id$
 * @author Rod Johnson
 */
public class TransactionAttributeEditorTests extends TestCase {
	
	public void testNull() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		pe.setAsText(null);
		TransactionAttribute ta = (TransactionAttribute) pe.getValue();
		assertTrue(ta == null);
	}
	
	public void testEmptyString() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		pe.setAsText("");
		TransactionAttribute ta = (TransactionAttribute) pe.getValue();
		assertTrue(ta == null);
	}
	
	/**
	 * Format is PROPAGATION
	 */
	public void testValidPropagationCodeOnly() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		pe.setAsText("PROPAGATION_REQUIRED");
		TransactionAttribute ta = (TransactionAttribute) pe.getValue();
		assertTrue(ta != null);
		assertTrue(ta.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED);
		assertTrue(ta.getIsolationLevel() == TransactionDefinition.ISOLATION_DEFAULT);
		assertTrue(!ta.isReadOnly());
	}
	
	public void testInvalidPropagationCodeOnly() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		try {
			pe.setAsText("XXPROPAGATION_REQUIRED");
			fail("Should have failed with bogus propagation code");
		}
		catch (IllegalArgumentException ex) {
			// Ok
		}
	}
	
	public void testValidPropagationCodeAndIsolationCode() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		pe.setAsText("PROPAGATION_REQUIRED,ISOLATION_READ_UNCOMMITTED");
		TransactionAttribute ta = (TransactionAttribute) pe.getValue();
		assertTrue(ta != null);
		assertTrue(ta.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED);
		assertTrue(ta.getIsolationLevel() == TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
	}
	
	public void testValidPropagationAndIsolationCodesAndInvalidRollbackRule() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		
		try {
			pe.setAsText("PROPAGATION_REQUIRED,ISOLATION_READ_UNCOMMITTED,XXX");
			fail("Should have failed with bogus rollback rule");
		}
		catch (IllegalArgumentException ex) {
			// Ok
		}
	}
	
	public void testValidPropagationCodeAndIsolationCodeAndRollbackRules1() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		pe.setAsText("PROPAGATION_MANDATORY,ISOLATION_REPEATABLE_READ,-ServletException,+EJBException");
		TransactionAttribute ta = (TransactionAttribute) pe.getValue();
		assertTrue(ta != null);
		assertTrue(ta.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY);
		assertTrue(ta.getIsolationLevel() == TransactionDefinition.ISOLATION_REPEATABLE_READ);
		assertTrue(!ta.isReadOnly());
		assertTrue(ta.rollbackOn(new RuntimeException()));
		assertTrue(!ta.rollbackOn(new Exception()));
		
		// Check for our bizarre customized rollback rules
		assertTrue(ta.rollbackOn(new ServletException()));
		assertTrue(!ta.rollbackOn(new EJBException()));
	}

	public void testValidPropagationCodeAndIsolationCodeAndRollbackRules2() {
		TransactionAttributeEditor pe = new TransactionAttributeEditor();
		pe.setAsText("+ServletException,readOnly,ISOLATION_READ_COMMITTED,-EJBException,PROPAGATION_SUPPORTS");
		TransactionAttribute ta = (TransactionAttribute) pe.getValue();
		assertTrue(ta != null);
		assertTrue(ta.getPropagationBehavior() == TransactionDefinition.PROPAGATION_SUPPORTS);
		assertTrue(ta.getIsolationLevel() == TransactionDefinition.ISOLATION_READ_COMMITTED);
		assertTrue(ta.isReadOnly());
		assertTrue(ta.rollbackOn(new RuntimeException()));
		assertTrue(!ta.rollbackOn(new Exception()));

		// Check for our bizarre customized rollback rules
		assertTrue(!ta.rollbackOn(new ServletException()));
		assertTrue(ta.rollbackOn(new EJBException()));
	}

}
