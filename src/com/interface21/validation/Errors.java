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
 * Interface to be implemented by objects that can store and expose
 * information about data binding errors.
 *
 * <p>Field names can be properties of the given object (e.g. "name"
 * when binding to a customer object), or nested fields in case of
 * subobjects (e.g. "address.street"). Supports subtree navigation
 * via setNestedPath, e.g. an AddressValidator validates "address",
 * not being aware that this is a subobject of customer.
 *
 * <p>Note: Errors objects are single-threaded.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setNestedPath
 */
public interface Errors {

	/**
	 * Return the name of the bound object.
	 */
	String getObjectName();

	/**
	 * Reject the current object, using the given error description.
	 * @param errorCode error code, interpretable as message key
	 * @param errorArgs error arguments, for argument binding via MessageFormat
	 * @param defaultMessage fallback default message
	 */
	void reject(String errorCode, Object[] errorArgs, String defaultMessage);

	/**
	 * Reject the given field of the current object, using the given error description.
	 * @param field field name
	 * @param errorCode error code, interpretable as message key
	 * @param errorArgs error arguments, for argument binding via MessageFormat
	 * @param defaultMessage fallback default message
	 */
	void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage);

	/**
	 * Return if there were any errors.
	 */
	boolean hasErrors();

	/**
	 * Return the total number of errors.
	 */
	int getErrorCount();

	/**
	 * Get all errors, both global and field ones.
	 * @return List of ObjectError instances
	 */
	List getAllErrors();

	/**
	 * Return if there were any global (i.e. not field-specific) errors.
	 */
	boolean hasGlobalErrors();

	/**
	 * Return the number of global (i.e. not field-specific) errors.
	 */
	int getGlobalErrorCount();

	/**
	 * Get all global errors.
	 * @return List of ObjectError instances
	 */
	List getGlobalErrors();

	/**
	 * Get the first global error, if any.
	 * @return the global error, or null
	 */
	ObjectError getGlobalError();

	/**
	 * Return if there are any errors associated with the given field.
	 * @param field field name
	 * @return if there were any errors associated with the given field
	 */
	boolean hasFieldErrors(String field);

	/**
	 * Return the number of errors associated with the given field.
	 * @param field field name
	 * @return the number of errors associated with the given field
	 */
	int getFieldErrorCount(String field);

	/**
	 * Get all errors associated with the given field.
	 * @param field field name
	 * @return List of FieldError instances
	 */
	List getFieldErrors(String field);

	/**
	 * Get the first error associated with the given field, if any.
	 * @return the field-specific error, or null
	 */
	FieldError getFieldError(String field);

	/**
	 * Return the current value of the given field, either the current
	 * bean property value or a rejected update from the last binding.
	 * Allows for convenient access to user-specified field values,
	 * even if there were type mismatches.
	 * @param field field name
	 * @return the current value of the given field
	 */
	Object getFieldValue(String field);

	/**
	 * Allow context to be changed so that standard validators can
	 * validate subtrees. Reject calls prepend the given nested
	 * path to the field names.
	 * <p>For example, an address validator could validate the
	 * subobject address of a customer object.
	 * @param nestedPath nested path within this object,
	 * e.g. "address" (defaults to "", null is also acceptable)
	 */
	void setNestedPath(String nestedPath);

}
