package com.interface21.remoting.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;

/**
 * RMI exporter that exposes the specified service as transparent RMI object
 * with the specified name. Such services can be accessed via RmiProxyFactoryBean.
 *
 * <p>Transparent means that RMI communication works on the RemoteInvocationHandler
 * level, needing only one stub for any service. Service interfaces do not have to
 * extend java.rmi.Remote or throw RemoteException on all methods, but in and out
 * parameters have to be serializable.
 *
 * <p>A conventional RMI lookup will return a RemoteInvocationHandler instance
 * instead of a proxy implementing the service interface. To access the service,
 * a java.lang.reflect.Proxy has to be created, using a StubInvocationHandler
 * configured for the RemoteInvocationHandler instance. Of course, a helper like
 * RmiProxyFactoryBean makes this much simpler!
 *
 * <p>The major advantage of RMI, compared to Hessian and Burlap, is serialization.
 * Effectively, any serializable Java object can be transported without hassle.
 * Hessian and Burlap have their own (de-)serialization mechanisms, but are
 * HTTP-based and thus much easier to setup than RMI. 
 *
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see RmiProxyFactoryBean
 * @see RemoteInvocationHandler
 * @see RemoteInvocationWrapper
 */
public class RmiServiceExporter implements InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private Object service;

	private String name;

	private int port = Registry.REGISTRY_PORT;

	/**
	 * Set the service to export via RMI.
	 * Typically populated via a bean reference.
	 */
	public void setService(Object service) {
		this.service = service;
	}

	/**
	 * Set the name of the exported RMI object,
	 * i.e. rmi://localhost:port/NAME
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the port of the registry for the exported RMI object,
	 * i.e. rmi://localhost:PORT/name
	 * Default is Registry.REGISTRY_PORT (1099).
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Register the service as RMI object.
	 * Creates an RMI registry on the specified port if none exists.
	 */
	public void afterPropertiesSet() throws AlreadyBoundException, RemoteException {
		Remote wrapper = new RemoteInvocationWrapper(this.service);
		Registry registry = null;
		try {
			// retrieve registry
			registry = LocateRegistry.getRegistry(this.port);
		}
		catch (RemoteException ex) {
			logger.debug("Could not retrieve RMI registry", ex);
			// assume no registry found -> create new one
			registry = LocateRegistry.createRegistry(this.port);
		}
		// bind wrapper to registry
		registry.rebind(this.name, wrapper);
	}

}
