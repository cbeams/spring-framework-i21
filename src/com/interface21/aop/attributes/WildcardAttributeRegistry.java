/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.attributes;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.aopalliance.AspectException;
import org.aopalliance.AttributeRegistry;

/**
 * 
 * @author Rod Johnson
 * @since 15-Jul-2003
 * @version $Id$
 */
public class WildcardAttributeRegistry implements AttributeRegistry {

	/** 
	 * Map from literal or wildcard method name string 
	 * to single attribute or list of attributes
	 */
	private Map map;
	
	public void setMap(Map map) {
		// TODO copy?
		this.map = map;
	}
	
	/**
	 * @see org.aopalliance.AttributeRegistry#getAttributes(java.lang.reflect.AccessibleObject)
	 */
	public Object[] getAttributes(AccessibleObject ao) {
		if (!(ao instanceof Method))
			throw new AspectException("Only method attributes are supported");
		String name = ((Method) ao).getName();
		Object val = this.map.get(name);
		
		if (val == null) {
			// Never return null
			return new Object[0];
		}
		else if (val instanceof List) {
			// Convert list to an array if necessary
			return ((List) val).toArray();
		}
		else {
			// Return the map entry without changing it
			return new Object[] { val };
		}
	}

	/**
	 * @see org.aopalliance.AttributeRegistry#getAttributes(java.lang.Class)
	 */
	public Object[] getAttributes(Class clazz) {
		throw new UnsupportedOperationException("getAttributes(class)");
	}

}
