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
 * <p>Application code must retrieve a JDO PersistenceManager via
 * PersistenceManagerFactoryUtils' getPersistenceManager method, to be able to detect
 * a thread-bound PersistenceManager. It is preferable to use getPersistenceManager
 * with allowCreate=false, as the code relies on the interceptor to provide proper
 * PersistenceManager handling. Typically the code will look as follows:
 *
 * <p><code>
 * public void doJdoAction() {<br>
 * &nbsp;&nbsp;PersistenceManager pm = PersistenceManagerFactoryUtils.getPersistenceManager(this.pmf, false);<br>
 * &nbsp;&nbsp;try {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;catch (JDOException ex) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;throw PersistenceManagerFactoryUtils.convertJdoAccessException(ex);<br>
 * &nbsp;&nbsp;}<br>
 * }
 * </code>
 *
 * <p>Note that the application must care about handling JDOExceptions itself,
 * preferably via delegating to PersistenceManagerFactoryUtils' convertJdoAccessException
 * that converts them to ones that are compatible with the com.interface21.dao exception
 * hierarchy (jlike JdoTemplate does). As JDOExceptions are unchecked, they can simply
 * get thrown too, sacrificing generic DAO abstraction in terms of exceptions though.
 *
 * <p>This interceptor could convert unchecked JDOExceptions to unchecked dao ones
 * on-the-fly. The intercepted method wouldn't have to throw any special checked
 * exceptions to be able to achieve this. Nevertheless, such a mechanism would
 * effectively break the contract of the intercepted method (runtime exceptions
 * can be considered part of the contract too), therefore it isn't supported.
 *
 * <p>This class can be considered a declarative alternative to JdoTemplate's
 * callback approach. The advantages are:
 * <ul>
 * <li>no anonymous classes necessary for callback implementations;
 * <li>the possibility to throw any application exceptions from within data access code.
 * </ul>
 * The drawbacks are:
 * <ul>
 * <li>the dependency on interceptor configuration;
 * <li>the delegating try/catch blocks.
 * </ul>
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
					// just log it, to keep an invocation-related exception
					logger.error("Cannot close JDO PersistenceManager after method interception", ex);
				}
			}
			else {
				logger.debug("Not closing pre-bound JDO PersistenceManager after interceptor");
			}
		}
	}

}
