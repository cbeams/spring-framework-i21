package com.interface21.web.mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class is a test implementation of the ServletContext class.
 * Based on ServletUnit, by Rusell Gold
 * <p/>It only requires a pretty simple implementation to bootstrap the I21 framework.
 * getResourceAsStream() and getInitParameter() must be implemented.
 **/
public class MockServletContext implements ServletContext {

	/** Root of web app */
	private String warRoot;
	
	private String displayName;

	private java.util.Properties initParams = new java.util.Properties();

	private final static Vector EMPTY_VECTOR = new Vector();

	private Hashtable _attributes = new Hashtable();

	/**
	 * Param warRoot should not end with a /
	 */
	public MockServletContext(String warRoot) throws Exception {
		this.warRoot = warRoot;
		//System.out.println("------> MockServletContext: WAR root is '" + warRoot + "'");

		//InputStream is = new FileInputStream(
		
		InputStream is = getClass().getResourceAsStream(warRoot + "/WEB-INF/web.xml");
		
		//System.out.println("Loading web.xml from InputStream [" + is + "]");
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(is);
		parseWebXml(doc);

	}

	/** Won't be able to load resources */
	public MockServletContext() {
		this.warRoot = null;
		//System.out.println("------> MockServletContext: no WAR root!!");
	}

	/**
	 * Parse the web.xml
	 */
	private void parseWebXml(Document webxml) throws Exception {
		NodeList nl = webxml.getElementsByTagName("display-name");
		if (nl.getLength() > 0)
			displayName = getText(nl.item(0));
		nl = webxml.getElementsByTagName("context-param");
		for (int i = 0; i < nl.getLength(); i++) {
			//System.out.println(nl.item(i));
			parseContextParamNode((Element) nl.item(i));
		}
	}
	
	
	private void parseContextParamNode(Element n) throws Exception {
		//<context-param>
		//<param-name>configPath</param-name>
  		//<param-value>/WEB-INF/applicationContext.xml</param-value>   	
		//</context-param>
		// CRIMSON HACK!?
		Node name = n.getElementsByTagName("param-name").item(0);
		Node value = n.getElementsByTagName("param-value").item(0);
//		System.out.println("Added context param '" + name);
		addInitParameter(getText(name), getText(value));
	}
	
	
	private String getText(Node e) {
		if (e instanceof Text)
			return ((Text) e).getData();
			
		NodeList nl2 = e.getChildNodes();
		if (nl2.getLength() != 1 || !(nl2.item(0) instanceof Text))
			throw new RuntimeException("Unexpected element or type mismatch: " + nl2.item(0) + "; tag name was <" + ((Element) e).getTagName() + ">" );
		Text t = (Text) nl2.item(0);
		return t.getData();
	}

	/**
	 * Returns a ServletContext object that corresponds to a specified URL on the server.
	 * <p>
	 * This method allows servlets to gain access to the context for various parts of the server,
	 * and as needed obtain RequestDispatcher objects from the context. The given path must be
	 * absolute (beginning with "/") and is interpreted based on the server's document root.
	 * <p>
	 * In a security conscious environment, the servlet container may return null for a given URL.
	 **/
	public javax.servlet.ServletContext getContext(java.lang.String A) {
		throw new UnsupportedOperationException("getContext not implemented");
	}

	/**
	 * Returns the major version of the Java Servlet API that this servlet container supports.
	 * All implementations that comply with Version 2.3 must have this method return the integer 2.
	 **/
	public int getMajorVersion() {
		return 2;
	}

	/**
	 * Returns the minor version of the Servlet API that this servlet container supports.
	 * All implementations that comply with Version 2.3 must have this method return the integer 3.
	 **/
	public int getMinorVersion() {
		return 3;
	}

	/**
	 * Returns the MIME type of the specified file, or null if the MIME type is not known.
	 * The MIME type is determined by the configuration of the servlet container, and
	 * may be specified in a web application deployment descriptor. Common MIME types are
	 * "text/html" and "image/gif".
	 **/
	public java.lang.String getMimeType(String filePath) {
		return null; // XXX not implemented
	}

