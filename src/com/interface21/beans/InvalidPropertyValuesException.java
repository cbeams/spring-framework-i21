package com.interface21.beans;

import java.util.LinkedList;
import java.util.List;

import com.interface21.core.ErrorCoded;

/**
 * Exception thrown when PropertyValues are flagged as invalid
 * by a PropertyValuesValidator. After an object of this class
 * is constructed, MissingFieldExceptions can be added one by one.
 * @author Rod Johnson
 * @version 1.0
 */
public class InvalidPropertyValuesException extends BeansException {
	
	private List missingFields = new LinkedList();
	
	private PropertyValues pvs;
	
	public InvalidPropertyValuesException(PropertyValues pvs) {
		super("InvalidPropertyValues");
		this.pvs = pvs;
	}
	
	public void addMissingField(String field, String errorCode) {
		missingFields.add(new MissingFieldException(field, errorCode));
	}
	
	public boolean fieldsAreMissing() {
		return missingFields.size() > 0;
	}
	
	public List getMissingFields() {
		return missingFields;
	}
	
	
	public PropertyValues getPropertyValues() {
		return pvs;
	}
	
	public static class MissingFieldException extends Exception implements ErrorCoded {
		
		private String field;
		
		private String errorCode = UNCODED;
		
		public MissingFieldException(String field, String errorCode) {
			super("Field '" + field + "' is required");
			this.field = field;
			this.errorCode = errorCode;
		}
		
		public String getField() {
			return field;
		}
		
		/** Return the error code associated with this failure.
		 * The GUI can render this anyway it pleases, allowing for Int8ln etc.
		 * @return a String error code associated with this failure
		 */
		public String getErrorCode() {
			return errorCode;
		}
	}
	
}

