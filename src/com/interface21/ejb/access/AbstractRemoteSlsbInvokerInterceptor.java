package com.interface21.ejb.access;

import javax.ejb.EJBObject;

/**
 * Superclass for interceptors proxying remote EJBs.
 *  @author Rod Johnson
 *  @version $Revision$
 */
public abstract class AbstractRemoteSlsbInvokerInterceptor extends AbstractSlsbInvokerInterceptor {
	
	/**
	 * Return a new instance of the stateless session bean.
	 * Can be overridden to change the algorithm.
	 * @return EJBObject
	 */
	protected EJBObject newSessionBeanInstance() {
		if (logger.isDebugEnabled())
			logger.debug("Trying to create EJB");
	
		EJBObject session = (EJBObject) getHomeBeanWrapper().invoke(CREATE_METHOD, null);
	
		// if it throws remote exception (wrapped in bean exception, retry?)
	
		if (logger.isDebugEnabled())
			logger.debug("EJB created OK [" + session + "]");
		return session;
	}

}
