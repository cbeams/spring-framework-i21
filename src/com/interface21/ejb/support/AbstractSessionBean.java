/**
 * Generic framework code included with
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * This code is free to use and modify.
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.ejb.support;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * Convenient superclass for session beans.
 * <br>SFSBs can extend this class directly,
 * leaving them to implement the ejbActivate() and ejbPassivate() lifecycle methods
 * to comply with the requirements of the EJB specification.
 * <br>SLSBs will extend the AbstractStatelessSessionBean subclass of this class.
 * <br>This class saves the session context provided by the EJB container in an instance
 * variable and provides a NOP implementation of the ejbRemove() lifecycle method.
 * <br>NB: We cannot use final for our implementation of EJB lifecycle methods,
 * as this violates the EJB specification.
 * @author Rod Johnson
 */
public abstract class AbstractSessionBean extends AbstractEnterpriseBean implements SessionBean {

	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/** The SessionContext passed to this object */
	private SessionContext	sessionContext;


	//-------------------------------------------------------------------------
	// EJB lifecycle methods
	//-------------------------------------------------------------------------
	/**
	 * Sets the session context.
	 * <br><b>If overriding this method, be sure to invoke this form of it
	 * first.</b>
	 * @param sessionContext SessionContext Context for session
	 */
	public void setSessionContext(SessionContext sessionContext) {
		logger.debug("setSessionContext");
		this.sessionContext = sessionContext;
	}


	/**
	 * This method is required by the EJB Specification.
	 */
	public void ejbRemove() {
		logger.info("AbstractSessionBean NOP ejbRemove");
	}


	//-------------------------------------------------------------------------
	// Convenience methods for subclasses
	//-------------------------------------------------------------------------

	/**
	 * Convenience method for subclasses. Return the EJB context saved on
	 * initialization.
	 * @return the SessionContext saved on initialization by this class's
	 * implementation of the setSessionContext() method.
	 */
	protected final SessionContext getSessionContext() {
		return sessionContext;
	}

} 	// class AbstractSessionBean