	/**
	 * Returns a URL to the resource that is mapped to a specified path. The path must begin
	 * with a "/" and is interpreted as relative to the current context root.
	 * <p>
	 * This method allows the servlet container to make a resource available to servlets from any source.
	 * Resources can be located on a local or remote file system, in a database, or in a .war file.
	 * <p>
	 * The servlet container must implement the URL handlers and URLConnection objects that are necessary to access the resource.
	 * <p>
	 * This method returns null if no resource is mapped to the pathname.
	 *
	 * Some containers may allow writing to the URL returned by this method using the methods of the URL class.
	 *
	 * The resource content is returned directly, so be aware that requesting a .jsp page returns the JSP source code. Use a
	 * RequestDispatcher instead to include results of an execution.
	 *
	 * This method has a different purpose than java.lang.Class.getResource, which looks up resources based on a class loader. This
	 * method does not use class loaders.
	 **/
	public java.net.URL getResource(String path) throws java.net.MalformedURLException {
		if (warRoot == null)
			//return null;
			throw new UnsupportedOperationException("No war root: getResource fails");

		return new java.net.URL(warRoot + path);
	}

	/**
	 * Returns the resource located at the named path as an InputStream object.
	 *
	 * The data in the InputStream can be of any type or length. The path must be specified according to the rules given in getResource.
	 * This method returns null if no resource exists at the specified path.
	
	 * Meta-information such as content length and content type that is available via getResource method is lost when using this method.
	
	 * The servlet container must implement the URL handlers and URLConnection objects necessary to access the resource.
	
	 * This method is different from java.lang.Class.getResourceAsStream, which uses a class loader. This method allows servlet
	 * containers to make a resource available to a servlet from any location, without using a class loader.
	 **/
	public java.io.InputStream getResourceAsStream(String path) {
		if (warRoot == null)
			//return null;
			throw new UnsupportedOperationException("No war root: getResourceAsStream fails");

		try {
			//return new java.io.FileInputStream(warRoot + path);
			InputStream is = getClass().getResourceAsStream(this.warRoot + "/" + path);
			if (is == null)
				throw new IOException("Can't open resource '" + path + "'");
				return is;
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a RequestDispatcher object that acts as a wrapper for the resource located at the given path. A RequestDispatcher
	 * object can be used to forward a request to the resource or to include the resource in a response. The resource can be dynamic or static.
	
	 * The pathname must begin with a "/" and is interpreted as relative to the current context root. Use getContext to obtain a
	 * RequestDispatcher for resources in foreign contexts. This method returns null if the ServletContext cannot return a
	 * RequestDispatcher.
	 **/
	public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
		return new MockRequestDispatcher(path);
	}

	/**
	 * Returns a RequestDispatcher object that acts as a wrapper for the named servlet.
	 *
	 * Servlets (and JSP pages also) may be given names via server administration or via a web application deployment descriptor. A servlet
	 * instance can determine its name using ServletConfig.getServletName().
	 *
	 * This method returns null if the ServletContext cannot return a RequestDispatcher for any reason.
	 **/
	public javax.servlet.RequestDispatcher getNamedDispatcher(java.lang.String A) {
		throw new UnsupportedOperationException("MockServletContext.getNamedDispatcher");
	}

	/**
	 * @deprecated as of Servlet API 2.1
	 **/
	public javax.servlet.Servlet getServlet(java.lang.String A) {
		throw new UnsupportedOperationException("getServlet was deprecated in Servlet 2.1: don't use it");
	}

	/**
	 * @deprecated as of Servlet API 2.0
	 **/
	public java.util.Enumeration getServlets() {
		throw new UnsupportedOperationException("getServlets was deprecated in Servlet 2.0: don't use it");
	}

	/**
	 * @deprecated as of Servlet API 2.1
	 **/
	public java.util.Enumeration getServletNames() {
		throw new UnsupportedOperationException("getServletNames was deprecated in Servlet 2.1: don't use it");
	}

	/**
	 * Writes the specified message to a servlet log file, usually an event log.
	 * The name and type of the servlet log file is specific to the servlet container.
	 **/
	public void log(String message) { // XXX change this to use something testable
		System.out.println("SERVLET LOG --> " + message);
	}

	/**
	 * @deprecated use log( String, Throwable )
	 **/
	public void log(Exception e, String message) {
		log(message, e);
	}

	/**
	 * Writes an explanatory message and a stack trace for a given Throwable exception to the servlet log file.
	 * The name and type of the servlet log file is specific to the servlet container, usually an event log.
	 **/
	public void log(String message, Throwable t) {
		log(message);
		log("  " + t);
	}

	/**
	 * Returns a String containing the real path for a given virtual path. For example, the virtual path "/index.html" has a real path of
	 * whatever file on the server's filesystem would be served by a request for "/index.html".
	 *
	 * The real path returned will be in a form appropriate to the computer and operating system on which the servlet container is running,
	 * including the proper path separators. This method returns null if the servlet container cannot translate the virtual path to a real path for
	 * any reason (such as when the content is being made available from a .war archive).
	 **/
	public String getRealPath(String path) {
		//        return null;  // XXX not implemented
		System.out.println("WARNING: getRealPath() may not be portable");
		if (!path.startsWith("/"))
			path = "/" + path;
		return warRoot + path;
	}

	/**
	 * Returns the name and version of the servlet container on which the servlet is running.
	
	 * The form of the returned string is servername/versionnumber. For example, the JavaServer Web Development Kit may return the
	 * string JavaServer Web Dev Kit/1.0.
	
	 * The servlet container may return other optional information after the primary string in parentheses, for example, JavaServer Web
	 * Dev Kit/1.0 (JDK 1.1.6; Windows NT 4.0 x86).
	 **/
	public String getServerInfo() {
		return "Interface21 test framework";
	}

	/**
	 * Returns a String containing the value of the named context-wide initialization parameter, or null if the parameter does not exist.
	 *
	 * This method can make available configuration information useful to an entire "web application". For example, it can provide a
	 * webmaster's email address or the name of a system that holds critical data.
	 **/
	public java.lang.String getInitParameter(String name) {
		return initParams.getProperty(name);
	}

	public void addInitParameter(String name, String s) {
		//System.out.println("------> MockServletContext: added init parameter '" + name + "' with value '" + s + "'");
		initParams.put(name, s);
	}

	/**
	 * Returns the names of the context's initialization parameters as an Enumeration of String objects,
	 * or an empty Enumeration if the context has no initialization parameters.
	 **/
	public java.util.Enumeration getInitParameterNames() {
		return EMPTY_VECTOR.elements(); // XXX not implemented
	}

	/**
	 * Returns the servlet container attribute with the given name, or null if there is no attribute by that name.
	 * An attribute allows a servlet container to give the servlet additional information not already
	 * provided by this interface. See your server documentation for information
	 * about its attributes. A list of supported attributes can be retrieved using getAttributeNames.
	 **/
	public Object getAttribute(String name) {
		return _attributes.get(name);
	}

	public Enumeration getAttributeNames() {
		return _attributes.keys();
	}

	public void setAttribute(String name, Object attribute) {
		//System.out.println("------> MockServletContext: set context attribute '" + name + "' to " + attribute);
		_attributes.put(name, attribute);
	}

	public void removeAttribute(String name) {
		_attributes.remove(name);
	}

	//----------------------------- methods added to ServletContext in JSDK 2.3 --------------------------------------

	/**
	 * Returns a directory-like listing of all the paths to resources within the web application
	 * whose longest sub-path matches the supplied path argument. Paths indicating subdirectory paths end with a '/'.
	 * The returned paths are all relative to the root of the web application and have a leading '/'.
	 * For example, for a web application containing
	 * <p>
	 * /welcome.html<br />
	 * /catalog/index.html<br /><br />
	 * /catalog/products.html<br />
	 * /catalog/offers/books.html<br />
	 * /catalog/offers/music.html<br />
	 * /customer/login.jsp<br />
	 * /WEB-INF/web.xml<br />
	 * /WEB-INF/classes/com.acme.OrderServlet.class,<br />
	 * <br />
	 * getResourcePaths("/") returns {"/welcome.html", "/catalog/", "/customer/", "/WEB-INF/"}<br />
	 * getResourcePaths("/catalog/") returns {"/catalog/index.html", "/catalog/products.html", "/catalog/offers/"}.
	 *
	 * @param path partial path used to match the resources, which must start with a /
	 * @return a Set containing the directory listing, or null if there are no resources
	 *         in the web application whose path begins with the supplied path.
	 * @since HttpUnit 1.3
	 */
	public Set getResourcePaths(String path) {
		throw new UnsupportedOperationException("getResourcePaths");
	}

	/**
	 * Returns the name of this web application correponding to this ServletContext as specified
	 * in the deployment descriptor for this web application by the display-name element.
	 *
	 * @return The name of the web application or null if no name has been declared in the deployment descriptor
	 * @since HttpUnit 1.3
	 */
	public String getServletContextName() {
		return this.displayName;
	}

}