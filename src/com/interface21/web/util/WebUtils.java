package com.interface21.web.util;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Miscellaneous utilities for web applications.
 * Also used by various framework classes.
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class WebUtils {

	/**
	 * Web app root key parameter at the servlet context level
	 * (i.e. web.xml): "webAppRootKey".
	 */
	public static final String WEB_APP_ROOT_KEY_PARAM = "webAppRootKey";

	/**
	 * Default web app root key: "webapp.root".
	 */
	public static final String DEFAULT_WEB_APP_ROOT_KEY = "webapp.root";

	/** HTTP header value */
	public static final String HEADER_IFMODSINCE = "If-Modified-Since";

	/** HTTP header value */
	public static final String HEADER_LASTMOD = "Last-Modified";

	/** Name suffix in case of image buttons */
	public static final String SUBMIT_IMAGE_SUFFIX = ".x";

	/**
	 * Set a system property to the web application root directory.
	 * The key of the system property can be defined with the
	 * "webAppRootKey" init parameter at the servlet context level
	 * (i.e. web.xml), the default key is "webapp.root".
	 *
	 * <p>Can be used for toolkits that support substition with
	 * system properties (i.e. System.getProperty values),
	 * like Log4J's ${key} syntax within log file locations.
	 *
	 * @param servletContext the servlet context of the web application
	 * @see #WEB_APP_ROOT_KEY_PARAM
	 * @see #DEFAULT_WEB_APP_ROOT_KEY
	 * @see WebAppRootListener
	 */
	public static void setWebAppRootSystemProperty(ServletContext servletContext) {
		String param = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
		String key = (param != null ? param : DEFAULT_WEB_APP_ROOT_KEY);
		String oldValue = System.getProperty(key);
		if (oldValue != null) {
			servletContext.log("WARNING: Web app root system property already set: "  + key + " = " + oldValue);
			servletContext.log("WARNING: Choose unique webAppRootKey values in your web.xml files!");
		} else {
			String root = servletContext.getRealPath("/");
			System.setProperty(key, root);
			servletContext.log("Set web app root system property: " + key + " = " + root);
		}
	}

	/**
	 * Check the given request for a session attribute of the given name.
	 * Returns null if there is no session or if the session has no such attribute.
	 * Does not create a new session if none has existed before!
	 * @param request current HTTP request
	 * @param name the name of the session attribute
	 * @return the value of the session attribute, or null if not found
	 */
	public static Object getSessionAttribute(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(false);
		return (session != null ? session.getAttribute(name) : null);
	}

	/**
	 * Set the session attribute with the given name to the given value.
	 * Removes the session attribute if value is null, if a session existed at all.
	 * Does not create a new session on remove if none has existed before!
	 * @param request current HTTP request
	 * @param name the name of the session attribute
	 */
	public static void setSessionAttribute(HttpServletRequest request, String name, Object value) {
		if (value != null) {
			request.getSession().setAttribute(name, value);
		}
		else {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(name);
			}
		}
	}

	/**
	 * Retrieve the first cookie with the given name.
	 * Note that multiple cookies can have the same name
	 * but different paths or domains.
	 * @param name cookie name
	 * @return the first cookie with the given name, or null if none is found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (name.equals(cookies[i].getName()))
					return cookies[i];
			}
		}
		return null;
	}

	/**
	 * Return the URL of the root of the current application.
	 * @param request current HTTP request
	 */
	public static String getUrlToApplication(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
	}

	/**
	 * Return the path within the web application for the given request.
	 * @param request current HTTP request
	 * @return the path within the web application
	 */
	public static String getPathWithinApplication(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length());
	}

	/**
	 * Return the path within the servlet mapping for the given request,
	 * i.e. the part of the request's URL beyond the part that called the servlet,
	 * or "" if the whole URL has been used to identify the servlet.
	 * <p>E.g.: servlet mapping = "/test/*"; request URI = "/test/a" -> "/a".
	 * <p>E.g.: servlet mapping = "/test"; request URI = "/test" -> "".
	 * <p>E.g.: servlet mapping = "/*.test"; request URI = "/a.test" -> "".
	 * @param request current HTTP request
	 * @return the path within the servlet mapping, or ""
	 */
	public static String getPathWithinServletMapping(HttpServletRequest request) {
		int servletIndex = request.getRequestURI().indexOf(request.getServletPath());
		return request.getRequestURI().substring(servletIndex + request.getServletPath().length());
	}

	/**
	 * Return the mapping lookup path for the given request,
	 * within the current servlet mapping if applicable,
	 * else within the web application context.
	 * @param request current HTTP request
	 * @param alwaysUseFullPath if the full path within the context
	 * should be used in any case
	 * @return the lookup path
	 */
	public static String getLookupPathForRequest(HttpServletRequest request, boolean alwaysUseFullPath) {
		// always use full path within current servlet context?
		if (alwaysUseFullPath)
			return WebUtils.getPathWithinApplication(request);
		// else use path within current servlet mapping if applicable
		String rest = WebUtils.getPathWithinServletMapping(request);
		if (!"".equals(rest))
			return rest;
		else
			return WebUtils.getPathWithinApplication(request);
	}

	/**
	 * Given a servlet path string, determine the directory within the WAR
	 * this belongs to, ending with a /. For example, /cat/dog/test.html would be
	 * returned as /cat/dog/. /test.html would be returned as /
	 */
	public static String getDirectoryForServletPath(String servletPath) {
		// Arg will be of form /dog/cat.jsp. We want to see /dog/
		if (servletPath == null || servletPath.indexOf("/") == -1)
			return "/";
		String left = servletPath.substring(0, servletPath.lastIndexOf("/") + 1);
		return left;
	}

	/**
	 * Convenience method to return a map from un-prefixed property names
	 * to values. E.g. with a prefix of price, price_1, price_2 produce
	 * a properties object with mappings for 1, 2 to the same values.
	 * @param request HTTP request in which to look for parameters
	 * @param base beginning of parameter name
	 * (if this is null or the empty string, all parameters will match)
	 * @return properties mapping request parameters <b>without the prefix</b>
	 */
	public static Properties getParametersStartingWith(ServletRequest request, String base) {
		Enumeration enum = request.getParameterNames();
		Properties props = new Properties();
		if (base == null)
			base = "";
		while (enum != null && enum.hasMoreElements()) {
			String paramName = (String) enum.nextElement();
			if (base == null || "".equals(base) || paramName.startsWith(base)) {
				String val = request.getParameter(paramName);
				String unprefixed = paramName.substring(base.length());
				props.setProperty(unprefixed, val);
			}
		}
		return props;
	}

	/**
	 * Check if a specific input type="submit" parameter was sent in the request,
	 * either via a button (directly with name) or via an image (name + ".x").
	 * @param request current HTTP request
	 * @param name name of the parameter
	 * @return if the parameter was sent
	 * @see #SUBMIT_IMAGE_SUFFIX
	 */
	public static boolean hasSubmitParameter(ServletRequest request, String name) {
		return (request.getParameter(name) != null || request.getParameter(name + SUBMIT_IMAGE_SUFFIX) != null);
	}

}
