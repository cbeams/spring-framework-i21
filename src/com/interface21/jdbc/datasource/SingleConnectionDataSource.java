package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.interface21.dao.InvalidDataAccessApiUsageException;

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
public class SingleConnectionDataSource extends DriverManagerDataSource {

	/** if close() calls on the connection should be suppressed */
	private boolean suppressClose;

	/** wrapped connection */
	private Connection connection;

	public SingleConnectionDataSource() {
		super();
	}

	/**
	 * Create a new SingleConnectionDataSource with a given connection.
	 * @param source underlying source connection
	 * @param suppressClose if the connection should be wrapped with a* connection that
	 * suppresses close() calls (to allow for normal close() usage in applications that
	 * expect a pooled connection but do not know our SmartDataSource interface).
	 */
	public SingleConnectionDataSource(Connection source, boolean suppressClose) {
		super();
		if (source == null) {
			throw new InvalidDataAccessApiUsageException("Connection is null in SingleConnectionDataSource");
		}
		this.suppressClose = suppressClose;
		init(source);
	}

	/**
	 * Create a new SingleConnectionDataSource with the given standard
	 * DriverManager parameters.
	 * @param suppressClose if the connection should be wrapped with a* connection that
	 * suppresses close() calls (to allow for normal close() usage in applications that
	 * expect a pooled connection but do not know our SmartDataSource interface).
	 */
	public SingleConnectionDataSource(String driverName, String url, String user, String password,
	                                  boolean suppressClose) throws CannotGetJdbcConnectionException {
		setDriverName(driverName);
		setUrl(url);
		setUser(user);
		setPassword(password);
		this.suppressClose = suppressClose;
		afterPropertiesSet();
	}

	public void setSuppressClose(boolean suppressClose) {
		this.suppressClose = suppressClose;
	}

	public boolean isSuppressClose() {
		return suppressClose;
	}

	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		try {
			init(DriverManager.getConnection(getUrl(), getUser(), getPassword()));
		}
		catch (SQLException ex) {
			throw new CannotCloseJdbcConnectionException("Could not create connection", ex);
		}
	}

	protected void init(Connection source) {
		logger.info("Creating SingleConnectionDataSource with source [" + source + "]" +
		            (this.suppressClose ? " (close calls suppressed)" : ""));
		try {
			source.setAutoCommit(true);
		}
		catch (SQLException ex) {
			throw new CannotGetJdbcConnectionException("Could not set autoCommit", ex);
		}
		this.connection = this.suppressClose ? DataSourceUtils.getCloseSuppressingConnectionProxy(source) : source;
	}

	/**
	 * Closes the underlying connection.
	 * The provider of this DataSource needs to care for proper shutdown.
	 */
	public void close() throws SQLException {
		try {
			this.connection.close();
		}
		catch (SQLException ex) {
			throw new CannotCloseJdbcConnectionException("Cannot close connection", ex);
		}
	}

	/**
	 * This is a single connection: Do not close it when returning to the "pool".
	 */
	public boolean shouldClose(Connection conn) {
		return false;
	}

	public Connection getConnection() throws SQLException {
		logger.debug("SingleConnectionConnectionFactory.getConnection with con=" + connection);
		if (this.connection.isClosed()) {
			String mesg = "Connection was closed in SingleConnectionDataSource. "
						+ "Check that user code checks shouldClose() before closing connections";
			logger.warn(mesg);
			throw new SQLException(mesg);
		}
		return this.connection;
	}

	/**
	 * Specifying a custom user and password doesn't make sense with a single connection.
	 * Returns the single connection if given the same user and password, though.
	 */
	public Connection getConnection(String user, String password) throws SQLException {
		if (user != null && password != null && user.equals(getUser()) && password.equals(getPassword())) {
			return getConnection();
		}
		else {
			throw new UnsupportedOperationException("SingleConnectionDataSource does not support custom user and password");
		}
	}

}
