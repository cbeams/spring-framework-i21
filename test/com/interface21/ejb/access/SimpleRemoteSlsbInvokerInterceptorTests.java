
package com.interface21.ejb.access;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.EJBObject;
import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.aop.attributes.Attrib4jAttributeRegistry;
import com.interface21.aop.framework.ProxyFactory;
import com.interface21.jndi.JndiTemplate;

/**
 * @author Rod Johnson
 */
public class SimpleRemoteSlsbInvokerInterceptorTests extends TestCase {

	/**
	 * Constructor for SimpleRemoteSlsbInvokerInterceptorTests.
	 * @param arg0
	 */
	public SimpleRemoteSlsbInvokerInterceptorTests(String arg0) {
		super(arg0);
	}
	
	/*
	public void testConstructors() {
		SimpleRemoteSlsbInvokerInterceptor si = new SimpleRemoteSlsbInvokerInterceptor(jndiName, inContainer);
		
	}
	*/
	
	/**
	 * Test that it performs the correct lookup
	 * @throws java.lang.Exception
	 */
	public void testPerformsLookup() throws Exception {
		MockControl ejbControl = EasyMock.controlFor(RemoteInterface.class);
		final RemoteInterface ejb = (RemoteInterface) ejbControl.getMock();
		ejbControl.activate();
		
		final String jndiName= "foobar";
		MockControl contextControl = contextControl(jndiName, ejb);
		
		SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(contextControl, jndiName);
		
		contextControl.verify();
	}
	
	
	public void testLookupFailure() throws Exception {
		final NamingException nex = new NamingException();
		final String jndiName= "foobar";
		JndiTemplate jt = new JndiTemplate() {
			public Object lookup(String name) throws NamingException {
				assertTrue(jndiName.equals(name));
				throw nex;
			}
		};

		SimpleRemoteSlsbInvokerInterceptor si = new SimpleRemoteSlsbInvokerInterceptor();
		si.setJndiName("foobar");
		si.setJndiTemplate(jt);
		// Stop java:comp/env addition
		si.setInContainer(false);
		try {
			si.afterPropertiesSet();
			fail("Should have failed with naming exception");
		}
		catch (NamingException ex) {
			assertTrue(ex == nex);
		}
	}
	
	public void testInvokesMethodOnEjbInstance() throws Exception {
		Object retVal = new Object();
		MockControl ejbControl = EasyMock.controlFor(RemoteInterface.class);
		final RemoteInterface ejb = (RemoteInterface) ejbControl.getMock();
		ejb.targetMethod();
		ejbControl.setReturnValue(retVal, 1);
		ejbControl.activate();
	
		final String jndiName= "foobar";
		MockControl contextControl = contextControl(jndiName, ejb);
	
		SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(contextControl, jndiName);
	
		ProxyFactory pf = new ProxyFactory(new Class[] { RemoteInterface.class } );
		pf.setAttributeRegistry(new Attrib4jAttributeRegistry());
		pf.addInterceptor(si);
		RemoteInterface target = (RemoteInterface) pf.getProxy();
	
		assertTrue(target.targetMethod() == retVal);
	
		contextControl.verify();
		ejbControl.verify();
	}
	
	private void testException(Exception expected) throws Exception {
		MockControl ejbControl = EasyMock.controlFor(RemoteInterface.class);
		final RemoteInterface ejb = (RemoteInterface) ejbControl.getMock();
		ejb.targetMethod();
		ejbControl.setThrowable(expected);
		ejbControl.activate();

		final String jndiName= "foobar";
		MockControl contextControl = contextControl(jndiName, ejb);

		SimpleRemoteSlsbInvokerInterceptor si = configuredInterceptor(contextControl, jndiName);

		ProxyFactory pf = new ProxyFactory(new Class[] { RemoteInterface.class } );
		pf.setAttributeRegistry(new Attrib4jAttributeRegistry());
		pf.addInterceptor(si);
		RemoteInterface target = (RemoteInterface) pf.getProxy();

		try {
			target.targetMethod();
			fail("Should have thrown remote exception");
		}
		catch (Exception thrown) {
			assertTrue(thrown == expected);
		}

		contextControl.verify();
		ejbControl.verify();
	}
	
	public void testApplicationException() throws Exception {
		testException(new ApplicationException());
	}
	
	public void testRemoteException() throws Exception {
		testException(new RemoteException());
	}
	
	
	protected MockControl contextControl(final String jndiName, final RemoteInterface ejbInstance) throws Exception {
		MockControl homeControl = EasyMock.controlFor(SlsbHome.class);
		final SlsbHome mockHome = (SlsbHome) homeControl.getMock();
		mockHome.create();
		homeControl.setReturnValue(ejbInstance, 1);
		homeControl.activate();
		
		MockControl ctxControl = EasyMock.controlFor(Context.class);
		final Context mockCtx = (Context) ctxControl.getMock();
		
		mockCtx.lookup("java:comp/env/" + jndiName);
		ctxControl.setReturnValue(mockHome);
		mockCtx.close();
		ctxControl.setVoidCallable();
		ctxControl.activate();
		return ctxControl;
	}
		
	protected SimpleRemoteSlsbInvokerInterceptor configuredInterceptor(MockControl contextControl, final String jndiName) throws Exception {
		final Context mockCtx = (Context) contextControl.getMock();
		SimpleRemoteSlsbInvokerInterceptor si = new SimpleRemoteSlsbInvokerInterceptor();
		si.setJndiTemplate(new JndiTemplate() {
			protected Context createInitialContext() throws NamingException {
				return mockCtx;
			}
		});
		si.setJndiName(jndiName);
		si.afterPropertiesSet();
		
		return si;
	}
	
	
	/** 
	 * Needed so that we can mock create() method
	 */
	protected interface SlsbHome extends EJBHome {
		EJBObject create() throws RemoteException, CreateException;
	}
	
	protected interface RemoteInterface extends EJBObject {
		// Also business exception!?
		Object targetMethod() throws RemoteException, ApplicationException;
	}
	
	protected class ApplicationException extends Exception {
		public ApplicationException() {
			super("appException");
		}
	}
 
}
