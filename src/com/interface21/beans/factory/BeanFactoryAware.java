/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory;

/**
 * Interface to be implemented by beans that wish to be aware of their owning
 * BeanFactory. Beans can e.g. look up collaborating beans via the factory.
 *
 * <p>Note that most beans will choose to receive references to collaborating
 * beans via respective bean properties.
 *
 * @author Rod Johnson
 * @since 11-Mar-2003
 * @version $Revision$
 */
public interface BeanFactoryAware {
	
	/**
	 * Callback that supplies the owning factory to a bean instance.
	 * <p>If the bean also implements InitializingBean, this method will
	 * be invoked after InitializingBean's <code>afterPropertiesSet</code>.
	 * @param beanFactory owning BeanFactory (may not be null).
	 * The bean can immediately call methods on the factory.
	 * @throws Exception this method can throw any exception. Normally we want
	 * methods to declare more precise exceptions, but in this case the owning
	 * BeanFactory will catch and handle the exception (treating it as fatal),
	 * and we want to make it easy to implement BeanFactoryAware beans by
	 * freeing developers from the need to catch and wrap fatal exceptions.
	 * Exceptions thrown here are considered fatal.
	 */
	void setBeanFactory(BeanFactory beanFactory) throws Exception;

}
