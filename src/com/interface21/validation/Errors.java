/*
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
  
package com.interface21.validation;

/**
 * Interface to be implemented by objects that can store
 * and expose information about data binding errors.
 * <br>Errors objects are single-threaded
 * @author Rod Johnson
 */
public interface Errors {
	 
	// Reject a value in the last update
	void rejectValue(String field, String code, String message);
	
	void rejectValue(String objName, String field, String code, String message);
	
	int getErrorCount();
	
	boolean hasError(String objName, String field);
	
	Object getPropertyValueOrRejectedUpdate(String objName, String field);
	
	boolean hasErrors();
	
	/**
	 * Return FieldError or null
	 */
	FieldError getError(String objName, String field);
	
	/** Apply to last update */
	boolean hasError(String field);
	
	/** Apply to last update */
	FieldError getError(String field);
	
	/** 
	 * Apply to last update 
	 */
	Object getPropertyValueOrRejectedUpdate(String field);
	
	
	/**
	 * Allows context to be changed so that standard validators can validate subtrees.
	 * E.g. an address validator could validate address.
	 * @param nestedPath defaults to "". Null is also acceptable. 
	 * Nested path within this object, e.g. "billingAddress"
	 * Rejection amounts to adding this
	 */
	void setNestedPath(String nestedPath);

}
