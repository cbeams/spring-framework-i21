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
 * <p>Mainly targetted at test environments, but also usable for standalone
 * applications. Typically used for binding a JDBC DataSource to a well-known JNDI
 * location, to be able to use J2EE data access code outside of a J2EE container.
 *
 * <p>There are various choices for DataSource implementations:<br>
 * - SingleConnectionDataSource (using the same Connection for all getConnection calls);<br>
 * - DriverManagerDataSource (creating a new Connection on each getConnection call);<br>
 * - Apache's Jakarta Commons DBCP offers BasicDataSource (a real pool).
 *
 * <p>Typical usage in bootstrap code:<br><br>
 * <code>
 * SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
 * DataSource ds = new DriverManagerDataSource(...);<br>
 * builder.bind("java:comp/env/jdbc/myds", ds);<br>
 * builder.activate();
 * </code>
 *
 * <p>An instance of this class is just necessary at setup time.
 * An application does not need to keep it after activation.
 *
 * @author Juergen Hoeller
 * @see #bind
 * @see #activate
 * @see SimpleNamingContext
 * @see com.interface21.jdbc.datasource.SingleConnectionDataSource
 * @see com.interface21.jdbc.datasource.DriverManagerDataSource
 */
public class SimpleNamingContextBuilder implements InitialContextFactoryBuilder {

	private final Log logger = LogFactory.getLog(getClass());

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
	 * @throws javax.naming.NamingException if there's already a naming context builder
	 * registered with the JNDI NamingManager
	 */
	public void activate() throws NamingException {
		logger.info("Activating simple JNDI environment");
		NamingManager.setInitialContextFactoryBuilder(this);
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
