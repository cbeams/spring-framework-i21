package com.interface21.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.util.StopWatch;

/**
 * Trivial performance monitor interceptor.
 * This interceptor has no effect on the intercepted method call.
 *
 * <p>Presently logs information using Commons Logging, at "info" level.
 * Could make this much more sophisticated, storing information etc.
 *
 * @author Rod Johnson
 * @author Dmitriy Kopylenko
 * @version $Id$
 */
public class PerformanceMonitorInterceptor implements MethodInterceptor {

	protected final Log logger = LogFactory.getLog(getClass());

	public Object invoke(MethodInvocation invocation) throws Throwable {
		String name = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		logger.debug("Begin performance monitoring of method '" + name + "'");

		StopWatch sw = new StopWatch(name);
		sw.start(name);
		Object rval = invocation.proceed();
		sw.stop();

		logger.info(sw.shortSummary());
		logger.debug("End performance monitoring of method '" + name + "'");

		return rval;
	}

}
