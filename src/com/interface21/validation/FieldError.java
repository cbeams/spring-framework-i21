

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
	
	private String objName;
	
	private String field;
	
	private String errorCode;
	
	private Object rejectedValue;
	
	private Throwable t;
	
	private String message;
	
	// TYPOE?
	
	 FieldError(String objectName, String field, Object rejectedValue, String errorCode, String message) {
	 	this.objName = objectName;
		this.field = field;
		this.errorCode = errorCode;
		this.rejectedValue = rejectedValue;
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
	 
	 public String getObject() {
	 	return objName;
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
	 
	 /** Will usually return null */
	 public Throwable getThrowable() {
		 return t;
	 }
	 
	 public String toString() {
		 return "FieldError in object '" + objName + "' on '" + field + "': " + getMessage() + "; code=" + errorCode + "; rejected [" + rejectedValue + "]";
	 }

}
