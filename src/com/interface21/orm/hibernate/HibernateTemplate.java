package com.interface21.orm.hibernate;

import java.io.Serializable;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.type.Type;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.DataAccessException;

/**
 * Helper class that simplifies Hibernate data access code, and converts
 * checked HibernateExceptions into unchecked HibernateJdbc/SystemExceptions,
 * compatible to the com.interface21.dao exception hierarchy.
 *
 * <p>Typically used to implement data access or business logic services that
 * use Hibernate within their implementation but are Hibernate-agnostic in
 * their interface. The latter resp. code calling the latter only have to deal
 * with business objects, query objects, and com.interface21.dao exceptions.
 *
 * <p>The central method is "execute", supporting Hibernate code implementing
 * the HibernateCallback interface. It provides Hibernate Session handling
 * such that neither the HibernateCallback implementation nor the calling
 * code needs to explicitly care about retrieving/closing Hibernate Sessions,
 * or handling Session lifecycle exceptions. For typical single step actions,
 * there are various convenience methods (find, load, saveOrUpdate, delete).
 *
 * <p>Can be used within a service implementation via direct instantiation
 * with a SessionFactory reference, or get prepared in an application context
 * and given to services as bean reference. Note: The SessionFactory should
 * always be configured as bean in the application context, in the first case
 * given to the service directly, in the second case to the prepared template.
 *
 * <p>This class can be considered a programmatic alternative to
 * HibernateInterceptor. The major advantage is its straightforwardness, the
 * major disadvantage that no checked application exceptions can get thrown
 * from within data access code. Respective checks and the actual throwing of
 * such exceptions can often be deferred to after callback execution, though. 
 *
 * <p>Note that even if HibernateTransactionManager is used for transaction
 * demarcation in higher-level services, all those services above the data
 * access layer don't need need to be Hibernate-aware. Setting such a special
 * PlatformTransactionManager is a configuration issue, without introducing
 * code dependencies. For example, switching to JTA is just a matter of
 * Spring configuration (use JtaTransactionManager instead), without needing
 * to touch application code.
 *
 * <p>LocalSessionFactoryBean is the preferred way of obtaining a reference
 * to a specific Hibernate SessionFactory, at least in a non-EJB environment.
 *
 * <p>Note: This class, like all of Spring's Hibernate support, requires
 * Hibernate 2.0 (initially developed with RC1).
 *
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see HibernateCallback
 * @see HibernateInterceptor
 * @see HibernateTransactionManager
 * @see LocalSessionFactoryBean
 * @see com.interface21.jndi.JndiObjectFactoryBean
 */
public class HibernateTemplate implements InitializingBean {

	private SessionFactory sessionFactory;

	private boolean forceFlush = false;

	/**
	 * Create a new HibernateTemplate instance.
	 */
	public HibernateTemplate() {
	}

	/**
	 * Create a new HibernateTemplate instance.
	 * @param sessionFactory SessionFactory to create Sessions
	 */
	public HibernateTemplate(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		afterPropertiesSet();
	}

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
	 * If a flush of the Hibernate Session should be forced after executing the
	 * callback code. By default, the template will only trigger a flush if not in
	 * a Hibernate transaction, as a final flush will occur on commit anyway.
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

	/**
	 * Return if a flush should be forced after executing the callback code.
	 */
	public boolean isForceFlush() {
		return forceFlush;
	}

	public void afterPropertiesSet() {
		if (this.sessionFactory == null) {
			throw new IllegalArgumentException("sessionFactory is required");
		}
	}

