/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.aop.framework;

import org.aopalliance.Interceptor;
import org.aopalliance.Invocation;
import org.aopalliance.MethodInterceptor;
import org.aopalliance.MethodInvocation;

import com.interface21.aop.interceptor.AbstractQaInterceptor;
import com.interface21.aop.interceptor.DebugInterceptor;
import com.interface21.beans.IOther;
import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;
import com.interface21.core.TimeStamped;
import com.interface21.util.StringUtils;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Also tests DefaultProxyConfig superclass
 * @author Rod Johnson
 * @since 14-Mar-2003
 * @version $Revision$
 */
public class ProxyFactoryTests extends TestCase {

	/**
	 * Constructor for ProxyFactoryTests.
	 * @param arg0
	 */
	public ProxyFactoryTests(String arg0) {
		super(arg0);
	}

	public void testNullTarget() {

		try {
			// Use the constructor taking Object
			new ProxyFactory((Object) null);
			fail("Should't allow proxy with null target");
		} catch (AopConfigException ex) {
		}
	}

	public static class Concrete {
		public void foo() {
		}
	}

	public void testNoInterfaces() {
		Concrete c = new Concrete();
		try {
			new ProxyFactory(c);
			fail("Should't allow proxy with no interfaces");
		} catch (AopConfigException ex) {
		}
	}
	
	public void testAddRepeatedInterface() {
		TimeStamped tst = new TimeStamped() {
			public long getTimeStamp() {
				throw new UnsupportedOperationException("getTimeStamp");
			}
		};
		ProxyFactory pf = new ProxyFactory(tst);
		// We've already implicitly added this interface.
		// This call should be ignored without error
		pf.addInterface(TimeStamped.class);
		// All cool
		TimeStamped ts = (TimeStamped) pf.getProxy();
	}

	public void testGetsAllInterfaces() throws Exception {
		// Extend to get new interface
		class TestBeanSubclass extends TestBean implements Comparable {
			public int compareTo(Object arg0) {
				throw new UnsupportedOperationException("compareTo");
			}
		};
		TestBeanSubclass raw = new TestBeanSubclass();
		ProxyFactory factory = new ProxyFactory(raw);
		assertTrue("Found 3 interfaces", factory.getProxiedInterfaces().length == 3);
		System.out.println("Proxied interfaces are " + StringUtils.arrayToDelimitedString(factory.getProxiedInterfaces(), ","));
		ITestBean tb = (ITestBean) factory.getProxy();
		assertTrue("Picked up secondary interface", tb instanceof IOther);
				
		raw.setAge(25);
		assertTrue(tb.getAge() == raw.getAge());

		long t = 555555L;
		TimestampIntroductionInterceptor ti = new TimestampIntroductionInterceptor(t);
		
		System.out.println(StringUtils.arrayToDelimitedString(factory.getProxiedInterfaces(), "/"));
		
		factory.addInterceptor(0, ti);
		
		System.out.println(StringUtils.arrayToDelimitedString(factory.getProxiedInterfaces(), "/"));
		

		TimeStamped ts = (TimeStamped) factory.getProxy();
		assertTrue(ts.getTimeStamp() == t);
		// Shouldn't fail;
		 ((IOther) ts).absquatulate();
	}
	
	
	/**
	 * Test that we can't add another interceptor in a chain
	 * that already has a proxy interceptor.
	 * Note that we can end a chain with a non proxy interceptor.
	 *
	 */
	public void testCantAddInterceptorsAfterProxyInterceptor() {
		TestBean raw = new TestBean();
		ProxyFactory factory = new ProxyFactory(raw);
		assertTrue(factory.getMethodPointcuts().size() == 1);
		//assertTrue(factory.getMethodPointcuts().get(0) instanceof InvokerInterceptor);
		try {
			factory.addInterceptor(new DebugInterceptor());
			fail("Shouldn't be able to add an interface after a proxy interceptor");
		}
		catch (AopConfigException ex) {
			// Ok
		}
		
		// Should be able to continue to use it normally
		// despite the attempt to add an interceptor invalidly
		factory.addInterceptor(0, new DebugInterceptor());
		factory.addInterceptor(1, new MethodInterceptor() {
			public Object invoke(MethodInvocation mi) throws Throwable {
				// Check it was invoked in correct position
				// Index must be this index now
				assertTrue("Index should be 1, not " + mi.getCurrentInterceptorIndex(), mi.getCurrentInterceptorIndex() == 1);
				Object ret = mi.invokeNext();
				// Index must have been incremented following this call
				assertTrue(mi.getCurrentInterceptorIndex() == 2);
				return ret;
			}
		});
		
		ITestBean tb = (ITestBean) factory.getProxy();
		assertTrue(tb.getAge() == raw.getAge());
	}
	
	public void testCanOnlyAddMethodInterceptors() {
		ProxyFactory factory = new ProxyFactory(new TestBean());
		factory.addInterceptor(0, new DebugInterceptor());
		try {
			factory.addInterceptor(0, new Interceptor() {
			});
			fail("Should only be able to add MethodInterceptors");
		}
		catch (AopConfigException ex) {
		}
		
		// Check we can still use it
		IOther other = (IOther) factory.getProxy();
		other.absquatulate();
	}
	
	public void testInterceptorInclusionMethods() {
		DebugInterceptor di = new DebugInterceptor();
		DebugInterceptor diUnused = new DebugInterceptor();
		ProxyFactory factory = new ProxyFactory(new TestBean());
		factory.addInterceptor(0, di);
		//factory.addInterceptor(new InvokerInterceptor());
		ITestBean tb = (ITestBean) factory.getProxy();
		assertTrue(factory.interceptorIncluded(di));
		assertTrue(!factory.interceptorIncluded(diUnused));
		assertTrue(factory.countInterceptorsOfType(DebugInterceptor.class) == 1);
		assertTrue(factory.countInterceptorsOfType(InvokerInterceptor.class) == 1);
		assertTrue(factory.countInterceptorsOfType(AbstractQaInterceptor.class) == 0);
	
		factory.addInterceptor(0, diUnused);
		assertTrue(factory.interceptorIncluded(diUnused));
		assertTrue(factory.countInterceptorsOfType(DebugInterceptor.class) == 2);
	}

	/**
	 * Used for profiling
	 *
	 */
//	public void testManyInvocations() {
//		ProxyFactory factory = new ProxyFactory(new TestBean());
//
//		IOther other = (IOther) factory.getProxy();
//		for (int i = 0; i < 100000; i++) {
//			other.absquatulate();
//		}
//	}
//
//	public static void main(String[] args) {
//		TestRunner.run(new TestSuite(ProxyFactoryTests.class));
//	}

}
