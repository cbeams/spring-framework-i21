package com.interface21.remoting.support;

import java.net.MalformedURLException;

import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.remoting.RemoteAccessException;

/**
 * Abstract base class for factory beans proxying a remote service.
 * Exposes the proxy when used as bean reference.
 * Used e.g. by the Caucho and RMI proxy factory implementations.
 *
 * <p>Subclasses just need to implement createProxy,
 * using the properties of the factory instance.
 *
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see #createProxy
 */
public abstract class RemoteProxyFactoryBean implements FactoryBean, InitializingBean {

	private Class serviceInterface;

	private String serviceUrl;

	private Object serviceProxy;

	/**
	 * Set the interface of the serviceProxy that this factory should create a proxy for.
	 */
	public void setServiceInterface(Class serviceInterface) {
		if (!serviceInterface.isInterface()) {
			throw new IllegalArgumentException("serviceInterface must be an interface");
		}
		this.serviceInterface = serviceInterface;
	}

	protected Class getServiceInterface() {
		return serviceInterface;
	}

	/**
	 * Set the URL of the serviceProxy that this factory should create a proxy for.
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	protected String getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * Creates and validates the proxy, and keeps the reference.
	 * Delegates to createProxy.
	 * @see #createProxy
	 */
	public void afterPropertiesSet() throws MalformedURLException, RemoteAccessException {
		this.serviceProxy = createProxy();
		if (this.serviceInterface != null && !this.serviceInterface.isInstance(this.serviceProxy)) {
			throw new IllegalArgumentException("Service interface and proxy instance do not match");
		}
	}

	/**
	 * Create the proxy instance. Can use the properties of this factory,
	 * although the actually used settings will vary.
	 * For example, certain protocols do not support authentication.
	 * @return the new proxy instance
	 */
	protected abstract Object createProxy() throws MalformedURLException, RemoteAccessException;

	/**
	 * Return the singleton serviceProxy proxy.
	 */
	public Object getObject() {
		return this.serviceProxy;
	}

	public boolean isSingleton() {
		return true;
	}

	public PropertyValues getPropertyValues() {
		return null;
	}

}
