/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
package com.interface21.web.servlet.view.xslt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.WebApplicationContext;
import com.interface21.web.servlet.view.AbstractView;

/**
 * Convenient superclass for views rendered using an XSLT stylesheet.
 * Subclasses must provide the XML W3C document to transform. They do not need to
 * concern themselves with XSLT.
 * Properties:
 * <li>stylesheet: no transform is null
 * <li>uriResolver:
 * <li>cache (optional, default=false): debug setting only. Setting this
 * to true will cause the templates object to be reloaded for each rendering.
 * This is useful during development, but will seriously affect performance in
 * production and isn't threadsafe.
 * root: name of the root element
 * @author  Rod Johnson
 * @version $Id$
 */
public abstract class AbstractXsltView extends AbstractView implements ApplicationContextAware {
	
	/**
	 * Name of the XML element that will contain locale and language information about the
	 * request, enabling XSLT stylesheets to use this information without access
	 * to the Servlet API.
	 */
	public static final String REQUEST_INFO_KEY = "request-info"; 


	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/**
	 * XSLT Template
	 */
	private Templates templates;

	private TransformerFactory transformerFactory;

	/** Custom URIResolver, set by subclass or as bean property */
	private URIResolver uriResolver;

	/**
	 * URL of stylesheet
	 */
	private String url;

	private String root;
	
	private boolean cache = true;
	
	

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/** 
	 * Creates new XsltView 
	 */
	public AbstractXsltView() {
	}

	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------
	/** 
	 * Set the URL of the XSLT stylesheet
	 * @param url the URL of the XSLT stylesheet
	 */
	public final void setStylesheet(String url) {
		this.url = url;
	}

	/** 
	 * Document root element name. Only
	 * used if we're not passed a Node.
	 * @param root document root element name
	 */
	public final void setRoot(String root) {
		this.root = root;
	}

	/**
	 * Set the URIResolver used in the transform. The
	 * URIResolver handles calls to the XSLT document()
	 * function.
	 * This method can be used by subclasses or as a bean property
	 * @param uriResolver URIResolver to set. No URIResolver
	 * will be set if this is null (this is the default).
	 */
	public final void setUriResolver(URIResolver uriResolver) {
		this.uriResolver = uriResolver;
	}
	
	/**
	 * Gets the cache.
	 * @return Returns a boolean
	 */
	public final boolean getCache() {
		return cache;
	}

	/**
	 * Sets the cache.
	 * @param cache The cache to set
	 */
	public final void setCache(boolean cache) {
		this.cache = cache;
	}
	
	
	/**
	 * Public static to allow use by other classes
	 */
	

	//---------------------------------------------------------------------
	// Implementation of ApplicationContextAware
	//---------------------------------------------------------------------
	/** 
	 * Set the ApplicationContext object used by this object.
	 * Here is also where we load our template, as we need the
	 * Application Context to do it
	 * @param ctx ApplicationContext object used by this object
	 * @param namespace namespace this object is in: null means default namespace
	 * @throws ApplicationContextException if initialization attempted by this object
	 * after it has access to the WebApplicatinContext fails
	 */
	protected final void initApplicationContext() throws ApplicationContextException {
		this.transformerFactory = TransformerFactory.newInstance();
		
		if (this.uriResolver != null) {
			logger.info("Using custom URIResolver [" + this.uriResolver + "] in XsltView with name '" + getName() + "'");
			this.transformerFactory.setURIResolver(this.uriResolver);
		}
		logger.debug("Url in view is " + url);
		cacheTemplates();
	}	
	

	
	private void cacheTemplates() {
		if (url != null && !"".equals(url)) {
			Source s = getStylesheetSource(url);
			try {
				this.templates = transformerFactory.newTemplates(s);
				logger.info("Loaded templates " + templates + " in XsltView with name '" + getName() + "'");
			}
			catch (TransformerConfigurationException ex) {
				throw new ApplicationContextException(
					"Can't load stylesheet at '" + url + "' in XsltView with name '" + getName() + "'",
					ex);
			}
		}
	}	// onSetContext


	/** 
	 * Load the stylesheet.
	 * This implementation uses getRealPath().
	 * Subclasses can override this method to avoid any container
	 * restrictions on use of this slightly questionable method.
	 * However, when it does work it's efficient and convenient.
	 */
	protected Source getStylesheetSource(String url) throws ApplicationContextException {
		// Shouldn't use this: it's not guaranteed
		// QUESTIONABLE: Servlet 2.2 idea!?
		// TODO
		logger.info("Loading XSLT stylesheet '" + url + "' from filesystem using getRealPath");
		String realpath = ((WebApplicationContext) getApplicationContext()).getServletContext().getRealPath(url);
		if (realpath == null)
			throw new ApplicationContextException(
				"Can't resolve real path for XSLT stylesheet at '" + url + "'; probably results from container restriction: override XsltView.getStylesheetSource() to use an alternative approach to getRealPath()");
		logger.info("Realpath is '" + realpath + "'");

		Source s = new StreamSource(new File(realpath));
		return s;
	}	// getStylesheetSource
	
	

