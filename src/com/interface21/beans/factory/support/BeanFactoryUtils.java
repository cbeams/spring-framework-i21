/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.support;

import java.util.ArrayList;
import java.util.List;

import com.interface21.beans.factory.ListableBeanFactory;

/**
 * Convenience methods operating on bean factories.
 * @author Rod Johnson
 * @since 04-Jul-2003
 * @version $Id$
 */
public abstract class BeanFactoryUtils {
	
	/**
	 * Return all beans of the given type or subtypes.
	 * Useful convenience method when we don't care about the
	 * bean name.
	 * @param type
	 * @param lbf
	 * @return List return the empty list if there are no beans
	 * of this type
	 */
	public static List beansOfType(Class type, ListableBeanFactory lbf) {
		 String[] beanNames = lbf.getBeanDefinitionNames(type);
		 List l = new ArrayList(beanNames.length);
		 for (int i = 0; i < beanNames.length; i++) {
		 	l.add(lbf.getBean(beanNames[i]));
		 }
		 return l;
	}

}
