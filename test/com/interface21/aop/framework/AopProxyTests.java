/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.aop.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import org.aopalliance.AspectException;
import org.aopalliance.AttributeRegistry;
import org.aopalliance.MethodInterceptor;
import org.aopalliance.MethodInvocation;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.aop.attributes.Attrib4jAttributeRegistry;
import com.interface21.aop.interceptor.DebugInterceptor;
import com.interface21.beans.IOther;
import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;

/**
 * 
 * @author Rod Johnson
 * @since 13-Mar-2003
 * @version $Revision$
 */
public class AopProxyTests extends TestCase {

	/**
	 * Constructor for AopProxyTests.
	 * @param arg0
	 */
	public AopProxyTests(String arg0) {
		super(arg0);
	}

	public void testNullConfig() {
		try {
			AopProxy aop = new AopProxy(null);
			AopProxy.getProxy(aop);
			fail("Shouldn't allow null interceptors");
		} catch (AopConfigException ex) {
			// Ok
		}
	}

	public void testNoInterceptors() {
		ProxyConfig pc =
			new DefaultProxyConfig(new Class[] { ITestBean.class }, false, new Attrib4jAttributeRegistry());
		// Add no interceptors
		try {
			AopProxy aop = new AopProxy(pc);
			AopProxy.getProxy(aop);
			fail("Shouldn't allow no interceptors");
		} catch (AopConfigException ex) {
			// Ok
		}
	}

	public void testInterceptorIsInvoked() throws Throwable {
		// Test return value
		int age = 25;
		MockControl miControl = EasyMock.controlFor(MethodInterceptor.class);
		MethodInterceptor mi = (MethodInterceptor) miControl.getMock();

		AttributeRegistry r = new Attrib4jAttributeRegistry();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, r);
		pc.addInterceptor(mi);
		AopProxy aop = new AopProxy(pc);

		// Really would like to permit null arg:can't get exact mi
		mi.invoke(null);
		//mi.invoke(new MethodInvocationImpl(aop, null, ITestBean.class, 
		//	ITestBean.class.getMethod("getAge", null),
		//	null, l, r));
		//miControl.
		//miControl.setReturnValue(new Integer(age));
		// Have disabled strong argument checking
		miControl.setDefaultReturnValue(new Integer(age));
		miControl.activate();

		ITestBean tb = (ITestBean) AopProxy.getProxy(aop);

		assertTrue("correct return value", tb.getAge() == age);

