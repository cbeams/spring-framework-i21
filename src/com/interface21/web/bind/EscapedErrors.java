package com.interface21.web.bind;

import com.interface21.validation.Errors;
import com.interface21.validation.FieldError;
import com.interface21.web.util.HtmlUtils;

/**
 * Errors wrapper that adds automatic HTML escaping to the wrapped instance.
 * @author Juergen Hoeller
 * @since 01.03.2003
 */
public class EscapedErrors implements Errors {

	private Errors source = null;

	/**
	 * Create a new EscapedErrors instance for the given source instance.
	 */
	public EscapedErrors(Errors source) {
		this.source = source;
	}

	public String getObjectName() {
		return source.getObjectName();
	}

	public void reject(String code, String message) {
		source.reject(code, message);
	}

	public void rejectValue(String field, String code, String message) {
		source.rejectValue(field, code, message);
	}

	public boolean hasErrors() {
		return source.hasErrors();
	}

	public int getErrorCount() {
		return source.getErrorCount();
	}

	public FieldError[] getAllErrors() {
		return escapeFieldErrors(source.getAllErrors());
	}

	public boolean hasGlobalErrors() {
		return source.hasGlobalErrors();
	}

	public int getGlobalErrorCount() {
		return source.getGlobalErrorCount();
	}

	public FieldError[] getGlobalErrors() {
		return escapeFieldErrors(source.getGlobalErrors());
	}

	public FieldError getGlobalError() {
		return escapeFieldError(source.getGlobalError());
	}

	public boolean hasFieldErrors(String field) {
		return source.hasFieldErrors(field);
	}

	public int getFieldErrorCount(String field) {
		return source.getFieldErrorCount(field);
	}

	public FieldError[] getFieldErrors(String field) {
		return escapeFieldErrors(source.getFieldErrors(field));
	}

	public FieldError getFieldError(String field) {
		return escapeFieldError(source.getFieldError(field));
	}

	public Object getPropertyValueOrRejectedUpdate(String field) {
		return source.getPropertyValueOrRejectedUpdate(field);
	}

	public void setNestedPath(String nestedPath) {
		source.setNestedPath(nestedPath);
	}

	private FieldError escapeFieldError(FieldError source) {
		if (source == null)
			return null;
		String rejectedValue = (source.getRejectedValue() != null ? HtmlUtils.htmlEscape(source.getRejectedValue().toString()) : null);
		return new FieldError(source.getObjectName(), source.getField(), rejectedValue, source.getErrorCode(), HtmlUtils.htmlEscape(source.getMessage()));
	}

	private FieldError[] escapeFieldErrors(FieldError[] source) {
		FieldError[] escaped = new FieldError[source.length];
		for (int i = 0; i < source.length; i++) {
			escaped[i] = escapeFieldError(source[i]);
		}
		return escaped;
	}
}
