/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.CreateException;
import javax.ejb.EnterpriseBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.support.BeanFactoryLoader;
import com.interface21.beans.factory.support.BootstrapException;

/** 
 * Superclass for all EJBs. Provides a logger and
 * a standard way of loading a BeanFactory. 
 * Subclasses act as a facade, with the business logic
 * deferred to beans in the BeanFactory.
 * Package-visible: not intended for direct subclassing
 * Provides logging support.
 * <br>Default is to use an XmlBeanFactoryLoader.
 * For a strategy other than loading an XML bean factory from the
 * classpath (with a JNDI name specified) call the
 * setBeanFactoryLoader() method <i>before</i> your
 * EJB's ejbCreate() method is invoked--for example,
 * in setSessionContext().
 * @author Rod Johnson
 * @version $Id$
 */
abstract class AbstractEnterpriseBean implements EnterpriseBean {

	/**
	 * Logger, available to subclasses
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Spring BeanFactory that provides the namespace for this EJB
	 */
	private BeanFactory beanFactory;

	/** Helper Strategy that knows how to load Spring BeanFactory */
	private BeanFactoryLoader beanFactoryLoader;

	/**
	 * Load Spring BeanFactory namespace and DelegateMapper.
	 * Subclasses must invoke this method.
	 * Package-visible as it shouldn't be called directly
	 * by user-created subclasses.
	 * @see com.interface21.ejb.support.AbstractStatelessSessionBean#ejbCreate()
	 */
	void loadBeanFactory() throws CreateException {
		if (this.beanFactoryLoader == null) {
			this.beanFactoryLoader = new XmlBeanFactoryLoader();
		}
		try {
			this.beanFactory = this.beanFactoryLoader.loadBeanFactory();
		}
		catch (BootstrapException ex) {
			throw new CreateException(ex.getMessage());
		}
	}


	/**
	 * Can be invoked before loadBeanFactory
	 * Invoke in constructor or setXXXXContext() if you want
	 * to override the default bean factory loader 
	 * @param beanFactoryLoader
	 */
	public void setBeanFactoryLoader(BeanFactoryLoader beanFactoryLoader) {
		this.beanFactoryLoader = beanFactoryLoader;
	}

	/**
	 * May be called after ejbCreate()
	 * @return the bean Factory
	 */
	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Useful EJB lifecycle method. Override if necessary.
	 */
	public void ejbRemove() {
		// Empty
	}

}
