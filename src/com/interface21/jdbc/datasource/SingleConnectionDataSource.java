package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.interface21.dao.InvalidDataAccessApiUsageException;

/**
 * Implementation of SmartDataSource that wraps a single connection which is not
 * closed after use. Obviously, this is not multi-threading capable.
 *
 * <p>Note that at shutdown, someone should close the underlying connection via the
 * close() method. Client code will never call close on the connection handle if it
 * is SmartDataSource-aware (e.g. uses DataSourceUtils.closeConnectionIfNecessary).
 *
 * <p>If client code will call close in the assumption of a pooled connection, like
 * when using persistence toolkits, set suppressClose to true. This will return a
 * close-suppressing proxy instead of the physical connection. Be aware that you will
 * not be able to cast this to an OracleConnection anymore, for example.
 *
 * <p>This is primarily a test class. For example, it enables easy testing of code
 * outside of an application server, in conjunction with a mock JNDI InitialContext.
 * In contrast to DriverManagerDataSource, it reuses the same connection all the time,
 * avoiding excessive creation of physical connections.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DataSourceUtils#closeConnectionIfNecessary
 * @see com.interface21.jndi.mock.MockInitialContextFactoryBuilder
 */
public class SingleConnectionDataSource extends DriverManagerDataSource {

	private boolean suppressClose;

	/** wrapped connection */
	private Connection connection;

	/**
	 * Constructor for bean-style configuration.
	 */
	public SingleConnectionDataSource() {
		super();
	}

	/**
	 * Create a new SingleConnectionDataSource with the given standard
	 * DriverManager parameters.
	 * @param suppressClose if the returned connection will be a close-suppressing
	 * proxy or the physical connection.
	 */
	public SingleConnectionDataSource(String driverName, String url, String user, String password,
	                                  boolean suppressClose) throws CannotGetJdbcConnectionException {
		setDriverClassName(driverName);
		setUrl(url);
		setUsername(user);
		setPassword(password);
		this.suppressClose = suppressClose;
	}

	/**
	 * Create a new SingleConnectionDataSource with a given connection.
	 * @param source underlying source connection
	 * @param suppressClose if the connection should be wrapped with a* connection that
	 * suppresses close() calls (to allow for normal close() usage in applications that
	 * expect a pooled connection but do not know our SmartDataSource interface).
	 */
	public SingleConnectionDataSource(Connection source, boolean suppressClose)
	    throws CannotGetJdbcConnectionException, InvalidDataAccessApiUsageException {
		super();
		if (source == null) {
			throw new InvalidDataAccessApiUsageException("Connection is null in SingleConnectionDataSource");
		}
		this.suppressClose = suppressClose;
		init(source);
	}

	/**
	 * Set if the returned connection will be a close-suppressing proxy or
	 * the physical connection.
	 */
	public void setSuppressClose(boolean suppressClose) {
		this.suppressClose = suppressClose;
	}

	/**
	 * Return if the returned connection will be a close-suppressing proxy or
	 * the physical connection.
	 */
	public boolean isSuppressClose() {
		return suppressClose;
	}

	/**
	 * Initialized the underlying connection.
	 * @param source the JDBC Connection to use,
	 * or null for initialization via DriverManager
	 */
	protected void init(Connection source) throws CannotGetJdbcConnectionException {
		if (source == null) {
			// no JDBC Connection given -> initialize via DriverManager
			try {
				source = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
			}
			catch (SQLException ex) {
				throw new CannotCloseJdbcConnectionException("Could not create connection", ex);
			}
		}

		// prepare connection
		try {
			source.setAutoCommit(true);
		}
		catch (SQLException ex) {
			throw new CannotGetJdbcConnectionException("Could not set autoCommit", ex);
		}

		// wrap connection?
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
		if (this.connection == null) {
			// No underlying connection -> lazy init via DriverManager
			init(null);
		}
		logger.debug("SingleConnectionConnectionFactory.getConnection: " + connection);
		if (this.connection.isClosed()) {
			throw new SQLException("Connection was closed in SingleConnectionDataSource. " +
			                       "Check that user code checks shouldClose() before closing connections, " +
			                       "or set suppressClose to true");
		}
		return this.connection;
	}

	/**
	 * Specifying a custom username and password doesn't make sense with a single connection.
	 * Returns the single connection if given the same username and password, though.
	 */
	public Connection getConnection(String username, String password) throws SQLException {
		if (username != null && password != null && username.equals(getUsername()) && password.equals(getPassword())) {
			return getConnection();
		}
		else {
			throw new SQLException("SingleConnectionDataSource does not support custom username and password");
		}
	}

}
