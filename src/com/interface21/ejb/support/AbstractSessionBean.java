/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * Ssuperclass for all session beans, not intended for direct client subclassing.
 * <br>This class saves the session context provided by the EJB container in an instance
 * variable and provides a NOP implementation of the ejbRemove() lifecycle method.
 * @version $Id$
 * @author Rod Johnson
 */
abstract class AbstractSessionBean extends AbstractEnterpriseBean implements SessionBean {

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
