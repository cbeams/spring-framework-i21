/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.ejb.support;

import javax.jms.MessageListener;

/** 
 * Convenient superclass for JMS MDBs.
 * Requires subclasses to implement the JMS  interface MessageListener
 * @author Rod Johnson
 * @version $RevisionId: ResultSetHandler.java,v 1.1 2001/09/07 12:48:57 rod Exp $
 */
public abstract class AbstractJmsMessageDrivenBean 
	extends AbstractMessageDrivenBean
	implements MessageListener {
	
	// Empty: the purpose of this class is to ensure
	// that subclasses implement javax.jms.MessageListener
	
} 
