/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.attributes;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.aopalliance.intercept.AttributeRegistry;


import junit.framework.TestCase;

import com.interface21.beans.TestBean;
import com.interface21.beans.factory.xml.XmlBeanFactory;

/**
 * 
 * @author Rod Johnson
 * @since 15-Jul-2003
 * @version $Id$
 */
public class WildcardAttributeRegistryTests extends TestCase {
	
	private static Method SET_AGE = null;
	private static Method GET_AGE = null;
	
	static {
		try {
			SET_AGE = TestBean.class.getMethod("setAge", new Class[] { int.class} );
			GET_AGE = TestBean.class.getMethod("getAge", null );
		}
		catch (NoSuchMethodException ex) {
			// Can't happen
		}
	}

	/**
	 * Constructor for WildcardAttributeRegistryTests.
	 * @param arg0
	 */
	public WildcardAttributeRegistryTests(String arg0) {
		super(arg0);
	}
	
	/**
	 * Should never return null
	 * @throws Exception
	 */
	public void testReturnsEmptyArrayNotNull() throws Exception {
		InputStream is = getClass().getResourceAsStream("wildcardAtts.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		TestBean rod = (TestBean) xbf.getBean("rod");
	
		AttributeRegistry wca1 = (AttributeRegistry) xbf.getBean("wca1");
		Object[] noAtts = wca1.getAttributes(Object.class.getMethod("hashCode", null));
		assertNotNull(noAtts);
		assertTrue(noAtts.length == 0);
	}
	
	public void testFullMethodNames() throws Exception {
		InputStream is = getClass().getResourceAsStream("wildcardAtts.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		TestBean rod = (TestBean) xbf.getBean("rod");
		
		AttributeRegistry wca1 = (AttributeRegistry) xbf.getBean("wca1");
		assertTrue(wca1.getAttributes(SET_AGE).length == 1);
		assertTrue(wca1.getAttributes(SET_AGE)[0].equals("setAgeAtt"));
		
		assertTrue(wca1.getAttributes(GET_AGE).length == 2);
		assertTrue(wca1.getAttributes(GET_AGE)[0].equals("zero"));
		assertTrue(wca1.getAttributes(GET_AGE)[1] == rod);
		
	}

}
