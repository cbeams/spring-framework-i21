package com.interface21.validation;

import java.io.Serializable;

import com.interface21.core.ErrorCoded;

/**
 * Encapsulates an object error, i.e. a global reason for rejection.
 * @author Juergen Hoeller
 * @since 10.03.2003
 */
public class ObjectError implements Serializable, ErrorCoded {

	protected String objectName;

	protected String errorCode;

	protected String message;

	public ObjectError(String objectName, String errorCode, String message) {
	 	this.objectName = objectName;
		this.errorCode = errorCode;
		this.message = message;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return "Error in object '" + objectName + "': " + getMessage() + "; code=" + errorCode;
	}
}
