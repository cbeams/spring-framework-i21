/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jndi;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class that simplifies JNDI operations. It provides methods to lookup
 * and bind, and allows implementations of the ContextCallback interface to
 * perform any operation they like with a JNDI naming context provided.
 *
 * <p>This is the central class in this package. It is analogous to the
 * JdbcTemplate class. This class performs all error handling.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ContextCallback
 * @see com.interface21.jdbc.core.JdbcTemplate
 */
public class JndiTemplate {
	
	protected final Log logger = LogFactory.getLog(getClass());

	private Properties environment;

	/**
	 * Create a new JndiTemplate instance.
	 */
	public JndiTemplate() {
	}

	/**
	 * Create a new JndiTemplate instance, using the given environment.
	 */
	public JndiTemplate(Properties environment) {
		this.environment = environment;
	}

	/**
	 * Set the environment for the InitialContext.
	 */
	public void setEnvironment(Properties environment) {
		this.environment = environment;
	}

	public Properties getEnvironment() {
		return environment;
	}

	/**
	 * Lookup the object with the given name in the current JNDI context.
	 * @param name JNDI name of the object
	 * @return object found (cannot be null, if a not so well-behaved
	 * JNDI implementations returns null, a NamingException gets thrown)
	 * @throws NamingException if there is no object with the given
	 * name bound to JNDI
	 */
	public Object lookup(final String name) throws NamingException {
		return execute(new ContextCallback() {
			public Object doInContext(Context ctx) throws NamingException {
				logger.debug("Looking up JNDI object with name '" + name + "'");
				Object lookedUp = ctx.lookup(name);
				if (lookedUp == null) {
					throw new NamingException("JNDI object not found: JNDI implementation returned null");
				}
				return lookedUp;
			}
		});
	}

	/**
	 * Bind the given object to the current JNDI context, using the given name.
	 * @param name JNDI name of the object
	 * @param object object to bind
	 * @throws NamingException  thrown by JNDI, mostly name already bound
	 */
	public void bind(final String name, final Object object) throws NamingException {
		execute(new ContextCallback() {
			public Object doInContext(Context ctx) throws NamingException {
				logger.info("Binding JNDI object with name " + name);
				ctx.bind(name, object);
				return null;
			}
		});
	}
	
	/**
	 * Remove the binding for the given name from the current JNDI context.
	 * @param name  the JNDI name of the object
	 * @throws NamingException thrown by JNDI, mostly name not found
	 */
	public void unbind(final String name) throws NamingException {
		execute(new ContextCallback() {
			public Object doInContext(Context ctx) throws NamingException {
				logger.info("Unbinding JNDI object with name " + name);
				ctx.unbind(name);
				return null;
			}
		});
	}
	
	/**
	 * Execute the given callback implementation.
	 * @param contextCallback ContextCallback implementation
	 * @return a result object returned by the callback, or null
	 * @throws NamingException thrown by the callback implementation.
	 */
	public Object execute(ContextCallback contextCallback) throws NamingException {
		Context ctx = null;
		try {
			ctx = createInitialContext();
			return contextCallback.doInContext(ctx);
		}
		finally {
			try {
				if (ctx != null)
					ctx.close();
			}
			catch (NamingException ex) {
				logger.warn("InitialContext threw exception on close", ex);
			}
		}
	}

	/**
	 * Create a new initial context.
	 * The default implementation use this template's environment settings.
	 * Can be subclassed for custom contexts, e.g. for testing.
	 * @return the new InitialContext instance
	 * @throws NamingException in case of initialization errors
	 */
	protected Context createInitialContext() throws NamingException {
		return new InitialContext(getEnvironment());
	}

}
