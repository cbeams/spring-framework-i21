package com.interface21.orm.jdo;

import java.io.IOException;
import java.util.Properties;

import javax.jdo.JDOFatalUserException;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;

/**
 * @author Juergen Hoeller
 */
public class LocalPersistenceManagerFactoryTests extends TestCase {

	public void testLocalPersistenceManagerFactoryBeanWithInvalidSettings() throws IOException {
		LocalPersistenceManagerFactoryBean pmfb = new LocalPersistenceManagerFactoryBean();
		try {
			pmfb.afterPropertiesSet();
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

	public void testLocalPersistenceManagerFactoryBeanWithJdoHelper() throws IOException {
		LocalPersistenceManagerFactoryBean pmfb = new LocalPersistenceManagerFactoryBean();
		Properties prop = new Properties();
		prop.setProperty("myKey", "myValue");
		pmfb.setJdoProperties(prop);
		try {
			pmfb.afterPropertiesSet();
			fail("Should have thrown JDOFatalUserException");
		}
		catch (JDOFatalUserException ex) {
			// expected
		}
	}

	public void testLocalPersistenceManagerFactoryBeanWithFile() throws IOException {
		LocalPersistenceManagerFactoryBean pmfb = new LocalPersistenceManagerFactoryBean() {
			protected PersistenceManagerFactory newPersistenceManagerFactory(Properties prop) {
				throw new IllegalArgumentException(prop.getProperty("myKey"));
			}
		};
		pmfb.setConfigLocation("/com/interface21/orm/jdo/test.properties");
		try {
			pmfb.afterPropertiesSet();
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
			assertTrue("Correct exception", "myValue".equals(ex.getMessage()));
		}
	}

	public void testLocalPersistenceManagerFactoryBeanWithProperties() throws IOException {
		LocalPersistenceManagerFactoryBean pmfb = new LocalPersistenceManagerFactoryBean() {
			protected PersistenceManagerFactory newPersistenceManagerFactory(Properties prop) {
				throw new IllegalArgumentException(prop.getProperty("myKey"));
			}
		};
		Properties prop = new Properties();
		prop.setProperty("myKey", "myValue");
		pmfb.setJdoProperties(prop);
		try {
			pmfb.afterPropertiesSet();
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
			assertTrue("Correct exception", "myValue".equals(ex.getMessage()));
		}
	}

}
