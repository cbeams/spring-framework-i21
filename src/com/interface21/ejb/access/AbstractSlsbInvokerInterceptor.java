/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.ejb.access;

import org.aopalliance.intercept.MethodInterceptor;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.jndi.AbstractJndiLocator;

/**
 * Superclass for all AOP interceptors invoking EJBs.
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class AbstractSlsbInvokerInterceptor extends AbstractJndiLocator implements MethodInterceptor, InitializingBean {

	/** 
	 * Name of no arg create() method required on EJB homes,
	 * but not part of EJBLocalHome
	 */
	protected static final String CREATE_METHOD = "create";
	
	private BeanWrapper homeBeanWrapper;


	/**
	 * We can get actual home from the BeanWrapper, if we ever need it
	 * @return a BeanWrapper for the EJB home interface. This
	 * may be a local or remote home.
	 */
	protected BeanWrapper getHomeBeanWrapper() {
		return this.homeBeanWrapper;
	}

	
 	/**
 	 * Implementation of AbstractJndiLocator's callback, to cache the home wrapper.
	 * Triggers afterLocated after execution.
	 * @see #afterLocated
	 */
	protected void located(Object o) {
		this.homeBeanWrapper = new BeanWrapperImpl(o);
		afterLocated();
	}

	/**
	 * Initialization hook after the AbstractJndiLocator's located callback.
	 * @see #located
	 */
	protected void afterLocated() {
	}

}
