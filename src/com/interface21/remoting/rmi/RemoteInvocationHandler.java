package com.interface21.remoting.rmi;

import java.rmi.Remote;

/**
 * Interface for RemoteInvocationWrapper instances on the server.
 * A client's StubInvocationHandler uses a stub implementing this interface.
 *
 * @author Juergen Hoeller
 * @since 14.05.2003
 */
interface RemoteInvocationHandler extends Remote {

	/**
	 * Called by the StubInvocationHandler on each invocation.
	 * Invokes the given method with the given parameters on the actual object.
	 * @param methodName the name of the invoked method
	 * @param paramTypes the method's parameter types.
	 * @param params the method's parameters
	 * @return the object returned from the invoked method, if any
	 * @throws Exception in case of invocation or invocation target exceptions
	 */
	public Object invokeRemote(String methodName, Class[] paramTypes, Object[] params) throws Exception;

}
