/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.ejb.support;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;

import org.easymock.EasyMock;
import org.easymock.MockControl;

import junit.framework.TestCase;

/**
 * Most of the value of the tests here is in being forced
 * to implement ejbCreate() methods.
 * @author Rod Johnson
 * @since 21-May-2003
 * @version $Id$
 */
public class EjbSupportTests extends TestCase {

	/**
	 * @param arg0
	 */
	public EjbSupportTests(String arg0) {
		super(arg0);
	}
	
	public void testSfsb() {
		MockControl mc = EasyMock.controlFor(SessionContext.class);
		SessionContext sc = (SessionContext) mc.getMock();
		mc.activate();
		
		// Basically the test is largely what needed to be implemented here!
		AbstractSessionBean sfsb = new AbstractSessionBean() {
			public void ejbCreate() throws CreateException {
				assertTrue(logger != null);
			}
			public void ejbActivate() throws EJBException, RemoteException {
				throw new UnsupportedOperationException("ejbActivate");
			}
			public void ejbPassivate() throws EJBException, RemoteException {
				throw new UnsupportedOperationException("ejbPassivate");
			}

		};
		sfsb.setSessionContext(sc);
		assertTrue(sc == sfsb.getSessionContext());
	}
	
	public void testSlsb() throws Exception {
		MockControl mc = EasyMock.controlFor(SessionContext.class);
		SessionContext sc = (SessionContext) mc.getMock();
		mc.activate();
	
		AbstractStatelessSessionBean slsb = new AbstractStatelessSessionBean() {
			public void ejbCreate() throws CreateException {
				assertTrue(logger != null);
			}
		};
		slsb.setSessionContext(sc);
		assertTrue(sc == slsb.getSessionContext());
		slsb.ejbCreate();
		try {
			slsb.ejbActivate();
			fail("Shouldn't allow activation of SLSB");
		}
		catch (IllegalStateException ex) {
			// Ok
		}
		try {
			slsb.ejbPassivate();
			fail("Shouldn't allow passivation of SLSB");
		}
		catch (IllegalStateException ex) {
			// Ok
		}
	}

	
}
