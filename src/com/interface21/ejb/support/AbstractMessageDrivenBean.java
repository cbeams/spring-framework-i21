/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.ejb.support;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;

/** 
 * Convenient superclass for MDBs.
 * Doesn't require JMS, as EJB 2.1 MDBs are no longer
 * JMS-specific: see the AbstractJmsMessageDrivenBean subclass.
 * <br>This class ensures that subclasses have access to the
 * MessageDrivenContext provided by the EJB container, and implement
 * a no argument ejbCreate() method as required by the EJB specification,
 * but not the javax.ejb.MessageDrivenBean interface.
 * <br>
 * NB: we cannot use final methods to implement EJB API methods,
 * as this violates the EJB specification.
 * @author Rod Johnson
 */
public abstract class AbstractMessageDrivenBean 
				extends AbstractEnterpriseBean 
				implements MessageDrivenBean {
	
	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/** MessageDrivenContext passed to this object */
	private MessageDrivenContext	messageDrivenContext;
	
	
	//-------------------------------------------------------------------------
	// Lifecycle methods
	//-------------------------------------------------------------------------	
	/**
	 * Convenience method for subclasses to use
	 * @return the MessageDrivenContext passed to this EJB by the EJB container
	 */
	protected final MessageDrivenContext getMessageDrivenContext() {
		return messageDrivenContext;
	}
	
	/**
	 * Required lifecycle method. Sets the MessageDriven context.
	 * @param messageDrivenContext MessageDrivenContext
	 */
	public void setMessageDrivenContext(MessageDrivenContext messageDrivenContext) {
		logger.debug("setMessageContext");
		this.messageDrivenContext = messageDrivenContext;
	}
	
	/**
	 * Lifecycle method required by the EJB specification but not the MessageDrivenBean interface.
	 * We implement this as an abstract method to force subclasses to implement it.
	 * Can use BeanFactory here for initialization if required.
	 */
	public abstract void ejbCreate();
	
	
	/**
	 * This method is required by the EJB Specification.
	 */
	public void ejbRemove() {
		logger.info("ejbRemove");
	}
	
} 	// class AbstractMessageDrivenBean
