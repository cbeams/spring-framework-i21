package com.interface21.transaction.support;

/**
 * Interface for callbacks after transaction completion.
 * Supported by AbstractPlatformTransactionManager.
 * @author Juergen Hoeller
 * @since 02.06.2003
 * @see TransactionSynchronizationManager
 * @see AbstractPlatformTransactionManager
 */
public interface TransactionSynchronization {

	/**
	 * Completion status in case of proper commit
	 */
	int STATUS_COMMITTED = 0;

	/**
	 * Completion status in case of proper rollback
	 */
	int STATUS_ROLLED_BACK = 1;

	/**
	 * Status in case of heuristic mixed completion or system errors
	 */
	int STATUS_UNKNOWN = 2;

	/**
	 * Invoked after transaction completion.
	 * Can e.g. perform proper resource cleanup.
	 * @param status completion status according to the STATUS_ constants
	 */
	void afterCompletion(int status);

}
