/**
 * Generic framework code included with
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code.
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jndi;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;

/**
 * Convenient superclass for JNDI-based Service Locators. Subclasses are
 * JavaBeans, exposing a jndiName property. This may or may not include
 * the "java:comp/env/" prefix expected by J2EE applications. If it doesn't,
 * it will be prepended.
 *
 * <p>Subclasses must implement the located() method to cache the results
 * of the JNDI lookup. They don't need to worry about error handling.
 *
 * <p><b>Assumptions: </b>The resource obtained from JNDI can be cached.
 *
 * @author Rod Johnson
 */
public abstract class AbstractJndiLocator implements InitializingBean {

	/** JNDI prefix used in a J2EE container */
	public static String CONTAINER_PREFIX = "java:comp/env/";

	protected final Log logger = LogFactory.getLog(getClass());

	private JndiTemplate jndiTemplate = new JndiTemplate();

	private String jndiName;

	private boolean inContainer = true;

	/**
	 * Create a new JNDI locator. The jndiName property must be set,
	 * and afterPropertiesSet be called to perform the JNDI lookup.
	 * <p>Obviously, this class is typically used via a BeanFactory.
	 */
	public AbstractJndiLocator() {
	}

	/**
	 * Create a new JNDI locator, specifying the JNDI name. If the name
	 * doesn't include a java:comp/env/ prefix, it will be prepended.
	 * <p>As this is a shortcut, it calls afterPropertiesSet to perform
	 * the JNDI lookup immediately.
	 * @param jndiName JNDI name.
	 */
	public AbstractJndiLocator(String jndiName) throws NamingException, IllegalArgumentException {
		setJndiName(jndiName);
		afterPropertiesSet();
	}

	/**
	 * Set the JNDI template to use for the JNDI lookup.
	 */
	public final void setJndiTemplate(JndiTemplate template) {
		jndiTemplate = template;
	}

	/**
	 * Return the JNDI template to use for the JNDI lookup.
	 */
	public final JndiTemplate getJndiTemplate() {
		return jndiTemplate;
	}

	/**
	 * Set the JNDI name. If it doesn't begin "java:comp/env/"
	 * we add this prefix if we're running in a container.
	 * @param jndiName JNDI name of bean to look up
	 * @see #setInContainer
	 */
	public final void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * Return the JNDI name to look up.
	 */
	public final String getJndiName() {
		return jndiName;
	}

	/**
	 * Set if the lookup occurs in a J2EE container, i.e. if the prefix
	 * "java:comp/env/" needs to be added if the JNDI name doesn't already
	 * contain it. Default is true.
	 */
	public final void setInContainer(boolean inContainer) {
		this.inContainer = inContainer;
	}

	/**
	 * Return if the lookup occurs in a J2EE container.
	 */
	public final boolean isInContainer() {
		return inContainer;
	}

	public final void afterPropertiesSet() throws NamingException, IllegalArgumentException {
		if (this.jndiName == null || this.jndiName.equals("")) {
			throw new IllegalArgumentException("Property 'jndiName' must be set on " + getClass().getName());
		}
		if (this.inContainer && !this.jndiName.startsWith(CONTAINER_PREFIX)) {
			this.jndiName = CONTAINER_PREFIX + this.jndiName;
		}
		Object o = lookup(this.jndiName);
		located(o);
	}

	private Object lookup(String jndiName) throws NamingException {
		Object o = this.jndiTemplate.lookup(jndiName);
		logger.debug("Looked up objet with jndiName '" + jndiName + "' OK: [" + o + "]");
		return o;
	}

	/**
	 * Subclasses must implement this to cache the object this class has obtained
	 * from JNDI.
	 * @param o object successfully retrieved from JNDI
	 */
	protected abstract void located(Object o);

}
