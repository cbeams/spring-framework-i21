/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.jndi.support;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.jndi.support.SimpleNamingContext;

/**
 * Simple implementation of a JNDI naming context builder.
 *
 * <p>Mainly targeted at test environments, where each test case can
 * configure JNDI appropriately, so that new InitialContext() will
 * expose the required objects. 
 * <p>
 * This context is also usable for standalone
 * applications. Typically used for binding a JDBC DataSource to a well-known JNDI
 * location, to be able to use J2EE data access code outside of a J2EE container.
 *
 * <p>There are various choices for DataSource implementations:
 * <ul>
 * <li>SingleConnectionDataSource (using the same Connection for all getConnection calls);
 * <li>DriverManagerDataSource (creating a new Connection on each getConnection call);
 * <li>Apache's Jakarta Commons DBCP offers BasicDataSource (a real pool).
 * </ul>
 *
 * <p>Typical usage in bootstrap code:<br><br>
 * <code>
 * SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
 * DataSource ds = new DriverManagerDataSource(...);<br>
 * builder.bind("java:comp/env/jdbc/myds", ds);<br>
 * builder.activate();
 * </code>
 * 
 * Note that it's impossible to create and active multiple builders within the same
 * JVM, due to JNDI restrictions. Thus to get and configure a new builder repeatedly, try:
 * <code>
 * SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
 * DataSource ds = new DriverManagerDataSource(...);<br>
 * builder.bind("java:comp/env/jdbc/myds", ds);<br>
 * </code>
 * Note that you <i>should not</i> call activate() on a context from this
 * static factory method, as this will already have been done and JNDI
 * lets us do it only once.
 *
 * <p>An instance of this class is only necessary at setup time.
 * An application does not need to keep it after activation.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @see #bind
 * @see #activate
 * @see SimpleNamingContext
 * @see com.interface21.jdbc.datasource.SingleConnectionDataSource
 * @see com.interface21.jdbc.datasource.DriverManagerDataSource
 * @version $Id$
 */
public class SimpleNamingContextBuilder implements InitialContextFactoryBuilder {
	
	/** Any instance of this class bound to JNDI. */
	private static SimpleNamingContextBuilder activated;

	private final Log logger = LogFactory.getLog(getClass());
	
	/**
	 * If no SimpleNamingContextBuilder is already configuring JNDI, create and activate one.
	 * Otherwise take the existing activate SimpleNamingContextBuilder, clear it and return it
	 * @return SimpleNamingContextBuilder an empty SimpleNamingContextBuilder that can be used
	 * to control the results of using JNDI
	 */
	public static SimpleNamingContextBuilder emptyActivatedContextBuilder() throws NamingException {
		if (activated != null) {
			activated.boundObjects.clear();
			return activated;
		}
		else {
			SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
			builder.activate();
			return builder;
		}
	}

	private Hashtable boundObjects = new Hashtable(); 

	/**
	 * Bind the given object under the given name, for all naming contexts
	 * that this context builder will generate.
	 * @param name the JNDI name of the object (e.g. "java:comp/env/jdbc/myds")
	 * @param obj the object to bind (e.g. a DataSource implementation)
	 */
	public void bind(String name, Object obj) {
		logger.info("Static JNDI binding: [" + name + "] = [" + obj + "]");
		boundObjects.put(name, obj);
	}

	/**
	 * Register the context builder by registering it with the JNDI NamingManager.
	 * Note that once this has been done, new InitialContext() will always return
	 * a context from this factory. Use the emptyActivatedContextBuilder() static
	 * method to get an empty context (for example, in test methods).
	 * @throws IllegalStateException if there's already a naming context builder
	 * registered with the JNDI NamingManager
	 */
	public void activate() throws IllegalStateException, NamingException {
		if (activated == null) {
			logger.info("Activating simple JNDI environment");
			NamingManager.setInitialContextFactoryBuilder(this);
			activated = this;
		}
		else {
			throw new IllegalStateException("Cannot create and activate a SimpleNamingContextBuilder if one has already been activate (JNDI restriction)");
		}
	}

	/**
	 * Simple InitialContextFactoryBuilder implementation,
	 * creating a new SimpleNamingContext instance.
	 */
	public InitialContextFactory createInitialContextFactory(Hashtable environment) {
		return new InitialContextFactory() {
			public Context getInitialContext(Hashtable environment) {
				return new SimpleNamingContext("", boundObjects, environment);
			}
		};
	}

}
