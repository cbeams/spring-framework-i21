package com.interface21.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;

/**
 * Exception thrown when there's no request handling method for
 * this request.
 * @author Rod Johnson
 */
public class NoSuchRequestHandlingMethodException extends ServletException {
	
	private String name;
	
	public NoSuchRequestHandlingMethodException(HttpServletRequest request) {
		super("No handling method can be found for request [" + request + "]");
	}
	
	public NoSuchRequestHandlingMethodException(String name, MultiActionController multiActionRequestController) {
		super(
			"No request handling method with name '" + name + "' in class " + multiActionRequestController.getClass().getName());
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
