package com.interface21.web.servlet.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.web.servlet.HandlerAdapter;
import com.interface21.web.servlet.LastModified;
import com.interface21.web.servlet.ModelAndView;

/**
 * Adapter to use the Controller workflow interface
 * with the generic ControllerServlet. This is an SPI
 * class, not used directly by application code.
 * @author Rod Johnson
 * @see com.interface21.web.servlet.ControllerServlet
 * @version $Id$
 */
public class SimpleControllerHandlerAdapter extends ApplicationObjectSupport implements HandlerAdapter {
	
	public SimpleControllerHandlerAdapter() {
	}

	public boolean supports(Object handler) {
		return handler != null && Controller.class.isAssignableFrom(handler.getClass());
	}
	
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object delegate) throws IOException, ServletException {
		Controller controller = (Controller) delegate;
		return controller.handleRequest(request, response);
	}
	
	public long getLastModified(HttpServletRequest request, Object delegate) {
		if (delegate instanceof LastModified) {
			return ((LastModified) delegate).getLastModified(request);
		}
		return -1L;
	}

}
