/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.CreateException;
import javax.ejb.EJBException;

/**
 * Convenient superclass for stateless session beans (SLSBs), minimizing the work
 * involved in implementing an SLSB and preventing common errors. <b>Note that
 * SLSBs are the most useful kind of EJB.</b>
 * <br>As the ejbActivate() and ejbPassivate() methods cannot be invoked on SLSBs,
 * these methods are implemented to throw an exception and should not be overriden by 
 * subclasses. (Unfortunately the EJB specification forbids enforcing this by making
 * EJB lifecycle methods final.)
 * <br>There should
 * be no need to override the setSessionContext() or
 * ejbCreate() lifecycle methods.
 * <br>Subclasses are left to implement the onEjbCreate() method to do
 * whatever initialization they wish to do after their BeanFactory
 * has already been loaded, and is available from the getBeanFactory() method.
 * This class provides the no-argument ejbCreate() method required
 * by the EJB specification, but not the SessionBean interface,
 * eliminating a common cause of EJB deployment failure.
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class AbstractStatelessSessionBean extends AbstractSessionBean {

	/** 
	 * This implementation loads the BeanFactory.
	 * Don't override it (although it can't be made final):
	 * code your initialization in onEjbCreate(), which is
	 * called when the BeanFactory is available.
	 * Unfortunately we can't load the BeanFactory in setSessionContext(), as ResourceManager
	 * access isn't permitted and the BeanFactory may require it. 
	 */
	public void ejbCreate() throws CreateException {
		loadBeanFactory();
		onEjbCreate();
	}
	
	/**
	 * Subclasses must implement this method to do any initialization
	 * they would otherwise have done in an ejbCreate() method. 
	 * The BeanFactory will have been loaded.
	 * The same restrictions apply to the work of this method as
	 * to an ejbCreate() method.
	 * @throws CreateException
	 */
	protected abstract void onEjbCreate() throws CreateException;

	
	/**
	 * @see javax.ejb.SessionBean#ejbActivate(). This method always throws an exception, as
	 * it should not be invoked by the EJB container.
	 */
	public void ejbActivate() throws EJBException {
		throw new IllegalStateException("ejbActivate must not be invoked on a stateless session bean");
	}

	/**
	 * @see javax.ejb.SessionBean#ejbPassivate(). This method always throws an exception, as
	 * it should not be invoked by the EJB container.
	 */
	public void ejbPassivate() throws EJBException {
		throw new IllegalStateException("ejbPassivate must not be invoked on a stateless session bean");
	}

}
