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
 * Simple MVC controller interface. Most similar to a Struts
 * Action.
 * <br>An implementation of the Controller interface is a reusable,
 * threadsafe object that handles multiple HTTP requests throughout
 * the lifecycle of an application. Controllers are normally
 * JavaBeans, allowing for easy and consistent configuration 
 * on application startup.
 * <br>This is the core interface of the default
 * Interface21 MVC workflow.
 * @author Rod Johnson
 * @version $Id$
 */
public interface Controller {	

	/**
	 * Process the request and return a ModelAndView object which the ControllerServlet
	 * will render.
	 * @return a ModelAndView for the ControllerServlet to render. A null 
	 * return is not an error. It indicates that this object completed 
	 * request processing itself, and there is no ModelAndView to render.
	 */
	ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	
}

