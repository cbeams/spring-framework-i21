
package com.interface21.web.servlet.mvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.servlet.ModelAndView;

/**
 * Simple demonstration of how to implement a controller.
 */
public class DemoController implements Controller {
	
	public static final String ENTER_NAME_VIEW = "enterNameView";
	public static final String INVALID_NAME_VIEW = "invalidNameView";
	public static final String VALID_NAME_VIEW = "validNameView";

	/**
	 * @see AbstractController#handleRequest(HttpServletRequest, HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String name = request.getParameter("name");
		if (name == null || "".equals(name)) {
			return new ModelAndView(ENTER_NAME_VIEW);
		}
		else if (name.indexOf("-") != -1) {
			return new ModelAndView(INVALID_NAME_VIEW, "name", name);
		}
		else {
			return new ModelAndView(VALID_NAME_VIEW, "name",  name);
		}
	}

}
