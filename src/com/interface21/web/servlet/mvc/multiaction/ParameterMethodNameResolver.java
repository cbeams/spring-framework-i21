package com.interface21.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;

/**
 * Simple implementation of MethodNameResolver that looks for a
 * parameter value containing the name of the method to invoke.
 * This class is a JavaBean, so the parameter name can be set
 * via the paramName property. The default value is "action".
 * @author Rod Johnson
 */
public class ParameterMethodNameResolver implements MethodNameResolver {
	
	private String paramName = "action";
	
	/**
	 * Set the parameter name we're looking for.
	 * The default is "action".
	 * @param paramName the parameter name we're looking for
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	} 
	
	public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
		String name = request.getParameter(paramName);
		if (name == null)
			throw new NoSuchRequestHandlingMethodException(request);
		return name;
	}

}
