package com.interface21.validation;

/**
 * Encapsulates an field error, i.e. a field-specific reason for rejection.
 * @author Rod Johnson
 * @version
 */
public class FieldError extends ObjectError {

	private String field;

	private Object rejectedValue;

	public FieldError(String objectName, String field, Object rejectedValue, String errorCode, String message) {
		super(objectName, errorCode, message);
		this.field = field;
		this.rejectedValue = rejectedValue;
	}

	public String getField() {
		return field;
	}

	/** May be null if missing */
	public Object getRejectedValue() {
		return rejectedValue;
	}

	public String toString() {
		return "FieldError in object '" + objectName + "' on '" + field + "': " + getMessage() + "; code=" + errorCode + "; rejected [" + rejectedValue + "]";
	}
}
