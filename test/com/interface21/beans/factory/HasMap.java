/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory;

import java.util.Map;
import java.util.Properties;

/**
 * Bean exposing a map. Used for bean factory tests.
 * @author Rod Johnson
 * @since 05-Jun-2003
 * @version $Id$
 */
public class HasMap {
	
	private Map map;
	
	private Properties props;
	
	private Object[] objectArray;
	
	private Class[] classArray;
	
	private Integer[] intArray;

	/**
	 * @return Map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Sets the map.
	 * @param map The map to set
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * @return Properties
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * Sets the props.
	 * @param props The props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * @return Object[]
	 */
	public Object[] getObjectArray() {
		return objectArray;
	}

	/**
	 * Sets the objectArray.
	 * @param objectArray The objectArray to set
	 */
	public void setObjectArray(Object[] objectArray) {
		this.objectArray = objectArray;
	}

	/**
	 * @return Class[]
	 */
	public Class[] getClassArray() {
		return classArray;
	}

	/**
	 * Sets the classArray.
	 * @param classArray The classArray to set
	 */
	public void setClassArray(Class[] classArray) {
		this.classArray = classArray;
	}

	/**
	 * @return
	 */
	public Integer[] getIntegerArray() {
		return intArray;
	}

	/**
	 * @param is
	 */
	public void setIntegerArray(Integer[] is) {
		intArray = is;
	}

}
