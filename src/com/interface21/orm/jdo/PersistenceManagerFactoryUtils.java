package com.interface21.orm.jdo;

import javax.jdo.JDOException;
import javax.jdo.JDOFatalUserException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.dao.CleanupFailureDataAccessException;
import com.interface21.dao.DataAccessException;
import com.interface21.dao.DataAccessResourceFailureException;
import com.interface21.util.ThreadObjectManager;

/**
 * Helper class featuring methods for JDO PersistenceManager handling,
 * allowing for reuse of PersistenceManager instances within transactions.
 *
 * <p>Used by JdoTemplate, JdoInterceptor, and JdoTransactionManager.
 * Can also be used directly in application code, e.g. in combination
 * with JdoInterceptor.
 *
 * @author Juergen Hoeller
 * @since 03.06.2003
 * @see JdoTemplate
 * @see JdoInterceptor
 * @see JdoTransactionManager
 */
public abstract class PersistenceManagerFactoryUtils {

	private static final Log logger = LogFactory.getLog(PersistenceManagerFactoryUtils.class);

	/**
	 * Per-thread mappings: PersistenceManagerFactory -> PersistenceManagerHolder
	 */
	private static final ThreadObjectManager threadObjectManager = new ThreadObjectManager();

	/**
	 * Return the thread object manager for JDO PersistenceManagers keeping a
	 * PersistenceManagerFactory/PersistenceManagerHolder map per thread for
	 * JDO transactions.
	 * <p>Note: This is an SPI method, not intended to be used by applications.
	 * @return the thread object manager
	 * @see #getPersistenceManager
	 * @see JdoTransactionManager
	 */
	public static ThreadObjectManager getThreadObjectManager() {
		return threadObjectManager;
	}

	/**
	 * Return if the given PersistenceManager is bound to the current thread,
	 * for the given PersistenceManagerFactory.
	 * @param pm PersistenceManager that should be checked
	 * @param pmf PersistenceManagerFactory that the PersistenceManager was created with
	 * @return if the PersistenceManager is bound for the PersistenceManagerFactory
	 */
	public static boolean isPersistenceManagerBoundToThread(PersistenceManager pm, PersistenceManagerFactory pmf) {
		PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) threadObjectManager.getThreadObject(pmf);
		return (pmHolder != null && pm == pmHolder.getPersistenceManager());
	}

	/**
	 * Get a JDO PersistenceManager via the given factory.
	 * Is aware of a respective PersistenceManager bound to the current thread,
	 * for example when using JdoTransactionManager.
	 * Will create a new PersistenceManager else, if allowCreate is true.
	 * @param pmf PersistenceManagerFactory to create the session with
	 * @param allowCreate if a new PersistenceManager should be created if no thread-bound found
	 * @return the PersistenceManager
	 * @throws DataAccessResourceFailureException if the PersistenceManager couldn't be created
	 * @throws IllegalStateException if no thread-bound PersistenceManager found and allowCreate false
	 */
	public static PersistenceManager getPersistenceManager(PersistenceManagerFactory pmf, boolean allowCreate)
	    throws DataAccessResourceFailureException {
		PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) threadObjectManager.getThreadObject(pmf);
		if (pmHolder != null) {
			return pmHolder.getPersistenceManager();
		}
		if (!allowCreate) {
			throw new IllegalStateException("Not allowed to create new PersistenceManager");
		}
		logger.debug("Opening JDO PersistenceManager");
		try {
			return pmf.getPersistenceManager();
		}
		catch (JDOException ex) {
			throw new DataAccessResourceFailureException("Cannot get JDO PersistenceManager", ex);
		}
	}

	/**
	 * Convert the given JDOException to an appropriate exception from
	 * the com.interface21.dao hierarchy.
	 * @param ex JDOException that occured
	 * @return the corresponding DataAccessException instance
	 */
	public static DataAccessException convertJdoAccessException(JDOException ex) {
		if (ex instanceof JDOUserException || ex instanceof JDOFatalUserException) {
			return new JdoUsageException("Invalid JDO usage", ex);
		}
		// fallback
		return new JdoSystemException("Exception in JDO access code", ex);
	}

	/**
	 * Close the given PersistenceManager, created via the given factory,
	 * if it isn't bound to the thread.
	 * @param pm PersistenceManager to close
	 * @param pmf PersistenceManagerFactory that the PersistenceManager was created with
	 * @throws DataAccessResourceFailureException if the PersistenceManager couldn't be closed
	 */
	public static void closePersistenceManagerIfNecessary(PersistenceManager pm, PersistenceManagerFactory pmf)
	    throws CleanupFailureDataAccessException {
		if (pm == null || isPersistenceManagerBoundToThread(pm, pmf)) {
			return;
		}
		logger.debug("Closing JDO PersistenceManager");
		try {
			pm.close();
		}
		catch (JDOException ex) {
			throw new CleanupFailureDataAccessException("Cannot close JDO PersistenceManager", ex);
		}
	}

}
