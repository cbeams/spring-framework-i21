/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.transaction.TransactionDefinition;
import com.interface21.util.ConstantException;

/**
 * Format is 
 * FQN.Method=tx attribute representation
 * @author Rod Johnson
 * @since 26-Apr-2003
 * @version $Revision$
 */
public class TransactionAttributeSourceEditorTests extends TestCase {

	public TransactionAttributeSourceEditorTests(String arg0) {
		super(arg0);
	}

	public void testNull() throws Exception {
		TransactionAttributeSourceEditor pe = new TransactionAttributeSourceEditor();
		pe.setAsText(null);
		TransactionAttributeSource tas = (TransactionAttributeSource) pe.getValue();

		MockControl miControl = EasyMock.controlFor(MethodInvocation.class);
		MethodInvocation mi = (MethodInvocation) miControl.getMock();
		Method m = Object.class.getMethod("hashCode", null);
		mi.getMethod();
		miControl.setReturnValue(m);
		miControl.activate();
		
		assertTrue(tas.getTransactionAttribute(mi) == null);
	}

	public void testInvalid() throws Exception {
		TransactionAttributeSourceEditor pe = new TransactionAttributeSourceEditor();
		try {
			pe.setAsText("foo=bar");
			fail();
		}
		catch (IllegalArgumentException ex) {
			// Ok
		}
	}
	
	public void testMatchesSpecific() throws Exception {
		TransactionAttributeSourceEditor pe = new TransactionAttributeSourceEditor();
		// TODO need FQN?
		pe.setAsText("java.lang.Object.hashCode=PROPAGATION_REQUIRED\n" +
			"java.lang.Object.equals=PROPAGATION_MANDATORY\n" +
		  "java.lang.Object.wait=PROPAGATION_SUPPORTS\n" +
			"java.lang.Object.not*=PROPAGATION_REQUIRED");
		TransactionAttributeSource tas = (TransactionAttributeSource) pe.getValue();

		checkTransactionProperties(tas, Object.class.getMethod("hashCode", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("equals", new Class[] { Object.class }),
		                     TransactionDefinition.PROPAGATION_MANDATORY);
		checkTransactionProperties(tas, Object.class.getMethod("wait", null),
		                     TransactionDefinition.PROPAGATION_SUPPORTS);
		checkTransactionProperties(tas, Object.class.getMethod("wait", new Class[] { long.class }),
		                     TransactionDefinition.PROPAGATION_SUPPORTS);
		checkTransactionProperties(tas, Object.class.getMethod("wait", new Class[] { long.class, int.class }),
		                     TransactionDefinition.PROPAGATION_SUPPORTS);
		checkTransactionProperties(tas, Object.class.getMethod("notify", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("notifyAll", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("toString", null),
		                     -1);
	}

	public void testMatchesAll() throws Exception {
		TransactionAttributeSourceEditor pe = new TransactionAttributeSourceEditor();
		pe.setAsText("java.lang.Object.*=PROPAGATION_REQUIRED");
		TransactionAttributeSource tas = (TransactionAttributeSource) pe.getValue();

		checkTransactionProperties(tas, Object.class.getMethod("hashCode", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("equals", new Class[] { Object.class }),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("wait", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("wait", new Class[] { long.class }),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("wait", new Class[] { long.class, int.class }),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("notify", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("notifyAll", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
		checkTransactionProperties(tas, Object.class.getMethod("toString", null),
		                     TransactionDefinition.PROPAGATION_REQUIRED);
	}

	private void checkTransactionProperties(TransactionAttributeSource tas, Method method, int propagationBehavior) {
		MockControl miControl = EasyMock.controlFor(MethodInvocation.class);
		MethodInvocation mi = (MethodInvocation) miControl.getMock();
		mi.getMethod();
		miControl.setReturnValue(method);
		miControl.activate();
		TransactionAttribute ta = tas.getTransactionAttribute(mi);
		if (propagationBehavior >= 0) {
			assertTrue(ta != null);
			assertTrue(ta.getIsolationLevel() == TransactionDefinition.ISOLATION_DEFAULT);
			assertTrue(ta.getPropagationBehavior() == propagationBehavior);
		}
		else {
			assertTrue(ta == null);
		}
	}

}
