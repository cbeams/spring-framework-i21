package com.interface21.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.util.ClassLoaderUtils;

/**
 * Trivial classloader analyzer interceptor.
 * @version $Id$
 * @author Rod Johnson
 * @author Dmitriy Kopylenko
 */
public class ClassLoaderAnalyzerInterceptor implements MethodInterceptor {

	protected final Log logger = LogFactory.getLog(getClass());

	public Object invoke(MethodInvocation pInvocation) throws Throwable {
		logger.debug("Begin class loader analysis");

		logger.info(ClassLoaderUtils.showClassLoaderHierarchy(
			pInvocation.getThis(),
			pInvocation.getThis().getClass().getName(),
			"\n",
			"-"));
		Object rval = pInvocation.proceed();

		logger.debug("End class loader analysis");
		return rval;
	}

}
