package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.interface21.beans.factory.InitializingBean;

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
public class DriverManagerDataSource extends AbstractDataSource implements SmartDataSource, InitializingBean {

	private String driverName = "";
	private String url = "";
	private String user = "";
	private String password = "";

	public DriverManagerDataSource() {
	}

	public DriverManagerDataSource(String driverName, String url, String user, String password) throws CannotGetJdbcConnectionException {
		this.driverName = driverName;
		this.url = url;
		this.user = user;
		this.password = password;
		afterPropertiesSet();
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void afterPropertiesSet() {
		try {
			Class.forName(this.driverName);
			logger.info("Loaded JDBC driver: " + this.driverName);
		}
		catch (ClassNotFoundException ex) {
			throw new CannotGetJdbcConnectionException("Cannot load JDBC driver class '" + this.driverName + "'", ex);
		}
	}

	/**
	 * This returns a new connection every time: Close it when returning it to the "pool".
	 */
	public boolean shouldClose(Connection conn) {
		return true;
	}

	public Connection getConnection() throws SQLException {
		logger.info("Creating new JDBC connection: " + this.url);
		Connection con = DriverManager.getConnection(this.url, this.user, this.password);
		con.setAutoCommit(true);
		return con;
	}

	public Connection getConnection(String user, String password) throws SQLException {
		return DriverManager.getConnection(this.url, user, password);
	}

}
