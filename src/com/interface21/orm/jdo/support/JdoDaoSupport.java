package com.interface21.orm.jdo.support;

import javax.jdo.PersistenceManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.orm.jdo.JdoTemplate;

/**
 * Convenient super class for JDO data access objects.
 * Requires a PersistenceManagerFactory to be set,
 * providing a JdoTemplate based on it to subclasses.
 *
 * <p>This base class is mainly intended for JdoeTemplate usage but can
 * also be used when working with PersistenceManagerFactoryUtils directly,
 * e.g. in combination with JdoInterceptor-managed PersistenceManagers.
 *
 * @author Juergen Hoeller
 * @since 28.07.2003
 * @see #setPersistenceManagerFactory
 * @see com.interface21.orm.jdo.JdoTemplate
 * @see com.interface21.orm.jdo.JdoInterceptor
 */
public abstract class JdoDaoSupport implements InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private JdoTemplate jdoTemplate;

	/**
	 * Set the JDO PersistenceManagerFactory to be used by this DAO.
	 */
	public final void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
	  this.jdoTemplate = new JdoTemplate(persistenceManagerFactory);
	}

	/**
	 * Return the JDO PersistenceManagerFactory used by this DAO.
	 */
	protected final PersistenceManagerFactory getPersistenceManagerFactory() {
		return jdoTemplate.getPersistenceManagerFactory();
	}

	/**
	 * Return the JdoTemplate for this DAO, pre-initialized
	 * with the PersistenceManagerFactory of this DAO.
	 */
	protected final JdoTemplate getJdoTemplate() {
	  return jdoTemplate;
	}

	public final void afterPropertiesSet() {
		if (this.jdoTemplate == null) {
			throw new IllegalArgumentException("persistenceManagerFactory is required");
		}
		initDao();
	}

	/**
	 * Subclasses can override this for custom initialization behavior.
	 * Gets called after population of this instance's bean properties.
	 */
	protected void initDao() {
	}

}
