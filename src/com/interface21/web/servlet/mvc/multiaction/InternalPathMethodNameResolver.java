package com.interface21.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.web.util.WebUtils;

/**
 * Simple implementation of MethodNameResolver that maps URL to method
 * name. Although this is the default implementation used by the
 * MultiActionController class (because it requires no configuration),
 * it's bit naive for most applications. In particular, we don't usually
 * want to tie URL to implementation methods.
 *
 * <p>Maps the resource name after the last slash, ignoring an extension.
 * E.g. "/foo/bar/baz.html" to "baz", assuming a "/foo/bar/baz.html"
 * controller mapping to the respective MultiActionController.
 * Doesn't support wildcards.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
*/
public class InternalPathMethodNameResolver implements MethodNameResolver {

	protected final Log logger = LogFactory.getLog(getClass());

	public String getHandlerMethodName(HttpServletRequest request) {
		String lookupPath = WebUtils.getLookupPathForRequest(request, false);
		String name = lookupPath;
		// look at resource name after last slash
		int slashIndex = name.lastIndexOf('/');
		if (slashIndex != -1) {
			name = name.substring(slashIndex+1);
		}
		// ignore extension
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex != -1) {
			name = name.substring(0, dotIndex);
		}
		logger.debug("Returning MultiActionController method name '" + name + "' for lookup path '" + lookupPath + "'");
		return name;
	}
}