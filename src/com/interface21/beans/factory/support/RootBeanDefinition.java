package com.interface21.beans.factory.support;

import com.interface21.beans.BeanWrapper;

/**
 * Extension of BeanDefinition for beans whose class is known.
 * @author Rod Johnson
 */
public interface RootBeanDefinition extends BeanDefinition {
	
	/**
	 * Return the class of the bean
	 * @return the class of the bean
	 */
	Class getBeanClass();

	/**
	 * Return a BeanWrapper for a new instance of the bean
	 */
	BeanWrapper getBeanWrapperForNewInstance();
}