package com.interface21.orm.hibernate;

import net.sf.hibernate.SessionFactory;

import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.DataAccessResourceFailureException;

/**
 * FactoryBean that creates local Hibernate SessionFactory instances.
 * Behaves like a SessionFactory instance when used as bean reference,
 * e.g. for HibernateTemplate's sessionFactory property. Note that
 * switching to JndiObjectFactoryBean is just a matter of configuration!
 *
 * <p>The typical usage will be to register this as singleton factory
 * (for a certain underlying data source) in an application context,
 * and give bean references to application services that need it.
 *
 * <p>This SessionFactory handling strategy is most appropriate for
 * applications that solely use Hibernate for data access, probably
 * using HibernateTransactionManager for transaction demarcation.
 *
 * @author Juergen Hoeller
 * @since 05.05.2003
 * @see HibernateTemplate
 * @see HibernateTransactionManager
 * @see com.interface21.jndi.JndiObjectFactoryBean
 */
public class LocalSessionFactoryBean implements FactoryBean, InitializingBean {

	private String location;

	private SessionFactory sessionFactory;

	/**
	 * Set the location of the Hibernate XML config file, as URL or classpath
	 * resource location. Hibernate 2.0's default is the "/hibernate.cfg.xml",
	 * in the case of web applications normally in WEB-INF/classes.
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Initialize the SessionFactory for the given or the default location.
	 * @throws DataAccessResourceFailureException in case of initialization errors
	 */
	public void afterPropertiesSet() throws DataAccessResourceFailureException {
		this.sessionFactory = SessionFactoryUtils.createSessionFactory(this.location);
	}

	/**
	 * Return the singleton SessionFactory.
	 */
	public Object getObject() {
		return this.sessionFactory;
	}

	public boolean isSingleton() {
		return false;
	}

	public PropertyValues getPropertyValues() {
		return null;
	}

}
