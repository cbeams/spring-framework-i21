package com.interface21.orm.hibernate;

import net.sf.hibernate.SessionFactory;
import org.aopalliance.MethodInterceptor;
import org.aopalliance.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.dao.CleanupFailureDataAccessException;

/**
 * This interceptor binds a new Hibernate Session to the thread before a method
 * call, closing and removing it afterwards in case of any method outcome.
 * If there already was a pre-bound Session (e.g. from HibernateTransactionManager,
 * or from a surrounding Hibernate-intercepted method), the interceptor simply
 * takes part in it.
 *
 * <p>Application code must retrieve a Hibernate Session via SessionFactoryUtils'
 * getSession method, to be able to detect a thread-bound Session. It is preferable
 * to use getSession with allowCreate=false, as the code relies on the interceptor
 * to provide proper Session handling. Typically the code will look as follows:
 *
 * <p><code>
 * public void doHibernateAction() {<br>
 * &nbsp;&nbsp;Session session = SessionFactoryUtils.getSession(this.sessionFactory, false);<br>
 * &nbsp;&nbsp;try {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;catch (HibernateException ex) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;throw SessionFactoryUtils.convertHibernateAccessException(ex);<br>
 * &nbsp;&nbsp;}<br>
 * }
 * </code>
 *
 * <p>Note that the application must care about handling HibernateExceptions itself,
 * preferably via delegating to SessionFactoryUtils' convertHibernateAccessException
 * that converts them to ones that are compatible with the com.interface21.dao
 * exception hierarchy (like HibernateTemplate does).
 *
 * <p>Unfortunately, this interceptor cannot convert checked HibernateExceptions
 * to unchecked dao ones automatically. The intercepted method would have to throw
 * HibernateException to be able to achieve this - thus the caller would still have
 * to catch or rethrow it, even if it will never be thrown if intercepted.
 *
 * <p>This class can be considered a declarative alternative to HibernateTemplate's
 * callback approach. The advantages are:
 * <ul>
 * <li>no anonymous classes necessary for callback implementations;
 * <li>the possibility to throw any application exceptions from within data access code.
 * </ul>
 * The drawbacks are:
 * <ul>
 * <li>the dependency on interceptor configuration;
 * <li>the delegating try/catch blocks.
 * </ul>
 *
 * <p>Note: This class, like all of Spring's Hibernate support, requires
 * Hibernate 2.0 (initially developed with RC1).
 *
 * @author Juergen Hoeller
 * @since 13.06.2003
 * @see SessionFactoryUtils#getSession
 * @see HibernateTransactionManager
 * @see HibernateTemplate
 */
public class HibernateInterceptor implements MethodInterceptor {

	private final Log logger = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;

	private boolean forceFlush = false;

	/**
	 * Set the SessionFactory that this instance should create thread-bound Sessions for.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * If a flush of the Hibernate Session should be forced after executing the
	 * target method. By default, the interceptor will only trigger a flush if not
	 * in a Hibernate transaction, as a final flush will occur on commit anyway.
	 * <p>A forced flush leads to immediate synchronization with the database,
	 * even if in a Hibernate transaction. This causes inconsistencies to show up
	 * and throw a respective exception immediately. But the drawbacks are:
	 * <ul>
	 * <li>additional communication roundtrips with the database, instead of a
	 * single batch at transaction commit;
	 * <li>the fact that an actual database rollback is needed if the Hibernate
	 * transaction rolls back (due to already submitted SQL statements).
	 * </ul>
	 */
	public void setForceFlush(boolean forceFlush) {
		this.forceFlush = forceFlush;
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		SessionHolder sessionHolder = null;
		if (!SessionFactoryUtils.getThreadObjectManager().hasThreadObject(this.sessionFactory)) {
			logger.debug("Using new Session for Hibernate interceptor");
			sessionHolder = new SessionHolder(SessionFactoryUtils.getSession(this.sessionFactory, true));
			SessionFactoryUtils.getThreadObjectManager().bindThreadObject(this.sessionFactory, sessionHolder);
		}
		else {
			logger.debug("Found thread-bound Session for Hibernate interceptor");
		}
		try {
			Object retVal = methodInvocation.invokeNext();
			if (this.forceFlush || sessionHolder != null) {
				SessionHolder flushHolder = (SessionHolder) SessionFactoryUtils.getThreadObjectManager().getThreadObject(this.sessionFactory);
				flushHolder.getSession().flush();
			}
			return retVal;
		}
		finally {
			if (sessionHolder != null) {
				SessionFactoryUtils.getThreadObjectManager().removeThreadObject(this.sessionFactory);
				try {
					SessionFactoryUtils.closeSessionIfNecessary(sessionHolder.getSession(), this.sessionFactory);
				}
				catch (CleanupFailureDataAccessException ex) {
					// just log it, to keep an invocation-related exception
					logger.error("Cannot close Hibernate Session after method interception", ex);
				}
			}
			else {
				logger.debug("Not closing pre-bound Hibernate Session after interceptor");
			}
		}
	}

}
