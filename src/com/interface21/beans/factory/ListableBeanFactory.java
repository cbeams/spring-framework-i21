/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.beans.factory;

/**
 * Extension of BeanFactory to be implemented by bean factories that can
 * enumerate all their bean instances, rather than attempting bean lookup
 * by name one by one as requested by clients.
 *
 * <p>If this is a HierarchicalBeanFactory, the return values will not take any
 * BeanFactory hierarchy into account, but will relate only to the beans defined
 * in the current factory. Use the BeanFactoryUtils helper class to get all.
 * 
 * <p>With the exception of getBeanDefinitionCount(), the methods in this interface
 * are not designed for frequent invocation. Implementations may be slow.
 *
 * <p>BeanFactory implementations that preload all their beans (for example,
 * DOM-based XML factories) may implement this interface.
 *
 * <p>This interface is discussed in "Expert One-on-One J2EE", by Rod Johnson.
 *
 * @author Rod Johnson
 * @since 16 April 2001
 * @version $Id$
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * Return the number of beans defined in the factory.
	 * Does not consider any hierarchy this factory may participate in.
	 * @return the number of beans defined in the factory
	 */
	int getBeanDefinitionCount();

	/**
	 * Return the names of all beans defined in this factory
	 * Does not consider any hierarchy this factory may participate in.
	 * @return the names of all beans defined in this factory,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();
	
	/**
	 * Return the names of beans matching the given object type 
	 * (including subclasses). 
	 * Does not consider any hierarchy this factory may participate in.
	 * @param type class or interface to match
	 * @return the names of beans matching the given object type 
	 * (including subclasses), or an empty array if none
	 */
	String[] getBeanDefinitionNames(Class type);
    
}
