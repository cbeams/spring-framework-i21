/**
 * The Spring framework is distributed under the Apache
 * Software License.
 */

package com.interface21.beans.factory;

import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValues;

/**
 * Interface to be implemented by objects used within a 
 * BeanFactory that are themselves factories. If a
 * bean implements this interface, it is used as a factory,
 * not directly as a bean. 
 * <br>
 * <b>NB: a bean that implements this interface cannot be
 * used as a normal bean.</b>
 * <br>FactoryBeans can support singletons and prototypes.
 * @author  Rod Johnson
 * @since March 08, 2003
 * @see com.interface21.beans.factory.BeanFactory
 * @version $Id$
 */
public interface FactoryBean {

	/** 
	 * Return an instance (possibly shared or independent) of the object
	 * managed by this factory.
	 * As with a BeanFactory, this allows support for both
	 * the Singleton and Prototype design pattern.
	 * @return an instance of the bean
	 */
    Object getObject() throws BeansException;
	
	
	/**
	 * Is the bean managed by this factory a singleton
	 * or a prototype? That is, will getBean() always
	 * return the same object?
	 * <br>The singleton status of a FactoryBean will generally
	 * be provided by the owning BeanFactory.
	 * @return is this bean a singleton
	*/
	boolean isSingleton();
	
	/**
	 * Property values to pass to new bean instances created
	 * by this factory. Mapped directly onto the bean instance using 
	 * reflection. This occurs <i>after</i> any configuration of the
	 * instance performed by the factory itself, and is an
	 * optional step within the control of the owning BeanFactory.
	 * @return PropertyValues to pass to each new instance,
	 * or null (the default) if there are no properties to 
	 * pass to the instance.
	 */
	PropertyValues getPropertyValues();
    
}

