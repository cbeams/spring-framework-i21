package com.interface21.ejb.access;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJBObject;

import org.aopalliance.AspectException;
import org.aopalliance.MethodInvocation;

import com.interface21.aop.framework.AopUtils;
import com.interface21.ejb.access.AbstractRemoteSlsbInvokerInterceptor;


/**
 * Basic remote invoker for EJBs.
 * "Creates" a new EJB instance for each invocation.
 * @version $Revision$
 */
public class SimpleRemoteSlsbInvokerInterceptor extends AbstractRemoteSlsbInvokerInterceptor {
	
	/**
	 * JavaBean constructor
	 */
	public SimpleRemoteSlsbInvokerInterceptor() {		
	}
	
	/**
	 * Convenient constructor for programmatic use.
	 * @param jndiName
	 * @param inContainer
	 * @throws org.aopalliance.AspectException
	 */
	public SimpleRemoteSlsbInvokerInterceptor(String jndiName, boolean inContainer) throws AspectException {
		setJndiName(jndiName);
		setInContainer(inContainer);
		try {
			afterPropertiesSet();
		}
		catch (Exception ex) {
			throw new AspectException("Failed to create EJB invoker interceptor", ex);
		}
	}
	
	/**
	 * This is the last invoker in the chain
	 * @see org.aopalliance.MethodInterceptor#invoke(org.aopalliance.Invocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		EJBObject ejb = newSessionBeanInstance();
		try {
			return invocation.getMethod().invoke(ejb, AopUtils.getArguments(invocation));
		}
		catch (InvocationTargetException ex) {
			logger.warn(ex + " thrown invoking remote EJB method " + invocation.getMethod());
			throw ex.getTargetException();
		}
		catch (Throwable t) {
			throw new AspectException("Failed to invoke remote EJB", t);
		}
	}

}
