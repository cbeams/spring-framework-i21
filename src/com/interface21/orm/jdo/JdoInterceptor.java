package com.interface21.orm.jdo;

import javax.jdo.PersistenceManagerFactory;

import org.aopalliance.MethodInterceptor;
import org.aopalliance.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.dao.CleanupFailureDataAccessException;

/**
 * This interceptor binds a new JDO PersistenceManager to the thread before a method
 * call, closing and removing it afterwards in case of any method outcome.
 * If there already was a pre-bound PersistenceManager (e.g. from JdoTransactionManager,
 * or from a surrounding JDO-intercepted method), the interceptor simply takes part in it.
 *
 * <p>Target code must retrieve a JDO PersistenceManager via PersistenceManagerFactoryUtils'
 * getPersistenceManager method, to be able to detect a thread-bound PersistenceManager.
 * It is preferable to use getPersistenceManager with allowCreate=false, as the code
 * relies on the interceptor to provide proper PersistenceManager handling.
 *
 * <p>This class can be considered a declarative alternative to JdoTemplate.
 * Note that this interceptor does not convert JDOExceptions into ones that are compatible
 * to the com.interface21.dao exception hierarchy. Thus, the application must care about
 * handling JDOExceptions instead of DataAccessExceptions, breaking the DAO abstraction.
 * Consider using JdoTemplate to avoid this.
 *
 * @author Juergen Hoeller
 * @since 13.06.2003
 */
public class JdoInterceptor implements MethodInterceptor {

	private final Log logger = LogFactory.getLog(getClass());

	private PersistenceManagerFactory persistenceManagerFactory;

	public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
		this.persistenceManagerFactory = persistenceManagerFactory;
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		PersistenceManagerHolder pmHolder = null;
		if (!PersistenceManagerFactoryUtils.getThreadObjectManager().hasThreadObject(this.persistenceManagerFactory)) {
			logger.debug("Using new PersistenceManager for JDO interceptor");
			pmHolder = new PersistenceManagerHolder(PersistenceManagerFactoryUtils.getPersistenceManager(this.persistenceManagerFactory, true));
			PersistenceManagerFactoryUtils.getThreadObjectManager().bindThreadObject(this.persistenceManagerFactory, pmHolder);
		}
		else {
			logger.debug("Found thread-bound PersistenceManager for JDO interceptor");
		}
		try {
			return methodInvocation.invokeNext();
		}
		finally {
			if (pmHolder != null) {
				PersistenceManagerFactoryUtils.getThreadObjectManager().removeThreadObject(this.persistenceManagerFactory);
				try {
					PersistenceManagerFactoryUtils.closePersistenceManagerIfNecessary(pmHolder.getPersistenceManager(), this.persistenceManagerFactory);
				}
				catch (CleanupFailureDataAccessException ex) {
					// just log it, to keep a invocation-related exception
					logger.error("Cannot close JDO PersistenceManager after method interception", ex);
				}
			}
			else {
				logger.debug("Not closing pre-bound JDO PersistenceManager after interceptor");
			}
		}
	}

}
