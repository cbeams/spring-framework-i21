
package com.interface21.beans;

/**
 * Static convenience methods for JavaBeans.
 * @author Rod Johnson
 */
public abstract class BeanUtils {

	/**
	 * Convenience method to instantiate a class using its
	 * no arg constructor. As this method doesn't try to load
	 * classes by name, it should avoid class-loading issues.
	 * @param clazz class to instantiate.
	 */
	public static Object instantiateClass(Class clazz) throws BeansException {
		try {
			//Object bean = Beans.instantiate(null, className);
			return clazz.newInstance();
		}
		catch (InstantiationException ex) {
			throw new FatalBeanException("Cannot instantiate [" + clazz + "]; is it an interface or an abstract class?", ex);
		}
		catch (IllegalAccessException ex) {
			throw new FatalBeanException("Cannot instantiate [" + clazz + "]; has class definition changed? Is there a public constructor?", ex);
		}
	}

}