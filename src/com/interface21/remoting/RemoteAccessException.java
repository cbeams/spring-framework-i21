package com.interface21.remoting;

import com.interface21.core.NestedRuntimeException;

/**
 * @author jho
 * @since 14.05.2003
 */
public class RemoteAccessException extends NestedRuntimeException {

	public RemoteAccessException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
