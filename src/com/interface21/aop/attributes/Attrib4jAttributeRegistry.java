/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.attributes;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.AttributeRegistry;

import attrib4j.Attributes;

/**
 * @author Rod Johnson
 * @since 13-Mar-2003
 * @version $Revision$
 */
public class Attrib4jAttributeRegistry implements AttributeRegistry {

	/**
	 * @see org.aopalliance.AttributeRegistry#getAttributes(java.lang.reflect.AccessibleObject)
	 */
	public Object[] getAttributes(AccessibleObject ao) {
		// TODO: could cache, or want independent instances?
		Method m = (Method) ao;
		attrib4j.Attribute[] attrib4jAtts = Attributes.getAttributes(m);
		return attrib4jAtts;
	}
		

	/**
	 * @see org.aopalliance.AttributeRegistry#getAttributes(java.lang.Class)
	 */
	public Object[] getAttributes(Class clazz) {
		throw new UnsupportedOperationException("getAttributes(Class)");
	}

}
