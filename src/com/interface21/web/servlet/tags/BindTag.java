package com.interface21.web.servlet.tags;

import java.util.List;

import javax.servlet.jsp.JspException;

import com.interface21.context.NoSuchMessageException;
import com.interface21.validation.Errors;
import com.interface21.validation.ObjectError;
import com.interface21.web.util.HtmlUtils;

/**
 * Bind tag, supporting evaluation of binding errors for a certain
 * bean or bean property. Exports a "status" variable of type BindStatus.
 *
 *<p> Discussed in Chapter 12 of
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class BindTag extends RequestContextAwareTag {

	public static final String STATUS_VARIABLE_NAME = "status";

	private String path;

	/**
	 * Set the path that this tag should apply.
	 * Can be a bean (e.g. "person"), or a bean property
	 * (e.g. "person.name"), also supporting nested beans.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public int doStartTag() throws JspException {
		super.doStartTag();

		String name = null;
		String property = null;
		int dotPos = this.path.indexOf('.');
		if (dotPos == -1) {
			name = this.path;
		}
		else {
			name = this.path.substring(0, dotPos);
			property = this.path.substring(dotPos + 1);
		}

		Errors errors = getRequestContext().getErrors(name, false);
		if (errors == null) {
			throw new JspException("Invalid bind path [" + this.path + "]: Errors instance not found in request");
		}

		List fes = null;
		Object value = null;
		
		if (property != null) {
			fes = errors.getFieldErrors(property);
			value = errors.getFieldValue(property);
			if (isHtmlEscape() && value instanceof String) {
				value = HtmlUtils.htmlEscape((String) value);
			}
		}
		else {
			fes = errors.getGlobalErrors();
		}

		BindStatus status = new BindStatus(property, value, getErrorCodes(fes), getErrorMessages(fes));
		this.pageContext.setAttribute(STATUS_VARIABLE_NAME, status);
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Extract the error codes from the given ObjectError list.
	 */
	private String[] getErrorCodes(List fes) {
		String[] codes = new String[fes.size()];
		for (int i = 0; i < fes.size(); i++) {
			ObjectError error = (ObjectError) fes.get(i);
			codes[i] = error.getCode();
		}
		return codes;
	}

	/**
	 * Extract the error messages from the given ObjectError list.
	 */
	private String[] getErrorMessages(List fes) throws JspException {
		String[] messages = new String[fes.size()];
		for (int i = 0; i < fes.size(); i++) {
			ObjectError error = (ObjectError) fes.get(i);
			try {
				messages[i] = getRequestContext().getMessage(error, isHtmlEscape());
			}
			catch (NoSuchMessageException ex) {
				throw new JspException(ex.getMessage());
			}
		}
		return messages;
	}

}
