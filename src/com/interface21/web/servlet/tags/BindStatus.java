package com.interface21.web.servlet.tags;

import com.interface21.util.StringUtils;

/**
 * Simple adapter to expose status of a field or object.
 * Set as a variable by the bind tag. Intended for use by
 * JSP and JSTL expressions, and to allow for tag cooperation.
 *
 * <p>Obviously, object status representations do not have an
 * expression and a value but only error codes and messages.
 * For simplicity's sake and to be able to use the same tag,
 * the same status class is used for both scenarios.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class BindStatus {
	
	private final String expression;

	private final Object value;

	private final String[] errorCodes;

	private final String[] errorMessages;

	/**
	 * Create a new BindStatus instance,
	 * representing a field or object status.
	 * @param expression expression suitable for HTML input name
	 * @param value current field value
	 * @param errorCodes error codes for the field or object
	 * @param errorMessages resolved error messages for the field or object
	 */
	protected BindStatus(String expression, Object value, String[] errorCodes, String[] errorMessages) {
		this.expression = expression;
		this.value = value;
		this.errorCodes = errorCodes;
		this.errorMessages = errorMessages;
	}

	/**
	 * Return a bind expression that can be used in HTML forms
	 * as input name for the respective field.
	 *
	 * <p>Returns a bind path appropriate for resubmission, e.g. "address.street".
	 * Note that the complete bind path as required by the bind tag is
	 * "customer.address.street", if bound to a "customer" bean.
	 */
	public String getExpression() {
		return this.expression;
	}

	/**
	 * Return the current value of the field,
	 * either the property value or a rejected update.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Return a suitable display value for the field,
	 * i.e. empty string instead of a null value.
	 */
	public String getDisplayValue() {
		return (value != null) ? value.toString() : "";
	}

	/**
	 * Return if this status represents a field or object error.
	 */
	public boolean isError() {
		// need to check array size since BindTag creates empty String[]
		return (errorCodes != null) && (errorCodes.length > 0);
	}

	/**
	 * Return the error codes for the field or object, if any.
	 * Returns an empty array instead of null if none.
	 */
	public String[] getErrorCodes() {
		return (errorCodes != null ? errorCodes : new String[0]);
	}

	/**
	 * Return the first error codes for the field or object, if any.
	 */
	public String getErrorCode() {
		return (errorCodes != null && errorCodes.length > 0 ? errorCodes[0] : "");
	}

	/**
	 * Return the resolved error messages for the field or object,
	 * if any. Returns an empty array instead of null if none.
	 */
	public String[] getErrorMessages() {
		return (errorMessages != null ? errorMessages : new String[0]);
	}

	/**
	 * Return the first error message for the field or object, if any.
	 */
	public String getErrorMessage() {
		return (errorMessages != null && errorMessages.length > 0 ? errorMessages[0] : "");
	}

	/**
	 * Return an error message string, concatenating all messages
	 * separated by the given delimiter.
	 * @param delimiter separator string, e.g. ", " or "<br>"
	 * @return the error message string
	 */
	public String getErrorMessagesAsString(String delimiter) {
		if (errorMessages == null)
			return "";
		return StringUtils.arrayToDelimitedString(errorMessages, delimiter);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("BindStatus: value=[" + value + "]");
		if (isError()) {
			sb.append("; error codes='" + errorCodes + "'; error messages='" + errorMessages + "'; ");
		}
		sb.append("source=" + (isError() ? "error" : "bean"));
		return sb.toString();
	}

}
