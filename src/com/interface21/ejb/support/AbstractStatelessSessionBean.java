/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
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
 * these two methods final.)
 * <br>Subclasses are left to implement the ejbCreate() method to ensure that they
 * offer a no-argument implementation of the home interface's create() method as
 * required by the EJB specification.
 * @author Rod Johnson
 */
public abstract class AbstractStatelessSessionBean extends AbstractSessionBean {

	/** 
	 * This is declared abstract to ensure that subclasses implement this method. 
	 * Otherwise it isn't required by the compiler, but will
	 * fail on deployment. This is a common cause of errors in implementing SLSBs.
	 * <br/>The BeanFactory is available at this point
	 */
	public abstract void ejbCreate() throws CreateException;
	
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
