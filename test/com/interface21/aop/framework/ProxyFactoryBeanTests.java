/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.aop.framework;

import java.io.InputStream;
import java.lang.reflect.Proxy;

import org.aopalliance.Invocation;
import org.aopalliance.MethodInterceptor;

import com.interface21.aop.framework.AopConfigException;
import com.interface21.aop.framework.ProxyFactoryBean;
import com.interface21.aop.interceptor.misc.DebugInterceptor;
import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.support.XmlBeanFactory;
import com.interface21.core.TimeStamped;

import junit.framework.TestCase;

/**
 * 
 * @author Rod Johnson
 * @since 13-Mar-2003
 * @version $Revision$
 */
public class ProxyFactoryBeanTests extends TestCase {
	
	
	private BeanFactory factory;

	/**
	 * Constructor for ProxyFactoryBeanTests.
	 * @param arg0
	 */
	public ProxyFactoryBeanTests(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// Load from classpath, NOT a file path
		InputStream is = getClass().getResourceAsStream("test.xml");
		this.factory = new XmlBeanFactory(is, null);
	}


	public void testIsDynamicProxy() {
		ITestBean test1 = (ITestBean) factory.getBean("test1");
		assertTrue("test1 is a dynamic proxy", Proxy.isProxyClass(test1.getClass()));
	}
	
	public void testSingletonInstancesAreEqual() {
		ITestBean test1 = (ITestBean) factory.getBean("test1");
		ITestBean test1_1 = (ITestBean) factory.getBean("test1");
		assertTrue("Singleton instances ==", test1 == test1_1);
	}
	
	// TODO independent instances
	
	public void testPrototypeInstancesAreNotEqual() {
		ITestBean test2 = (ITestBean) factory.getBean("test2");
		ITestBean test2_1 = (ITestBean) factory.getBean("test2");
		assertTrue("Prototype instances !=", test2 != test2_1);
		assertTrue("Prototype instances equal", test2.equals(test2_1));
	}
	
	/**
	 * Not a singleton:
	 * this might break factory init
	 * SCREWS WHOLE FACTORY: this is probably OK
	 */
//	public void testInvalidPrototype() {
//		try {
//			ITestBean test2 = (ITestBean) factory.getBean("noInterceptorPrototype");
//			fail("Shouldn't have returned invalid prototype");
//		}
//		catch (AopConfigException ex) {
//			// Ok
//		}
//		
//	}

	public void testCanGetFactoryReferenceAndManipulate() {
		ProxyFactoryBean config = (ProxyFactoryBean) factory.getBean("&test1");
		assertTrue(config.getExposeInvocation() == false);
		assertTrue(config.getInterceptors().length == 2);
		
		ITestBean tb = (ITestBean) factory.getBean("test1");
		// no exception 
		tb.hashCode();
		
		final Exception ex = new UnsupportedOperationException("invoke");
		// Add evil interceptor to head of list
		config.addInterceptor(0, new MethodInterceptor() {
			public Object invoke(Invocation invocation) throws Throwable {
				throw ex;
			}
		});
		assertTrue(config.getInterceptors().length == 3);
		
		tb = (ITestBean) factory.getBean("test1"); 
		try {
			// Will fail now
			tb.hashCode();
			fail("Evil interceptor added programmatically should fail all method calls");
		} 
		catch (Exception thrown) {
			assertTrue(thrown == ex);
		}
	}
	
	/**
	 * Do against prototype
	 *
	 */
	public void testCanAddAndRemoveAspectInterfaces() {
		try {
			TimeStamped ts = (TimeStamped) factory.getBean("test2");
			fail("Shouldn't implement TimeStamped before manipulation");
		}
		catch (ClassCastException ex) {
		}
		
		ProxyFactoryBean config = (ProxyFactoryBean) factory.getBean("&test2");
		long time = 666L;
		TimestampAspectInterceptor ti = new TimestampAspectInterceptor();
		ti.setTime(time);
		// add to front of queue
		int oldCount = config.getInterceptors().length;
		config.addInterceptor(0, ti);
		assertTrue(config.getInterceptors().length == oldCount + 1);
		
		TimeStamped ts = (TimeStamped) factory.getBean("test2");
		assertTrue(ts.getTimeStamp() == time);
		
		// Can remove
		config.removeInterceptor(ti);
		assertTrue(config.getInterceptors().length == oldCount);
		
		try {
			// Existing reference will fail
			ts.getTimeStamp();
			fail("Existing object won't implement this interface any more");
		}
		catch (RuntimeException ex) {
		}
		
		try {
			ts = (TimeStamped) factory.getBean("test2");
			fail("Should no longer implement TimeStamped");
		}
		catch (ClassCastException ex) {
		}
		
		// Now check non-effect of removing interceptor that isn't there
		config.removeInterceptor(new DebugInterceptor());
		assertTrue(config.getInterceptors().length == oldCount);
		
		ITestBean it = (ITestBean) ts;
		DebugInterceptor debugInterceptor = new DebugInterceptor();
		config.addInterceptor(0, debugInterceptor);
		it.getSpouse();
		assertTrue(debugInterceptor.getCount() == 1);
		config.removeInterceptor(debugInterceptor);
		it.getSpouse();
		// not invoked again
		assertTrue(debugInterceptor.getCount() == 1);
	}
	
	// test get factory!
	
	// Use mock object interceptor!?
	
	// test singletons
}
