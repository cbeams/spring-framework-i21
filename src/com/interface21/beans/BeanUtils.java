package com.interface21.beans;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Static convenience methods for JavaBeans.
 * Provides e.g. methods for sorting lists of beans by any property.
 * @author Rod Johnson
 * @author Juergen Hoeller
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

	/**
	 * Copy the property values of the given source bean into the given target bean,
	 * ignoring the given ignoreProperties.
	 * @param source source bean
	 * @param target target bean
	 * @throws IllegalArgumentException if the classes of source and target do not match
	 */
	public static void copyProperties(Object source, Object target)
	    throws IllegalArgumentException, BeansException {
		copyProperties(source, target, null);
	}

	/**
	 * Copy the property values of the given source bean into the given target bean,
	 * ignoring the given ignoreProperties.
	 * @param source source bean
	 * @param target target bean
	 * @param ignoreProperties array of property names to ignore
	 * @throws IllegalArgumentException if the classes of source and target do not match
	 */
	public static void copyProperties(Object source, Object target, String[] ignoreProperties)
	    throws IllegalArgumentException, BeansException {
		if (source == null || target == null || !source.getClass().isInstance(target))
			throw new IllegalArgumentException("Target must an instance of source");
		List ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
		BeanWrapper sourceBw = new BeanWrapperImpl(source);
		BeanWrapper targetBw = new BeanWrapperImpl(target);
		MutablePropertyValues values = new MutablePropertyValues();
		for (int i = 0; i < sourceBw.getPropertyDescriptors().length; i++) {
			PropertyDescriptor sourceDesc = sourceBw.getPropertyDescriptors()[i];
			String name = sourceDesc.getName();
			PropertyDescriptor targetDesc = targetBw.getPropertyDescriptor(name);
			if (targetDesc.getWriteMethod() != null &&
			    (ignoreProperties == null || (!ignoreList.contains(name)))) {
				values.addPropertyValue(new PropertyValue(name, sourceBw.getPropertyValue(name)));
			}
		}
		targetBw.setPropertyValues(values);
	}

	/**
	 * Sorts the given List by the given propertyName.
	 * <p>Note: Contained objects have to provide the given propertyName in the form of a
	 * bean property, i.e. a getXXX method.
	 * @param source the input List
	 * @param propertyName the property to sort by
	 * @param ignoreCase ignore upper and lower case in Strings
	 * @param ascending ascending or descending?
	 * @throws IllegalArgumentException in case of a missing propertyName
	 */
	public static void sortByProperty(List source, String propertyName, boolean ascending, boolean ignoreCase)
	    throws BeansException {
		Collections.sort(source, new BeanPropertyComparator(propertyName, ascending, ignoreCase));
	}

	/**
	 * Sorts the given source by the given bean property.
	 * <p>Note: Contained objects have to provide the given propertyName in the form of a
	 * bean property, i.e. a getXXX method.
	 * @param source input source
	 * @param propertyName the property to sort by
	 * @param ignoreCase ignore upper and lower case in Strings
	 * @param ascending ascending or descending?
	 * @return the sorted source
	 * @throws IllegalArgumentException in case of a missing propertyName
	 */
	public static Object[] sortByProperty(Object[] source, String propertyName, boolean ascending, boolean ignoreCase)
	    throws BeansException {
		Object[] result = (Object[]) source.clone();
		Arrays.sort(result, new BeanPropertyComparator(propertyName, ascending, ignoreCase));
		return result;
	}

}