	//---------------------------------------------------------------------
	// Implementation of protected abstract methods
	//---------------------------------------------------------------------
	/**
	 * Renders the view given the specified model.  There can be many types of
	 * view.<br/>
	 * The first take will be preparing the request: this may include setting the model
	 * as an attribute, in the case of a JSP view.
	 */
	protected final void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		if (!this.cache) {
			logger.warn("DEBUG SETTING: NOT THREADSAFE AND WILL IMPAIR PERFORMANCE: template will be refreshed");
			cacheTemplates();
		}

		if (this.templates == null) {
			if (this.transformerFactory == null)
				throw new ServletException("View is incorrectly configured. Templates AND TransformerFactory are null");

			logger.warn("XSLTView is not configured: will copy XML input");
			response.setContentType("text/xml; charset=ISO-8859-1");
		}
		else {
			// Normal case
			response.setContentType(getContentType());
		}

		if (model == null)
			throw new ServletException("Cannot do XSLT transform on null model");

		Node dom = null;
		String docRoot = null;

		//System.out.println("INitializing XML: view with hc=" + hashCode());
		// Value of a single element in the map, if there is one
		Object singleModel = null;

		if (model.size() == 1) {
			docRoot = (String) model.keySet().iterator().next();
			singleModel = model.get(docRoot);
		}

		// Handle special case when we have a single node
		if (singleModel != null && (singleModel instanceof Node)) {
			// Don't domify if the model is already an XML node
			// We don't need to worry about model name, either:
			// we leave the Node alone
			logger.debug("No need to domify: was passed an XML node");
			dom = (Node) singleModel;
		}
		else {
			if (this.root == null && docRoot == null)
				throw new ServletException(
					"Cannot domify multiple non-Node objects without a root element name in XSLT view with name='" + getName() + "'");

			// docRoot local variable takes precedence
			try {
				addRequestInfoToModel(model, request);
				dom = createDomNode(model, (docRoot == null) ? this.root : docRoot);
			}
			catch (Exception rex) {
				throw new ServletException("Error creating XML node from model in XSLT view with name='" + getName() + "'", rex);
			}
		}

		doTransform(response, dom);
	}   // renderMergedOutputModel
	
	
	/**
	 * Subclasses must implement this method
	 * Return the XML node to transform.
	 */
	protected abstract Node createDomNode(Map model, String root) throws Exception;
	 
	protected void addRequestInfoToModel(Map model, HttpServletRequest request) {
		// TODO implement this
		//System.out.println("SOMETHING SHOULD GO HERE IN ADD RequestInfo");
		//model.put(REQUEST_INFO_KEY, new RequestInf(request));
	}
	

	/**
	 * Use TrAX to perform the transform
	 */
	protected void doTransform(HttpServletResponse response, Node dom) throws IllegalArgumentException, IOException, ServletException {
		try {
			Transformer trans = (this.templates != null) ? this.templates.newTransformer() : // we have a stylesheet
						transformerFactory.newTransformer(); // just a copy
		
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			// Xalan-specific, but won't do any harm in other XSLT engines
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			trans.transform(new DOMSource(dom), new StreamResult(new BufferedOutputStream(response.getOutputStream())));
		
			logger.debug("XSLT transformed OK with stylesheet '" + url + "'");
		}
		catch (TransformerConfigurationException ex) {
			//Category.getInstance(getClass()).error("Couldn't dispatch to JSP with url '" + getUrl() + "' defined in view with name '" + cr.getViewName() + "': " + ex, ex);
			throw new ServletException(
				"Couldn't create XSLT transformer for stylesheet '" + url + "' in XSLT view with name='" + getName() + "'",
				ex);
		}
		catch (TransformerException ex) {
			//Category.getInstance(getClass()).error("Couldn't dispatch to JSP with url '" + getUrl() + "' defined in view with name '" + cr.getViewName() + "': " + ex, ex);
			throw new ServletException(
				"Couldn't perform transform with stylesheet '" + url + "' in XSLT view with name='" + getName() + "'",
				ex);
		}
	} 

} 	// class AbstractXsltView