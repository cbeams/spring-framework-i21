package com.interface21.web.servlet.tags;

import javax.servlet.jsp.JspException;

import com.interface21.validation.Errors;

/**
 * Evaluates content if there are bind errors for a certain bean.
 * Exports an "errors" variable of type Errors for the given bean.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see BindTag
 */
public class BindErrorsTag extends RequestContextAwareTag {

	public static final String ERRORS_VARIABLE_NAME = "errors";

	private String name;

	/**
	 * Set the name of the bean that this tag should check.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public int doStartTag() throws JspException {
		super.doStartTag();

		Errors errors = getRequestContext().getErrors(this.name, isHtmlEscape());

		if (errors != null && errors.hasErrors()) {
			this.pageContext.setAttribute(ERRORS_VARIABLE_NAME, errors);
			return EVAL_BODY_INCLUDE;
		}
		else {
			return SKIP_BODY;
		}
	}

}