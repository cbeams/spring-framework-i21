package com.interface21.orm.jdo;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.dao.CleanupFailureDataAccessException;
import com.interface21.transaction.CannotCreateTransactionException;
import com.interface21.transaction.InvalidIsolationException;
import com.interface21.transaction.InvalidTimeoutException;
import com.interface21.transaction.TransactionDefinition;
import com.interface21.transaction.TransactionException;
import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.TransactionSystemException;
import com.interface21.transaction.support.AbstractPlatformTransactionManager;

/**
 * PlatformTransactionManager implementation for single JDO persistence manager
 * factories. Binds a JDO PersistenceManager from the specified factory to the
 * thread, potentially allowing for one thread PersistenceManager per factory.
 * PersistenceManagerFactoryUtils and JdoTemplate are aware of thread-bound
 * persistence managers and take part in such transactions automatically.
 * Using either is required for JDO access code supporting this transaction
 * handling mechanism.
 *
 * <p>This implementation is appropriate for applications that solely use JDO
 * for transactional data access. JTA resp. JtaTransactionManager is necessary
 * for accessing multiple transactional resources. Unfortunately, there is no
 * way of using JTA with a local JDO PersistenceManagerFactory, at least not
 * with popular implementations like Kodo and Lido. One needs to deploy the
 * respective JCA connector to be able to take part in JTA transactions.
 *
 * <p>Note that Hibernate can take part in JTA transactions even with a local
 * Hibernate SessionFactory. Due to Spring's transaction synchronization
 * support, this doesn't even involve container-specific setup when using
 * Hibernate's transactional JVM-level cache.
 *
 * @author Juergen Hoeller
 * @since 03.06.2003
 * @see PersistenceManagerFactoryUtils#getPersistenceManager
 * @see PersistenceManagerFactoryUtils#closePersistenceManagerIfNecessary
 * @see JdoTemplate#execute
 * @see com.interface21.orm.hibernate.HibernateTransactionManager
 */
public class JdoTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

	private PersistenceManagerFactory persistenceManagerFactory;

	/**
	 * Create a new JdoTransactionManager instance.
	 * A PersistenceManagerFactory has to be set to be able to use it.
	 * @see #setPersistenceManagerFactory
	 */
	public JdoTransactionManager() {
	}

	/**
	 * Create a new JdoTransactionManager instance.
	 * @param pmf PersistenceManagerFactory to manage transactions for
	 */
	public JdoTransactionManager(PersistenceManagerFactory pmf) {
		this.persistenceManagerFactory = pmf;
		afterPropertiesSet();
	}

	/**
	 * Set the PersistenceManagerFactory that this instance should manage transactions for.
	 */
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf) {
		this.persistenceManagerFactory = pmf;
	}

	/**
	 * Return the PersistenceManagerFactory that this instance should manage transactions for.
	 */
	public PersistenceManagerFactory getPersistenceManagerFactory() {
		return persistenceManagerFactory;
	}

	public void afterPropertiesSet() {
		if (this.persistenceManagerFactory == null) {
			throw new IllegalArgumentException("persistenceManagerFactory is required");
		}
	}

	protected Object doGetTransaction() throws CannotCreateTransactionException, TransactionException {
		if (PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(this.persistenceManagerFactory)) {
			logger.debug("Found thread-bound PersistenceManager for JDO transaction");
			PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) PersistenceManagerFactoryUtils.getThreadObjectManager().getThreadObject(this.persistenceManagerFactory);
			return new JdoTransactionObject(pmHolder, false);
		}
		else {
			logger.debug("Using new PersistenceManager for JDO transaction");
			PersistenceManager pm = PersistenceManagerFactoryUtils.getPersistenceManager(this.persistenceManagerFactory, true);
			return new JdoTransactionObject(new PersistenceManagerHolder(pm), true);
		}
	}

	protected boolean isExistingTransaction(Object transaction) throws TransactionException {
		JdoTransactionObject txObject = (JdoTransactionObject) transaction;
		return txObject.getPersistenceManagerHolder().getPersistenceManager().currentTransaction().isActive();
	}

	protected void doBegin(Object transaction, int isolationLevel, int timeout) throws TransactionException {
		if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
			throw new InvalidIsolationException("JdoTransactionManager does not support custom isolation levels");
		}
		if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
			throw new InvalidTimeoutException("JdoTransactionManager does not support timeouts");
		}
		JdoTransactionObject txObject = (JdoTransactionObject) transaction;
		logger.debug("Beginning JDO transaction");
		try {
			txObject.getPersistenceManagerHolder().getPersistenceManager().currentTransaction().begin();
			if (txObject.isNewPersistenceManagerHolder()) {
				PersistenceManagerFactoryUtils.getThreadObjectManager().bindThreadObject(
						this.persistenceManagerFactory, txObject.getPersistenceManagerHolder());
			}
		}
		catch (JDOException ex) {
			throw new CannotCreateTransactionException("Cannot create JDO transaction", ex);
		}
	}

	protected void doCommit(TransactionStatus status) throws TransactionException {
		JdoTransactionObject txObject = (JdoTransactionObject) status.getTransaction();
		if (txObject.getPersistenceManagerHolder().isRollbackOnly()) {
			// nested JDO transaction demanded rollback-only
			doRollback(status);
		}
		else {
			logger.debug("Committing JDO transaction");
			try {
				txObject.getPersistenceManagerHolder().getPersistenceManager().currentTransaction().commit();
			}
			catch (JDOException ex) {
				throw new TransactionSystemException("Cannot commit JDO transaction", ex);
			}
			finally {
				closePersistenceManager(txObject);
			}
		}
	}

	protected void doRollback(TransactionStatus status) throws TransactionException {
		JdoTransactionObject txObject = (JdoTransactionObject) status.getTransaction();
		logger.debug("Rolling back JDO transaction");
		try {
			txObject.getPersistenceManagerHolder().getPersistenceManager().currentTransaction().rollback();
		}
		catch (JDOException ex) {
			throw new TransactionSystemException("Cannot rollback JDO transaction", ex);
		}
		finally {
			closePersistenceManager(txObject);
		}
	}

	protected void doSetRollbackOnly(TransactionStatus status) throws TransactionException {
		JdoTransactionObject txObject = (JdoTransactionObject) status.getTransaction();
		logger.debug("Setting JDO transaction rollback-only");
		txObject.getPersistenceManagerHolder().setRollbackOnly();
	}

	private void closePersistenceManager(JdoTransactionObject txObject) {
		if (txObject.isNewPersistenceManagerHolder()) {
			PersistenceManagerFactoryUtils.getThreadObjectManager().removeThreadObject(this.persistenceManagerFactory);
			try {
				PersistenceManagerFactoryUtils.closePersistenceManagerIfNecessary(
				    txObject.getPersistenceManagerHolder().getPersistenceManager(), this.persistenceManagerFactory);
			}
			catch (CleanupFailureDataAccessException ex) {
				// just log it, to keep a transaction-related exception
				logger.error("Cannot close JDO PersistenceManager after transaction", ex);
			}
		}
		else {
			logger.debug("Not closing pre-bound JDO PersistenceManager after transaction");
		}
	}

}
