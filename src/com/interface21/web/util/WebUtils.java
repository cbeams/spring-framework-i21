package com.interface21.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Miscellaneous utilities for web applications
 * @author  Rod Johnson
 */
public abstract class WebUtils {
	
	/**
	 * @return the first cookie with the given name or null if none is found
	 * Note that multiple cookies can have the same name but different paths or domains
	 * @param name cookie name
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
	
	public static boolean isRemote(String url) {
		try {
			URL confUrl = new URL(url);
			return true;
		}
		catch (MalformedURLException ex) {
			return false;
		}
	}
	
	/** Given a String which may be either a URL or a path within this WAR,
	 * open an input stream allowing access to its contents.
	 * Note that callers are responsible for closing the input stream.
	 * @param url String which may be either a URL (e.g. http://somecompany.com/foo.xml)
	 * or a path within this WAR (e.g. /WEB-INF/data/file.dat)
	 * @param servletConfig the application's servlet config. We'll need this if we
	 * need to load from within the WAR
	 * @throws IOException if we can't open the resource
	 * @return an InputStream for the resources contents.
	 * <B>Callers are responsible for closing this input stream.</B>
	 */
	public static InputStream getResourceInputStream(String url, ServletContext servletContext) throws IOException {
		//Category.getInstance(Utils.class).debug("Looking up resource stream [" + url + "]");
		if (!isRemote(url)) {
			// Load from within WAR
			if (!url.startsWith("/"))
				url = "/" + url;
			InputStream is = servletContext.getResourceAsStream(url);
			if (is == null)
				throw new IOException("Can't open " + url);
			return is;
		}
		else {
			// Remote loading
			return new URL(url).openStream();
		}
	}	// getResourceInputStream
	
	
	/** FOR BACKWARD COMPATIBILITY ONLY
	 * <B>Callers are responsible for closing this input stream.</B>
	 */
	public static InputStream getResourceInputStream(String url, ServletConfig servletConfig) throws IOException {
		return getResourceInputStream(url, servletConfig.getServletContext());
	}	// getResourceInputStream
	
	
	/** Given a servlet path string, determine the directory within the WAR
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
	
	
	/** Return the URL of the root of the current application
	 */
	public static String getURLToApplication(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
	}
	
	
	/** Convenience method to return a map from un-prefixed property names
	 * to values. E.g. with a prefix of price, price_1, price_2 produce
	 * a properties object with mappings for 1, 2 to the same values.
     * @param request HTTP request in which to look for parameters
     * @param base beginning of parameter name. If this is null or the empty string,
	 * all parameters will match
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
    }   // getParameterValuesStartingWith
	
	
	
	public static void main(String[] args) {
		String spec = "foo.html";
		String contexts = "http://127.0.0.1/barrier/bunny.jsp";
		com.interface21.util.StopWatch sw = new com.interface21.util.StopWatch();
		sw.start("1000 urls");
		try {
			
			for (int i = 0; i < 100000; i++) {
				java.net.URL context = new java.net.URL(contexts);
				java.net.URL u = new java.net.URL(context, spec);
				String s = u.toExternalForm();
				
			}
			//System.out.println(u);
			sw.stop();
			System.out.println(sw);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		sw.stop();
		
		
		
		
		String [] tests = new String[] { "/dog/cat.jsp", "/index.jsp", "/dog", "/dog/", "/dog/cat/big/a065.html", "/cat/dog/" };
		for (int i = 0; i < tests.length; i++) {
			System.out.println("For " + tests[i] + " result is " + getDirectoryForServletPath(tests[i]));
		}
	}
	
}
