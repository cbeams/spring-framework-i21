/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory;

import java.util.Map;

/**
 * Bean exposing a map. Used for bean factory tests.
 * @author Rod Johnson
 * @since 05-Jun-2003
 * @version $Id$
 */
public class HasMap {
	
	private Map map;

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

}
