package com.interface21.orm.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.CleanupFailureDataAccessException;
import com.interface21.jdbc.datasource.ConnectionHolder;
import com.interface21.jdbc.datasource.DataSourceUtils;
import com.interface21.transaction.CannotCreateTransactionException;
import com.interface21.transaction.InvalidTimeoutException;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionException;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.TransactionSystemException;
import com.interface21.transaction.support.AbstractPlatformTransactionManager;

/**
 * PlatformTransactionManager implementation for single Hibernate session
 * factories. Binds a Hibernate Session from the specified factory to the
 * thread, potentially allowing for one thread Session per factory.
 * SessionFactoryUtils and HibernateTemplate are aware of thread-bound
 * Sessions and take part in such transactions automatically.
 * Using either is required for proper Hibernate access code supporting
 * this transaction handling mechanism.
 * Supports custom isolation levels but not timeouts.
 *
 * <p>This implementation is appropriate for applications that solely use
 * Hibernate for transactional data access, but it also supports direct
 * data source access within a transaction (i.e. plain JDBC code working
 * with the same DataSource). This allows for mixing services that access
 * Hibernate including proper transactional caching, and services that use
 * plain JDBC without being aware of Hibernate! To be able to register the
 * Connection for plain JDBC code, the instance needs to be aware of the
 * DataSource (see setDataSource). Application code needs to stick to the
 * same Connection lookup pattern as with DataSourceTransactionManager
 * (i.e. DataSourceUtils.getConnection).
 *
 * <p>JTA resp. JtaTransactionManager is necessary for accessing multiple
 * transactional resources. The DataSource that Hibernate uses needs to be
 * JTA-enabled then (see container setup), alternatively the Hibernate JCA
 * connector can to be used for direct container integration. Normally,
 * Hibernate JTA setup is somewhat container-specific due to the JTA
 * TransactionManager lookup, required for proper transactional handling of
 * the JVM-level cache. Using the JCA Connector can solve this but involves
 * classloading issues and container-specific connector deployment.
 * Fortunately, there is an easier way with Spring: SessionFactoryUtils'
 * close and thus HibernateTemplate register synchronizations with
 * JtaTransactionManager, for proper completion callbacks.
 * Therefore, as long as JtaTransactionManager demarcates the JTA transactions,
 * Hibernate does not require any special JTA configuration for proper JTA.
 *
 * <p>Note: This class, like all of Spring's Hibernate support, requires
 * Hibernate 2.0 (initially developed with RC1).
 * 
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see SessionFactoryUtils#getSession
 * @see SessionFactoryUtils#closeSessionIfNecessary
 * @see HibernateTemplate#execute
 * @see #setDataSource
 * @see com.interface21.transaction.datasource.DataSourceTransactionManager
 * @see com.interface21.jdbc.datasource.DataSourceUtils#getConnection
 */
