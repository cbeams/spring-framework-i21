package com.interface21.web.servlet.tags;

import java.util.List;

import javax.servlet.jsp.JspException;

import com.interface21.context.NoSuchMessageException;
import com.interface21.validation.Errors;
import com.interface21.validation.ObjectError;
import com.interface21.web.util.ExpressionEvaluationUtils;
import com.interface21.web.util.HtmlUtils;

/**
 * <p>Bind tag, supporting evaluation of binding errors for a certain
 * bean or bean property. Exports a "status" variable of type BindStatus</p>
 * <p>The errors object that has been bound using this tag is exposed, as well
 * as the property that this errors object applies to. Children tags can
 * use the exposed properties</p>
 *
 * <p>Discussed in Chapter 12 of
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class BindTag extends RequestContextAwareTag {

	public static final String STATUS_VARIABLE_NAME = "status";

	/** the path */
    private String path;
    /** the errors */
    private Errors errors;
    /** the property */
    private String property;

	/**
	 * Set the path that this tag should apply.
	 * Can be a bean (e.g. "person"), or a bean property
	 * (e.g. "person.name"), also supporting nested beans.
	 */
	public void setPath(String path) throws JspException {
		this.path = ExpressionEvaluationUtils.evaluateString("path", path, pageContext);
	}

    /**
     * Retrieves the errors object this tag has currently bound
     * @return an instance of Errors
     */
    public Errors getErrors() {
        return errors;
    }

    /**
     * Retrieves the property the errors object applies to, or
     * null if the errors object applies to the object rather
     * than a property of the object
	 */
    public String getProperty() {
        return property;
    }

	public int doStartTag() throws JspException {
		super.doStartTag();

		// determine name of the object and property
		String name = null;
		property = null;
		int dotPos = this.path.indexOf('.');
		if (dotPos == -1) {
			// property not set, only the object itself
			name = this.path;
		}
		else {
			name = this.path.substring(0, dotPos);
			property = this.path.substring(dotPos + 1);
		}

		// retrieve errors object
		errors = getRequestContext().getErrors(name, false);

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

		// instantiate the bindstatus object
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

    public void release() {
        super.release();
        property = null;
        errors = null;
        path = null;
    }

}
