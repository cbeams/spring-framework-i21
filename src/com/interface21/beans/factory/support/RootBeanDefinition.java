/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.support;

import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValues;

/** 
* Root bean definitions have a class and properties.
* @author Rod Johnson
* @version $Id$
*/
public class RootBeanDefinition extends AbstractBeanDefinition {

	/** Class of the wrapped object */
	private Class clazz;
	
	private String initMethodName;

	private String destroyMethodName;

	public RootBeanDefinition(Class clazz, PropertyValues pvs, boolean singleton,
	                          String initMethodName, String destroyMethodName) {
		super(pvs, singleton);
		this.clazz = clazz;
		this.initMethodName = initMethodName;
		this.destroyMethodName = destroyMethodName;
	}
	
	public RootBeanDefinition(Class clazz, PropertyValues pvs, boolean singleton) {
		this(clazz, pvs, singleton, null, null);
	}
	
	/**
	 * Deep copy constructor.
	 */
	public RootBeanDefinition(RootBeanDefinition other) {
		super(new MutablePropertyValues(other.getPropertyValues()), other.isSingleton());
		this.clazz = other.clazz;
		this.initMethodName = other.initMethodName;
		this.destroyMethodName = other.destroyMethodName;
	}
	
	/**
	 * Returns the name of the initializer method. The default is null
	 * in which case there is no initializer method.
	 */
	public String getInitMethodName() {
		return this.initMethodName;
	}

	/**
	 * Returns the name of the destroy method. The default is null
	 * in which case there is no initializer method.
	 */
	public String getDestroyMethodName() {
		return this.destroyMethodName;
	}

	/**
	 * Returns the class of the wrapped bean.
	 */
	public final Class getBeanClass() {
		return this.clazz;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RootBeanDefinition))
			return false;
		return super.equals(obj) && ((RootBeanDefinition) obj).getBeanClass().equals(this.getBeanClass());
	}

	public String toString() {
		return "RootBeanDefinition for class '" + getBeanClass().getName() + "'; " + super.toString();
	}

}
