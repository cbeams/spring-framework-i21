/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.support;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.BeansException;
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
	
	protected RootBeanDefinition() {
	}
	
	protected void setBeanClass(Class clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * Setter for the name of the JavaBean target class.
	 */
	public void setBeanClassName(String classname) throws ClassNotFoundException {
		this.clazz = Class.forName(classname);
	}


	/**
	 * @return the class of the wrapped bean
	 */
	public final Class getBeanClass() {
		return this.clazz;
	}

	/**
	 * Subclasses may override this, to create bean
	 * wrappers differently or perform custom preprocessing.
	 * This implementation wraps the bean class directly.
	 * @return a new BeanWrapper wrapper the target object
	 */
	protected BeanWrapper newBeanWrapper() {
		return new BeanWrapperImpl(getBeanClass());
	}

	/**
	 * Given a bean wrapper, add listeners
	*/
	public final BeanWrapper getBeanWrapperForNewInstance() throws BeansException {
		BeanWrapper bw = newBeanWrapper();

		return bw;
	} // getBeanWrapperForNewInstance


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