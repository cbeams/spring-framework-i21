package com.interface21.remoting.caucho;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.server.HessianSkeleton;

import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.mvc.Controller;

/**
 * Web controller that exports the specified service bean as Hessian service
 * endpoint, accessible via a Hessian proxy.
 *
 * <p>Hessian is a slim, binary RPC protocol.
 * For information on Hessian, see the
 * <a href="http://www.caucho.com/hessian">Hessian website</a>
 *
 * <p>Note: Hessian services exported with this class can be accessed by
 * any Hessian client, as there isn't any special handling involved.
 *
 * @author Juergen Hoeller
 * @since 13.05.2003
 * @see HessianProxyFactoryBean
 */
public class HessianServiceExporter implements Controller {

	private HessianSkeleton skeleton;

	/**
	 * Set the service to export via Hessian.
	 * Typically populated via a bean reference.
	 */
	public void setService(Object service) {
		this.skeleton = new HessianSkeleton(service);
	}

	/**
	 * Process the incoming Hessian request and create a Hessian response.
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HessianInput in = new HessianInput(request.getInputStream());
		HessianOutput out = new HessianOutput(response.getOutputStream());
		try {
		  this.skeleton.invoke(in, out);
		} catch (Throwable e) {
		  throw new ServletException(e);
		}
		return null;
	}

}
