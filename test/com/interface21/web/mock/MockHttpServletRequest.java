package com.interface21.web.mock;


import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.security.Principal;


/**
 * Dummy request for use outside a server to unit test servlets and
 * RequestHandlers. Can never fully implement methods, but should at least
 * finish off attribute and parameter methods (the main purpose of this class).
 * @author  Rod Johnson
 * @since November 28, 2000
 */
public class MockHttpServletRequest implements HttpServletRequest, Serializable {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	private HashMap	params = new HashMap(), atts = new HashMap(), headers = new HashMap();
	private String	url;
	private HttpSession session;
	//private SerializableCookie[] cookies;
	private Cookie[] cookies;
	private String	servletPath;
	private int		port = 80;
	private String	contentType;
	private Principal userPrincipal;
	private String pathInfo;
	private String	method;
	private String contextPath;
	
	/** List of locales in descending order */
	private List locales = new LinkedList();
	
	/** role name -> object or null */
	private Map	roles = new HashMap();

	private transient ServletContext servletContext;

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/** Creates a new TestRequest */
	public MockHttpServletRequest(ServletContext sc, String method, String url) {
		this.url = url;
		this.contextPath = "";
		this.servletPath = url;
		this.method = method;
		
		// We must save the request dispatcher
		this.servletContext = sc;
		
		locales.add(Locale.ENGLISH);
	}
	
	public int getIntHeader(String p1) {
		return -1;
	}

	public Principal getUserPrincipal() {
		return userPrincipal;
	}

	public Enumeration getHeaders(String s) {
		List l = new LinkedList();
		l.add(headers.get(s));
		return Collections.enumeration(l);
	}

	public RequestDispatcher getRequestDispatcher(String url) {
		return servletContext.getRequestDispatcher(url);
	}

	public Enumeration getLocales() {
		return Collections.enumeration(this.locales);
	}

	public boolean isSecure() {
		return false;
	}

	public Locale getLocale() {
		return (Locale) this.locales.get(0);
	}

	public boolean isUserInRole(String role) {
		return roles.get(role) != null;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getPathTranslated() {
		return null;
	}

	public Enumeration getHeaderNames() {
		return  Collections.enumeration(headers.keySet());
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public long getDateHeader(java.lang.String p1) {
		return 1L;
	}

	public boolean isRequestedSessionIdValid() {
		return true;
	}

	public java.lang.String getHeader(String p1) {
		return null;
	}

	public String getRemoteUser() {
		return null;
	}
	public String getRequestedSessionId() {
		return null;
	}

	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	public String getServletPath() {
		return servletPath;
	}

	public HttpSession getSession(boolean create) {
		if (session == null && create)
			session = new MockHttpSession();
		return session;
	}

	/** Returns null if no cookies */
	public Cookie[] getCookies() {
		return cookies;
	}

	public String getRequestURI() {
		return url;
	}

	public String getMethod() {
		return method;
	}

	public String getQueryString() {
		return null;
	}

	public boolean isRequestedSessionIdFromCookie() {
		return true;
	}

	public java.lang.String getAuthType() {
		return null;
	}

	public ServletInputStream getInputStream() throws java.io.IOException {
		return null;
	}

	public Enumeration getAttributeNames() {
		return Collections.enumeration(atts.keySet());
	}

	public String getScheme() {
		return null;
	}

	public int getServerPort() {
		return port;
	}

	public Object getAttribute(java.lang.String p1) {
		return atts.get(p1);
	}

	public void removeAttribute(String name) {
		atts.remove(name);
	}

	public void setAttribute(String p1, Object p2) {
		atts.put(p1, p2);
	}

	public java.lang.String getProtocol() {
		return null;
	}

	public BufferedReader getReader() throws java.io.IOException {
		return null;
	}

	public String getRemoteHost() {
		return null;
	}

	public String getServerName() {
		return null;
	}

	public Enumeration getParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	public String getParameter(String name) {
		return (String) params.get(name);
	}

	public String[] getParameterValues(String name) {
		Object obj = params.get(name);
		if (obj instanceof String[])
			return (String[]) obj;
		else
			return new String[] {obj.toString()};
	}

	public String getRealPath(java.lang.String p1) {
		return null;
	}

	public String getRemoteAddr() {
		return null;
	}

	public String getCharacterEncoding() {
		return null;
	}

	public int getContentLength() {
		return -1;
	}

	public String getContentType() {
		return contentType;
	}

	//---------------------------------------------------------------------
	// Setters to allow manipulation
	//---------------------------------------------------------------------

	/** Add a new preferred locale, before any existing locales */
	public void addPreferredLocale(Locale l) {
		locales.add(0, l);
	}

	public void addParameter(String name, String value) {
		params.put(name, value);
	}

	public void addParameter(String name, String[] values) {
		params.put(name, values);
	}

	public void addRole(String role) {
		roles.put(role, Boolean.TRUE);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setServletPath(String sp) {
		servletPath = sp;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSession(HttpSession sess) {
		this.session = sess;
	}

	public void setPathInfo(String pi) {
		this.pathInfo = pi;
	}

	/** Use after Serialization */
	//public void setServletConfig(ServletConfig servletConfig) {
	//	this.servletConfig = servletConfig;
	//}

	//public ServletConfig getServletConfig() {
	//	return servletConfig;
	//}

	public void  setUserPrincipal(Principal p) {
		this.userPrincipal = p;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setCookies(Cookie[] cks) {
		//cookies = new SerializableCookie[cks.length];
		//for (int i = 0; i < cookies.length; i++)
		//	cookies[i] = new SerializableCookie(cks[i]);
		this.cookies = cks;
	}

	public void setContextPath(String s) {
		contextPath = s;
	}

	public void addHeader(String name, String val) {
		headers.put(name, val);
	}
	
	public StringBuffer getRequestURL() {
		return new StringBuffer(contextPath);
	}
	
	public Map getParameterMap() {
		throw new UnsupportedOperationException("getParameterMap");
	}
	
	public void setCharacterEncoding(String enc) {
		throw new UnsupportedOperationException("setCharacterEncoding");
	}

}
