package com.interface21.jndi;

import javax.naming.NamingException;

import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.InitializingBean;

/**
 * FactoryBean that looks up a JNDI object. Behaves like the object when
 * used as bean reference, e.g. for JdbcTemplate's dataSource property.
 * Note that switching to e.g. DriverManagerDataSource is just a matter of
 * configuration: replace the definition of this FactoryBean with a
 * DriverManagerDataSource definition!
 *
 * <p>The typical usage will be to register this as singleton factory
 * (e.g. for a certain JNDI data source) in an application context,
 * and give bean references to application services that need it.
 *
 * <p>Of course, service implementations can lookup e.g. a DataSource from
 * JNDI themselves, but this class enables central configuration of the
 * JNDI name, and easy switching to mock replacements. The latter can be
 * used for test setups, standalone clients, etc.
 *
 * <p>Instead of using bean references via this class, a property can also
 * be fed with a JNDI object via the JndiObjectEditor property editor,
 * i.e. by providing the JNDI name as non-beanRef string value. Note that
 * this does not allow for central configuration of the JNDI name!
 *
 * @author Juergen Hoeller
 * @since 22.05.2003
 * @see JndiObjectEditor
 * @see com.interface21.jdbc.core.JdbcTemplate#setDataSource
 */
public class JndiObjectFactoryBean implements FactoryBean, InitializingBean {

	private JndiTemplate jndiTemplate = new JndiTemplate();

	private String jndiName;

	private Object jndiObject;

	/**
	 * Set the JndiTemplate to use for JNDI lookup.
	 * A default one is used if not set.
	 */
	public final void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiTemplate = jndiTemplate;
	}

	/**
	 * Set the JNDI jndiName of the DataSource.
	 * Supports "test", "jdbc/test", and "java:comp/env/jdbc/test" syntaxes.
	 */
	public final void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * Initialize the object from JNDI.
	 */
	public void afterPropertiesSet() throws NamingException {
		this.jndiObject = this.jndiTemplate.lookup(this.jndiName);
	}

	/**
	 * Return the singleton SessionFactory.
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
