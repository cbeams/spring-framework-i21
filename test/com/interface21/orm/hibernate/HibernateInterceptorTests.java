package com.interface21.orm.hibernate;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.aopalliance.intercept.AttributeRegistry;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.Invocation;
import org.aopalliance.intercept.MethodInvocation;
import org.easymock.EasyMock;
import org.easymock.MockControl;

/**
 * @author Juergen Hoeller
 */
public class HibernateInterceptorTests extends TestCase {

	public void testInterceptor() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateInterceptor interceptor = new HibernateInterceptor();
		interceptor.setSessionFactory(sf);
		try {
			interceptor.invoke(new TestInvocation(sf));
		}
		catch (Throwable t) {
			fail("Should not have thrown Throwable: " + t.getMessage());
		}

		sfControl.verify();
		sessionControl.verify();
	}

	public void testInterceptorWithPrebound() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		sfControl.activate();
		sessionControl.activate();

		SessionFactoryUtils.getThreadObjectManager().bindThreadObject(sf, new SessionHolder(session));
		HibernateInterceptor interceptor = new HibernateInterceptor();
		interceptor.setSessionFactory(sf);
		try {
			interceptor.invoke(new TestInvocation(sf));
		}
		catch (Throwable t) {
			fail("Should not have thrown Throwable: " + t.getMessage());
		}

		sfControl.verify();
		sessionControl.verify();
	}

	public void testInterceptorWithPreboundAndForceFlush() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		try {
			session.flush();
			sessionControl.setVoidCallable(1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		SessionFactoryUtils.getThreadObjectManager().bindThreadObject(sf, new SessionHolder(session));
		HibernateInterceptor interceptor = new HibernateInterceptor();
		interceptor.setForceFlush(true);
		interceptor.setSessionFactory(sf);
		try {
			interceptor.invoke(new TestInvocation(sf));
		}
		catch (Throwable t) {
			fail("Should not have thrown Throwable: " + t.getMessage());
		}

		sfControl.verify();
		sessionControl.verify();
	}

	public void testInterceptorWithEntityInterceptor() {
		MockControl interceptorControl = EasyMock.controlFor(net.sf.hibernate.Interceptor.class);
		net.sf.hibernate.Interceptor entityInterceptor = (net.sf.hibernate.Interceptor) interceptorControl.getMock();
		interceptorControl.activate();
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		try {
			sf.openSession(entityInterceptor);
			sfControl.setReturnValue(session, 1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateInterceptor interceptor = new HibernateInterceptor();
		interceptor.setSessionFactory(sf);
		interceptor.setEntityInterceptor(entityInterceptor);
		try {
			interceptor.invoke(new TestInvocation(sf));
		}
		catch (Throwable t) {
			fail("Should not have thrown Throwable: " + t.getMessage());
		}

		sfControl.verify();
		sessionControl.verify();
	}


	private static class TestInvocation implements MethodInvocation {

		private SessionFactory sessionFactory;

		public TestInvocation(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
		}

		public Object proceed() throws Throwable {
			if (!SessionFactoryUtils.getThreadObjectManager().hasThreadObject(this.sessionFactory)) {
				throw new IllegalStateException("Session not bound");
			}
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
			return null;
		}

		public Object getArgument(int i) {
			return null;
		}

		public Object[] getArguments() {
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
