package com.interface21.remoting.rmi;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server-side implementation of RemoteInvocationHandler.
 * An instance of this class exists for each remote object.
 *
 * @author Juergen Hoeller
 * @since 14.05.2003
 */
class RemoteInvocationWrapper extends UnicastRemoteObject implements RemoteInvocationHandler {

	private Object wrappedObject;

	/**
	 * Create a new RemoteInvocationWrapper.
	 * @param wrappedObject	the locally wrapped object, on which methods are invoked
	 */
	public RemoteInvocationWrapper(Object wrappedObject) throws RemoteException {
		super();
		this.wrappedObject = wrappedObject;
	}

	public Object invokeRemote(String methodName, Class[] paramTypes, Object[] params) throws Exception {
		Method method = wrappedObject.getClass().getMethod(methodName, paramTypes);
		return method.invoke(wrappedObject, params);
	}

}
