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
 * <br>Errors objects are single-threaded.
 * @author Rod Johnson
 */
public interface Errors {

	/**
	 * @return the name of the bound object
	 */
	String getObjectName();

	/**
	 * Rejects the current object, using the given error description.
	 * @param errorCode  the error code, interpretable as message key
	 * @param errorArgs  the error arguments, for argument binding via MessageFormat
	 * @param defaultMessage  the fallback default message
	 */
	void reject(String errorCode, Object[] errorArgs, String defaultMessage);

	/**
	 * Rejects the given field of the current object, using the given error description.
	 * @param field  the name of the field
	 * @param errorCode  the error code, interpretable as message key
	 * @param errorArgs  the error arguments, for argument binding via MessageFormat
	 * @param defaultMessage  the fallback default message
	 */
	void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage);

	/**
	 * @return if there were any errors
	 */
	boolean hasErrors();

	/**
	 * @return the number of errors
	 */
	int getErrorCount();

	/**
	 * Gets all errors, both global and field ones.
	 * @return List of ObjectError instances
	 */
	List getAllErrors();

	/**
	 * @return if there were any global errors (i.e. not field-specific)
	 */
	boolean hasGlobalErrors();

	/**
	 * @return the number of global errors
	 */
	int getGlobalErrorCount();

	/**
	 * Gets all global errors.
	 * @return List of ObjectError instances
	 */
	List getGlobalErrors();

	/**
	 * Gets the first global error, if any.
	 * @return the global error, or null
	 */
	ObjectError getGlobalError();

	/**
	 * @param field  the field name
	 * @return if there were any errors associated with the given field
	 */
	boolean hasFieldErrors(String field);

	/**
	 * @param field  the field name
	 * @return the number of errors associated with the given field
	 */
	int getFieldErrorCount(String field);

	/**
	 * Gets all errors associated with the given field.
	 * @param field  the field name
	 * @return List of FieldError instances
	 */
	List getFieldErrors(String field);

	/**
	 * Gets the first error associated with the given field, if any.
	 * @return the field-specific error, or null
	 */
	FieldError getFieldError(String field);

	/**
	 * Returns the current value of the given field, or a rejected update value
	 * value from the last binding, if any. Allows for convenient access to
	 * user-specified field values, even if there were type mismatches.
	 * @param field  the field name
	 * @return the current value of the given field
	 */
	Object getPropertyValueOrRejectedUpdate(String field);

	/**
	 * Allows context to be changed so that standard validators can validate subtrees.
	 * Reject calls prepend the given nested path to the field names.
	 * E.g. an address validator could validate the subobject address of a user object.
	 * @param nestedPath  nested path within this object, e.g. "address"
	 * (defaults to "", null is also acceptable)
	 */
	void setNestedPath(String nestedPath);
}
