/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.aop.framework;

import java.lang.reflect.Method;
import java.util.LinkedList;

import javax.servlet.ServletException;

import org.aopalliance.MethodInterceptor;

import com.interface21.beans.IOther;
import com.interface21.beans.TestBean;

import junit.framework.TestCase;

/**
 * 
 * @author Rod Johnson
 * @since 14-Mar-2003
 * @version $Revision$
 */
public class InvokerInterceptorTests extends TestCase {

	/**
	 * Constructor for InvocationInterceptorTests.
	 * @param arg0
	 */
	public InvokerInterceptorTests(String arg0) {
		super(arg0);
	}

	public void testVoidNoException() throws Throwable {
		class Test implements IOther {
			boolean done;
			public void absquatulate() {
				done = true;
			}
		}
		Test t = new Test();
		InvokerInterceptor ii = new InvokerInterceptor(t);

		Method m = IOther.class.getMethod("absquatulate", null);
			MethodInvocationImpl invocation = new MethodInvocationImpl(null, t, m.getDeclaringClass(), //?
	m, null, new MethodInterceptor[] { ii }, // list
		null);
		Object ret = ii.invoke(invocation);
		assertTrue(ret == null);
		assertTrue(t.done);
	}

	public void testCheckedException() throws Throwable {
		testException(new ServletException());
	}
	
	public void testRuntimeException() throws Throwable {
		testException(new NullPointerException());
	}
	
	private void testException(final Exception t) throws Throwable {
		class Test implements Demo {
			public Object doSomething() throws Exception {
				throw t;
			}
		}
		Test test = new Test();
		InvokerInterceptor ii = new InvokerInterceptor(test);

		Method m = Demo.class.getMethod("doSomething", null);
			MethodInvocationImpl invocation = new MethodInvocationImpl(null, t, m.getDeclaringClass(), //?
	m, null, new MethodInterceptor[] { ii }, // list
		null);
		try {
			ii.invoke(invocation);
			fail("Should throw exception");
		}
		catch (Exception thrown) {
			assertTrue(thrown == t);
		}
	}

	public interface Demo {
		Object doSomething() throws Exception;
	}


}
