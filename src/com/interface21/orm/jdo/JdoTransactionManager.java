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
 * @see PersistenceManagerFactoryUtils#closePersistenceManager
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
			return PersistenceManagerFactoryUtils.getThreadObjectManager().getThreadObject(this.persistenceManagerFactory);
		}
		else {
			PersistenceManager pm = PersistenceManagerFactoryUtils.getPersistenceManager(this.persistenceManagerFactory);
			return new PersistenceManagerHolder(pm);
		}
	}

	protected boolean isExistingTransaction(Object transaction) throws TransactionException {
		return PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(this.persistenceManagerFactory);
	}

	protected void doBegin(Object transaction, int isolationLevel, int timeout) throws TransactionException {
		if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
			throw new InvalidIsolationException("JdoTransactionManager does not support custom isolation levels");
		}
		if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
			throw new InvalidTimeoutException("JdoTransactionManager does not support timeouts");
		}
		PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) transaction;
		try {
			pmHolder.getPersistenceManager().currentTransaction().begin();
			PersistenceManagerFactoryUtils.getThreadObjectManager().bindThreadObject(this.persistenceManagerFactory, pmHolder);
		}
		catch (JDOException ex) {
			throw new CannotCreateTransactionException("Cannot create JDO transaction", ex);
		}
	}

	protected void doCommit(TransactionStatus status) throws TransactionException {
		PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) status.getTransaction();
		if (pmHolder.isRollbackOnly()) {
			// nested JDO transaction demanded rollback-only
			doRollback(status);
		}
		else {
			logger.debug("Committing JDO transaction");
			try {
				pmHolder.getPersistenceManager().currentTransaction().commit();
			}
			catch (JDOException ex) {
				throw new TransactionSystemException("Cannot commit JDO transaction", ex);
			}
			finally {
				closePersistenceManager(pmHolder);
			}
		}
	}

	protected void doRollback(TransactionStatus status) throws TransactionException {
		PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) status.getTransaction();
		logger.debug("Rolling back JDO transaction");
		try {
			pmHolder.getPersistenceManager().currentTransaction().rollback();
		}
		catch (JDOException ex) {
			throw new TransactionSystemException("Cannot rollback JDO transaction", ex);
		}
		finally {
			closePersistenceManager(pmHolder);
		}
	}

	protected void doSetRollbackOnly(TransactionStatus status) throws TransactionException {
		PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) status.getTransaction();
		logger.debug("Setting JDO transaction rollback-only");
		pmHolder.setRollbackOnly();
	}

	private void closePersistenceManager(PersistenceManagerHolder pmHolder) {
		PersistenceManagerFactoryUtils.getThreadObjectManager().removeThreadObject(this.persistenceManagerFactory);
		try {
			PersistenceManagerFactoryUtils.closePersistenceManager(pmHolder.getPersistenceManager(), this.persistenceManagerFactory);
		}
		catch (CleanupFailureDataAccessException ex) {
			// just log it, to keep a transaction-related exception
			logger.error("Cannot close JDO PersistenceManager after transaction", ex);
		}
	}

}
