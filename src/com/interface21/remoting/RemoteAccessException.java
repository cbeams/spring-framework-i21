package com.interface21.remoting;

import com.interface21.core.NestedRuntimeException;

/**
 * Generic remote access exception. A service proxy for any remoting
 * protocol and toolkit should throw this exception or subclasses of it,
 * to be able to transparently expose a plain Java business interface.
 *
 * <p>When using conforming proxies, switching the actual remoting toolkit
 * e.g. from Hessian to Burlap does not affect client code. The latter
 * works with a plain Java business interface that the service exposes.
 * A client object simply receives an implementation for the interface that
 * it needs via a bean reference, like it does for local beans too.
 *
 * <p>A client can catch RemoteAccessException if it wants too, but as
 * remote access errors are typically unrecoverable, it will probably let
 * such exceptions propagate to a higher level that handles them generically.
 * In this case, the client code doesn't show any signs of being involved in
 * remote access, as there aren't any remoting-specific dependencies.
 *
 * <p>Even when switching from a remote service proxy to a local implementation
 * of the same interface, this amounts to just a matter of configuration.
 * Obviously, the client code should be somewhat aware that it _could work_
 * on a remote service, for example in terms of repeated method calls that
 * cause unnecessary roundtrips etc. But it doesn't have to be aware whether
 * it _actually works_ on a remote service or a local implementation, or with
 * which remoting toolkit under the hood.
 *
 * @author Juergen Hoeller
 * @since 14.05.2003
 * @see com.interface21.remoting.support.RemoteProxyFactoryBean
 */
public class RemoteAccessException extends NestedRuntimeException {

	public RemoteAccessException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