	/**
	 * Executes the action specified by the given action object within a session.
	 * Application exceptions thrown by the action object get propagated to the
	 * caller, Hibernate exceptions are transformed into appropriate DAO ones.
	 * Allows for returning a result object, i.e. a business object or a
	 * collection of business objects.
	 * <p>Note: Callback code is not supposed to handle transactions itself!
	 * Use an appropriate transaction manager like HibernateTransactionManager.
	 * @param action action object that specifies the Hibernate action
	 * @return a result object returned by the action, or null
	 * @throws DataAccessException in case of Hibernate errors
	 * @throws RuntimeException in case of application exceptions thrown by
	 * the action object
	 * @see HibernateTransactionManager
	 * @see com.interface21.dao
	 * @see com.interface21.transaction
	 */
	public Object execute(HibernateCallback action) throws DataAccessException, RuntimeException {
		Session session = SessionFactoryUtils.getSession(this.sessionFactory, true);
		try {
			Object result = action.doInHibernate(session);
			if (this.forceFlush || !SessionFactoryUtils.isSessionBoundToThread(session, this.sessionFactory)) {
				// forced flush, or not in a transaction -> explicit flush
				session.flush();
			}
			return result;
		}
		catch (HibernateException ex) {
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
		catch (RuntimeException ex) {
			// callback code threw application exception
			throw ex;
		}
		finally {
			SessionFactoryUtils.closeSessionIfNecessary(session, this.sessionFactory);
		}
	}

	/**
	 * Execute the specified action assuming that the result object is a List.
	 * This is a convenience method for executing Hibernate find calls within
	 * an action.
	 * @param action action object that specifies the Hibernate action
	 * @return a result object returned by the action, or null
	 * @throws DataAccessException in case of Hibernate errors
	 * @throws RuntimeException in case of application exceptions thrown by
	 * the action object
	 */
	public List executeFind(HibernateCallback action) throws DataAccessException, RuntimeException {
		return (List) execute(action);
	}

	/**
	 * Execute a query for persistent instances.
	 * <p>This is a convenience method for single step actions,
	 * mirroring Session.find.
	 * @param query a query expressed in Hibernate's query language
	 * @return the List of persistent instances
	 * @throws DataAccessException in case of Hibernate errors
	 * @see net.sf.hibernate.Session#find(String)
	 */
	public List find(final String query) {
		return (List) execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return session.find(query);
			}
		});
	}

	/**
	 * Execute a query for persistent instances,
	 * binding one value to a "?" parameter in the query.
	 * <p>This is a convenience method for single step actions,
	 * mirroring Session.find.
	 * @param query a query expressed in Hibernate's query language
	 * @return the List of persistent instances
	 * @throws DataAccessException in case of Hibernate errors
	 * @see net.sf.hibernate.Session#find(String)
	 */
	public List find(final String query, final Object value, final Type type) {
		return (List) execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return session.find(query, value, type);
			}
		});
	}

	/**
	 * Execute a query for persistent instances,
	 * binding a number of values to "?" parameters in the query.
	 * <p>This is a convenience method for single step actions,
	 * mirroring Session.find.
	 * @param query a query expressed in Hibernate's query language
	 * @return the List of persistent instances
	 * @throws DataAccessException in case of Hibernate errors
	 * @see net.sf.hibernate.Session#find(String)
	 */
	public List find(final String query, final Object[] values, final Type[] types) {
		return (List) execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return session.find(query, values, types);
			}
		});
	}

	/**
	 * Return the persistent instance of the given entity class with the
	 * given identifier. Note that this method returns null if not found,
	 * in contrast to Session.load itself.
	 * <p>This is a convenience method for single step actions,
	 * mirroring Session.load.
	 * @param entityClass a persistent class
	 * @param id an identifier of the persistent instance
	 * @return the persistent instance, or null if not found
	 * @throws DataAccessException in case of Hibernate errors
	 * @see net.sf.hibernate.Session#load(Class,Serializable)
	 */
	public Object load(final Class entityClass, final Serializable id) throws DataAccessException {
		return execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				try {
					return session.load(entityClass, id);
				}
				catch (ObjectNotFoundException ex) {
					// Session.load throws this exception if not found
					// -> the contract of this template method is to return null
					return null;
				}
			}
		});
	}

	/**
	 * Save respectively update the given persistent instance,
	 * according to its id (matching the configured "unsaved-value"?).
	 * <p>This is a convenience method for single step actions,
	 * mirroring Session.saveOrUpdate.
	 * @param entity the persistent instance to save resp. update
	 * @throws DataAccessException in case of Hibernate errors
	 * @see net.sf.hibernate.Session#saveOrUpdate(Object)
	 */
	public void saveOrUpdate(final Object entity) {
		execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.saveOrUpdate(entity);
				return null;
			}
		});
	}

	/**
	 * Delete the given persistent instance.
	 * <p>This is a convenience method for single step actions,
	 * mirroring Session.delete.
	 * @param entity the persistent instance to delete
	 * @throws DataAccessException in case of Hibernate errors
	 * @see net.sf.hibernate.Session#delete(Object)
	 */
	public void delete(final Object entity) {
		execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.delete(entity);
				return null;
			}
		});
	}

}
