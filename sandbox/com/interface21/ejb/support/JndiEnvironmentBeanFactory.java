/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import java.util.HashMap;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;

/**
 * BeanFactory implementation populated by JNDI environment
 * variables available to an object running in a J2EE application server.
 * Such a bean factory might be used to parameterize EJBs.
 * <br>Only environment entries with names beginning with "beans."
 * are included.
 * @author Rod Johnson
 * @version $Id$
 */
public class JndiEnvironmentBeanFactory extends ListableBeanFactoryImpl {
	
	/** Syntax is beans.name.class=Y */
	public static final String BEANS_PREFIX = "beans.";
	
	/** Delimiter for properties */
	public static final String DELIMITER = ".";

	/** 
	 * Creates new JNDIBeanFactory
	 * @param root likely to be "java:comp/env"
	 */
    public JndiEnvironmentBeanFactory(String root) throws BeansException {
		
		// We'll take everything from the NamingContext and dump it in a
		// Properties object, so that the superclass can efficiently manipulate it
		// after we've closed the context.
		HashMap m = new HashMap();
		
		Context initCtx = null;		
		try {
			initCtx = new InitialContext();
			// Parameterize
			NamingEnumeration enum = initCtx.listBindings(root);
			
			// Orion 1.5.2 doesn't seem to regard anything under a /
			// as a true subcontext, so we need to search all bindings
			// Not all that fast, but it doesn't matter				
			while (enum.hasMore()) {
				Binding binding = (Binding) enum.next();								
				logger.debug("Name: " + binding.getName( ));
				logger.debug("Type: " + binding.getClassName( ));
				logger.debug("Value: " + binding.getObject());								
				m.put(binding.getName(), binding.getObject());
			}
			enum.close();
			
			registerBeanDefinitions(m, BEANS_PREFIX);
		}
		catch (NamingException ex) {
			logger.debug("----- NO PROPERTIES FOUND " + ex);
		}
		finally {
			try {
				if (initCtx != null) {
					initCtx.close();
				}
			}
			catch (NamingException ex) {
				// IGNORE OR THROW RTE?
			}
		}
    }	// constructor
	
}	// class JndiBeanFactory
