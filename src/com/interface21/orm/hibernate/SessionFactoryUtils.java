package com.interface21.orm.hibernate;

import java.net.MalformedURLException;
import java.net.URL;

import javax.naming.NamingException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import org.apache.log4j.Logger;

import com.interface21.dao.DataAccessResourceFailureException;
import com.interface21.jndi.JndiTemplate;
import com.interface21.util.ThreadObjectManager;

/**
 * Helper class featuring methods for Hibernate session handling.
 * Used by HibernateTemplate, HibernateTransactionManager, etc.
 *
 * <p>Note: This class, like all of Spring's Hibernate support, requires
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
	 * SessionFactory/SessionHolder map per thread for Hibernate transactions.
	 * @return the thread object manager
	 * @see #openSession
	 * @see HibernateTransactionManager
	 */
	public static ThreadObjectManager getThreadObjectManager() {
		return threadObjectManager;
	}

	/**
	 * Look up the specified SessionFactory in JNDI, using a default JndiTemplate.
 	 * @param name name of the SessionFactory
	 * @return the SessionFactory
	 * @throws DataAccessResourceFailureException if the SessionFactory wasn't found
	 */
	public static SessionFactory getSessionFactoryFromJndi(String name)
	    throws DataAccessResourceFailureException {
		return getSessionFactoryFromJndi(name, new JndiTemplate());
	}

	/**
	 * Look up the specified SessionFactory in JNDI, using the given JndiTemplate.
 	 * @param name name of the SessionFactory
	 * @param jndiTemplate template instance to use for lookup, or null for default
	 * @return the SessionFactory
	 * @throws DataAccessResourceFailureException if the SessionFactory wasn't found
	 */
	protected static SessionFactory getSessionFactoryFromJndi(String name, JndiTemplate jndiTemplate)
	    throws DataAccessResourceFailureException {
		if (jndiTemplate == null) {
			jndiTemplate = new JndiTemplate();
		}
		try {
			return (SessionFactory) jndiTemplate.lookup(name);
		}
		catch (NamingException ex) {
			throw new DataAccessResourceFailureException("Could not initialize Hibernate SessionFactory from JNDI", ex);
		}

	}
	/**
	 * Create a Hibernate SessionFactory using the default config file.
	 * @return the new SessionFactory instance
	 * @throws DataAccessResourceFailureException if the SessionFactory could not be created
	 */
	public static SessionFactory createSessionFactory()
	    throws DataAccessResourceFailureException {
		return createSessionFactory(null);
	}


	/**
	 * Create a Hibernate SessionFactory with the given config file.
	 * @param configLocation location of the config file (can be a URl or a
	 * classpath resource), or null if default
	 * @return the new SessionFactory instance
	 * @throws DataAccessResourceFailureException if the SessionFactory could not be created
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
	 * Open a Hibernate session via the given factory.
	 * <p>Is aware of a respective session bound to the current thread,
	 * for example when using HibernateTransactionManager.
	 * @param sessionFactory Hibernate SessionFactory to create the session with
	 * @return the Hibernate Session
	 * @throws DataAccessResourceFailureException if the Session couldn't be created
	 */
	public static Session openSession(SessionFactory sessionFactory)
	    throws DataAccessResourceFailureException {
		SessionHolder holder = (SessionHolder) threadObjectManager.getThreadObject(sessionFactory);
		if (holder != null) {
			return holder.getSession();
		}
		try {
			logger.debug("Opening Hibernate session");
			return sessionFactory.openSession();
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
	 * @throws DataAccessResourceFailureException if the Session couldn't be closed
	 */
	public static void closeSessionIfNecessary(Session session, SessionFactory sessionFactory)
	    throws DataAccessResourceFailureException {
		if (session == null)
			return;
		// only close if it isn't thread-bound
		SessionHolder sessionHolder = (SessionHolder) threadObjectManager.getThreadObject(sessionFactory);;
		if (sessionHolder == null || session != sessionHolder.getSession()) {
			logger.debug("Closing Hibernate session");
			try {
				session.close();
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
