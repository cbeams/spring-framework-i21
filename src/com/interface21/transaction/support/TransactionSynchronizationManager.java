package com.interface21.transaction.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a list of transactions synchronizations per thread.
 * Must be activated and deactivated by a transaction manager via init and clear.
 * Automatically supported by AbstractPlatformTransactionManager, and thus all
 * standard Spring transaction managers like JtaTransactionManager.
 *
 * <p>Resource management code should only register synchronizations when this
 * manager is active, and perform resource cleanup immediately else.
 * If transaction synchronization isn't active, there is either no current
 * transaction, or the transaction manager doesn't support synchronizations.
 *
 * <p>E.g. used for Hibernate Session close calls when using JTA,
 * for proper transactional handling of the JVM-level cache.
 *
 * @author Juergen Hoeller
 * @since 02.06.2003
 * @see #isActive
 * @see #register
 * @see TransactionSynchronization
 * @see AbstractPlatformTransactionManager
 * @see com.interface21.transaction.jta.JtaTransactionManager
 * @see com.interface21.orm.hibernate.SessionFactoryUtils#closeSessionIfNecessary
 */
public abstract class TransactionSynchronizationManager {

	private static ThreadLocal synchronizations = new ThreadLocal();

	/**
	 * Activate thread synchronizations for the current thread.
	 * Called by transaction manager on transaction begin.
	 */
	public static void init() {
		synchronizations.set(new ArrayList());
	}

	/**
	 * Return if thread synchronizations are active for the current thread.
	 * Can be called before register to avoid unnecessary instance creation.
	 * @see #register
	 */
	public static boolean isActive() {
		return (synchronizations.get() != null);
	}

	/**
	 * Register a new JTA synchronization for the current thread.
	 * Called by resource management code.
	 * Calls get ignored if transaction synchronization isn't active.
	 */
	public static void register(TransactionSynchronization synchronization) {
		if (isActive()) {
			((List) synchronizations.get()).add(synchronization);
		}
	}

	/**
	 * Trigger afterCompletion calls for the current thread.
	 * Called by transaction manager after transaction commit/rollback.
	 * Calls get ignored if transaction synchronization isn't active.
	 * @param status completion status according to TransactionSynchronization constants
	 * @see com.interface21.transaction.support.TransactionSynchronization
	 */
	public static void triggerAfterCompletion(int status) {
		if (isActive()) {
			for (Iterator it = ((List) synchronizations.get()).iterator(); it.hasNext();) {
				TransactionSynchronization synchronization = (TransactionSynchronization) it.next();
				synchronization.afterCompletion(status);
			}
		}
	}

	/**
	 * Deactivate thread synchronizations for the current thread.
	 * Called by transaction manager on transaction cleanup.
	 */
	public static void clear() {
		synchronizations.set(null);
	}

}
