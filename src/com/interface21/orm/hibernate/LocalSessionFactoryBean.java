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
 * <p>This SessionFactory handling strategy is appropriate for most types of
 * applications, from Hibernate-only single database apps to ones that need
 * distributed transactions. Either HibernateTransactionManager or
 * JtaTransactionManager can be used for transaction demarcation, the latter
 * only being necessary for transactions that span multiple databases.
 *
 * <p>Registering a SessionFactory with JNDI is only advisable when using
 * Hibernate's JCA Connector, i.e. when the application server cares for
 * initialization. Else, portability is rather limited: Manual JNDI binding
 * isn't supported by some application servers (e.g. Tomcat). Unfortunately,
 * JCA has drawbacks too: Its setup is container-specific and can be tedious.
 *
 * <p>Note that the JCA Connector's sole major strength is its seamless
 * cooperation with EJB containers and JTA services. If you do not use EJB
 * and initiate your JTA transactions via Spring's JtaTransactionManager,
 * you can get all benefits including distributed transactions and proper
 * transactional JVM-level caching with local SessionFactory setup too -
 * without any configuration hassle like container-specific setup.
 *
 * @author Juergen Hoeller
 * @since 05.05.2003
 * @see HibernateTemplate#setSessionFactory
 * @see HibernateTransactionManager#setSessionFactory
 * @see com.interface21.jndi.JndiObjectFactoryBean
 */
public class LocalSessionFactoryBean implements FactoryBean, InitializingBean {

	private String location;

	private SessionFactory sessionFactory;

	/**
	 * Set the location of the Hibernate XML config file, as URL or classpath
	 * resource location. Hibernate 2.0's default is "/hibernate.cfg.xml",
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
