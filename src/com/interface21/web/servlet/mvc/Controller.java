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

package com.interface21.web.servlet.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.ModelAndView;

/**
 * Base class for any Controller implementation. Most similar to the Struts'
 * notion of <code>Actions</code>.
 *
 * <p>Any implementation of the Controller interface should be a
 * <i>reusable, threadsafe</i> class, capable of handling multiple
 * HTTP requests throughout the lifecycle of an application. To be able to
 * configure Controller in an easy, Controllers are usually JavaBeans.</p>
 *
 * <p><b><a name="workflow">Workflow</a>:</b><br>
 * After the DispatcherServlet has received a request and has done its work
 * to resolve locales, themes and things a like, it tries to resolve
 * a Controller, using a {@link com.interface21.web.servlet.HandlerMapping
 * HandlerMapping}. When a Controller has been found, the \
 * {@link #handleRequest(HttpServletRequest, HttpServletResponse) handleRequest()}
 * method will be invoked, which is responsible for handling the actual
 * request and - if applicable - returning an appropriate ModelAndView.
 * So actually, this method is the main entrypoint for the
 * {@link com.interface21.web.servlet.DispatcherServlet DispatcherServlet}
 * which delegates requests to controllers. This method - and also this interface -
 * should preferrably not be implemented by custom controllers <i>directly</i>, since
 * abstract controller also provided by this package already provide a lot
 * of functionality common for virtually all webapplications. A few examples of
 * those abstract controllers:
 * {@link AbstractController AbstractController},
 * {@link AbstractCommandController AbstractCommandController} and
 * {@link AbstractFormController AbstractFormController}.
 * </p>
 * <p>So basically any <i>direct</i> implementation of the Controller interface
 * just handles HttpServletRequests and should return a ModelAndView, to be
 * further used by the DispatcherServlet. Any additional functionality such
 * as optional validation, formhandling, etcetera should be obtained by
 * extended one of the abstract controller mentioned above.
 * </p>
 *
 * <p><b>Exposed configuration properties:</b><br>
 * none</p>
 *
 * @author Rod Johnson
 * @version $Id$
 */
public interface Controller {

	/**
	 * Process the request and return a ModelAndView object which the
	 * DispatcherServlet will render. A null return is not an error.
	 * It indicates that this object completed request processing itself,
	 * thus there is no ModelAndView to render.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render, or null if handled directly
	 */
	ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException;

}
