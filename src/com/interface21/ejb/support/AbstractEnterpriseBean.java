/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.ejb.support;

import javax.ejb.EnterpriseBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Superclass for all EJBs. Provides logging support.
 * As javax.ejb.EnterpriseBean is a tag interface,
 * there are no EJB methods to implement.
 *
 * <p>Subclasses will often want to create a BeanFactory
 * via a classpath resource for accessing preconfigured beans.
 *
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class AbstractEnterpriseBean implements EnterpriseBean {
	
	/**
	 * Logger, available to subclasses
	 */
	protected final Log logger = LogFactory.getLog(getClass());

}
