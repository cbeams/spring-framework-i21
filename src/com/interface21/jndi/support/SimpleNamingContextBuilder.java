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

/**
 * Simple implementation of a JNDI naming context builder.
 *
 * <p>Mainly targeted at test environments, where each test case can
 * configure JNDI appropriately, so that new InitialContext() will
 * expose the required objects. Also usable for standalone applications,
 * e.g. for binding a JDBC DataSource to a well-known JNDI location,
 * to be able to use J2EE data access code outside of a J2EE container.
 *
 * <p>There are various choices for DataSource implementations:
 * <ul>
 * <li>SingleConnectionDataSource (using the same Connection for all getConnection calls);
 * <li>DriverManagerDataSource (creating a new Connection on each getConnection call);
 * <li>Apache's Jakarta Commons DBCP offers BasicDataSource (a real pool).
 * </ul>
 *
 * <p>Typical usage in bootstrap code:
 * <p><code>
 * SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();<br>
 * DataSource ds = new DriverManagerDataSource(...);<br>
 * builder.bind("java:comp/env/jdbc/myds", ds);<br>
 * builder.activate();
 * </code>
 * 
 * <p>Note that it's impossible to activate multiple builders within the same JVM,
 * due to JNDI restrictions. Thus to configure a fresh builder repeatedly, use
 * the following code to get a reference to either an already activated builder
 * or a newly activated one:
 * <p><code>
 * SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();<br>
 * DataSource ds = new DriverManagerDataSource(...);<br>
 * builder.bind("java:comp/env/jdbc/myds", ds);<br>
 * </code>
 * <p>Note that you <i>should not</i> call activate() on a builder from this
 * factory method, as there will already be an activated one in any case.
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
	
	/** Any instance of this class bound to JNDI */
	private static SimpleNamingContextBuilder activated;

	/**
	 * Checks if a SimpleNamingContextBuilder is active.
	 * @return the current SimpleNamingContextBuilder instance,
	 * or null if none
	 */
	public static SimpleNamingContextBuilder getCurrentContextBuilder() {
		return activated;
	}

	/**
	 * If no SimpleNamingContextBuilder is already configuring JNDI,
	 * create and activate one. Otherwise take the existing activate
	 * SimpleNamingContextBuilder, clear it and return it.
	 * <p>This is mainly intended for test suites that want to
	 * reinitialize JNDI bindings from scratch repeatedly.
	 * @return an empty SimpleNamingContextBuilder that can be used
	 * to control JNDI bindings
	 */
	public static SimpleNamingContextBuilder emptyActivatedContextBuilder() throws NamingException {
		if (activated != null) {
			// clear already activated context builder
			activated.clear();
			return activated;
		}
		else {
			// create and activate new context builder
			SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
			builder.activate();
			return builder;
		}
	}


	private final Log logger = LogFactory.getLog(getClass());

	private Hashtable boundObjects = new Hashtable();

	/**
	 * Register the context builder by registering it with the JNDI NamingManager.
	 * Note that once this has been done, new InitialContext() will always return
	 * a context from this factory. Use the emptyActivatedContextBuilder() static
	 * method to get an empty context (for example, in test methods).
	 * @throws IllegalStateException if there's already a naming context builder
	 * registered with the JNDI NamingManager
	 */
	public void activate() throws IllegalStateException, NamingException {
		logger.info("Activating simple JNDI environment");
		NamingManager.setInitialContextFactoryBuilder(this);
		activated = this;
	}

	/**
	 * Clears all bindings in this context builder.
	 */
	public void clear() {
		boundObjects.clear();
	}

	/**
	 * Binds the given object under the given name, for all naming contexts
	 * that this context builder will generate.
	 * @param name the JNDI name of the object (e.g. "java:comp/env/jdbc/myds")
	 * @param obj the object to bind (e.g. a DataSource implementation)
	 */
	public void bind(String name, Object obj) {
		logger.info("Static JNDI binding: [" + name + "] = [" + obj + "]");
		boundObjects.put(name, obj);
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
