/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.ejb.access;

import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.beans.MethodInvocationException;
import com.interface21.jndi.JndiTemplate;

/**
 * Tests Business Methods pattern
 * @author Rod Johnson
 * @since 21-May-2003
 * @version $Id$
 */
public class SimpleRemoteStatelessSessionProxyFactoryBeanTests extends TestCase {

	/**
	 * Constructor for LocalStatelessSessionProxyFactoryBeanTests.
	 * @param arg0
	 */
	public SimpleRemoteStatelessSessionProxyFactoryBeanTests(String arg0) {
		super(arg0);
	}
	
	public void testInvokesMethod() throws Exception {
		final int value = 11;
		final String jndiName = "foo";
		
		MockControl ec = EasyMock.controlFor(MyEjb.class);
		MyEjb myEjb = (MyEjb) ec.getMock();
		myEjb.getValue();
		ec.setReturnValue(value, 1);
		ec.activate();
		
		MockControl mc = EasyMock.controlFor(MyHome.class);
		final MyHome home = (MyHome) mc.getMock();
		home.create();
		mc.setReturnValue(myEjb, 1);
		mc.activate();
		
		JndiTemplate jt = new JndiTemplate() {
			public Object lookup(String name) throws NamingException {
				// parameterize
				assertTrue(name.equals("java:comp/env/" + jndiName));
				return home;
			}
		};
		
		SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
		fb.setJndiName(jndiName);
		fb.setBusinessInterface(MyBusinessMethods.class);
		fb.setJndiTemplate(jt);
		
		// Need lifecycle methods
		fb.afterPropertiesSet();

		MyBusinessMethods mbm = (MyBusinessMethods) fb.getObject();
		assertTrue(Proxy.isProxyClass(mbm.getClass()));
		assertTrue(mbm.getValue() == value);
		mc.verify();	
		ec.verify();	
	}
	
	
	public void testRemoteException() throws Exception {
		final RemoteException rex = new RemoteException();
		final String jndiName = "foo";
	
		MockControl ec = EasyMock.controlFor(MyEjb.class);
		MyEjb myEjb = (MyEjb) ec.getMock();
		myEjb.getValue();
		ec.setThrowable(rex);
		ec.activate();
	
		MockControl mc = EasyMock.controlFor(MyHome.class);
		final MyHome home = (MyHome) mc.getMock();
		home.create();
		mc.setReturnValue(myEjb, 1);
		mc.activate();
	
		JndiTemplate jt = new JndiTemplate() {
			public Object lookup(String name) throws NamingException {
				// parameterize
				assertTrue(name.equals("java:comp/env/" + jndiName));
				return home;
			}
		};
	
		SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
		fb.setJndiName(jndiName);
		fb.setBusinessInterface(MyBusinessMethods.class);
		fb.setJndiTemplate(jt);
	
		// Need lifecycle methods
		fb.afterPropertiesSet();

		MyBusinessMethods mbm = (MyBusinessMethods) fb.getObject();
		assertTrue(Proxy.isProxyClass(mbm.getClass()));
		try {
			mbm.getValue();
			fail("Should've thrown remote exception");
		}
		catch (RemoteException ex) {
			assertTrue(ex == rex);
		}
		mc.verify();	
		ec.verify();	
	}
	
	
	public void testCreateException() throws Exception {
		final String jndiName = "foo";
	
		final CreateException cex = new CreateException();
		MockControl mc = EasyMock.controlFor(MyHome.class);
		final MyHome home = (MyHome) mc.getMock();
		home.create();
		mc.setThrowable(cex);
		mc.activate();
	
		JndiTemplate jt = new JndiTemplate() {
			public Object lookup(String name) throws NamingException {
				// parameterize
				assertTrue(name.equals(jndiName));
				return home;
			}
		};
	
		SimpleRemoteStatelessSessionProxyFactoryBean fb = new SimpleRemoteStatelessSessionProxyFactoryBean();
		fb.setJndiName(jndiName);
		fb.setInContainer(false);	// no java:comp/env prefix
		fb.setBusinessInterface(MyBusinessMethods.class);
		fb.setJndiTemplate(jt);
	
		// Need lifecycle methods
		fb.afterPropertiesSet();

		MyBusinessMethods mbm = (MyBusinessMethods) fb.getObject();
		assertTrue(Proxy.isProxyClass(mbm.getClass()));
		
		try {
			mbm.getValue();
			fail("Should have failed to create EJB");
		}
		catch (MethodInvocationException ex) {
			assertTrue(ex.getRootCause() == cex);
		}
		
		mc.verify();	
	}
	
	
	public static interface MyHome extends EJBHome {
		MyBusinessMethods create() throws CreateException, RemoteException;	
	}
	
	public static interface MyBusinessMethods  {
		int getValue() throws RemoteException;
	}
	
	public static interface MyEjb extends EJBObject, MyBusinessMethods {
		
	}

}
