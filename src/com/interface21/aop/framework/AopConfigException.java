/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import com.interface21.core.NestedRuntimeException;

/**
 * 
 * @author Rod Johnson
 * @since 13-Mar-2003
 * @version $Revision$
 */
public class AopConfigException extends NestedRuntimeException {

	/**
	 * @param mesg
	 */
	public AopConfigException(String mesg) {
		super(mesg);
	}


}