		miControl.verify();
	}

	public void testContext() throws Throwable {
		testContext(true);
	}

	public void testNoContext() throws Throwable {
		testContext(false);
	}

	/** 
	 * 
	 * @param context if true, want context
	 * @throws Throwable
	 */
	private void testContext(final boolean context) throws Throwable {
		final String s = "foo";
		// Test return value
		MethodInterceptor mi = new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				if (!context) {
					assertNoInvocationContext();
				} else {
					assertTrue("have context", AopContext.currentInvocation() != null);
				}
				return s;
			}
		};
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, context, r);
		pc.addInterceptor(mi);
		AopProxy aop = new AopProxy(pc);

		assertNoInvocationContext();
		ITestBean tb = (ITestBean) AopProxy.getProxy(aop);
		assertNoInvocationContext();
		assertTrue("correct return value", tb.getName() == s);
	}

	/**
	 * Test that the proxy returns itself when the
	 * target returns <code>this</code>
	 * @throws Throwable
	 */
	public void testTargetReturnsThis() throws Throwable {
		final String s = "foo";
		// Test return value
		TestBean raw = new TestBean() {
			public ITestBean getSpouse() {
				return this;
			}
		};
		InvokerInterceptor ii = new InvokerInterceptor(raw);

		AttributeRegistry r = new Attrib4jAttributeRegistry();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, r);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) AopProxy.getProxy(aop);
		assertTrue("this is wrapped in a proxy", Proxy.isProxyClass(tb.getSpouse().getClass()));

		assertTrue("this return is wrapped in proxy", tb.getSpouse() == tb);
	}

	/**
	 * Equality means set of interceptors and
	 * set of interfaces are equal
	 * @throws Throwable
	 */
	public void testEquals() throws Throwable {
		// Test return value
		TestBean raw = new TestBean() {
			public ITestBean getSpouse() {
				return this;
			}
		};
		InvokerInterceptor ii = new InvokerInterceptor(raw);

		AttributeRegistry r = new Attrib4jAttributeRegistry();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, r);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) AopProxy.getProxy(aop);
		assertTrue("proxy equals itself", tb.equals(tb));
		assertTrue("proxy isn't equal to proxied object", !tb.equals(raw));
		//test null eq
		assertTrue("tb.equals(null) is false", !tb.equals(null));
		assertTrue("test equals proxy", tb.equals(aop));

		// Test with AOP proxy with additional interceptor
		
		
		ProxyConfig pc2 = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, r);
		pc2.addInterceptor(new DebugInterceptor());
		assertTrue(!tb.equals(new AopProxy(pc2)));
		//assertTrue(!tb.equals(AopProxy.getProxy(new AopProxy(pc2))));

		// Test with any old dynamic proxy
		assertTrue(
			!tb
			.equals(
				Proxy
				.newProxyInstance(getClass().getClassLoader(), new Class[] { ITestBean.class }, new InvocationHandler() {
			/**
			 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
			 */
			public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
				throw new UnsupportedOperationException("invoke");
			}
		})));
	}

	/**
	 * Test canAttach
	 * @throws Throwable
	 */
	public void testCanAttach() throws Throwable {

		List l = new LinkedList();

		final TrapInvocationInterceptor tii = new TrapInvocationInterceptor();

		AttributeRegistry r = new Attrib4jAttributeRegistry();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, r);
		pc.addInterceptor(tii);
		pc.addInterceptor(new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				assertTrue("Saw same interceptor", invocation == tii.invocation);
				return null;
			}
		});
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) AopProxy.getProxy(aop);
		tb.getSpouse();
		assertTrue(tii.invocation != null);
		assertTrue(tii.invocation.getProxy() == tb);
		assertTrue(tii.invocation.getInvokedObject() == null);
	}

	/**
	 * TODO also test undeclared: decide what it should do!
	 * @throws Throwable
	 */
	public void testDeclaredException() throws Throwable {
		final Exception ex = new Exception();
		// Test return value
		MethodInterceptor mi = new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				throw ex;
			}
		};
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, true, r);
		pc.addInterceptor(mi);
		AopProxy aop = new AopProxy(pc);

		try {
			ITestBean tb = (ITestBean) AopProxy.getProxy(aop);
			// Note: exception param below isn't used
			tb.exceptional(ex);
			fail("Should have thrown exception raised by interceptor");
		} catch (Exception thrown) {
			assertTrue("exception matches: not " + thrown, ex == thrown);
		}
	}

	public void testTargetCanGetInvocation() throws Throwable {
		class ContextTestBean extends TestBean {
			public MethodInvocation invocation;
			public String getName() {
				this.invocation = AopContext.currentInvocation();
				return super.getName();
			}
			public void absquatulate() {
				this.invocation = AopContext.currentInvocation();
				super.absquatulate();
			}
		}
		final ContextTestBean target = new ContextTestBean();
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class, IOther.class }, true, r);
		TrapInvocationInterceptor tii = new TrapInvocationInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
					// Assert that target matches BEFORE invocation returns
				assertTrue(invocation.getInvokedObject() == target);
				return super.invoke(invocation);
			}
		};
		pc.addInterceptor(tii);
		InvokerInterceptor ii = new InvokerInterceptor(target);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) AopProxy.getProxy(aop);
		tb.getName();
		assertTrue(tii.invocation == target.invocation);
		assertTrue(target.invocation.getInvokedObject() == target);
		assertTrue( target.invocation.getMethod().getDeclaringClass() == ITestBean.class);
		//assertTrue( target.invocation.getProxy() == tb);

		((IOther) tb).absquatulate();
		MethodInvocation minv =  tii.invocation;
		assertTrue("invoked on iother, not " + minv.getMethod().getDeclaringClass(), minv.getMethod().getDeclaringClass() == IOther.class);
		assertTrue(target.invocation == tii.invocation);
	}

	/**
	 * Throw an exception if there is an Invocation
	 */
	private void assertNoInvocationContext() {
		try {
			AopContext.currentInvocation();
			fail("Expected no invocation context");
		} catch (AspectException ex) {
			// ok
		}
	}

	private class TrapInvocationInterceptor implements MethodInterceptor {

		public MethodInvocation invocation;

		/**
		 * @see org.aopalliance.MethodInterceptor#invoke(org.aopalliance.Invocation)
		 */
		public Object invoke(MethodInvocation invocation) throws Throwable {
			this.invocation =  invocation;
			return invocation.invokeNext();
		}
	}

}