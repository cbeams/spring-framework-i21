

package com.interface21.validation;

// <i> to support diff error sources?

import java.io.Serializable;

import com.interface21.core.ErrorCoded;

/**
 *
 * @author Rod Johnson
 * @version 
 */
public class FieldError implements Serializable, ErrorCoded {
	
	private String objectName;
	
	private String field;
	
	private Object rejectedValue;
	
	private String errorCode;

	private String message;
	
	// TYPOE?
	
	public FieldError(String objectName, String field, Object rejectedValue, String errorCode, String message) {
	 	this.objectName = objectName;
		this.field = field;
		this.rejectedValue = rejectedValue;
		this.errorCode = errorCode;
		this.message = message;
	}

	public FieldError(String objectName, String errorCode, String message) {
	 	this.objectName = objectName;
		this.errorCode = errorCode;
		this.message = message;
	}

	/** Creates new FieldError */
//    public FieldError(String field, Object rejectedValue, String errorCode, Throwable t) {
//		this.field = field;
//		this.errorCode = errorCode;
//		this.rejectedValue = rejectedValue;
//		this.t = t;
//		if (t != null)
//			this.message = t.getLocalizedMessage();
//    }
	
//	 public FieldError(String field, Object rejectedValue, String errorCode, String message) {
//		 this(field, rejectedValue, errorCode, (Throwable) null);
//		 this.message = message;
//	 }
//	 
//	 public FieldError(String field, Object rejectedValue, String errorCode) {
//		 this(field, rejectedValue, errorCode, errorCode);
//	 }
	 
	 public String getObjectName() {
	 	return objectName;
	 }
	 
	 public String getField() {
		 return field;
	 }
	 
	 /** May be null if missing */
	 public Object getRejectedValue() {
		 return rejectedValue;
	 }
	 
	 public String getErrorCode() {
		 return errorCode;
	 }
	 
	 public String getMessage() {
		return message;
	 }
	 
	 public String toString() {
		 return "FieldError in object '" + objectName + "' on '" + field + "': " + getMessage() + "; code=" + errorCode + "; rejected [" + rejectedValue + "]";
	 }
}
