package com.interface21.orm.hibernate;

import net.sf.hibernate.Interceptor;
import net.sf.hibernate.SessionFactory;

import com.interface21.util.Constants;

/**
 * Base class for HibernateTemplate and HibernateInterceptor,
 * defining common properties and constants.
 *
 * <p>Not intended to be used directly.
 * See HibernateTemplate and HibernateInterceptor.
 *
 * @author Juergen Hoeller
 * @since 29.07.2003
 * @see HibernateTemplate
 * @see HibernateInterceptor
 */
public abstract class HibernateAccessor {

	/**
	 * Never flush is a good strategy for read-only units of work.
	 * Hibernate will not track and look for changes in this case,
	 * avoiding any overhead of modification detection.
	 * @see #setFlushMode
	 */
	public static final int FLUSH_NEVER = 0;

	/**
	 * Automatic flushing is the default mode of both a Hibernate Session
	 * and this class. A Session gets flushed on commit and on certain
	 * find operations that might involve already modified instances,
	 * but not after each unit of work like with eager flushing.
	 * @see #setFlushMode
	 */
	public static final int FLUSH_AUTO = 1;

	/**
	 * Eager flushing leads to immediate synchronization with the database,
	 * even if in a Hibernate transaction. This causes inconsistencies to show up
	 * and throw a respective exception immediately. But the drawbacks are:
	 * <ul>
	 * <li>additional communication roundtrips with the database, instead of a
	 * single batch at transaction commit;
	 * <li>the fact that an actual database rollback is needed if the Hibernate
	 * transaction rolls back (due to already submitted SQL statements).
	 * </ul>
	 * @see #setFlushMode
	 */
	public static final int FLUSH_EAGER = 2;

	/** Constants instance for HibernateAccessor */
	private static final Constants constants = new Constants(HibernateAccessor.class);

	private SessionFactory sessionFactory;

	private Interceptor entityInterceptor;

	private int flushMode = FLUSH_AUTO;

	/**
	 * Set the Hibernate SessionFactory that should be used to create
	 * Hibernate Sessions.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Return the Hibernate SessionFactory that should be used to create
	 * Hibernate Sessions.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Set a Hibernate entity interceptor that allows to inspect and change
	 * property values before writing to and reading from the database.
	 * Will get applied to any <b>new</b> Session created by this class.
	 * <p>Such an interceptor can either be set at the SessionFactory level,
	 * i.e. on LocalSessionFactoryBean, or at the Session level, i.e. on
	 * HibernateTemplate, HibernateInterceptor, and HibernateTransactionManager.
	 * @see LocalSessionFactoryBean#setEntityInterceptor
	 * @see HibernateTransactionManager#setEntityInterceptor
	 */
	public final void setEntityInterceptor(Interceptor entityInterceptor) {
		this.entityInterceptor = entityInterceptor;
	}

	/**
	 * Return the current Hibernate entity interceptor, or null if none.
	 */
	public Interceptor getEntityInterceptor() {
		return entityInterceptor;
	}

	/**
	 * Set the flush behavior by the name of the respective constant
	 * in this class, e.g. "FLUSH_AUTO".
	 * Will get applied to any <b>new</b> Session created by this class.
	 * @param constantName name of the constant
	 * @see #setFlushMode
	 * @see #FLUSH_AUTO
	 */
	public void setFlushModeName(String constantName) {
		setFlushMode(constants.asInt(constantName));
	}

	/**
	 * Set the flush behavior to one of the constants in this class.
	 * Will get applied to any <b>new</b> Session created by this class.
	 * @see #setFlushModeName
	 * @see #FLUSH_AUTO
	 */
	public void setFlushMode(int flushMode) {
		this.flushMode = flushMode;
	}

	/**
	 * Return if a flush should be forced after executing the callback code.
	 */
	public int getFlushMode() {
		return flushMode;
	}

	public void afterPropertiesSet() {
		if (this.sessionFactory == null) {
			throw new IllegalArgumentException("sessionFactory is required");
		}
	}

	/**
	 * Evaluate if a flush is necessary via the set flush mode.
	 * @param existingTransaction if executing within an existing transaction
	 * @return if Session.flush() should be called
	 */
	protected boolean isFlushNecessary(boolean existingTransaction) {
		return (getFlushMode() == FLUSH_EAGER || (!existingTransaction && getFlushMode() == FLUSH_AUTO));
	}

}
