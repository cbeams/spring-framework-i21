package com.interface21.orm.jdo;

import javax.jdo.PersistenceManagerFactory;

import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.DataAccessResourceFailureException;

/**
 * FactoryBean that creates local JDO PersistenceManager instances.
 * Behaves like a PersistenceManagerFactory instance when used as bean
 * reference, e.g. for JdoTemplate's persistenceManagerFactory property.
 * Note that switching to JndiObjectFactoryBean is just a matter of
 * configuration!
 *
 * <p>The typical usage will be to register this as singleton factory
 * (for a certain underlying data source) in an application context,
 * and give bean references to application services that need it.
 *
 * <p>This PersistenceManager handling strategy is most appropriate for
 * applications that solely use JDO for data access. In this case,
 * JdoTransactionManager is required for transaction demarcation, as
 * JTA support isn't possible if JDO isn't installed as JCA connector.
 *
 * @author Juergen Hoeller
 * @since 03.06.2003
 */
public class LocalPersistenceManagerFactoryBean implements FactoryBean, InitializingBean {

	private String location;

	private PersistenceManagerFactory persistenceManagerFactory;

	/**
	 * Set the location of the JDO properties config file, as URL or classpath
	 * resource location.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Initialize the PersistenceManagerFactory for the given location.
	 * @throws DataAccessResourceFailureException in case of initialization errors
	 */
	public void afterPropertiesSet() throws DataAccessResourceFailureException {
		this.persistenceManagerFactory = PersistenceManagerFactoryUtils.createPersistenceManagerFactory(location);
	}

	/**
	 * Return the singleton PersistenceManagerFactory.
	 */
	public Object getObject() {
		return this.persistenceManagerFactory;
	}

	public boolean isSingleton() {
		return true;
	}

	public PropertyValues getPropertyValues() {
		return null;
	}

}
