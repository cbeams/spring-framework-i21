package com.interface21.jdbc.datasource;

import java.sql.Connection;

/**
 * Connection holder, wrapping a JDBC Connection.
 * Features rollback-only support for nested JDBC transactions.
 *
 * <p>DataSourceTransactionManager binds instances of this class
 * to the thread, for a given DataSource.
 *
 * @author Juergen Hoeller
 * @since 06.05.2003
 * @see com.interface21.transaction.datasource.DataSourceTransactionManager
 * @see com.interface21.transaction.datasource.DataSourceTransactionObject
 * @see DataSourceUtils
 */
public class ConnectionHolder {

	private final Connection connection;

	private boolean rollbackOnly;

	public ConnectionHolder(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

}
