/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.beans.factory;

import com.interface21.beans.BeansException;

/**
 * Interface to be implemented by objects that hold a number of
 * bean definitions, each uniquely identified by a String name. An independent
 * instance of any of these objects can be obtained (the Prototype
 * design pattern), or a single shared instance can be obtained 
 * (a superior alternative to the Singleton design pattern). Which type of instance
 * will be returned depends on the bean factory configuration--the API is
 * the same. The Singleton approach is much more useful and more common in practice.
 * <p/>The point of this approach is that the BeanFactory is a central
 * registry of application components, and centralizes the configuring
 * of application components (no more do individual objects need to
 * read properties files, for example). See chapters 4 and 11 of "Expert One-on-One
 * J2EE" for a discussion of the benefits of this approach.
 * <br/>Normally the BeanFactory will load bean definitions stored in
 * a configuration source (such as an XML document),
 * and uses the com.interface21.beans package to configure the beans. However,
 * an implementation could simply return Java objects it creates as
 * necessary directly in Java code.
 * There are no constraints on how the definitions could be stored: LDAP, RDBMS, XML,
 * properties file etc. Implementations are encouraged to support
 * references amongst beans, to either Singlet5ons or Prototypes.
 * @author  Rod Johnson
 * @since 13 April 2001
 * @version $RevisionId$
 */
public interface BeanFactory {

	/** 
	 * Return an instance (possibly shared or independent) of the given bean name.
	 * This method allows a bean factory to be used as a replacement for
	 * the Singleton or Prototype design pattern.
	 * <br/>Note that callers should retain references to returned objects. There is
	 * no guarantee that this method will be implemented to be efficient. For example,
	 * it may be synchronized, or may need to run an RDBMS query.
	 * @param name name of the bean to return
	 * @return the instance of the bean
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 */
    Object getBean(String name) throws BeansException;
	
	/** 
	 * Return an instance (possibly shared or independent) of the given bean name.
	 * Provides a measure of type safety by throwing an exception if the bean is not
	 * of the required type.
	 * This method allows a bean factory to be used as a replacement for
	 * the Singleton or Prototype design pattern.
	 * <br/>Note that callers should retain references to returned objects. There is
	 * no guarantee that this method will be implemented to be efficient. For example,
	 * it may be synchronized, or may need to run an RDBMS query.
	 * @param name name of the bean to return
	 * @param requiredType type the bean may match. Can be an interface or superclass
	 * of the actual class. For example, if the value is Object.class, this method will
	 * succeed whatever the class of the returned instance.
	 * @return the instance of the bean
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 */
	Object getBean(String name, Class requiredType) throws BeansException; 		 
	
	/**
	 * Is this bean a singleton? That is, will getBean() always return the same object?
	 * @param name name of the bean to query
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @return is this bean a singleton
	*/
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Return the aliases for the given bean name, if defined.
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 */
	String[] getAliases(String name);

}
