/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.support;

import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;

/**
 * Convenient superclass for FactoryBean implementations.
 * Exposes properties for singleton and PropertyValues.
 * <br>
 * There's no need for FactoryBean implementation to extend this class:
 * it's just easier in some cases.
 * @author Rod Johnson
 * @since 10-Mar-2003
 * @version $Revision$
 */
public abstract class AbstractFactoryBean implements FactoryBean {

	/**
	 * PropertyValues, if any, to be passed through and applied
	 * to new instances created by the factory. If this is null 
	 * (the default) no properties are set on the new instance.
	 */
	private PropertyValues pvs;
	
	/**
	 * Default is for factories to return a singleton instance.
	 */
	private boolean singleton = true;


	/**
	 * @see com.interface21.beans.factory.FactoryBean#getPropertyValues()
	 */
	public PropertyValues getPropertyValues() {
		return this.pvs;
	}

	/**
	 * Sets the PropertyValues, if any, to pass through to
	 * bean instances created by this factory.
	 * @param pvs The pvs to set
	 */
	public void setPropertyValues(PropertyValues pvs) {
		this.pvs = pvs;
	}
	
	/**
	 * @see com.interface21.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return this.singleton;
	}

	/**
	 * Sets the singleton.
	 * @param singleton The singleton to set
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

}
