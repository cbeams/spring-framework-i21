/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory;

/**
 * Exception thrown when a bean is not a factory,
 * but a user tries to get at the factory for the given bean name.
 * Whether a bean is a factory is determined by whether it implements
 * the FactoryBean interface.
 * @author Rod Johnson
 * @since 10-Mar-2003
 * @see com.interface21.beans.factory.FactoryBean
 * @version $Revision$
 */
public class BeanIsNotAFactoryException extends BeanNotOfRequiredTypeException {

	public BeanIsNotAFactoryException(String name, Object actualInstance) {
		super(name, FactoryBean.class, actualInstance);
	}

}
