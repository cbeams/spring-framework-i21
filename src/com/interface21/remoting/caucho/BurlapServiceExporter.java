package com.interface21.remoting.caucho;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.burlap.server.BurlapSkeleton;

import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.mvc.Controller;

/**
 * Web controller that exports the specified service bean as Burlap service
 * endpoint, accessible via a Burlap proxy.
 *
 * <p>Burlap is a slim, XML-based RPC protocol.
 * For information on Hessian, see the
 * <a href="http://www.caucho.com/burlap">Burlap website</a>
 *
 * <p>Note: Burlap services exported with this class can be accessed by
 * any Burlap client, as there isn't any special handling involved.
 *
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see BurlapProxyFactoryBean
 */
public class BurlapServiceExporter implements Controller {

	private BurlapSkeleton skeleton;

	/**
	 * Set the service to export via Burlap.
	 * Typically populated via a bean reference.
	 */
	public void setService(Object service) {
		this.skeleton = new BurlapSkeleton(service);
	}

	/**
	 * Process the incoming Burlap request and create a Burlap response.
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BurlapInput in = new BurlapInput(request.getInputStream());
		BurlapOutput out = new BurlapOutput(response.getOutputStream());
		try {
		  this.skeleton.invoke(in, out);
		} catch (Throwable ex) {
		  throw new ServletException(ex);
		}
		return null;
	}
}
