/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory;


/**
 * Simple test of BeanFactory initialization
 * @author Rod Johnson
 * @since 12-Mar-2003
 * @version $Revision$
 */
public class MustBeInitialized implements InitializingBean {

	private boolean inited; 
	
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
		if (!this.inited)
			throw new RuntimeException("Factory didn't call afterPropertiesSet() on MustBeInitialized object");
	}

}
