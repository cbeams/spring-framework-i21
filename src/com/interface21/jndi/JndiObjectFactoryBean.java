package com.interface21.jndi;

import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;

/**
 * FactoryBean that looks up a JNDI object. Behaves like the object when
 * used as bean reference, e.g. for JdbcTemplate's dataSource property.
 * Note that switching to e.g. DriverManagerDataSource is just a matter of
 * configuration: replace the definition of this FactoryBean with a
 * DriverManagerDataSource definition!
 *
 * <p>The typical usage will be to register this as singleton factory
 * (e.g. for a certain JNDI DataSource) in an application context,
 * and give bean references to application services that need it.
 *
 * <p>Of course, service implementations can lookup e.g. a DataSource from
 * JNDI themselves, but this class enables central configuration of the
 * JNDI name, and easy switching to mock replacements. The latter can be
 * used for test setups, standalone clients, etc.
 *
 * @author Juergen Hoeller
 * @since 22.05.2003
 * @see com.interface21.jdbc.core.JdbcTemplate#setDataSource
 */
public class JndiObjectFactoryBean extends AbstractJndiLocator implements FactoryBean {

	private Object jndiObject;

	protected void located(Object o) {
		this.jndiObject = o;
	}

	/**
	 * Return the singleton JNDI object.
	 */
	public Object getObject() {
		return this.jndiObject;
	}

	public boolean isSingleton() {
		return false;
	}

	public PropertyValues getPropertyValues() {
		return null;
	}

}
