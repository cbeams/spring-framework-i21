/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.datasource;

import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.interface21.jndi.AbstractJndiLocator;
import com.interface21.jndi.JndiTemplate;
import com.interface21.util.ThreadObjectManager;
 
/**
 * Class containing static methods to obtain connections from JNDI and close
 * connections if necessary. Has support for thread-bound connections,
 * for example for using DataSourceTransactionManager.
 *
 * <p>Note: The getDataSourceFromJndi methods are targetted at applications
 * that do not use a BeanFactory resp. an ApplicationContext. With the latter,
 * it is preferable to preconfigure your beans or even JdbcTemplate instances
 * in the factory: JndiObjectFactoryBean can be used to fetch a DataSource
 * from JNDI and give the DataSource bean reference to other beans. Switching
 * to another DataSource is just a matter of configuration then: You can even
 * replace the definition of the FactoryBean with a non-JNDI DataSource!
 *
 * @version $Id$
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.interface21.transaction.datasource.DataSourceTransactionManager
 * @see com.interface21.jndi.JndiObjectFactoryBean
 */
public abstract class DataSourceUtils {

	/**
	 * Per-thread mappings: DataSource -> ConnectionHolder
	 */
	private static final ThreadObjectManager threadObjectManager = new ThreadObjectManager();

	/**
	 * Return the thread object manager for data sources, keeping a
	 * DataSource/ConnectionHolder map per thread for JDBC transactions.
	 * @return the thread object manager
	 * @see #getConnection
	 * @see com.interface21.transaction.datasource.DataSourceTransactionManager
	 */
	public static ThreadObjectManager getThreadObjectManager() {
		return threadObjectManager;
	}

	/**
	 * Return if the given Connection is bound to the current thread,
	 * for the given DataSource.
	 * @param con JDBC Connection that should be checked
	 * @param ds DataSource that the Connection was created with
	 * @return if the Connection is bound for the DataSource
	 */
	public static boolean isConnectionBoundToThread(Connection con, DataSource ds) {
		ConnectionHolder holder = (ConnectionHolder) getThreadObjectManager().getThreadObject(ds);
		return (holder != null && con == holder.getConnection());
	}

	/**
	 * Look up the specified DataSource in JNDI, assuming that the lookup
	 * occurs in a J2EE container, i.e. adding the prefix "java:comp/env/"
	 * to the JNDI name if it doesn't already contain it.
	 * <p>Use getDataSourceFromJndi(jndiName,false) in case of a custom JNDI name.
	 * @param jndiName jndiName of the DataSource
	 * @return the DataSource
	 * @throws CannotGetJdbcConnectionException if the data source cannot be located
	 * @see #getDataSourceFromJndi(String,boolean)
	 */
	public static DataSource getDataSourceFromJndi(String jndiName) throws CannotGetJdbcConnectionException {
		return getDataSourceFromJndi(jndiName, true);
	}

	/**
	 * Look up the specified DataSource in JNDI, explicitly specifying
	 * if the lookup occurs in a J2EE container.
	 * @param jndiName jndiName of the DataSource
	 * @param inContainer if the lookup occurs in a J2EE container, i.e. if the prefix
	 * "java:comp/env/" needs to be added if the JNDI name doesn't already contain it.
	 * @return the DataSource
	 * @throws CannotGetJdbcConnectionException if the data source cannot be located
	 */
	public static DataSource getDataSourceFromJndi(String jndiName, boolean inContainer) throws CannotGetJdbcConnectionException {
		if (jndiName == null || "".equals(jndiName)) {
			throw new IllegalArgumentException("jndiName must not be empty");
		}
		if (inContainer && !jndiName.startsWith(AbstractJndiLocator.CONTAINER_PREFIX)) {
			jndiName = AbstractJndiLocator.CONTAINER_PREFIX + jndiName;
		}
		try {
			// Perform JNDI lookup to obtain resource manager connection factory
			return (DataSource) new JndiTemplate().lookup(jndiName);
		}
		catch (NamingException ex) {
			throw new CannotGetJdbcConnectionException("Naming exception looking up JNDI data source [" + jndiName + "]", ex);
		}
	}

	/**
	 * Get a connection from the given DataSource. Changes any SQL exception into
	 * the Spring hierarchy of unchecked generic data access exceptions, simplifying
	 * calling code and making any exception that is thrown more meaningful.
	 * <p>Is aware of a respective connection bound to the current thread,
	 * for example when using DataSourceTransactionManager.
	 * @param ds DataSource to get connection from
	 * @throws com.interface21.jdbc.datasource.CannotGetJdbcConnectionException if we fail to get a connection from the given DataSource
	 * @return a JDBC connection from this DataSource
	 * @see #getThreadObjectManager
	 * @see com.interface21.transaction.datasource.DataSourceTransactionManager
	 */
	public static Connection getConnection(DataSource ds) throws CannotGetJdbcConnectionException {
		ConnectionHolder holder = (ConnectionHolder) getThreadObjectManager().getThreadObject(ds);
		if (holder != null) {
			return holder.getConnection();
		} else {
			try {
				return ds.getConnection();
			}
			catch (SQLException ex) {
				throw new CannotGetJdbcConnectionException("DataSource " + ds, ex);
			}
		}
	}

	/**
	 * Close the given connection if necessary, i.e. if it is not bound to the thread
	 * and it is not created by a SmartDataSource returning shouldClose=false.
	 * @param con connection to close if necessary
	 * (if this is null, the call will be ignored)
	 * @param ds DataSource that the connection came from (can be null)
	 */
	public static void closeConnectionIfNecessary(Connection con, DataSource ds) throws CannotCloseJdbcConnectionException {
		if (con == null)
			return;
		if (!isConnectionBoundToThread(con, ds)) {
			boolean shouldClose = true;
			// leave the connection open only if the DataSource is our
			// special data source, and it wants the connection left open
			if (ds != null && ds instanceof SmartDataSource) {
				shouldClose = ((SmartDataSource) ds).shouldClose(con);
			}
			if (shouldClose) {
				try {
					con.close();
				}
				catch (SQLException ex) {
					throw new CannotCloseJdbcConnectionException("Failed to close connection", ex);
				}
			}
		}
	}

	/**
	 * Wrap the given connection with a proxy that delegates every method call to it
	 * but suppresses close calls. This is useful for allowing application code to
	 * handle a special framework connection just like an ordinary DataSource connection.
	 * @param source original connection
	 * @return the wrapped connection
	 * @see com.interface21.jdbc.datasource.SingleConnectionDataSource
	 */
	public static Connection getCloseSuppressingConnectionProxy(Connection source) {
		return (Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
		                                           new Class[] {Connection.class},
		                                           new CloseSuppressingInvocationHandler(source));
	}


	private static class CloseSuppressingInvocationHandler implements InvocationHandler {

		private final Connection source;

		public CloseSuppressingInvocationHandler(Connection source) {
			this.source = source;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (method.getName().equals("close")) {
				// Don't pass the call on
				return null;
			}
			try {
				return method.invoke(this.source, args);
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}

}
