package com.interface21.orm.hibernate;

import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.ObjectDeletedException;
import net.sf.hibernate.PersistentObjectException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.StaleObjectStateException;
import net.sf.hibernate.TransientObjectException;
import org.apache.log4j.Logger;

import com.interface21.dao.DataAccessException;
import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.dao.InvalidDataAccessResourceUsageException;
import com.interface21.dao.OptimisticLockingFailureException;

/**
 * Helper class that simplifies Hibernate data access code, and converts
 * checked HibernateExceptions into unchecked HibernateDataAccessExceptions,
 * compatible to the com.interface21.dao exception hierarchy.
 *
 * <p>The central method is "execute", supporting Hibernate code implementing
 * the HibernateCallback interface. It provides Hibernate Session handling
 * such that neither the HibernateCallback implementation nor the calling
 * code needs to explicitly care about retrieving/closing Hibernate Sessions,
 * or handling Session lifecycle exceptions.
 *
 * <p>Typically used to implement data access or business logic services that
 * use Hibernate within their implementation but are Hibernate-agnostic in
 * their interface. The latter resp. code calling the latter only have to deal
 * with business objects, query objects, and com.interface21.dao exceptions.
 *
 * <p>Can be used within a service implementation via direct instantiation
 * with a session factory reference, or get prepared in an application context
 * and given to services as bean reference. Note: The session factory should
 * always be configured as bean in the application context, in the first case
 * given to the service directly, in the second case to the prepared template.
 *
 * <p>Note that even if HibernateTransactionManager is used for transaction
 * demarcation in higher-level services, all those services above the data
 * access layer don't need need to be Hibernate-aware. Setting such a special
 * PlatformTransactionManager is a configuration issue, without introducing
 * a code dependency. For example, switching to JTA is just a matter of
 * Spring and Hibernate configuration, without touching applicaiton code.
 *
 * <p>Note: This class, like all of Spring's Hibernate support, requires
 * Hibernate 2.0 (initially developed with RC1).
 *
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see HibernateCallback
 * @see HibernateTransactionManager
 */
public class HibernateTemplate {

	private final Logger logger = Logger.getLogger(getClass());

	private SessionFactory sessionFactory;

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
	 * Executes the action specified by the given action object within a session.
	 * Application exceptions thrown by the action object get propagated to the
	 * caller, Hibernate exceptions are transformed into appropriate DAO ones.
	 * Allows for returning a result object created within the transaction,
	 * i.e. a business object or a collection of business objects.
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
		Session session = SessionFactoryUtils.openSession(this.sessionFactory);
		try {
			Object result = action.doInHibernate(session);
			// flush the changes, also for validation
			session.flush();
			return result;
		}
		catch (JDBCException ex) {
			// SQLException during Hibernate code used by the application
			log(ex.getSQLException());
			throw new HibernateJdbcException("Exception in Hibernate data access code", ex);
		}
		catch (QueryException ex) {
			log(ex);
			throw new InvalidDataAccessResourceUsageException("Invalid Hibernate query", ex);
		}
		catch (StaleObjectStateException ex) {
			log(ex);
			throw new OptimisticLockingFailureException("Version check failed", ex);
		}
		catch (PersistentObjectException ex) {
			log(ex);
			throw new InvalidDataAccessApiUsageException("Invalid object state", ex);
		}
		catch (TransientObjectException ex) {
			log(ex);
			throw new InvalidDataAccessApiUsageException("Invalid object state", ex);
		}
		catch (ObjectDeletedException ex) {
			log(ex);
			throw new InvalidDataAccessApiUsageException("Invalid object state", ex);
		}
		catch (HibernateException ex) {
			log(ex);
			throw new HibernateSystemException("Exception in Hibernate data access code", ex);
		}
		catch (RuntimeException ex) {
			// application error
			log(ex);
			throw ex;
		}
		finally {
			SessionFactoryUtils.closeSessionIfNecessary(session, this.sessionFactory);
		}
	}

	private void log(Throwable ex) {
		logger.error("Callback code threw Hibernate exception", ex);
	}

}
