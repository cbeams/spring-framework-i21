package com.interface21.beans;

import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

import com.interface21.beans.BeansException;
import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;

/**
 * PropertyComparator performs a comparison of two beans,
 * using the specified bean property via a BeanWrapper.
 * @author Juergen Hoeller
 * @since 19.05.2003
 */
public class PropertyComparator implements Comparator {

	private SortDefinition sortDefinition;

	private Map cachedBeanWrappers = new HashMap();

	public PropertyComparator(SortDefinition sortDefinition) {
		this.sortDefinition = sortDefinition;
	}

	public int compare(Object o1, Object o2) {
		Object v1 = getPropertyValue(o1);
		Object v2 = getPropertyValue(o2);
		if (this.sortDefinition.isIgnoreCase() && (v1 instanceof String) && (v2 instanceof String)) {
			v1 = ((String) v1).toLowerCase();
			v2 = ((String) v2).toLowerCase();
		}
		int result = ((Comparable) v1).compareTo(v2);
		return (this.sortDefinition.isAscending() ? result : -result);
	}

	private Object getPropertyValue(Object o) throws BeansException {
		BeanWrapper bw = (BeanWrapper) this.cachedBeanWrappers.get(o);
		if (bw == null) {
			bw = new BeanWrapperImpl(o);
			this.cachedBeanWrappers.put(o, bw);
		}
		return bw.getPropertyValue(this.sortDefinition.getProperty());
	}


	/**
	 * Sorts the given List according to the given sort definition.
	 * <p>Note: Contained objects have to provide the given property
	 * in the form of a bean property, i.e. a getXXX method.
	 * @param source the input List
	 * @param sortDefinition the parameters to sort by
	 * @throws IllegalArgumentException in case of a missing propertyName
	 */
	public static void sort(List source, SortDefinition sortDefinition) throws BeansException {
		Collections.sort(source, new PropertyComparator(sortDefinition));
	}

	/**
	 * Sorts the given source according to the given sort definition.
	 * <p>Note: Contained objects have to provide the given property
	 * in the form of a bean property, i.e. a getXXX method.
	 * @param source input source
	 * @param sortDefinition the parameters to sort by
	 * @throws IllegalArgumentException in case of a missing propertyName
	 */
	public static void sort(Object[] source, SortDefinition sortDefinition) throws BeansException {
		Arrays.sort(source, new PropertyComparator(sortDefinition));
	}

}
