/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.ejb.access;

import com.interface21.aop.framework.ProxyFactory;
import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;

/**
 * Convenient factory for local or remote SLSB proxies.
 * If you want control over interceptor chaining, use an AOP ProxyFactoryBean
 * rather than rely on this class.
 * @author Rod Johnson
 * @since 09-May-2003
 * @version $Id$
 */
public class LocalStatelessSessionProxyFactoryBean extends LocalSlsbInvokerInterceptor
    implements FactoryBean {
	
	/*
	 * Instead of a separate subclass for each type of SLSBInvoker, we could have added
	 * this functionality to AbstractSlsbInvokerInterceptor. However, the avoiding of
	 * code duplication would be outweighed by the confusion this would produce over the
	 * purpose of AbstractSlsbInvokerInterceptor.
	 */
	
	/** EJBLocalObject */
	private Object proxy;
	
	/**
	 * The business interface of the EJB we're proxying.
	 */
	private Class businessInterface;

	/**
	 * Set the business interface of the EJB we're proxying
	 * @param clazz set the business interface of the EJB
	 */
	public void setBusinessInterface(Class clazz) {
		this.businessInterface = clazz;
	}

	/**
	 * @return the business interface of the EJB. Note that this
	 * will normally be the superinterface of the EJBLocal interface.
	 * Using a business methods interface is a best practice
	 * when implementing EJBs.
	 */
	public Class getBusinessInterface() {
		return businessInterface;
	}

	public void afterLocated() {
		if (this.businessInterface == null) {
			throw new IllegalArgumentException("businessInterface property must be set in LocalStatelessSessionProxyFactoryBean");
		}
		ProxyFactory pf = new ProxyFactory(new Class[] { this.businessInterface });
		pf.addInterceptor(this);
		this.proxy = pf.getProxy();
	}

	public Object getObject() throws BeansException {
		return this.proxy;
	}

	public boolean isSingleton() {
		return true;
	}

	public PropertyValues getPropertyValues() {
		return null;
	}

}
