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

import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.support.BeanFactoryLoader;
import com.interface21.beans.factory.support.BootstrapException;
import com.interface21.beans.factory.support.StaticListableBeanFactory;

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
	
	public void testSfsb() throws CreateException {
		MockControl mc = EasyMock.controlFor(SessionContext.class);
		SessionContext sc = (SessionContext) mc.getMock();
		mc.activate();
		
		final BeanFactory bf = new StaticListableBeanFactory();
		BeanFactoryLoader bfl = new BeanFactoryLoader() {
			public BeanFactory loadBeanFactory() throws BootstrapException {
				return bf;
			}
		};
		
		// Basically the test is what needed to be implemented here!
		class MySfsb extends AbstractStatefulSessionBean {
			public void ejbCreate() throws CreateException {
				loadBeanFactory();
				assertTrue(getBeanFactory() == bf);
				assertTrue(logger != null);
			}
			public void ejbActivate() throws EJBException, RemoteException {
				throw new UnsupportedOperationException("ejbActivate");
			}
			public void ejbPassivate() throws EJBException, RemoteException {
				throw new UnsupportedOperationException("ejbPassivate");
			}

		};
		
		MySfsb sfsb = new MySfsb();
		sfsb.setBeanFactoryLoader(bfl);
		sfsb.setSessionContext(sc);
		sfsb.ejbCreate();
		assertTrue(sc == sfsb.getSessionContext());
	}
	
	/**
	 * Check there's a helpful message if no JNDI key is present
	 *
	 */
	public void testHelpfulNamingLookupMessage() {
		MockControl mc = EasyMock.controlFor(SessionContext.class);
		SessionContext sc = (SessionContext) mc.getMock();
		mc.activate();
	
		// Leave with default XmlBeanFactoryLoader
	
		// Basically the test is what needed to be implemented here!
		AbstractStatelessSessionBean slsb = new AbstractStatelessSessionBean() {
			public void onEjbCreate() {
			}
		};
	
		slsb.setSessionContext(sc);
		try {
			slsb.ejbCreate();
			fail();
		}
		catch (CreateException ex) {
			System.err.println(ex.getMessage());
			assertTrue(ex.getMessage().indexOf("environment") != -1);
			assertTrue(ex.getMessage().indexOf("ejb/BeanFactoryPath") != -1);
		}
	}
	
	public void testSlsb() throws Exception {
		MockControl mc = EasyMock.controlFor(SessionContext.class);
		SessionContext sc = (SessionContext) mc.getMock();
		mc.activate();
		
		final BeanFactory bf = new StaticListableBeanFactory();
		BeanFactoryLoader bfl = new BeanFactoryLoader() {
			public BeanFactory loadBeanFactory() throws BootstrapException {
				return bf;
			}
		};
	
		AbstractStatelessSessionBean slsb = new AbstractStatelessSessionBean() {
			protected void onEjbCreate() throws CreateException {
				assertTrue(getBeanFactory() == bf);
				assertTrue(logger != null);
			}
		};
		// Must call this method before ejbCreate()
		slsb.setBeanFactoryLoader(bfl);
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
