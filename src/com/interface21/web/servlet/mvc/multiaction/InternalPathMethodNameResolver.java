package com.interface21.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;

import com.interface21.util.StringUtils;

/**
* Trivial implementation of MethodNameResolver that
* maps URL to method name. Although this is the
* default implementation used by  the MultiActionController
* class (because it requires no configuration) it's
* bit naive for most applications. In particular,
* we don't usually want to tie URL to implementation methods.
* <br>Maps /foo/bar/baz.html to foo_bar_baz
* <br>Dsn't support wildcards
* @author Rod Johnson
*/
public class InternalPathMethodNameResolver implements MethodNameResolver {
	

	/**
	 * @see MethodNameResolver#getHandlerMethodName(HttpServletRequest)
	 */
	public String getHandlerMethodName(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		String name = servletPath.substring(servletPath.indexOf("/") + 1);
		name = StringUtils.replace(name, "/", "_");
		if (name.indexOf(".") != -1)
			name = name.substring(0, name.indexOf("."));
		//System.out.println("REquest handler method name is '" + name + "'");
		return name;
	}
}