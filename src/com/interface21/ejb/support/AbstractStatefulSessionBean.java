/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.CreateException;

/**
 * Convenient superclass for stateful session beans.
 * SFSBs should extend this class, leaving them to implement
 * the ejbActivate() and ejbPassivate() lifecycle methods
 * to comply with the requirements of the EJB specification.
 *
 * <p><b>NB: Subclasses should invoke the loadBeanFactory()
 * method in their custom ejbCreate() methods.</b>
 *
 * @version $Id$
 * @author Rod Johnson
 */
public abstract class AbstractStatefulSessionBean extends AbstractSessionBean {

	/**
	 * Load a Spring BeanFactory namespace. Exposed for subclasses
	 * to load a BeanFactory in their ejbCreate() methods.
	 */
	protected void loadBeanFactory() throws CreateException {
		super.loadBeanFactory();
	}

}
