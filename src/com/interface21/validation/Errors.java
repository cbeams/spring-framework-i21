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

import java.util.List;

/**
 * Interface to be implemented by objects that can store
 * and expose information about data binding errors.
 * <br>Errors objects are single-threaded
 * @author Rod Johnson
 */
public interface Errors {

	String getObjectName();

        /**
         * The message should be resolved from a ResourceBundle
         * using the code as the key.
         * @param errorCode
         * @param defaultMessage
         */
        void reject(String errorCode, String defaultMessage);

        /**
         * The message should be resolved from a ResourceBundle
         * using the code as the key and fall back to the defaultMessage
         * if not found.
         * @param field
         * @param errorCode
         * @param defaultMessage
         */
        void rejectValue(String field, String errorCode, String defaultMessage);

	boolean hasErrors();

	int getErrorCount();

	/**
	 * @return List of ObjectError instances
	 */
	List getAllErrors();

	boolean hasGlobalErrors();

	int getGlobalErrorCount();

	/**
	 * @return List of ObjectError instances
	 */
	List getGlobalErrors();

	/**
	 * Returns ObjectError or null
	 */
	ObjectError getGlobalError();

	boolean hasFieldErrors(String field);

	int getFieldErrorCount(String field);

	/**
	 * @return List of FieldError instances
	 */
	List getFieldErrors(String field);

	/**
	 * Returns FieldError or null
	 */
	FieldError getFieldError(String field);

	Object getPropertyValueOrRejectedUpdate(String field);

	/**
	 * Allows context to be changed so that standard validators can validate subtrees.
	 * E.g. an address validator could validate address.
	 * @param nestedPath defaults to "". Null is also acceptable.
	 * Nested path within this object, e.g. "billingAddress"
	 * Rejection amounts to adding this.
	 */
	void setNestedPath(String nestedPath);
}
