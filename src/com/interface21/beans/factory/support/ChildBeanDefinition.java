
package com.interface21.beans.factory.support;

/**
 * Extension of BeanDefinition interface for beans whose
 * class is defined by their ancestry. PropertyValues
 * defined by the parent will also be "inherited",
 * although it's possible to override them by redefining
 * them in the property values associated with the child.
 */
public interface ChildBeanDefinition extends BeanDefinition {
	
	/**
	 * Return the name of the parent bean definition in
	 * the current bean factory.
	 * @return the name of the parent bean definition in
	 * the current bean factory
	 */
	String getParentName();

}
