/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.aop.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;

import org.aopalliance.intercept.AspectException;
import org.aopalliance.intercept.AttributeRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.aop.interceptor.DebugInterceptor;
import com.interface21.beans.IOther;
import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;

/**
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13-Mar-2003
 * @version $Revision$
 */
public class AopProxyTests extends TestCase {

	public AopProxyTests(String arg0) {
		super(arg0);
	}

	public void testNullConfig() {
		try {
			AopProxy aop = new AopProxy(null);
			aop.getProxy();
			fail("Shouldn't allow null interceptors");
		} catch (AopConfigException ex) {
			// Ok
		}
	}

	public void testNoInterceptors() {
		ProxyConfig pc =
			new DefaultProxyConfig(new Class[] { ITestBean.class }, false, null);
		// Add no interceptors
		try {
			AopProxy aop = new AopProxy(pc);
			aop.getProxy();
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

		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, null);
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

		ITestBean tb = (ITestBean) aop.getProxy();
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
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, context, null);
		pc.addInterceptor(mi);
		AopProxy aop = new AopProxy(pc);

		assertNoInvocationContext();
		ITestBean tb = (ITestBean) aop.getProxy();
		assertNoInvocationContext();
		assertTrue("correct return value", tb.getName() == s);
	}

	/**
	 * Test that the proxy returns itself when the
	 * target returns <code>this</code>
	 * @throws Throwable
	 */
	public void testTargetReturnsThis() throws Throwable {
		// Test return value
		TestBean raw = new TestBean() {
			public ITestBean getSpouse() {
				return this;
			}
		};
		InvokerInterceptor ii = new InvokerInterceptor(raw);

		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, null);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) aop.getProxy();
		assertTrue("this is wrapped in a proxy", Proxy.isProxyClass(tb.getSpouse().getClass()));

		assertTrue("this return is wrapped in proxy", tb.getSpouse() == tb);
	}

	public void testProxyIsJustInterface() throws Throwable {
		TestBean raw = new TestBean();
		raw.setAge(32);
		InvokerInterceptor ii = new InvokerInterceptor(raw);
		ProxyConfig pc = new DefaultProxyConfig(new Class[] {ITestBean.class}, false, null);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		Object proxy = aop.getProxy();
		assertTrue(proxy instanceof ITestBean);
		assertTrue(!(proxy instanceof TestBean));
	}

	public void testProxyCanBeFullClass() throws Throwable {
		TestBean raw = new TestBean();
		raw.setAge(32);
		InvokerInterceptor ii = new InvokerInterceptor(raw);
		ProxyConfig pc = new DefaultProxyConfig(new Class[] {}, false, null);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		Object proxy = aop.getProxy();
		assertTrue(proxy instanceof ITestBean);
		assertTrue(proxy instanceof TestBean);
		TestBean tb = (TestBean) proxy;
		assertTrue("Correct age", tb.getAge() == 32);
	}

	/**
	 * Equality means set of interceptors and
	 * set of interfaces are equal
	 * @throws Throwable
	 */
	public void testEqualsWithJdkProxy() throws Throwable {
		TestBean raw = new EqualsTestBean();
		InvokerInterceptor ii = new InvokerInterceptor(raw);

		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, null);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) aop.getProxy();
		assertTrue("proxy equals itself", tb.equals(tb));
		assertTrue("proxy isn't equal to proxied object", !tb.equals(raw));
		//test null eq
		assertTrue("tb.equals(null) is false", !tb.equals(null));
		assertTrue("test equals proxy", tb.equals(aop));

		// Test with AOP proxy with additional interceptor
		ProxyConfig pc2 = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, null);
		pc2.addInterceptor(new DebugInterceptor());
		assertTrue(!tb.equals(new AopProxy(pc2)));

		// Test with any old dynamic proxy
		assertTrue(!tb.equals(Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { ITestBean.class }, new InvocationHandler() {
			public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
				throw new UnsupportedOperationException("invoke");
			}
		})));
	}

	public void testCanAttach() throws Throwable {
		final TrapInvocationInterceptor tii = new TrapInvocationInterceptor();

		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, false, null);
		pc.addInterceptor(tii);
		pc.addInterceptor(new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				assertTrue("Saw same interceptor", invocation == tii.invocation);
				return null;
			}
		});
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) aop.getProxy();
		tb.getSpouse();
		assertTrue(tii.invocation != null);
		
		// TODO strengthen this
	//	assertTrue(tii.invocation.getProxy() == tb);
		assertTrue(tii.invocation.getThis() == null);
	}

	/**
	 * TODO also test undeclared: decide what it should do!
	 */
	public void testDeclaredException() throws Throwable {
		final Exception ex = new Exception();
		// Test return value
		MethodInterceptor mi = new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				throw ex;
			}
		};
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class }, true, null);
		pc.addInterceptor(mi);
		AopProxy aop = new AopProxy(pc);

		try {
			ITestBean tb = (ITestBean) aop.getProxy();
			// Note: exception param below isn't used
			tb.exceptional(ex);
			fail("Should have thrown exception raised by interceptor");
		} catch (Exception thrown) {
			assertTrue("exception matches: not " + thrown, ex == thrown);
		}
	}

	public void testTargetCanGetInvocation() throws Throwable {
		final ContextTestBean target = new ContextTestBean();
		ProxyConfig pc = new DefaultProxyConfig(new Class[] { ITestBean.class, IOther.class }, true, null);
		TrapInvocationInterceptor tii = new TrapInvocationInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
					// Assert that target matches BEFORE invocation returns
				assertTrue(invocation.getThis() == target);
				return super.invoke(invocation);
			}
		};
		pc.addInterceptor(tii);
		InvokerInterceptor ii = new InvokerInterceptor(target);
		pc.addInterceptor(ii);
		AopProxy aop = new AopProxy(pc);

		ITestBean tb = (ITestBean) aop.getProxy();
		tb.getName();
		assertTrue(tii.invocation == target.invocation);
		assertTrue(target.invocation.getThis() == target);
		assertTrue(target.invocation.getMethod().getDeclaringClass() == ITestBean.class);
		//assertTrue(target.invocation.getProxy() == tb);

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
	
	

	/**
	 * Test stateful interceptor
	 * @throws Throwable
	 */
	public void testMixin() throws Throwable {
		TestBean tb = new TestBean();
		ProxyFactory pc = new ProxyFactory(new Class[] { Lockable.class, ITestBean.class });
		pc.addInterceptor(new LockMixin());
		pc.addInterceptor(new InvokerInterceptor(tb));
		
		int newAge = 65;
		ITestBean itb = (ITestBean) pc.getProxy();
		itb.setAge(newAge);
		assertTrue(itb.getAge() == newAge);

		Lockable lockable = (Lockable) itb;
		assertFalse(lockable.locked());
		lockable.lock();
		
		assertTrue(itb.getAge() == newAge);
		try {
			itb.setAge(1);
			fail("Setters should fail when locked");
		} 
		catch (LockedException ex) {
			// ok
		}
		assertTrue(itb.getAge() == newAge);
		
		// Unlock
		assertTrue(lockable.locked());
		lockable.unlock();
		itb.setAge(1);
		assertTrue(itb.getAge() == 1);
	}
	
	
	public void testReplaceArgument() throws Throwable {
		TestBean tb = new TestBean();
		ProxyFactory pc = new ProxyFactory(new Class[] { ITestBean.class });
		pc.addMethodPointcut(new StringSetterNullReplacementPointcut());
		pc.addInterceptor(new InvokerInterceptor(tb));
	
		ITestBean t = (ITestBean) pc.getProxy();
		int newAge = 5;
		t.setAge(newAge);
		assertTrue(t.getAge() == newAge);
		String newName = "greg";
		t.setName(newName);
		assertEquals(newName, t.getName());
		
		t.setName(null);
		// Null replacement magic should work
		assertTrue(t.getName().equals(""));
	}
	
	/**
	 * Fires on setter methods that take a string. Replaces null arg
	 * with ""
	 */
	public static class StringSetterNullReplacementPointcut implements DynamicMethodPointcut {
		
		private MethodInterceptor cleaner = new MethodInterceptor() {
			public Object invoke(MethodInvocation mi) throws Throwable {
				// We know it can only be invoked if there's a single parameter of type string
				mi.setArgument(0, "");
				return mi.proceed();
			}
		};
		
		public boolean applies(Method m, Object[] args, AttributeRegistry attributeRegistry) {
			return args[0] == null;
		}

		public boolean applies(Method m, AttributeRegistry attributeRegistry) {
			return m.getName().startsWith("set") &&
				m.getParameterTypes().length == 1 &&
				m.getParameterTypes()[0].equals(String.class);
		}

		public MethodInterceptor getInterceptor() {
			return this.cleaner;
		}

}
	
	
	
	public void testDynamicMethodPointcutThatAlwaysAppliesStatically() throws Throwable {
		TestBean tb = new TestBean();
		ProxyFactory pc = new ProxyFactory(new Class[] { ITestBean.class });
		TestDynamicPointcut dp = new TestDynamicPointcut(new DebugInterceptor(), "getAge");
		pc.addMethodPointcut(dp);
		pc.addInterceptor(new InvokerInterceptor(tb));
		ITestBean it = (ITestBean) pc.getProxy();
		assertEquals(dp.count, 0);
		int age = it.getAge();
		assertEquals(dp.count, 1);
		it.setAge(11);
		assertEquals(it.getAge(), 11);
		assertEquals(dp.count, 2);
	}
	
	public void testDynamicMethodPointcutThatAppliesStaticallyOnlyToSetters() throws Throwable {
		TestBean tb = new TestBean();
		ProxyFactory pc = new ProxyFactory(new Class[] { ITestBean.class });
		// Could apply dynamically to getAge/setAge but not to getName
		TestDynamicPointcut dp = new TestDynamicPointcutForSettersOnly(new DebugInterceptor(), "Age");
		pc.addMethodPointcut(dp);
		pc.addInterceptor(new InvokerInterceptor(tb));
		ITestBean it = (ITestBean) pc.getProxy();
		assertEquals(dp.count, 0);
		int age = it.getAge();
		// Statically vetoed
		assertEquals(0, dp.count);
		it.setAge(11);
		assertEquals(it.getAge(), 11);
		assertEquals(dp.count, 1);
		// Applies statically but not dynamically
		it.setName("joe");
		assertEquals(dp.count, 1);
	}
	
	
	public void testStaticMethodPointcut() throws Throwable {
		TestBean tb = new TestBean();
		ProxyFactory pc = new ProxyFactory(new Class[] { ITestBean.class });
		TestStaticPointcut sp = new TestStaticPointcut(new DebugInterceptor(), "getAge");
		pc.addMethodPointcut(sp);
		pc.addInterceptor(new InvokerInterceptor(tb));
		ITestBean it = (ITestBean) pc.getProxy();
		assertEquals(sp.count, 0);
		int age = it.getAge();
		assertEquals(sp.count, 1);
		it.setAge(11);
		assertEquals(it.getAge(), 11);
		assertEquals(sp.count, 2);
	}
	
	// TODO AlwaysInvoked is static
	
	private static class TestDynamicPointcut extends AbstractMethodPointcut implements DynamicMethodPointcut {
		
		private String pattern;
		private int count;
		
		public TestDynamicPointcut(MethodInterceptor mi, String pattern) {
			super(mi);
			this.pattern = pattern;
		}
		/**
		 * @see com.interface21.aop.framework.DynamicMethodPointcut#applies(java.lang.reflect.Method, java.lang.Object[], org.aopalliance.AttributeRegistry)
		 */
		public boolean applies(Method m, Object[] args, AttributeRegistry attributeRegistry) {
			boolean run = m.getName().indexOf(pattern) != -1;
			if (run) ++count;
			return run;
		}

		/**
		 * @see com.interface21.aop.framework.DynamicMethodPointcut#couldApply(java.lang.reflect.Method, org.aopalliance.intercept.AttributeRegistry)
		 */
		public boolean applies(Method m, AttributeRegistry attributeRegistry) {
			return true;
		}
	}
	
	private static class TestDynamicPointcutForSettersOnly extends TestDynamicPointcut {
		public TestDynamicPointcutForSettersOnly(MethodInterceptor mi, String pattern) {
			super(mi, pattern);
		}
		
		public boolean applies(Method m, AttributeRegistry attributeRegistry) {
			return m.getName().startsWith("set");
		}
	}
	
	private static class TestStaticPointcut extends AbstractMethodPointcut implements StaticMethodPointcut {
		
		private String pattern;
		private int count;
	
		public TestStaticPointcut(MethodInterceptor mi, String pattern) {
			super(mi);
			this.pattern = pattern;
		}
		public boolean applies(Method m, AttributeRegistry attributeRegistry) {
			boolean run = m.getName().indexOf(pattern) != -1;
			if (run) ++count;
			return run;
		}

	}


	private static class TrapInvocationInterceptor implements MethodInterceptor {

		public MethodInvocation invocation;

		public Object invoke(MethodInvocation invocation) throws Throwable {
			this.invocation =  invocation;
			return invocation.proceed();
		}
	}

	private static class ContextTestBean extends TestBean {

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

	public static class EqualsTestBean extends TestBean {

		public ITestBean getSpouse() {
			return this;
		}
	};

}
