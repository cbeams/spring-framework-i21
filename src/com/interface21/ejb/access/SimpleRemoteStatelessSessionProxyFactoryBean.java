/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.ejb.access;

import com.interface21.aop.framework.ProxyFactory;
import com.interface21.aop.interceptor.client.ejb.SimpleRemoteSlsbInvokerInterceptor;
import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.Lifecycle;

/**
 * Convenient factory for local or remote SLSB proxies.
 * If you want control over interceptor chaining, use an AOP ProxyFactoryBean
 * rather than rely on this class.
 * @author Rod Johnson
 * @since 09-May-2003
 * @version $Id$
 */
public class SimpleRemoteStatelessSessionProxyFactoryBean extends SimpleRemoteSlsbInvokerInterceptor implements FactoryBean, Lifecycle {
	
	/*
	 * Instead of a separate subclass for each type of SLSBInvoker, we could have added
	 * this functionality to AbstractSlsbInvokerInterceptor. However, the avoiding of
	 * code duplication would be outweighed by the confusion this would produce over the
	 * purpose of AbstractSlsbInvokerInterceptor.
	 */
	
	/** EJBObject */
	private Object proxy;
	
	
	/**
	 * The business interface of the EJB we're proxying.
	 */
	private Class businessInterface;
	
	
	public SimpleRemoteStatelessSessionProxyFactoryBean() {
	}
	
		
	/**
	 * @return the business interface of the EJB. Note that this
	 * will normally be the superinterface of the EJB remote component interface.
	 * Using a business methods interface is a best practice
	 * when implementing EJBs.
	 */
	public Class getBusinessInterface() {
		return businessInterface;
	}

	/**
	 * Set the business interface of the EJB we're proxying
	 * @param class1 set the business interface of the EJB
	 */
	public void setBusinessInterface(Class class1) {
		this.businessInterface = class1;
	}
	

	
	/**
	 * @see com.interface21.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void setBeanFactory(BeanFactory bf) throws Exception {

		if (this.businessInterface == null) 
			throw new Exception("businessInterface property must be set in SimpleRemoteStatelessSessionProxyFactoryBean");
		
		ProxyFactory pf = new ProxyFactory(new Class[] { this.businessInterface });
		pf.addInterceptor(this);
		this.proxy = pf.getProxy();
	}

	/**
	 * @see com.interface21.beans.factory.FactoryBean#getObject()
	 */
	public Object getObject() throws BeansException {
		return this.proxy;
	}

	/**
	 * @see com.interface21.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @see com.interface21.beans.factory.FactoryBean#getPropertyValues()
	 */
	public PropertyValues getPropertyValues() {
		return null;
	}

}
