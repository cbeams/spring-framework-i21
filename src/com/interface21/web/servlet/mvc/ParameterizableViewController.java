
package com.interface21.web.servlet.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.web.servlet.ModelAndView;

/**
 * <p>Trivial controller that always returns a named view. The view
 * can be configured using an exposed configuration property. This
 * controller offers an alternative to sending a request straight to a view
 * such as a JSP. The advantage here is, that you're decoupling the controller
 * and the view, letter the some the configuration determine (instead of
 * the controller) the viewtechnology.</p>
 *
 * <p>An alternative to the ParameterizableViewController is of the
 * {@link com.interface21.web.servlet.mvc.multiaction MultiAction controllers},
 * some of which allow the same behavior, but then for more views at in one
 * controller.</p>
 *
 * <p><b><a name="workflow">Workflow
 * (<a href="AbstractController.html#workflow">and that defined by superclass</a>):</b><br>
 * <ol>
 *  <li>Request is received by the controller</li>
 *  <li>call to {@link #handleRequestInternal handleRequestInternal} which
 *      just returns the view, named by the configuration property
 *      <code>viewName</code>. Nothing more, nothing less</li>
 * </ol>
 * </p>
 *
 * <p><b><a name="config">Exposed configuration properties</a>
 * (<a href="AbstractController.html#config">and those defined by superclass</a>):</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></td>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>viewName</td>
 *      <td><i>null</i></td>
 *      <td>the name of the view the viewResolver will use to forward to
 *          (if this property is not set, an exception will be thrown during
 *          initialization)</td>
 *  </tr>
 * </table>
 * </p>
 *
 * @author Rod Johnson
 */
public class ParameterizableViewController extends AbstractController 
		implements InitializingBean {
	
	private String successView;
	

	/* Require only success view name */
	public ParameterizableViewController() {
	}
	
	
	//---------------------------------------------------------------------
	// Bean properties
	//---------------------------------------------------------------------
	/**
	 * Gets the viewName.
	 * Used in this class: other properties are for subclasses only.
	 * @return Returns a String
	 */
	public String getViewName() {
		return successView;
	}

	/**
	 * Sets the viewName.
	 * @param viewName The viewName to set
	 */
	public void setViewName(String viewName) {
		this.successView = viewName;
	}
	


	/**
	 * @see AbstractController#handleRequest(HttpServletRequest, HttpServletResponse)
	 */
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return new ModelAndView(this.successView);
	}
	
	/**
	 * Ensure at least successView is set!?
	 * @see InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (successView == null)
			throw new ServletException("viewName bean property must be set in " + getClass().getName());
	}

}
