package com.interface21.transaction.jta;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.interface21.transaction.TransactionStatus;
import com.interface21.transaction.support.AbstractPlatformTransactionManager;

/**
 * PlatformTransactionManager implementation for JTA.
 * Simply delegates to the appropriate JtaServices methods.
 *
 * <p>This implementation is appropriate for handling distributed transactions,
 * i.e. transactions that span multiple resources, and for managing transactions
 * on a J2EE Connector (e.g. a persistence toolkit registered as Connector).
 * For a single JDBC DataSource, DataSourceTransactionManager is perfectly sufficient,
 * and for accessing a single resource with Hibernate (including transactional cache),
 * HibernateTransactionManager is appropriate.
 *
 * <p>Note: This implementation does not handle isolation levels. This needs
 * to be done by server-specific subclasses. DataSourceTransactionManager and
 * HibernateTransactionManager do support custom isolation levels, though.
 *
 * @author Juergen Hoeller
 * @since 24.03.2003
 * @see JtaServices
 * @see com.interface21.transaction.support.DataSourceTransactionManager
 * @see com.interface21.orm.hibernate.HibernateTransactionManager
 */
public class JtaTransactionManager extends AbstractPlatformTransactionManager {

	/**
	 * Create a new JtaTransactionManager instance.
	 */
	public JtaTransactionManager() {
	}

	protected Object doGetTransaction() {
		return JtaServices.getTransaction();
	}

	protected boolean isExistingTransaction(Object transaction) {
		return (JtaServices.getStatus((UserTransaction) transaction) != Status.STATUS_NO_TRANSACTION);
	}

	/**
	 * This standard JTA implementation simply ignores the isolation level.
	 * To be overridden by server-specific subclasses that actually handle
	 * the isolation level.
	 * @param transaction current JTA transaction object
	 * @param isolationLevel desired isolation level
	 */
	protected void doBegin(Object transaction, int isolationLevel) {
		JtaServices.begin((UserTransaction) transaction);
	}

	protected void doCommit(TransactionStatus status) {
		JtaServices.commit((UserTransaction) status.getTransaction());
	}

	protected void doRollback(TransactionStatus status) {
		JtaServices.rollback((UserTransaction) status.getTransaction());
	}

	protected void doSetRollbackOnly(TransactionStatus status) {
		JtaServices.setRollbackOnly((UserTransaction) status.getTransaction());
	}

}
