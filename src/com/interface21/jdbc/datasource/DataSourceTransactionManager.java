package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.CleanupFailureDataAccessException;
import com.interface21.jdbc.datasource.ConnectionHolder;
import com.interface21.jdbc.datasource.DataSourceUtils;
import com.interface21.transaction.CannotCreateTransactionException;
import com.interface21.transaction.InvalidTimeoutException;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.TransactionSystemException;
import com.interface21.transaction.UnexpectedRollbackException;
import com.interface21.transaction.support.AbstractPlatformTransactionManager;

/**
 * PlatformTransactionManager implementation for single data sources.
 * Binds a JDBC connection from the specified data source to the thread,
 * potentially allowing for one thread connection per data source.
 * Supports custom isolation levels but not timeouts.
 *
 * <p>Application code is required to retrieve the JDBC connection via
 * DataSourceUtils.getConnection(DataSource) instead of J2EE's standard
 * DataSource.getConnection. This is recommended anyway, as it throws
 * unchecked com.interface21.dao exceptions instead of checked SQLException.
 * All framework classes like JdbcTemplate use this strategy implicitly.
 * If not used with this transaction manager, the lookup strategy
 * behaves exactly like the common one - it can thus be used in any case.
 *
 * <p>This implementation can be used instead of JtaTransactionManager
 * in the single resource case, as it does not require the container to
 * support JTA. Switching between both is just a matter of configuration,
 * if you stick to the required connection lookup pattern.
 *
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see DataSourceUtils#getConnection
 * @see SingleConnectionDataSource
 * @see com.interface21.util.ThreadObjectManager
 */
public class DataSourceTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

	private DataSource dataSource;

	/**
	 * Create a new DataSourceTransactionManager instance.
	 * A DataSource has to be set to be able to use it.
	 * @see #setDataSource
	 */
	public DataSourceTransactionManager() {
	}

	/**
	 * Create a new DataSourceTransactionManager instance.
	 * @param dataSource DataSource to manage transactions for
	 */
	public DataSourceTransactionManager(DataSource dataSource) {
		this.dataSource = dataSource;
		afterPropertiesSet();
	}

	/**
	 * Set the J2EE DataSource that this instance should manage transactions for.
	 */
	public final void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Return the J2EE DataSource that this instance manages transactions for.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	public void afterPropertiesSet() {
		if (this.dataSource == null) {
			throw new IllegalArgumentException("dataSource is required");
		}
	}

	protected Object doGetTransaction() {
		if (DataSourceUtils.getThreadObjectManager().hasThreadObject(this.dataSource)) {
			// existing transaction -> use it
			ConnectionHolder holder = (ConnectionHolder) DataSourceUtils.getThreadObjectManager().getThreadObject(this.dataSource);
			return new DataSourceTransactionObject(holder);
		}
		// no existing transaction -> create new holder
		ConnectionHolder holder = new ConnectionHolder(DataSourceUtils.getConnection(this.dataSource));
		return new DataSourceTransactionObject(holder);
	}

	protected boolean isExistingTransaction(Object transaction) {
		// standard DataSource -> check existence of thread connection
		return DataSourceUtils.getThreadObjectManager().hasThreadObject(this.dataSource);
	}

	/**
	 * This implementation sets the isolation level but ignores the timeout.
	 */
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
			throw new InvalidTimeoutException("DataSourceTransactionManager does not support timeouts", definition.getTimeout());
		}
		DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
		Connection con = txObject.getConnectionHolder().getConnection();
		logger.debug("Switching JDBC connection [" + con + "] to manual commit");
		try {
			// apply isolation level
			if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
				logger.debug("Changing isolation level to " + definition.getIsolationLevel());
				txObject.setPreviousIsolationLevel(new Integer(con.getTransactionIsolation()));
				con.setTransactionIsolation(definition.getIsolationLevel());
			}
			// apply read-only
			if (definition.isReadOnly()) {
				con.setReadOnly(true);
			}
			// switch to manual commit
			con.setAutoCommit(false);
		}
		catch (SQLException ex) {
			throw new CannotCreateTransactionException("Cannot configure connection", ex);
		}
		DataSourceUtils.getThreadObjectManager().bindThreadObject(this.dataSource, txObject.getConnectionHolder());
	}

	protected void doCommit(TransactionStatus status) {
		DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
		if (txObject.getConnectionHolder().isRollbackOnly()) {
			// nested JDBC transaction demanded rollback-only
			rollback(status);
		}
		else {
			logger.debug("Committing JDBC transaction [" + txObject.getConnectionHolder().getConnection() + "]");
			try {
				txObject.getConnectionHolder().getConnection().commit();
			}
			catch (SQLException ex) {
				throw new UnexpectedRollbackException("Cannot commit", ex);
			}
			finally {
				closeConnection(txObject);
			}
		}
	}

	protected void doRollback(TransactionStatus status) {
		DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
		logger.debug("Rolling back JDBC transaction [" + txObject.getConnectionHolder().getConnection() + "]");
		try {
			txObject.getConnectionHolder().getConnection().rollback();
		}
		catch (SQLException ex) {
			throw new TransactionSystemException("Cannot rollback", ex);
		}
		finally {
			closeConnection(txObject);
		}
	}

	protected void doSetRollbackOnly(TransactionStatus status) {
		DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
		logger.debug("Setting JDBC transaction [" + txObject.getConnectionHolder().getConnection() + "] rollback-only");
		txObject.getConnectionHolder().setRollbackOnly();
	}

	private void closeConnection(DataSourceTransactionObject txObject) {
		// remote the connection holder from the thread
		DataSourceUtils.getThreadObjectManager().removeThreadObject(this.dataSource);
		// reset connection
		Connection con = txObject.getConnectionHolder().getConnection();
		try {
			// reset to auto-commit
			con.setAutoCommit(true);
			// reset read-only
			if (con.isReadOnly()) {
				con.setReadOnly(false);
			}
			// reset transaction isolation to previous value, if changed for the transaction
			if (txObject.getPreviousIsolationLevel() != null) {
				logger.debug("Resetting isolation level to " + txObject.getPreviousIsolationLevel());
				con.setTransactionIsolation(txObject.getPreviousIsolationLevel().intValue());
			}
		}
		catch (SQLException ex) {
			logger.warn("Could not reset JDBC connection [" + con + "]", ex);
		}
		finally {
			try {
				DataSourceUtils.closeConnectionIfNecessary(con, this.dataSource);
			}
			catch (CleanupFailureDataAccessException ex) {
				// just log it, to keep a transaction-related exception
				logger.error("Cannot close connection after transaction", ex);
			}
		}
	}

}
