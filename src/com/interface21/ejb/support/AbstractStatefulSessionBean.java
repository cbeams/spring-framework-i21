/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.CreateException;

/**
 * Convenient superclass for stateful session beans.
 * <br>SFSBs should extend this class,
 * leaving them to implement the ejbActivate() and ejbPassivate() lifecycle methods
 * to comply with the requirements of the EJB specification.
 * <br><b>NB: subclasses should invoke the loadBeanFactory()
 * method in their custom ejbCreate() methods.</b>
 * <br>Note that we cannot use final for our implementation of EJB lifecycle methods,
 * as this violates the EJB specification.
 * @version $Id$
 * @author Rod Johnson
 */
public abstract class AbstractStatefulSessionBean extends AbstractSessionBean {

	/**
	 * Exposed for subclasses to load Spring BeanFactory in their ejbCreate() methods.
	 * @see com.interface21.ejb.support.AbstractEnterpriseBean#loadBeanFactory()
	 */
	protected void loadBeanFactory() throws CreateException {
		super.loadBeanFactory();
	}

} 	// class AbstractStatefulSessionBean
