/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * 
 * @author Rod Johnson
 * @since 14-Mar-2003
 * @version $Revision$
 */
public class ProxyFactory extends DefaultProxyConfig {
	
	/**
	 * Proxy all interfaces
	 * @param target
	 */
	public ProxyFactory(Object target) throws AopConfigException {
		if (target == null)
			throw new AopConfigException("Can't proxy null object");
						
		Set s = findAllImplementedInterfaces(target.getClass());
		
		if (s.size() == 0)
			throw new AopConfigException("Can't proxy class " + target.getClass() + 
				": it implements no interfaces");
		for (Iterator iter = s.iterator(); iter.hasNext(); ) {
			addInterface((Class) iter.next());
		}
		
		// Add the interceptor we'll always require
		InvokerInterceptor ii = new InvokerInterceptor(target);
		addInterceptor(ii);
	}
	
	
	/**
	 * No target, only interfaces
	 *
	 */
	public ProxyFactory(Class[] interfaces) {
		// TODO if null or empty
		for (int i = 0; i < interfaces.length; i++) {
			addInterface(interfaces[i]);
		}
		// Must add interceptors
	}
	
	/**
	 * Get all implemented interfaces, even those implemented by superclasses.
	 * @param clazz
	 * @return Set
	 */
	private static Set findAllImplementedInterfaces(Class clazz) {
		Set s = new HashSet();
		Class[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			s.add(interfaces[i]);
		}
		Class superclass = clazz.getSuperclass();
		if (superclass != null) {
			s.addAll(findAllImplementedInterfaces(superclass));
		}
		return s;
	}
	
	/**
	 * Can be called repeatedly. Effect will vary if we've added
	 * or removed interfaces. Can add and remove "interceptors"
	 * @return Object
	 */
	public Object getProxy() {
		AopProxy proxy = new AopProxy(this);
		return AopProxy.getProxy(proxy);
	}

}
