package com.interface21.orm.jdo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.dao.CleanupFailureDataAccessException;
import com.interface21.dao.DataAccessResourceFailureException;
import com.interface21.orm.hibernate.SessionFactoryUtils;
import com.interface21.util.ThreadObjectManager;

/**
 * Helper class featuring methods for JDO PersistenceManager handling,
 * allowing for reuse of PersistenceManager instances within transactions.
 * Used by JdoTemplate and JdoTransactionManager.
 * @author Juergen Hoeller
 * @since 03.06.2003
 */
public abstract class PersistenceManagerFactoryUtils {

	private static final Log logger = LogFactory.getLog(SessionFactoryUtils.class);

	/**
	 * Per-thread mappings: PersistenceManagerFactory -> PersistenceManagerHolder
	 */
	private static final ThreadObjectManager threadObjectManager = new ThreadObjectManager();

	/**
	 * Return the thread object manager for JDO PersistenceManagers keeping a
	 * PersistenceManagerFactory/PersistenceManagerHolder map per thread for
	 * JDO transactions.
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
	 * Create a JDO PersistenceManagerFactory with the given config file.
	 * @param configLocation location of the config file (can be a URL
	 * or a classpath resource)
	 * @return the new PersistenceManagerFactory instance
	 * @throws DataAccessResourceFailureException if the PersistenceManagerFactory could not be created
	 */
	public static PersistenceManagerFactory createPersistenceManagerFactory(String configLocation)
	    throws DataAccessResourceFailureException {
		Properties prop = new Properties();
		try {
			try {
				URL url = new URL(configLocation);
				prop.load(url.openStream());
			}
			catch (MalformedURLException ex) {
				// no URL -> try classpath resource
				if (!configLocation.startsWith("/")) {
					// always use root, as relative loading doesn't make sense
					configLocation = "/" + configLocation;
					InputStream in = PersistenceManagerFactoryUtils.class.getResourceAsStream(configLocation);
					if (in == null) {
						throw new DataAccessResourceFailureException("Cannot open config location: " + configLocation, null);
					}
					prop.load(in);
				}
			}
			return JDOHelper.getPersistenceManagerFactory(prop);
		}
		catch (IOException ex) {
			throw new DataAccessResourceFailureException("Cannot open config location: " + configLocation, ex);

		}
		catch (JDOException ex) {
			throw new DataAccessResourceFailureException("Cannot create JDO PersistenceManagerFactory", ex);
		}
	}

	/**
	 * Get a JDO PersistenceManager via the given factory.
	 * Is aware of a respective PersistenceManager bound to the current thread,
	 * for example when using JdoTransactionManager.
	 * @param pmf PersistenceManagerFactory to create the session with
	 * @return the PersistenceManager
	 * @throws DataAccessResourceFailureException if the PersistenceManager couldn't be created
	 */
	public static PersistenceManager getPersistenceManager(PersistenceManagerFactory pmf)
	    throws DataAccessResourceFailureException {
		PersistenceManagerHolder pmHolder = (PersistenceManagerHolder) threadObjectManager.getThreadObject(pmf);
		if (pmHolder != null) {
			return pmHolder.getPersistenceManager();
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
	 * Close the given PersistenceManager, created via the given factory,
	 * if it isn't bound to the thread.
	 * @param pm PersistenceManager to close
	 * @param pmf PersistenceManagerFactory that the PersistenceManager was created with
	 * @throws DataAccessResourceFailureException if the PersistenceManager couldn't be closed
	 */
	public static void closePersistenceManager(PersistenceManager pm, PersistenceManagerFactory pmf)
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
