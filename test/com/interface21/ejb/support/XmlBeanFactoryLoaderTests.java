
package com.interface21.ejb.support;


import junit.framework.TestCase;

import com.interface21.beans.factory.support.BootstrapException;
import com.interface21.jndi.support.SimpleNamingContextBuilder;

/**
 * 
 * @author Rod Johnson
 */
public class XmlBeanFactoryLoaderTests extends TestCase {

	/**
	 * Constructor for SlsbEndpointBeanTests.
	 * @param arg0
	 */
	public XmlBeanFactoryLoaderTests(String arg0) {
		super(arg0);
	}
	
	
	public void testBeanFactoryPathRequiredFromJndiEnvironment() throws Exception {
		// Set up initial context but don't bind anything
		SimpleNamingContextBuilder sncb = SimpleNamingContextBuilder.emptyActivatedContextBuilder();

		XmlBeanFactoryLoader xbfl = new XmlBeanFactoryLoader();
		try {
			xbfl.loadBeanFactory();
			fail();
		}
		catch (BootstrapException ex) {
			// Check for helpful JNDI message
			assertTrue(ex.getMessage().indexOf(XmlBeanFactoryLoader.BEAN_FACTORY_PATH_ENVIRONMENT_KEY) != -1);
		}
	}
	
	public void testBeanFactoryPathFromJndiEnvironmentNotFound() throws Exception  {
		SimpleNamingContextBuilder sncb = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		
		String bogusPath = "/RUBBISH/com/lch/framework/server/test1.xml";
	
		// Set up initial context
		sncb.bind(XmlBeanFactoryLoader.BEAN_FACTORY_PATH_ENVIRONMENT_KEY, bogusPath);

		XmlBeanFactoryLoader xbfl = new XmlBeanFactoryLoader();
		try {
			xbfl.loadBeanFactory();
			fail();
		}
		catch (BootstrapException ex) {
			// Check for helpful JNDI message
			assertTrue(ex.getMessage().indexOf(bogusPath) != -1);
		}
	}
	
	public void testBeanFactoryPathFromJndiEnvironmentNotValidXml() throws Exception {
		SimpleNamingContextBuilder sncb = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
	
		String nonXmlPath = "/com/lch/framework/server/SlsbEndpointBean.class";

		// Set up initial context
		sncb.bind(XmlBeanFactoryLoader.BEAN_FACTORY_PATH_ENVIRONMENT_KEY, nonXmlPath);

		XmlBeanFactoryLoader xbfl = new XmlBeanFactoryLoader();
		try {
			xbfl.loadBeanFactory();
			fail();
		}
		catch (BootstrapException ex) {
			// Check for helpful JNDI message
			assertTrue(ex.getMessage().indexOf(nonXmlPath) != -1);
		}
	}
	
}