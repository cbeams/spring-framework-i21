package com.interface21.remoting;

import java.net.ConnectException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import com.interface21.beans.ITestBean;
import com.interface21.beans.TestBean;
import com.interface21.remoting.caucho.BurlapProxyFactoryBean;
import com.interface21.remoting.caucho.HessianProxyFactoryBean;
import com.interface21.remoting.rmi.RmiProxyFactoryBean;
import com.interface21.remoting.support.RemoteProxyFactoryBean;

/**
 * @author jho
 * @since 16.05.2003
 */
public class RemotingTestSuite extends TestCase {

	public RemotingTestSuite(String msg) {
		super(msg);
	}

	public void testHessianProxyFactoryBean() throws Exception {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		try {
			factory.setServiceInterface(TestBean.class);
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
		factory.setServiceInterface(ITestBean.class);
		factory.setServiceUrl("http://localhost/testbean");
		factory.setUsername("test");
		factory.setPassword("bean");
		factory.afterPropertiesSet();
		assertTrue("Correct singleton value", factory.isSingleton());
		assertTrue("No property values", factory.getPropertyValues() == null);

		ITestBean bean = (ITestBean) factory.getObject();
		try {
			bean.setName("test");
			fail("Should have thrown RemoteAccessException");
		}
		catch (RemoteAccessException ex) {
			// expected
			assertTrue("Correct remote exception", ex.getRootCause().getClass().equals(ConnectException.class));
		}
	}

	public void testBurlapProxyFactoryBean() throws Exception {
		BurlapProxyFactoryBean factory = new BurlapProxyFactoryBean();
		factory.setServiceInterface(ITestBean.class);
		factory.setServiceUrl("http://localhost/testbean");
		factory.afterPropertiesSet();
		ITestBean bean = (ITestBean) factory.getObject();
		try {
			bean.setName("test");
			fail("Should have thrown RemoteAccessException");
		}
		catch (RemoteAccessException ex) {
			// expected
			assertTrue("Correct remote exception", ex.getRootCause().getClass().equals(ConnectException.class));
		}
	}

	public void testRmiProxyFactoryBean() throws Exception {
		RmiProxyFactoryBean factory = new RmiProxyFactoryBean();
		factory.setServiceInterface(ITestBean.class);
		factory.setServiceUrl("rmi://localhost/testbean");
		try {
			factory.afterPropertiesSet();
			fail("Should have thrown RemoteAccessException");
		}
		catch (RemoteAccessException ex) {
			// expected
			assertTrue("Correct remote exception", ex.getRootCause().getClass().equals(java.rmi.ConnectException.class));
		}
	}

	public void testInvalidProxyReturned() throws Exception {
		RemoteProxyFactoryBean factory = new RemoteProxyFactoryBean() {
			protected Object createProxy() throws MalformedURLException, RemoteAccessException {
				return "mock";
			}
		};
		factory.setServiceInterface(ITestBean.class);
		factory.setServiceUrl("rmi://localhost/testbean");
		try {
			factory.afterPropertiesSet();
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

}
