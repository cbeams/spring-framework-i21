/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.support;

import com.interface21.beans.PropertyValues;

/** 
* Root bean definitions have a class and properties.
* 
* 
* @author Rod Johnson
*/
public class RootBeanDefinition extends AbstractBeanDefinition {

	/**
	 * Class of the wrapped object
	 */
	private Class clazz;

	/** Creates new AbstractRootBeanDefinition */
	public RootBeanDefinition(Class clazz, PropertyValues pvs, boolean singleton) {
		super(pvs, singleton);
		this.clazz = clazz;
	}
	
	/**
	 * Deep copy constructor
	 * @param other
	 */
	public RootBeanDefinition(RootBeanDefinition other) {
		super(other.getPropertyValues(), other.isSingleton());
		this.clazz = other.clazz;
	}


	/**
	 * @return the class of the wrapped bean
	 */
	public final Class getBeanClass() {
		return this.clazz;
	}


	public String toString() {
		return "RootBeanDefinition: class is " + getBeanClass();
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof RootBeanDefinition))
			return false;
		return super.equals(arg0) && ((RootBeanDefinition) arg0).getBeanClass().equals(this.getBeanClass());
	}

} // class RootBeanDefinition