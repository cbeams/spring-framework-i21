/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.aop.framework;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.aopalliance.intercept.AspectException;
import org.aopalliance.intercept.AttributeRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.interface21.aop.attributes.Attrib4jAttributeRegistry;
import com.interface21.beans.TestBean;

/**
 * TODO COULD REFACTOR TO BE GENERIC
 * @author Rod Johnson
 * @since 14-Mar-2003
 * @version $Revision$
 */
public class MethodInvocationTests extends TestCase {
	
	/*
	public static MethodInvocation testInvocation(Object o, String methodName, Class[] args, Interceptor[] interceptors) throws Exception {
		Method m = o.getClass().getMethod(methodName, args);
		MethodInvocationImpl invocation = new MethodInvocationImpl(null, null, m.getDeclaringClass(), 
	m, null, interceptors, // list
new Attrib4jAttributeRegistry());
	return invocation;
}*/

	/**
	 * Constructor for MethodInvocationTests.
	 * @param arg0
	 */
	public MethodInvocationTests(String arg0) {
		super(arg0);
	}

	public void testNullInterceptor() throws Exception {
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		Method m = Object.class.getMethod("hashCode", null);
		Object proxy = new Object();
		try {
				MethodInvocationImpl invocation = new MethodInvocationImpl(proxy, null, m.getDeclaringClass(), //?
		m, null, null, // could customize here
	r);
			fail("Shouldn't be able to create methodInvocationImpl with null interceptors");
		} catch (AopConfigException ex) {
		}
	}

	public void testEmptyInterceptorList() throws Exception {
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		Method m = Object.class.getMethod("hashCode", null);
		Object proxy = new Object();
		try {
				MethodInvocationImpl invocation = new MethodInvocationImpl(proxy, null, m.getDeclaringClass(), //?
		m, null, new LinkedList(), // list
	r);
			fail("Shouldn't be able to create methodInvocationImpl with no interceptors");
		} catch (AopConfigException ex) {
		}
	}

	public void testValidInvocation() throws Throwable {
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		Method m = Object.class.getMethod("hashCode", null);
		Object proxy = new Object();
		final Object returnValue = new Object();
		List is = new LinkedList();
		is.add(new AlwaysInvoked(new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				return returnValue;
			}
		}));
			MethodInvocationImpl invocation = new MethodInvocationImpl(proxy, null, m.getDeclaringClass(), //?
		m, null, is, // list
	r);
		Object rv = invocation.proceed();
		assertTrue("correct response", rv == returnValue);
	}

	public void testLimits() throws Throwable {
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		Method m = Object.class.getMethod("hashCode", null);
		Object proxy = new Object();
		final Object returnValue = new Object();
		List is = new LinkedList();
		MethodInterceptor interceptor = new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				return returnValue;
			}
		};
		is.add(new AlwaysInvoked(interceptor));

			MethodInvocationImpl invocation = new MethodInvocationImpl(proxy, null, m.getDeclaringClass(), //?
		m, null, is, // list
	r);
		assertTrue(invocation.getArgumentCount() == 0);
		assertTrue(invocation.getAttributeRegistry() == r);
		//assertTrue(invocation.getCurrentInterceptorIndex() == 0);
		assertTrue(invocation.getInterceptor(0) == interceptor);
		Object rv = invocation.proceed();
		assertTrue("correct response", rv == returnValue);

		assertTrue(invocation.getCurrentInterceptorIndex() == 0);
		assertTrue(invocation.getNumberOfInterceptors() == 1);

		// Now it gets interesting
		try {
			invocation.proceed();
			fail("Shouldn't allow illegal invocation number");
		} catch (AspectException ex) {
			// Shouldn't have changed position in interceptor chain
			assertTrue(
				"Shouldn't have changed current interceptor index",
				invocation.getCurrentInterceptorIndex() == 0);
		}

		try {
			invocation.getInterceptor(666);
			fail("Shouldn't allow illegal interceptor get");
		} catch (AspectException ex) {
		}
	}

	public void testAttachments() throws Throwable {
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		Method m = Object.class.getMethod("hashCode", null);
		Object proxy = new Object();
		final Object returnValue = new Object();
		List is = new LinkedList();
		is.add(new AlwaysInvoked(new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
				return returnValue;
			}
		}));

			MethodInvocationImpl invocation = new MethodInvocationImpl(proxy, null, m.getDeclaringClass(), //?
		m, null, is, // list
	r);

		assertTrue("no bogus attachment", invocation.getAttachment("bogus") == null);
		String name = "foo";
		Object val = new Object();
		Object val2 = "foo";
		assertTrue("New attachment returns null", null == invocation.addAttachment(name, val));
		assertTrue(invocation.getAttachment(name) == val);
		assertTrue("Replace returns correct value", val == invocation.addAttachment(name, val2));
		assertTrue(invocation.getAttachment(name) == val2);
		assertTrue("Can clear by attaching null", val2 == invocation.addAttachment(name, null));
	}

	/**
	 * ToString on target can cause failure
	 * @throws Throwable
	 */
	public void testToStringDoesntHitTarget() throws Throwable {
		AttributeRegistry r = new Attrib4jAttributeRegistry();
		Object target = new TestBean() {
			public String toString() {
				throw new UnsupportedOperationException("toString");
			}
		};
		final Object returnValue = new Object();
		List is = new LinkedList();
		is.add(new AlwaysInvoked(new InvokerInterceptor(target)));

		Method m = Object.class.getMethod("hashCode", null);
		Object proxy = new Object();
			MethodInvocationImpl invocation = new MethodInvocationImpl(proxy, target, m.getDeclaringClass(), //?
		m, null, is, // list
	r);

		// if it hits target the test will fail with the UnsupportedOpException
		// in the inner class above
		invocation.toString();
	}
}