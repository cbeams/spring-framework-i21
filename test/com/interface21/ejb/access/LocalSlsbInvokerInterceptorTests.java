
package com.interface21.ejb.access;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
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
public class LocalSlsbInvokerInterceptorTests extends TestCase {

	/**
	 * Constructor for SimpleRemoteSlsbInvokerInterceptorTests.
	 * @param arg0
	 */
	public LocalSlsbInvokerInterceptorTests(String arg0) {
		super(arg0);
	}
	
	
	/**
	 * Test that it performs the correct lookup
	 * @throws java.lang.Exception
	 */
	public void testPerformsLookup() throws Exception {
		MockControl ejbControl = EasyMock.controlFor(LocalInterface.class);
		final LocalInterface ejb = (LocalInterface) ejbControl.getMock();
		ejbControl.activate();
		
		final String jndiName= "foobar";
		MockControl contextControl = contextControl(jndiName, ejb);
		
		LocalSlsbInvokerInterceptor si = configuredInterceptor(contextControl, jndiName);
		
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
	
		LocalSlsbInvokerInterceptor si = new LocalSlsbInvokerInterceptor();
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
		MockControl ejbControl = EasyMock.controlFor(LocalInterface.class);
		final LocalInterface ejb = (LocalInterface) ejbControl.getMock();
		ejb.targetMethod();
		ejbControl.setReturnValue(retVal, 1);
		ejbControl.activate();
	
		final String jndiName= "foobar";
		MockControl contextControl = contextControl(jndiName, ejb);
	
		LocalSlsbInvokerInterceptor si = configuredInterceptor(contextControl, jndiName);
	
		ProxyFactory pf = new ProxyFactory(new Class[] { LocalInterface.class } );
		pf.setAttributeRegistry(new Attrib4jAttributeRegistry());
		pf.addInterceptor(si);
		LocalInterface target = (LocalInterface) pf.getProxy();
	
		assertTrue(target.targetMethod() == retVal);
	
		contextControl.verify();
		ejbControl.verify();
	}
	
	private void testException(Exception expected) throws Exception {
		MockControl ejbControl = EasyMock.controlFor(LocalInterface.class);
		final LocalInterface ejb = (LocalInterface) ejbControl.getMock();
		ejb.targetMethod();
		ejbControl.setThrowable(expected);
		ejbControl.activate();

		final String jndiName= "foobar";
		MockControl contextControl = contextControl(jndiName, ejb);

		LocalSlsbInvokerInterceptor si = configuredInterceptor(contextControl, jndiName);

		ProxyFactory pf = new ProxyFactory(new Class[] { LocalInterface.class } );
		pf.setAttributeRegistry(new Attrib4jAttributeRegistry());
		pf.addInterceptor(si);
		LocalInterface target = (LocalInterface) pf.getProxy();

		try {
			target.targetMethod();
			fail("Should have thrown exception");
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
	
	
	
	protected MockControl contextControl(final String jndiName, final LocalInterface ejbInstance) throws Exception {
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
		
	protected LocalSlsbInvokerInterceptor configuredInterceptor(MockControl contextControl, final String jndiName) throws Exception {
		final Context mockCtx = (Context) contextControl.getMock();
		LocalSlsbInvokerInterceptor si = new LocalSlsbInvokerInterceptor();
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
	protected interface SlsbHome extends EJBLocalHome {
		LocalInterface create() throws CreateException;
	}
	
	protected interface BusinessMethods {
		Object targetMethod() throws ApplicationException;
	}
		
	protected interface LocalInterface extends EJBLocalObject, BusinessMethods {
		
	}
	
	protected class ApplicationException extends Exception {
		public ApplicationException() {
			super("appException");
		}
	}
 
}
