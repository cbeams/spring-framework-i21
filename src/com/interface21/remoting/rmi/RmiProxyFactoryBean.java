package com.interface21.remoting.rmi;

import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.aopalliance.MethodInvocation;
import org.aopalliance.MethodInterceptor;

import com.interface21.aop.framework.ProxyFactory;
import com.interface21.remoting.RemoteAccessException;
import com.interface21.remoting.support.RemoteProxyFactoryBean;

/**
 * Factory bean for transparent RMI proxies. Behaves like the proxied service when
 * used as bean reference, exposing the specified service interface.
 * The service URL must be a valid RMI URL like "rmi://localhost:1099/myservice".
 *
 * <p>Transparent means that RMI communication works on the RemoteInvocationHandler
 * level, needing only one stub for any service. Service interfaces do not have to
 * extend java.rmi.Remote or throw RemoteException on all methods, but in and out
 * parameters have to be serializable.
 *
 * <p>This proxy factory can only access RMI objects that got exported with a
 * RemoteInvocationWrapper, i.e. working on the RemoteInvocationHandler level.
 *
 * <p>The major advantage of RMI, compared to Hessian and Burlap, is serialization.
 * Effectively, any serializable Java object can be transported without hassle.
 * Hessian and Burlap have their own (de-)serialization mechanisms, but are
 * HTTP-based and thus much easier to setup than RMI.
 *
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see RmiServiceExporter
 * @see RemoteInvocationHandler
 * @see RemoteInvocationWrapper
 */
public class RmiProxyFactoryBean extends RemoteProxyFactoryBean {

	protected Object createProxy() throws MalformedURLException, RemoteAccessException {
		try {
			Remote remoteObj = java.rmi.Naming.lookup(getServiceUrl());
			if (!(remoteObj instanceof RemoteInvocationHandler)) {
				throw new NotBoundException("Bound RMI object isn't a wrapper");
			}
			StubInvocationHandler invocationHandler = new StubInvocationHandler((RemoteInvocationHandler) remoteObj);
			Object source = Proxy.newProxyInstance(RmiProxyFactoryBean.class.getClassLoader(),
					new Class[]{getServiceInterface()}, invocationHandler);

			// Create AOP interceptor wrapping source
			ProxyFactory pf = new ProxyFactory(source);
			pf.addInterceptor(0, new MethodInterceptor() {
				public Object invoke(MethodInvocation invocation) throws Throwable {
					try {
						return invocation.invokeNext();
					}
					catch (UndeclaredThrowableException ex) {
						throw new RemoteAccessException("Error on remote access", ex.getUndeclaredThrowable());
					}
				}
			});
			return pf.getProxy();
		}
		catch (RemoteException ex) {
			throw new RemoteAccessException("Cannot contact RMI registry", ex);
		}
		catch (NotBoundException ex) {
			throw new RemoteAccessException("Requested RMI object not bound", ex);
		}
	}

}
