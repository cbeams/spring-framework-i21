/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MVC View for a web interaction. Implementations are responsible
 * for rendering content, and exposing models. Multiple models can
 * be exposed by a single view.<br/>
 * * This class and the MVC approach associated with it is discussed in Chapter 12 of
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * <br>View implementations should be threadsafe.
 * <p/>View implementations may differ widely. An obvious
 * implementation would be JSP based. Other implementations might be
 * XSLT based, or use an HTML generation library. This interface is
 * designed to avoid restricting the range of possible implementations.
 * <p/>Views should be beans. They are likely to be instantiated as
 * beans by a ViewResolver.
 * <br/>This interface is stateless.
 * @author Rod Johnson
 */
public interface View {
	
	/** 
	 * Add static data to this view, exposed in each view.
	 * <br/>Must be invoked before any calls to render().
	 * <br/><b>Note that it is legal for dynamic and static attributes to
	 * be supplied with the same name; however, the dynamic attribute will 
	 * always take precedence</b>, as it was supplied later. This technique
	 * is commonly used with titles.
	 * @param name name of attribute to expose
	 * @param o object to expose
	 */
	void addStaticAttribute(String name, Object o);
	
	/**
	 * Renders the view given the specified model. 
	 * The first step will be preparing the request: this commonly consists of  
	 * setting models as request attributes.
	 * @param model HashMap of models (model name String mapped to model object)
	 * @param request HttpServletRequest request
	 * @param response HttpServletResponse we are building
	 * @throws IOException if there is an error outputing the data
	 * @throws ServletException if there is an unexpected error not related to IO.
	 */
	void render(Map model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
	
	/**
	 * Set the view's name. Helpful for traceability.
	 * Framework code must call this when constructing views.
	 * @param name the view's name. May not be null.
	 * Views should use this for log messages.
	 */
	void setName(String name);
	
	/** Return the view's name. Should
	 * never be null, if the view was correctly configured.
	 * @return the view's name
	 */
	String getName();

}

