/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.ejb.support;

import javax.ejb.EJBException;
import javax.ejb.EnterpriseBean;

import org.apache.log4j.Logger;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.jndi.JndiBeanFactory;


/** 
 * Superclass for all EJBs.
 * Provides BeanFactory and logging support.
 * As javax.ejb.EnterpriseBean is a tag interface, there
 * are no EJB methods to implement.
 * @author Rod Johnson
 * @version $RevisionId$
 */
public abstract class AbstractEnterpriseBean implements EnterpriseBean {
	
	
	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/**
	 * Logger, available to subclasses
	 */
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	
	/** BeanFactory available to subclasses.
	 * Lazy loaded for efficiency.
	 */ 
	private ListableBeanFactory		beanFactory;
	
	
	//-------------------------------------------------------------------------
	// BeanFactory methods
	//-------------------------------------------------------------------------	
	/**
	 * Get the BeanFactory for this EJB
	 * Uses lazy loading for efficiency. We don't
	 * need to consider thread safety in an EJB.
	 * @return the bean factory available to this EJB.
	 * In this implementation, the bean factory will be
	 * populated by the EJB's ejb-jar.xml environment
	 * variables.
	 */
	protected final ListableBeanFactory getBeanFactory() {		
		
		// ****TODO: this class could be enhanced by
		// allowing alternative sources for the bean factory.
		// For example, it might be taken from a database.
		
		if (this.beanFactory == null) {
			loadBeanFactory();
		}
		return this.beanFactory;
	}
	
	
	/**
	 * Load the bean factory used by this class.
	 */
	private void loadBeanFactory() {
		logger.info("Loading bean factory");
		try {
			this.beanFactory = new JndiBeanFactory("java:comp/env");
		}
		catch (BeansException ex) {
			throw new EJBException("Cannot create bean factory", ex);
		}
	}
	
} 	// class AbstractEnterpriseBean
