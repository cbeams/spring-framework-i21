/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.jndi.support;

import javax.naming.NamingException;

import com.interface21.jndi.JndiTemplate;

/**
 * Simple implementation of JndiTemplate interface that always
 * returns a given object. Very useful for testing.
 * Effectively a mock object.
 * @author Rod Johnson
 * @see com.interface21.jdbc.datasource.DriverManagerDataSource
 * @version $Id$
 */
public class ExpectedLookupTemplate extends JndiTemplate {

	private String jndiName;

	private Object o;

	/**
	 * Construct a new JndiTemplate that will always
	 * return the given object, but honour only requests for the
	 * given name.
	 * @param name name client is expected to look up
	 * @param o object that will be returned
	 */
	public ExpectedLookupTemplate(String name, Object o) {
		this.jndiName = name;
		this.o = o;
	}

	/**
	 * If the name is the expected name specified in the constructor,
	 * return the object provided in the constructor. If
	 * the name is unexpected, throw an exception.
	 * @see com.interface21.jndi.JndiTemplate#lookup(java.lang.String)
	 */
	public Object lookup(String name) throws NamingException {
		if (!name.equals(jndiName))
			throw new UnsupportedOperationException("unexpected JNDI name");
		return o;
	}
}
