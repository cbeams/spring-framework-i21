/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
 
package com.interface21.web.bind;

import javax.servlet.ServletRequest;

import com.interface21.validation.DataBinder;

/**
 * Use this class to perform manual data binding from servlet request parameters
 * to JavaBeans.
 * @author Rod Johnson
 */
public class ServletRequestDataBinder extends DataBinder  {
	
	public ServletRequestDataBinder(Object target, String name) {
		super(target, name);
	}

	/**
	 * Bind the parameters of the given request to this binder's target.
	 * This call can create field errors, representing basic binding
	 * errors like a required field (code "required"), or type mismatch
	 * between value and bean property (code "typeMismatch").
	 * @param request request with parameters to bind
	 */
	public void bind(ServletRequest request) {
		bind(new ServletRequestParameterPropertyValues(request));
	}

	/**
	 * Treats errors as fatal. Use this method only if 
	 * it's an error if the input isn't valid. 
	 * This might be appropriate
	 * if all input is from dropdowns, for example.
	 * @throws ServletRequestBindingException subclass of ServletException on any binding problem
	 */
	public void closeNoCatch() throws ServletRequestBindingException {
		if (hasErrors()) {
			throw new ServletRequestBindingException("Errors binding onto class " + getTarget(), this);
		}
	}
	
}
