/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.EnterpriseBean;

import org.apache.log4j.Logger;


/** 
 * Superclass for all EJBs.
 * Provides logging support.
 * As javax.ejb.EnterpriseBean is a tag interface, there
 * are no EJB methods to implement.
 * <br>Subclasses will often want to create an object of type
 * JndiEnvironmentBeanFactory, to provide a BeanFactory view
 * of their JNDI environment variables. However, as they may
 * also choose to use another BeanFactory strategy (or not require
 * a bean factory) this class no longer creates a BeanFactory.
 * @see JndiEnvironmentBeanFactory
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class AbstractEnterpriseBean implements EnterpriseBean {
	
	
	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/**
	 * Logger, available to subclasses
	 */
	protected final Logger logger = Logger.getLogger(getClass().getName());
	
	
} 	// class AbstractEnterpriseBean
