package com.interface21.remoting.rmi;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Accepts invocations from the client and forwards them via the RemoteInvocationHandler
 * stub to the RemoteInvocationWrapper on the server, where they are executed.
 *
 * @author Juergen Hoeller
 * @since 14.05.2003
 */
class StubInvocationHandler implements InvocationHandler, Serializable {

	/** remote stub around the server-side RemoteInvocationWrapper */
	private RemoteInvocationHandler stub;

	/**
	 * Create a new StubInvocationHandler.
	 * @param stub the remote object's stub.
	 */
	public StubInvocationHandler(RemoteInvocationHandler stub) {
		this.stub = stub;
	}

	/**
	 * Sends the invocation request to the remote object stub, and returns the response.
	 * Note: Performs invocations on java.lang.Object methods locally, like equals(),
	 * hashCode(), toString().
	 */
	public Object invoke(Object proxy, Method method, Object[] params) throws Exception {
		if (method.getDeclaringClass().equals(Object.class)) {
			return method.invoke(this, params);
		}
		return this.stub.invokeRemote(method.getName(), method.getParameterTypes(), params);
	}

}
