package com.interface21.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Implementation of SmartDataSource that wraps a single connection which is not
 * closed after use. Note that this means that something other than users of this
 * connection factory (which will never try to close the connection) should close
 * the connection.
 *
 * This is primarily a test class. For example, it enables easy testing of code
 * outside of an application server, in conjunction with a mock JNDI InitialContext.
 * Furthermore, it allows for executing server-oriented business services in
 * standalone scenarios (e.g. offline clients).
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.interface21.jndi.mock.MockInitialContextFactoryBuilder
 */
public class SingleConnectionDataSource implements SmartDataSource {

	protected final Logger logger = Logger.getLogger(getClass());
	
	/** Wrapped connection */
	private Connection connection;

	private PrintWriter pw = new PrintWriter(System.out);

	/**
	 * Create a new SingleConnectionDataSource.
	 * @param source underlying source connection
	 * @param suppressClose if the connection should be wrapped with a
	 * connection that suppressed close() calls (to allow for normal
	 * close() usage in applications that expect a pooled connection
	 * but do not know com.interface21.jdbc.core.SmartDataSource)
	 */
	public SingleConnectionDataSource(Connection source, boolean suppressClose) {
		if (source == null) {
			throw new NullPointerException("Connection is null in SingleConnectionDataSource");
		}
		init(source, suppressClose);
	}

	/**
	 * Create a new SingleConnectionDataSource with the given
	 * standard DriverManager parameters.
	 * @param suppressClose if the connection should be wrapped with a
	 * connection that suppressed close() calls (to allow for normal
	 * close() usage in applications that expect a pooled connection
	 * but do not know com.interface21.jdbc.core.SmartDataSource)
	 */
	public SingleConnectionDataSource(String driverName, String url, String user, String password,
	                                  boolean suppressClose) throws CannotGetJdbcConnectionException {
		try {
			Class.forName(driverName);
			init(DriverManager.getConnection(url, user, password), suppressClose);
		}
		catch (ClassNotFoundException ex) {
			throw new CannotGetJdbcConnectionException("cannot load driver " + driverName, ex);
		}
		catch (SQLException ex) {
			throw new CannotGetJdbcConnectionException("cannot create connection", ex);
		}
	}

	protected void init(Connection source, boolean suppressClose) {
		logger.info("Creating SingleConnectionDataSource with source [" + source + "]" +
		            (suppressClose ? " (close calls suppressed)" : ""));
		this.connection = suppressClose ? DataSourceUtils.getCloseSuppressingConnectionProxy(source) : source;
	}

	/**
	 * @see com.interface21.jdbc.datasource.SmartDataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		logger.debug("SingleConnectionConnectionFactory.getConnection with con=" + connection);
		if (connection.isClosed()) {
			String mesg = "Connection was closed in SingleConnectionDataSource. "
						+ "Check that user code checks shouldClose() before closing connections";
			logger.warn(mesg);
			throw new SQLException(mesg);
		}
		return connection;
	}

	/**
	 * @see com.interface21.jdbc.datasource.SmartDataSource#shouldClose(java.sql.Connection)
	 */
	public boolean shouldClose(Connection conn) {
		return false;
	}

	/** Must be invoked in a finally block
	 */
	public void close() throws SQLException {
		connection.close();
	}

	/*
	 * @see DataSource#getLogWriter()
	 */
	public PrintWriter getLogWriter() {
		// This is basically a debug class
		return pw;
	}

	public void setLogWriter(PrintWriter pw) {
		this.pw = pw;
	}

	/*
	 * We concentrate on use in an app server, so we don;t worry about this
	 * @see DataSource#getConnection(String, String)
	 */
	public Connection getConnection(String user, String password) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see DataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	/*
	 * @see DataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int timeout) throws SQLException {
	}
}