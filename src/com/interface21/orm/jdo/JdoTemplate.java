package com.interface21.orm.jdo;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.DataAccessException;

/**
 * Helper class that simplifies JDO data access code, and converts
 * JDOExceptions into JdoUsage/JdoSystemException, compatible to the
 * com.interface21.dao exception hierarchy.
 *
 * <p>The central method is "execute", supporting JDO code implementing
 * the JdoCallback interface. It provides JDO PersistenceManager handling
 * such that neither theJdoCallback implementation nor the calling code
 * needs to explicitly care about retrieving/closing PersistenceManagers,
 * or handling JDO lifecycle exceptions.
 *
 * <p>Typically used to implement data access or business logic services that
 * use JDO within their implementation but are JDO-agnostic in their interface.
 * The latter resp. code calling the latter only have to deal with business
 * objects, query objects, and com.interface21.dao exceptions.
 *
 * <p>Can be used within a service implementation via direct instantiation
 * with a PersistenceManagerFactory reference, or get prepared in an
 * application context and given to services as bean reference.
 * Note: The PersistenceManagerFactory should always be configured as bean in
 * the application context, in the first case given to the service directly,
 * in the second case to the prepared template.
 *
 * <p>This class can be considered a programmatic alternative to
 * JdoInterceptor. The major advantage is its straightforwardness, the
 * major disadvantage that no checked application exceptions can get thrown
 * from within data access code. Respective checks and the actual throwing of
 * such exceptions can often be deferred to after callback execution, though.
 *
 * <p>Note that even if JdoTransactionManager is used for transaction
 * demarcation in higher-level services, all those services above the data
 * access layer don't need need to be JDO-aware. Setting such a special
 * PlatformTransactionManager is a configuration issue, without introducing
 * code dependencies.
 *
 * <p>LocalPersistenceManagerFactoryBean is the preferred way of obtaining a
 * reference to a specific PersistenceManagerFactory, at least in a non-EJB
 * environment. Registering a PersistenceManagerFactory with JNDI is only
 * advisable when using a JCA Connector, i.e. when the application server
 * cares for initialization. Else, portability is rather limited: Manual
 * JNDI binding isn't supported by some application servers (e.g. Tomcat).
 *
 * @author Juergen Hoeller
 * @since 03.06.2003
 * @see JdoCallback
 * @see JdoTransactionManager
 */
public class JdoTemplate implements InitializingBean {

	private PersistenceManagerFactory persistenceManagerFactory;

	/**
	 * Create a new JdoTemplate instance.
	 */
	public JdoTemplate() {
	}

	/**
	 * Create a new JdoTemplate instance.
	 * @param pmf PersistenceManagerFactory to create PersistenceManagers
	 */
	public JdoTemplate(PersistenceManagerFactory pmf) {
		this.persistenceManagerFactory = pmf;
		afterPropertiesSet();
	}

	/**
	 * Set the JDO PersistenceManagerFactory that should be used to create
	 * PersistenceManagers.
	 */
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf) {
		this.persistenceManagerFactory = pmf;
	}

	/**
	 * Return the JDO PersistenceManagerFactory that should be used to create
	 * PersistenceManagers.
	 */
	public PersistenceManagerFactory getPersistenceManagerFactory() {
		return persistenceManagerFactory;
	}

	public void afterPropertiesSet() {
		if (this.persistenceManagerFactory == null) {
			throw new IllegalArgumentException("persistenceManagerFactory is required");
		}
	}

	/**
	 * Executes the action specified by the given action object within a
	 * PersistenceManager. Application exceptions thrown by the action object
	 * get propagated to the caller, JDO exceptions are transformed into
	 * appropriate DAO ones. Allows for returning a result object,
	 * i.e. a business object or a collection of business objects.
	 * <p>Note: Callback code is not supposed to handle transactions itself!
	 * Use an appropriate transaction manager like JdoTransactionManager.
	 * @param action action object that specifies the JDO action
	 * @return a result object returned by the action, or null
	 * @throws DataAccessException in case of JDO errors
	 * @throws RuntimeException in case of application exceptions thrown by
	 * the action object
	 * @see JdoTransactionManager
	 * @see com.interface21.dao
	 * @see com.interface21.transaction
	 */
	public Object execute(JdoCallback action) throws DataAccessException, RuntimeException {
		PersistenceManager pm = PersistenceManagerFactoryUtils.getPersistenceManager(this.persistenceManagerFactory, true);
		try {
			return action.doInJdo(pm);
		}
		catch (JDOException ex) {
			throw PersistenceManagerFactoryUtils.convertJdoAccessException(ex);
		}
		catch (RuntimeException ex) {
			// callback code threw application exception
			throw ex;
		}
		finally {
			PersistenceManagerFactoryUtils.closePersistenceManagerIfNecessary(pm, this.persistenceManagerFactory);
		}
	}

}
