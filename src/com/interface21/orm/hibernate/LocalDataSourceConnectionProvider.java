package com.interface21.orm.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.connection.ConnectionProvider;
import net.sf.hibernate.util.JDBCExceptionReporter;

/**
 * Hibernate connection provider for local DataSource instances in an
 * application context. Default provider when using LocalSessionFactoryBean,
 * if not explictly overridden by the Hibernate configuration.
 * @author Juergen Hoeller
 * @since 11.07.2003
 * @see LocalSessionFactoryBean
 */
public class LocalDataSourceConnectionProvider implements ConnectionProvider {

	/**
	 * This will hold the DataSource to use for the currently configured
	 * Hibernate SessionFactory. It will be set just before initialization
	 * of the respective SessionFactory, and reset immediately afterwards.
	 */
	protected static ThreadLocal configTimeDataSourceHolder = new ThreadLocal();

	private DataSource dataSource;

	public void configure(Properties props) throws HibernateException {
		this.dataSource = (DataSource) configTimeDataSourceHolder.get();
		// absoutely needs thread-bound DataSource to initialize
		if (this.dataSource == null) {
			throw new HibernateException("No local DataSource found for configuration - dataSource property must be set on LocalSessionFactoryBean");
		}
		// reset thread-bound DataSource
		configTimeDataSourceHolder.set(null);
	}

	public Connection getConnection() throws SQLException {
		try {
			return this.dataSource.getConnection();
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
	}

	public void closeConnection(Connection conn) throws SQLException {
		try {
			conn.close();
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
	}

	public boolean isStatementCache() {
		return true;
	}

	public void close() {
	}

}
