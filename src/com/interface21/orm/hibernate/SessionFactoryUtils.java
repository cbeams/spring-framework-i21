package com.interface21.orm.hibernate;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import javax.sql.DataSource;
import javax.naming.NamingException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import org.apache.log4j.Logger;

import com.interface21.dao.DataAccessResourceFailureException;
import com.interface21.jdbc.datasource.DataSourceUtils;
import com.interface21.util.ThreadObjectManager;
import com.interface21.jndi.JndiServices;

/**
 * Helper class featuring methods for Hibernate session handling.
 * Used by HibernateTemplate, HibernateTransactionManager, etc.
 *
 * Note: This class, like all of Spring's Hibernate support, requires
 * Hibernate 2.0 (initially developed with RC1).
 *
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see HibernateTemplate
 * @see HibernateTransactionManager
 */
public abstract class SessionFactoryUtils {

	private static Logger logger = Logger.getLogger(SessionFactoryUtils.class);

	/**
	 * Per-thread mappings: SessionFactory -> SessionHolder
	 */
	private static ThreadObjectManager threadObjectManager = new ThreadObjectManager();

	/**
	 * Return the thread object manager for Hibernate session, keeping a
	 * DataSource/Session map per thread for Hibernate transactions.
	 * @return the thread object manager
	 * @see #openSession
	 * @see HibernateTransactionManager
	 */
	public static ThreadObjectManager getThreadObjectManager() {
		return threadObjectManager;
	}

	/**
	 * Look up a Hibernate SessionFactory in JNDI.
	 * @param name JNDI name of the factory
	 * @return the SessionFactory instance
	 * @throws DataAccessResourceFailureException if the factory wasn't found
	 */
	public static SessionFactory getSessionFactoryFromJndi(String name) throws DataAccessResourceFailureException {
		SessionFactory sessionFactory = null;
		try {
			sessionFactory = (SessionFactory) JndiServices.lookup(name);
		}
		catch (NamingException ex) {
			throw new DataAccessResourceFailureException("Could not initialize Hibernate SessionFactory from JNDI", ex);
		}
		return sessionFactory;
	}

	/**
	 * Create a Hibernate SessionFactory using the default config file.
	 * @return the new SessionFactory instance
	 * @throws DataAccessResourceFailureException if the SessionFactory
	 * could not be created
	 */
	public static SessionFactory createSessionFactory()
	    throws DataAccessResourceFailureException {
		return createSessionFactory(null);
	}


	/**
	 * Create a Hibernate SessionFactory with the given config file.
	 * @param configLocation location of the config file (can be a URL
	 * or a classpath resource), or null if default
	 * @return the new SessionFactory instance
	 * @throws DataAccessResourceFailureException if the SessionFactory
	 * could not be created
	 */
	public static SessionFactory createSessionFactory(String configLocation)
	    throws DataAccessResourceFailureException {
		try {
			Configuration config = new Configuration();
			if (configLocation != null) {
				try {
					// try URL
					URL url = new URL(configLocation);
					config.configure(url);
				} catch (MalformedURLException ex) {
					// no URL -> try classpath resource
					if (!configLocation.startsWith("/")) {
						// always use root, as loading relative to some
						// Hibernate class' package doesn't make sense
						configLocation = "/" + configLocation;
					}
					config.configure(configLocation);
				}
			}
			else {
				// default config file
				config.configure();
			}
			return config.buildSessionFactory();
		}
		catch (HibernateException ex) {
			throw new DataAccessResourceFailureException("Could not create Hibernate session factory", ex);
		}
	}

	/**
	 * Open a Hibernate session via the given factory, letting Hibernate retrieve
	 * the underlying connection.
	 * <p>Is aware of a respective session bound to the current thread,
	 * for example when using HibernateTransactionManager.
	 * @param sessionFactory Hibernate SessionFactory to create the session with
	 * @return the Hibernate Session
	 */
	public static Session openSession(SessionFactory sessionFactory) {
		return openSession(sessionFactory, null);
	}

	/**
	 * Open a Hibernate Session via the given factory and the given data source.
	 * <p>Is aware of a respective Session bound to the current thread,
	 * for example when using HibernateTransactionManager.
	 * @param sessionFactory Hibernate SessionFactory to create the session with
	 * @param ds JDBC DataSource to create the underlying connection with,
	 * or null to let Hibernate retrieve it (e.g. when using
	 * DataSourceTransactionManager).
	 * @return the Hibernate Session
	 * @see HibernateTransactionManager
	 * @see com.interface21.transaction.support.DataSourceTransactionManager
	 */
	public static Session openSession(SessionFactory sessionFactory, DataSource ds) {
		SessionHolder holder = (SessionHolder) threadObjectManager.getThreadObject(sessionFactory);
		if (holder != null) {
			return holder.getSession();
		}
		try {
			logger.debug("Opening Hibernate session");
			if (ds != null) {
				// user-provided DataSource
				return sessionFactory.openSession(DataSourceUtils.getConnection(ds));
			}
			else {
				return sessionFactory.openSession();
			}
		}
		catch (JDBCException ex) {
			// SQLException underneath
			throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex.getSQLException());
		}
		catch (HibernateException ex) {
			throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
		}
	}

	/**
	 * Close the given session, created via the given factory and a Hibernate-retrieved
	 * underlying connection.
	 * @param session Session to close
	 * @param sessionFactory Hibernate SessionFactory that the Session was created with
	 */
	public static void closeSessionIfNecessary(Session session, SessionFactory sessionFactory) {
		closeSessionIfNecessary(session, sessionFactory, null);
	}

	/**
	 * Close the given session, created via the given factory and data source.
	 * @param session Session to close
	 * @param sessionFactory Hibernate SessionFactory that the Session was created with
	 * @param ds JDBC DataSource that created the underlying connection, or null
	 * if handled by Hibernate
	 * @see #openSession(SessionFactory,DataSource)
	 */
	public static void closeSessionIfNecessary(Session session, SessionFactory sessionFactory, DataSource ds) {
		if (session == null)
			return;
		// only close if it isn't thread-bound
		SessionHolder sessionHolder = (SessionHolder) threadObjectManager.getThreadObject(sessionFactory);;
		if (sessionHolder == null || session != sessionHolder.getSession()) {
			logger.debug("Closing Hibernate session");
			try {
				Connection con = session.close();
				if (ds != null) {
					// user-provided DataSource
					DataSourceUtils.closeConnectionIfNecessary(con, ds);
				}
			}
			catch (JDBCException ex) {
				// SQLException underneath
				throw new DataAccessResourceFailureException("Could not close Hibernate Session", ex.getSQLException());
			}
			catch (HibernateException ex) {
				throw new DataAccessResourceFailureException("Could not close Hibernate Session", ex);
			}
		}
	}

}
