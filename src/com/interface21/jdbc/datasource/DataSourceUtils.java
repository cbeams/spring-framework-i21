/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.interface21.beans.DynamicProxy;
import com.interface21.jndi.JndiServices;
import com.interface21.util.ThreadObjectManager;
 
/**
 * Class containing static methods to obtain connections from JNDI and close
 * connections if necessary. Has support for thread-bound connections,
 * for example when using DataSourceTransactionManager.
 * 
 * @version $Id$
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.interface21.transaction.support.DataSourceTransactionManager
 */
public abstract class DataSourceUtils {

	/**
	 * Per-thread mappings: DataSource -> ConnectionHolder
	 */
	private static ThreadObjectManager threadObjectManager = new ThreadObjectManager();

	/**
	 * Return the thread object manager for data sources, keeping a
	 * DataSource/Connection map per thread for JDBC transactions.
	 * @return the thread object manager
	 * @see #getConnection
	 * @see com.interface21.transaction.support.DataSourceTransactionManager
	 */
	public static ThreadObjectManager getThreadObjectManager() {
		return threadObjectManager;
	}

	/**
	 * Get a data source from JNDI, under java:comp/env/jdbc.
	 * <p>Uses JndiServices internally for correct InitialContext handling.
	 * @param name name of the data source
	 * (supporting "test", "jdbc/test", and "java:comp/env/jdbc/test" syntaxes)
	 * @return the data source
	 * @throws com.interface21.jdbc.datasource.CannotGetJdbcConnectionException if the data source cannot be located
 	*/
	public static DataSource getDataSourceFromJndi(String name) throws CannotGetJdbcConnectionException {
		// check prefixes in name
		String jndiName = null;
		if (name.startsWith("java:comp/env/"))
			jndiName = name;
		else if (name.startsWith("jdbc/"))
			jndiName = "java:comp/env/" + name;
		else
			jndiName = "java:comp/env/jdbc/" + name;

		try {
			// Perform JNDI lookup to obtain resource manager connection factory
			return (DataSource) JndiServices.lookup(jndiName);
		}
		catch (NamingException ex) {
			throw new CannotGetJdbcConnectionException("Naming exception looking up JNDI data source [" + jndiName + "]", ex);
		}
	}
	
	/**
	 * Get a connection from the given J2EE DataSource. Changes any SQL exception into
	 * the Spring hierarchy of unchecked generic data access exceptions, simplifying
	 * calling code and making any exception that is thrown more meaningful.
	 * <p>Is aware of a respective connection bound to the current thread,
	 * for example when using DataSourceTransactionManager.
	 * @param ds DataSource to get connection from
	 * @throws com.interface21.jdbc.datasource.CannotGetJdbcConnectionException if we fail to get a connection from the given DataSource
	 * @return a JDBC connection from this DataSource
	 * @see #getThreadObjectManager
	 * @see com.interface21.transaction.support.DataSourceTransactionManager
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
		// only close if it isn't thread-bound
		ConnectionHolder holder = (ConnectionHolder) getThreadObjectManager().getThreadObject(ds);
		if (holder == null || con != holder.getConnection()) {
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
	 * Wrap the given connection with a proxy that delegates every method call
	 * to it but suppresses close calls. This is useful for allowing application
	 * code to handle a special framework connection just like an ordinary
	 * DataSource connection.
	 * @param source original connection
	 * @return the wrapped connection
	 * @see com.interface21.jdbc.datasource.SingleConnectionDataSource
	 */
	public static Connection getCloseSuppressingConnectionProxy(Connection source) {
		// use proxy that suppresses close() calls
		InvocationHandler handler = new DynamicProxy(source) {
			protected boolean beforeInvocation(Object proxy, Method method, Object[] args) {
				return (!method.getName().equals("close"));
			}
		};
		ClassLoader classLoader = DataSourceUtils.class.getClassLoader();
		return (Connection) Proxy.newProxyInstance(classLoader, new Class[] {Connection.class}, handler);
	}

}
