/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

import org.easymock.EasyMock;
import org.easymock.MockControl;

import junit.framework.TestCase;

/**
 * 
 * @author Rod Johnson
 * @since 08-Jul-2003
 * @version $Id$
 */
public class JndiTemplateTests extends TestCase {

	/**
	 * Constructor for JndiTemplateTests.
	 * @param arg0
	 */
	public JndiTemplateTests(String arg0) {
		super(arg0);
	}
	
	public void testBind() throws Exception {
		Object o = new Object();
		String name = "foo";
		MockControl mc = EasyMock.controlFor(Context.class);
		final Context mock = (Context) mc.getMock();
		mock.bind(name, o);
		mc.setVoidCallable(1);
		mock.close();
		mc.setVoidCallable(1);
		mc.activate();
		
		JndiTemplate jt = new JndiTemplate() {
			protected Context createInitialContext() throws NamingException {
				return mock;
			}
		};
		
		jt.bind(name, o);
		mc.verify();
	}
	
	public void testUnbind() throws Exception {
		String name = "something";
		MockControl mc = EasyMock.controlFor(Context.class);
		final Context mock = (Context) mc.getMock();
		mock.unbind(name);
		mc.setVoidCallable(1);
		mock.close();
		mc.setVoidCallable(1);
		mc.activate();
	
		JndiTemplate jt = new JndiTemplate() {
			protected Context createInitialContext() throws NamingException {
				return mock;
			}
		};
	
		jt.unbind(name);
		mc.verify();
	}

}
