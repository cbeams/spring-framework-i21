package com.interface21.orm.jdo;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;
import org.aopalliance.intercept.AttributeRegistry;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.Invocation;
import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.easymock.MockControl;

/**
 * @author Juergen Hoeller
 */
public class JdoInterceptorTests extends TestCase {

	public void testInterceptor() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		pmf.getPersistenceManager();
		pmfControl.setReturnValue(pm, 1);
		pm.close();
		pmControl.setReturnValue(null, 1);
		pmfControl.activate();
		pmControl.activate();

		JdoInterceptor interceptor = new JdoInterceptor();
		interceptor.setPersistenceManagerFactory(pmf);
		try {
			interceptor.invoke(new TestInvocation(pmf));
		}
		catch (Throwable t) {
			fail("Should not have thrown Throwable: " + t.getMessage());
		}

		pmfControl.verify();
		pmControl.verify();
	}

	public void testInterceptorWithPrebound() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		MockControl pmControl = EasyMock.controlFor(PersistenceManager.class);
		PersistenceManager pm = (PersistenceManager) pmControl.getMock();
		pmfControl.activate();
		pmControl.activate();

		PersistenceManagerFactoryUtils.getThreadObjectManager().bindThreadObject(pmf, new PersistenceManagerHolder(pm));
		JdoInterceptor interceptor = new JdoInterceptor();
		interceptor.setPersistenceManagerFactory(pmf);
		try {
			interceptor.invoke(new TestInvocation(pmf));
		}
		catch (Throwable t) {
			fail("Should not have thrown Throwable: " + t.getMessage());
		}

		pmfControl.verify();
		pmControl.verify();
	}


	private static class TestInvocation implements MethodInvocation {

		private PersistenceManagerFactory persistenceManagerFactory;

		public TestInvocation(PersistenceManagerFactory persistenceManagerFactory) {
			this.persistenceManagerFactory = persistenceManagerFactory;
		}

		public Object proceed() throws Throwable {
			if (!PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(this.persistenceManagerFactory)) {
				throw new IllegalStateException("PersistenceManager not bound");
			}
			return null;
		}

		public Object[] getArguments() {
			return null;
		}

		public int getCurrentInterceptorIndex() {
			return 0;
		}

		public int getNumberOfInterceptors() {
			return 0;
		}

		public Interceptor getInterceptor(int i) {
			return null;
		}

		public Method getMethod() {
			return null;
		}

		public AccessibleObject getStaticPart() {
			return getMethod();
		}

		public Object getArgument(int i) {
			return null;
		}

		public void setArgument(int i, Object handler) {
		}

		public int getArgumentCount() {
			return 0;
		}

		public Object getThis() {
			return null;
		}

		public Object getProxy() {
			return null;
		}

		public Object addAttachment(String msg, Object handler) {
			return null;
		}

		public Object getAttachment(String msg) {
			return null;
		}

		public Invocation cloneInstance() {
			return null;
		}

		public AttributeRegistry getAttributeRegistry() {
			return null;
		}
	}

}
