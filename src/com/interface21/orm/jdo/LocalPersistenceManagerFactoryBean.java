package com.interface21.orm.jdo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.DataAccessResourceFailureException;

/**
 * FactoryBean that creates a local JDO PersistenceManager instance.
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

	private String configLocation;

	private Properties jdoProperties;

	private PersistenceManagerFactory persistenceManagerFactory;

	/**
	 * Set the location of the JDO properties config file as classpath
	 * resource, e.g. "/kodo.properties".
	 * <p>Note: Can be omitted when all necessary properties are
	 * specified locally via this bean.
	 */
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Set JDO properties, like "javax.jdo.PersistenceManagerFactoryClass".
	 * <p>Can be used to override values in a JDO properties config file,
	 * or to specify all necessary properties locally.
	 */
	public void setJdoProperties(Properties jdoProperties) {
		this.jdoProperties = jdoProperties;
	}

	/**
	 * Initialize the PersistenceManagerFactory for the given location.
	 * @throws IllegalArgumentException in case of illegal property values
	 * @throws IOException if the properties could not be loaded from the given location
	 * @throws JDOException in case of JDO initialization errors
	 */
	public void afterPropertiesSet() throws IllegalArgumentException, IOException, JDOException {
		if (this.configLocation == null && this.jdoProperties == null) {
			throw new IllegalArgumentException("Either configLocation (e.g. '/kodo.properties') or jdoProperties must be set");
		}
		Properties prop = new Properties();
		if (this.configLocation != null) {
			String resourceLocation = this.configLocation;
			if (!resourceLocation.startsWith("/")) {
				// always use root, as relative loading doesn't make sense
				resourceLocation = "/" + resourceLocation;
			}
			InputStream in = getClass().getResourceAsStream(resourceLocation);
			if (in == null) {
				throw new DataAccessResourceFailureException("Cannot open config location: " + resourceLocation, null);
			}
			prop.load(in);
		}
		if (this.jdoProperties != null) {
			prop.putAll(this.jdoProperties);
		}
		this.persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(prop);
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
