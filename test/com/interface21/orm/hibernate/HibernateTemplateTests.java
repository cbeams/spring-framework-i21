package com.interface21.orm.hibernate;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.ObjectDeletedException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.PersistentObjectException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.StaleObjectStateException;
import net.sf.hibernate.TransientObjectException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.type.Type;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.beans.TestBean;
import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.dao.InvalidDataAccessResourceUsageException;
import com.interface21.dao.OptimisticLockingFailureException;

/**
 * @author Juergen Hoeller
 * @since 06.05.2003
 */
public class HibernateTemplateTests extends TestCase {

	public void testTemplateExecuteWithNotAllowCreate() {
		HibernateTemplate ht = new HibernateTemplate();
		ht.setAllowCreate(false);
		try {
			ht.execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException{
					return null;
				}
			});
			fail("Should have thrown IllegalStateException");
		}
		catch (IllegalStateException ex) {
			// expected
		}
	}

	public void testTemplateExecuteWithNotAllowCreateAndThreadBound() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		ht.setAllowCreate(false);
		SessionFactoryUtils.getThreadObjectManager().bindThreadObject(sf, new SessionHolder(session));
		final List l = new ArrayList();
		l.add("test");
		List result = ht.executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return l;
			}
		});
		assertTrue("Correct result list", result == l);
		SessionFactoryUtils.getThreadObjectManager().removeThreadObject(sf);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateExecuteWithNewSession() {
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

		HibernateTemplate ht = new HibernateTemplate(sf);
		assertTrue("Correct allowCreate default", ht.isAllowCreate());
		assertTrue("Correct forceFlush default", !ht.isForceFlush());
		final List l = new ArrayList();
		l.add("test");
		List result = ht.executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return l;
			}
		});
		assertTrue("Correct result list", result == l);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateExecuteWithEntityInterceptor() {
		MockControl interceptorControl = EasyMock.controlFor(net.sf.hibernate.Interceptor.class);
		Interceptor entityInterceptor = (Interceptor) interceptorControl.getMock();
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

		HibernateTemplate ht = new HibernateTemplate(sf);
		ht.setEntityInterceptor(entityInterceptor);
		final List l = new ArrayList();
		l.add("test");
		List result = ht.executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return l;
			}
		});
		assertTrue("Correct result list", result == l);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateFind1() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		List list = new ArrayList();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.find("some query");
			sessionControl.setReturnValue(list, 1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		List result = ht.find("some query");
		assertTrue("Correct list", result == list);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateFind2() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		List list = new ArrayList();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.find("some query", "myvalue", Hibernate.STRING);
			sessionControl.setReturnValue(list, 1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		List result = ht.find("some query", "myvalue", Hibernate.STRING);
		assertTrue("Correct list", result == list);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateFind3() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		List list = new ArrayList();
		Object[] values = new Object[] {"myvalue1", new Integer(2)};
		Type[] types = new Type[]{Hibernate.STRING, Hibernate.INTEGER};
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.find("some query", values, types);
			sessionControl.setReturnValue(list, 1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		List result = ht.find("some query", values, types);
		assertTrue("Correct list", result == list);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateLoad() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		TestBean tb = new TestBean();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.load(TestBean.class, "");
			sessionControl.setReturnValue(tb, 1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		Object result = ht.load(TestBean.class, "");
		assertTrue("Correct result", result == tb);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateLoadWithNotFound() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.load(TestBean.class, "");
			sessionControl.setThrowable(new ObjectNotFoundException("msg", "id", TestBean.class));
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		Object result = ht.load(TestBean.class, "");
		assertTrue("Correct result", result == null);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateSaveOrUpdate() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		TestBean tb = new TestBean();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.saveOrUpdate(tb);
			sessionControl.setVoidCallable(1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		ht.saveOrUpdate(tb);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateSave() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		TestBean tb = new TestBean();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.save(tb);
			sessionControl.setReturnValue(new Integer(0), 1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		assertEquals("Correct return value", ht.save(tb), new Integer(0));
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateUpdate() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		TestBean tb = new TestBean();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.update(tb);
			sessionControl.setVoidCallable(1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		ht.update(tb);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateDelete() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		TestBean tb = new TestBean();
		try {
			sf.openSession();
			sfControl.setReturnValue(session, 1);
			session.delete(tb);
			sessionControl.setVoidCallable(1);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();

		HibernateTemplate ht = new HibernateTemplate(sf);
		ht.delete(tb);
		sfControl.verify();
		sessionControl.verify();
	}

	public void testTemplateExceptions() {
		try {
			createTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					throw new JDBCException(null);
				}
			});
		}
		catch (HibernateJdbcException ex) {
		}
		catch (Exception ex) {
			fail("Should have thrown HibernateJdbcException");
		}

		try {
			createTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					throw new QueryException("");
				}
			});
		}
		catch (InvalidDataAccessResourceUsageException ex) {
		}
		catch (Exception ex) {
			fail("Should have thrown InvalidDataAccessResourceUsageException");
		}

		try {
			createTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					throw new StaleObjectStateException(TestBean.class, "");
				}
			});
		}
		catch (OptimisticLockingFailureException ex) {
		}
		catch (Exception ex) {
			fail("Should have thrown OptimisticLockingFailureException");
		}

		try {
			createTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					throw new PersistentObjectException("");
				}
			});
		}
		catch (InvalidDataAccessApiUsageException ex) {
		}
		catch (Exception ex) {
			fail("Should have thrown InvalidDataAccessApiUsageException");
		}

		try {
			createTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					throw new TransientObjectException("");
				}
			});
		}
		catch (InvalidDataAccessApiUsageException ex) {
		}
		catch (Exception ex) {
			fail("Should have thrown InvalidDataAccessApiUsageException");
		}

		try {
			createTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					throw new ObjectDeletedException("", "");
				}
			});
		}
		catch (InvalidDataAccessApiUsageException ex) {
		}
		catch (Exception ex) {
			fail("Should have thrown InvalidDataAccessApiUsageException");
		}

		try {
			createTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					throw new HibernateException("");
				}
			});
		}
		catch (HibernateSystemException ex) {
		}
		catch (Exception ex) {
			fail("Should have thrown HibernateSystemException");
		}
	}

	private HibernateTemplate createTemplate() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		MockControl sessionControl = EasyMock.controlFor(Session.class);
		Session session = (Session) sessionControl.getMock();
		try {
			sf.openSession();
			sfControl.setReturnValue(session);
			session.flush();
			sessionControl.setVoidCallable(1);
			session.close();
			sessionControl.setReturnValue(null, 1);
		}
		catch (HibernateException ex) {
		}
		sfControl.activate();
		sessionControl.activate();
		return new HibernateTemplate(sf);
	}

}
