/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory;

import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.beans.factory.BeanFactoryAware;

/**
 * Simple test of BeanFactory initialization
 * and lifecycle callbacks.
 * @author Rod Johnson
 * @since 12-Mar-2003
 * @version $Revision$
 */
public class LifecycleBean implements InitializingBean, BeanFactoryAware {

	private boolean inited; 
	
	private BeanFactory owningFactory;
	
	/**
	 * @see com.interface21.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		this.inited = true;
	}
	
	/**
	 * Dummy business method that will fail unless the factory
	 * managed the bean's lifecycle correctly
	 */
	public void businessMethod() {
		if (!this.inited || this.owningFactory == null)
			throw new RuntimeException("Factory didn't initialize lifecycle object correctly");
	}

	/**
	 * @see com.interface21.beans.factory.BeanFactoryAware#setBeanFactory(com.interface21.beans.factory.BeanFactory)
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!inited)
			throw new RuntimeException("Factory didn't call afterPropertiesSet() before invoking setBeanFactory " +
				"on lifecycle bean");
		this.owningFactory = beanFactory;
	}

}
