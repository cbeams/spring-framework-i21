package com.interface21.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;

/**
 * Default implementation of the Errors interface, supporting
 * registration and evaluation of binding errors.
 * Slightly unusual, as it _is_ an exception.
 *
 * <p>This is mainly a framework-internal class. Normally,
 * application code will work with the Errors interface.
 *
 * <p>Supports exporting a model, suitable for example for web MVC.
 * Thus, it is sometimes used as parameter type instead of the
 * Errors interface itself - if extracting the model makes sense
 * in the respective context.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getModel
 */
public class BindException extends Exception implements Errors {

	/**
	 * Prefix for the name of the Errors instance in a model,
	 * followed by the object name.
	 */
	public static final String ERROR_KEY_PREFIX = BindException.class.getName() + ".";

	private List errors = new ArrayList();

	private BeanWrapper beanWrapper;

	private String objectName;

	private String nestedPath = "";

	/**
	 * Create a new BindException instance.
	 * @param target target object to bind onto
	 * @param name name of the target object
	 */
	public BindException(Object target, String name) {
		this.beanWrapper = new BeanWrapperImpl(target);
		this.objectName = name;
		this.nestedPath = "";
	}

	/**
	 * Return the BeanWrapper that this instance uses.
	 */
	protected BeanWrapper getBeanWrapper() {
		return beanWrapper;
	}

	/**
	 * Transform the given field into its full path,
	 * regarding the nested path of this instance.
	 */
	private String fixedField(String field) {
		return this.nestedPath + field;
	}

	/**
	 * Add a FieldError to the errors list.
	 * Intended to be used by subclasses like DataBinder.
	 */
	protected void addFieldError(FieldError fe) {
		errors.add(fe);
	}

	/**
	 * Return the wrapped target object.
	 */
	public Object getTarget() {
		return this.beanWrapper.getWrappedInstance();
	}

	public String getObjectName() {
		return objectName;
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		this.errors.add(new ObjectError(this.objectName, errorCode, errorArgs, defaultMessage));
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
		field = fixedField(field);
		Object newVal = getBeanWrapper().getPropertyValue(field);
		FieldError fe = new FieldError(this.objectName, field, newVal, errorCode, errorArgs, defaultMessage);
		this.errors.add(fe);
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}

	public int getErrorCount() {
		return this.errors.size();
	}

	public List getAllErrors() {
		return Collections.unmodifiableList(this.errors);
	}

	public boolean hasGlobalErrors() {
		return (getGlobalErrorCount() > 0);
	}

	public int getGlobalErrorCount() {
		return getGlobalErrors().size();
	}

	public List getGlobalErrors() {
		List result = new ArrayList();
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			ObjectError fe = (ObjectError) it.next();
			if (!(fe instanceof FieldError))
				result.add(fe);
		}
		return Collections.unmodifiableList(result);
	}

	public ObjectError getGlobalError() {
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			ObjectError fe = (ObjectError) it.next();
			if (!(fe instanceof FieldError))
				return fe;
		}
		return null;
	}

	public boolean hasFieldErrors(String field) {
		return (getFieldErrorCount(field) > 0);
	}

	public int getFieldErrorCount(String field) {
		return getFieldErrors(field).size();
	}

	public List getFieldErrors(String field) {
		List result = new ArrayList();
		field = fixedField(field);
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			ObjectError fe = (ObjectError) it.next();
			if (fe instanceof FieldError && field.equals(((FieldError) fe).getField()))
				result.add(fe);
		}
		return Collections.unmodifiableList(result);
	}

	public FieldError getFieldError(String field) {
		field = fixedField(field);
		for (Iterator it = errors.iterator(); it.hasNext();) {
			ObjectError fe = (ObjectError) it.next();
			if (fe instanceof FieldError && field.equals(((FieldError) fe).getField()))
				return (FieldError) fe;
		}
		return null;
	}

	public Object getFieldValue(String field) {
		field = fixedField(field);
		FieldError fe = getFieldError(field);
		if (fe == null)
			return getBeanWrapper().getPropertyValue(field);
		else
			return fe.getRejectedValue();
	}

	public void setNestedPath(String nestedPath) {
		if (nestedPath == null)
			nestedPath = "";
		if (nestedPath.length() > 0)
			nestedPath += ".";
		this.nestedPath = nestedPath;
	}

	/**
	 * Return a model Map for the contained state, exposing an Errors
	 * instance as ERROR_KEY_PREFIX + object name, and the object itself.
	 * @see #ERROR_KEY_PREFIX
	 */
	public final Map getModel() {
		Map model = new HashMap();
		// errors instance, even if no errors
		model.put(ERROR_KEY_PREFIX + this.objectName, this);
		// mapping from name to target object
		model.put(this.objectName, this.beanWrapper.getWrappedInstance());
		return model;
	}

	/**
	 * Returns diagnostic information about the errors held in this object.
	 */
	public String getMessage() {
		StringBuffer sb = new StringBuffer("BindException: " + getErrorCount() + " errors");
		Iterator it = this.errors.iterator();
		while (it.hasNext()) {
			sb.append("; " + it.next());
		}
		return sb.toString();
	}

}
