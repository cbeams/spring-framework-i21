/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * Superclass for all session beans, not intended for direct client subclassing.
 *
 * <p>This class saves the session context provided by the EJB container in an instance
 * variable and provides a NOP implementation of the ejbRemove() lifecycle method.
 *
 * @version $Id$
 * @author Rod Johnson
 */
abstract class AbstractSessionBean extends AbstractEnterpriseBean implements SessionBean {

	/** the SessionContext passed to this object */
	private SessionContext sessionContext;

	/**
	 * Sets the session context.
	 * <p><b>If overriding this method, be sure to invoke this form of it first.</b>
	 * @param sessionContext SessionContext context for session
	 */
	public void setSessionContext(SessionContext sessionContext) {
		logger.debug("setSessionContext called on [" + this + "]");
		this.sessionContext = sessionContext;
	}
			
	/**
	 * Convenience method for subclasses.
	 * Return the EJB context saved on initialization.
	 * @return the SessionContext saved on initialization by this class's
	 * implementation of the setSessionContext() method.
	 */
	protected final SessionContext getSessionContext() {
		return sessionContext;
	}

}
