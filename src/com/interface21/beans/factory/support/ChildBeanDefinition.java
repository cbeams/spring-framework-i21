/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.beans.factory.support;

import com.interface21.beans.PropertyValues;


/**
 * Extension of BeanDefinition interface for beans whose
 * class is defined by their ancestry. PropertyValues
 * defined by the parent will also be "inherited",
 * although it's possible to override them by redefining
 * them in the property values associated with the child.
 * @author Rod Johnson
 * @version $Revision$
 */
public class ChildBeanDefinition extends AbstractBeanDefinition {

	private String parentName;

	/** Creates new BeanDefinition */
	public ChildBeanDefinition(String parentName, PropertyValues pvs, boolean singleton) {
		super(pvs, singleton);
		this.parentName = parentName;
	}

	/**
	 * Return the name of the parent bean definition in
	 * the current bean factory.
	 * @return the name of the parent bean definition in
	 * the current bean factory
	 */
	public String getParentName() {
		return parentName;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof ChildBeanDefinition))
			return false;
		return super.equals(arg0) && ((ChildBeanDefinition) arg0).getParentName().equals(this.getParentName());
	}
} 