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

import org.apache.log4j.Logger;

import com.interface21.beans.factory.InitializingBean;
 
/** 
* Convenient superclass for JNDI-based Service Locators.
* Subclasses are JavaBeans, exposing a jndiName property.
* This may or may not include the java:comp/env/ prefix
* expected by J2EE applications. If it doesn't it will
* be prepended. Subclasses must implement the protected abstract
* located() method to cache the results of JNDI lookup.
* Subclasses don't need to worry about error handling.
* <br><b>Assumptions: </b>The resource obtained from JNDI can
* be cached.
* @author Rod Johnson
*/
public abstract class AbstractJndiLocator implements InitializingBean {
	
	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/**
	* Create a logging category that is available
	* to subclasses. 
	*/
	protected final Logger logger = Logger.getLogger(getClass());
	
	/** JNDI prefix used in J2EE applications */
	private static String PREFIX = "java:comp/env/";
	
	/** Holder for the jndiName property */
	private String jndiName;
	
	private boolean inContainer = true;
	
	private JndiTemplate jndiTemplate = new JndiTemplate();
	
	
	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	/** 
	 * Create a new JNDI locator
	 */
	public AbstractJndiLocator() {
	}
	
	
	/**
	 * Create a new JNDI locator, specifying the JNDI name
	 * @param jndiName JNDI name. If this doesn't include a java:comp/env/ prefix,
	 * this will be prepended.
	 */
	public AbstractJndiLocator(String jndiName) {
		setJndiName(jndiName);
	}

	
	//-------------------------------------------------------------------------
	// JavaBean properties
	//-------------------------------------------------------------------------
	/**
	 * Set the JNDI name. If it doesn't begin java:comp/env/
	 * we add this prefix if we're running in a container
	 * @param jndiName JNDI name of bean to look up
	 */
	public final void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}
	
	public final String getJndiName() {
		return jndiName;
	}
	
	/**
	 * @return
	 */
	public JndiTemplate getJndiTemplate() {
		return jndiTemplate;
	}

	/**
	 * @param template
	 */
	public void setJndiTemplate(JndiTemplate template) {
		jndiTemplate = template;
	}

	
	/**
	 */
	public boolean isInContainer() {
		return inContainer;
	}

	/**
	 */
	public void setInContainer(boolean inContainer) {
		this.inContainer = inContainer;
	}

	
	//---------------------------------------------------------------------
	// Implementation of InitializingBean
	//---------------------------------------------------------------------
	/**
	 * @see InitializingBean#afterPropertiesSet()
	 */
	public final void afterPropertiesSet() throws Exception {
		if (this.jndiName == null || this.jndiName.equals(""))
			throw new Exception("Property 'jndiName' must be set on " + getClass().getName());
			
		if (this.inContainer && !jndiName.startsWith(PREFIX))
				jndiName = PREFIX + jndiName;
		Object o = lookup(jndiName);
		located(o);
	}
	
	
	//-------------------------------------------------------------------------
	// Implementation methods
	//-------------------------------------------------------------------------
	/**
	 * Subclasses must implement this to cache the object this class has obtained
	 * from JNDI.
	 * @param o object successfully retrieved from JNDI
	 */
	protected abstract void located(Object o);
	
	
	/**
	 * Lookup the object.
	 * @param jndiName
	 * @return Object
	 * @throws NamingException
	 */
	private Object lookup(String jndiName) throws NamingException {
		Object o = this.jndiTemplate.lookup(jndiName);			
		logger.debug("Looked up objet with jndiName '" + jndiName + "' OK: [" + o + "]");
		return o;
	}
	

} 	// class AbstractJndiLocator