public class HibernateTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

	private SessionFactory sessionFactory;

	private DataSource dataSource;

	/**
	 * Create a new HibernateTransactionManager instance.
	 * A SessionFactory has to be set to be able to use it.
	 * @see #setSessionFactory
	 */
	public HibernateTransactionManager() {
	}

	/**
	 * Create a new HibernateTransactionManager instance.
	 * @param sessionFactory SessionFactory to manage transactions for
	 * @param dataSource DataSource to manage transactions for
	 */
	public HibernateTransactionManager(SessionFactory sessionFactory, DataSource dataSource) {
		this.sessionFactory = sessionFactory;
		this.dataSource = dataSource;
		afterPropertiesSet();
	}

	/**
	 * Set the SessionFactory that this instance should manage transactions for.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Return the SessionFactory that this instance should manage transactions for.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Set the J2EE DataSource that this instance should manage transactions for
   * (i.e. register the Hibernate transaction's JDBC connection to provide it
	 * to application code accessing this DataSource).
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
		if (this.sessionFactory == null) {
			throw new IllegalArgumentException("sessionFactory is required");
		}
	}

	protected Object doGetTransaction() throws CannotCreateTransactionException, TransactionException {
		if (SessionFactoryUtils.getThreadObjectManager().hasThreadObject(this.sessionFactory)) {
			logger.debug("Found thread-bound Session for Hibernate transaction");
			SessionHolder sessionHolder = (SessionHolder) SessionFactoryUtils.getThreadObjectManager().getThreadObject(this.sessionFactory);
			return new HibernateTransactionObject(sessionHolder, false);
		}
		else {
			logger.debug("Using new Session for Hibernate transaction");
			SessionHolder sessionHolder = new SessionHolder(SessionFactoryUtils.getSession(this.sessionFactory, true));
			return new HibernateTransactionObject(sessionHolder, true);
		}
	}

	protected boolean isExistingTransaction(Object transaction) throws TransactionException {
		HibernateTransactionObject txObject = (HibernateTransactionObject) transaction;
		return (txObject.getSessionHolder().getTransaction() != null);
	}

	protected void doBegin(Object transaction, int isolationLevel, int timeout) throws TransactionException {
		if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
			throw new InvalidTimeoutException("HibernateTransactionManager does not support timeouts");
		}
		HibernateTransactionObject txObject = (HibernateTransactionObject) transaction;
		logger.debug("Beginning Hibernate transaction");
		try {
			Session session = txObject.getSessionHolder().getSession();
			if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
				logger.debug("Changing isolation level to " + isolationLevel);
				txObject.setPreviousIsolationLevel(new Integer(session.connection().getTransactionIsolation()));
				session.connection().setTransactionIsolation(isolationLevel);
			}
			// add the Hibernate transaction to the session holder
			txObject.getSessionHolder().setTransaction(session.beginTransaction());
			if (txObject.isNewSessionHolder()) {
				// bind the session holder to the thread
				SessionFactoryUtils.getThreadObjectManager().bindThreadObject(this.sessionFactory, txObject.getSessionHolder());
			}
			// register the Hibernate Session's JDBC Connection for the DataSource, if set
			if (this.dataSource != null) {
				ConnectionHolder conHolder = new ConnectionHolder(session.connection());
				DataSourceUtils.getThreadObjectManager().bindThreadObject(this.dataSource, conHolder);
			}
		}
		catch (SQLException ex) {
			throw new CannotCreateTransactionException("Cannot set transaction isolation", ex);
		}
		catch (net.sf.hibernate.TransactionException ex) {
			throw new CannotCreateTransactionException("Cannot create Hibernate transaction", ex.getCause());
		}
		catch (HibernateException ex) {
			throw new CannotCreateTransactionException("Cannot create Hibernate transaction", ex);
		}
	}

	protected void doCommit(TransactionStatus status) throws TransactionException {
		HibernateTransactionObject txObject = (HibernateTransactionObject) status.getTransaction();
		if (txObject.getSessionHolder().isRollbackOnly()) {
			// nested Hibernate transaction demanded rollback-only
			doRollback(status);
		}
		else {
			logger.debug("Committing Hibernate transaction");
			try {
				txObject.getSessionHolder().getTransaction().commit();
			}
			catch (net.sf.hibernate.TransactionException ex) {
				throw new TransactionSystemException("Cannot commit Hibernate transaction", ex.getCause());
			}
			catch (HibernateException ex) {
				// assumably from an implicit flush
				throw SessionFactoryUtils.convertHibernateAccessException(ex);
			}
			finally {
				closeSession(txObject);
			}
		}
	}

	protected void doRollback(TransactionStatus status) throws TransactionException {
		HibernateTransactionObject txObject = (HibernateTransactionObject) status.getTransaction();
		logger.debug("Rolling back Hibernate transaction");
		try {
			txObject.getSessionHolder().getTransaction().rollback();
		}
		catch (net.sf.hibernate.TransactionException ex) {
			throw new TransactionSystemException("Cannot rollback Hibernate transaction", ex.getCause());
		}
		catch (HibernateException ex) {
			throw new TransactionSystemException("Cannot rollback Hibernate transaction", ex);
		}
		finally {
			closeSession(txObject);
		}
	}

	protected void doSetRollbackOnly(TransactionStatus status) throws TransactionException {
		HibernateTransactionObject txObject = (HibernateTransactionObject) status.getTransaction();
		logger.debug("Setting Hibernate transaction rollback-only");
		txObject.getSessionHolder().setRollbackOnly();
	}

	private void closeSession(HibernateTransactionObject txObject) {
		if (txObject.isNewSessionHolder()) {
			// remove the session holder from the thread
			SessionFactoryUtils.getThreadObjectManager().removeThreadObject(this.sessionFactory);
		}
		// remove the JDBC connection holder from the thread, if set
		if (this.dataSource != null) {
			DataSourceUtils.getThreadObjectManager().removeThreadObject(this.dataSource);
		}
		try {
			// reset transaction isolation to previous value, if changed for the transaction
			if (txObject.getPreviousIsolationLevel() != null) {
				logger.debug("Resetting isolation level to " + txObject.getPreviousIsolationLevel());
				Connection con = txObject.getSessionHolder().getSession().connection();
				con.setTransactionIsolation(txObject.getPreviousIsolationLevel().intValue());
			}
		}
		catch (HibernateException ex) {
			logger.warn("Cannot reset transaction isolation", ex);
		}
		catch (SQLException ex) {
			logger.warn("Cannot reset transaction isolation", ex);
		}
		finally {
			if (txObject.isNewSessionHolder()) {
				try {
					SessionFactoryUtils.closeSessionIfNecessary(txObject.getSessionHolder().getSession(), this.sessionFactory);
				}
				catch (CleanupFailureDataAccessException ex) {
					// just log it, to keep a transaction-related exception
					logger.error("Cannot close Hibernate Session after transaction", ex);
				}
			}
			else {
				logger.debug("Not closing pre-bound Hibernate Session after transaction");
			}
		}
	}
	
}
