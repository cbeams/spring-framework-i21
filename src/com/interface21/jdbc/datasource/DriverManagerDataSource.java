package com.interface21.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Implementation of SmartDataSource that configures a plain old JDBC Driver
 * and returns a new connection every time.
 * 
 * Useful for testing in conjunction with a mock JNDI InitialContext,
 * especially when pool-assuming Connection.close() calls cannot be avoided,
 * e.g. when using persistence toolkits.
 *
 * @author Juergen Hoeller
 * @since 14.03.2003
 * @version $Id$
 * @see com.interface21.jndi.mock.MockInitialContextFactoryBuilder
 */
public class DriverManagerDataSource implements SmartDataSource {

	protected final static Logger logger = Logger.getLogger(DriverManagerDataSource.class);

	private String url;
	private String user;
	private String password;

	private PrintWriter pw = new PrintWriter(System.out);

	public DriverManagerDataSource(String driverName, String url, String user, String password) throws CannotGetJdbcConnectionException {
		this.url = url;
		this.user = user;
		this.password = password;

		try {
			Class.forName(driverName);
			logger.info("Loaded JDBC driver " + driverName);
		}
		catch (ClassNotFoundException ex) {
			throw new CannotGetJdbcConnectionException("Cannot load JDBC driver class '" + driverName + "'", ex);
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * @see com.interface21.jdbc.datasource.SmartDataSource#shouldClose(java.sql.Connection)
	 */
	public boolean shouldClose(Connection conn) {
		return true;
	}

	/*
	 * @see ConnectionFactory#getLogWriter()
	 */
	public PrintWriter getLogWriter() {
		return pw;
	}

	/*
	 * @see DataSource#getConnection(String, String)
	 */
	public Connection getConnection(String uname, String pwd) throws SQLException {
		throw new UnsupportedOperationException("getConnection(uname, pwd) is not implemented");
	}

	/*
	 * Returns 0: means use default system timeout
	 * @see DataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	/*
	 * @see DataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int arg0) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout");
	}

	/*
	 * @see DataSource#setLogWriter(PrintWriter)
	 */
	public void setLogWriter(PrintWriter pw) throws SQLException {
		this.pw = pw;
	}
}
