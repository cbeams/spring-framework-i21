package com.interface21.validation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.VetoableChangeListener;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.ErrorCodedPropertyVetoException;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.PropertyVetoExceptionsException;
import com.interface21.util.ObjectArrayUtils;

/**
 * Binder that allows for binding property values to a target object.
 * Slightly unusual, as it _is_ an exception.
 * Supports property change listeners and vetoable change listeners.
 * @author Rod Johnson
 * @version 1.0
 */
public class DataBinder extends BindException {

	public static final String MISSING_FIELD_ERROR_CODE = "required";

	protected static final Log logger = LogFactory.getLog(DataBinder.class);

	private String[] requiredFields;

	public DataBinder(Object target, String objName) {
		super(target, objName);
	}

	public void setRequiredFields(String[] requiredFields) {
		this.requiredFields = requiredFields;
	}

	protected String[] getRequiredFields() {
		return requiredFields;
	}

	public void addVetoableChangeListener(VetoableChangeListener vcl) {
		getBeanWrapper().setEventPropagationEnabled(true);
		getBeanWrapper().addVetoableChangeListener(vcl);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		getBeanWrapper().setEventPropagationEnabled(true);
		getBeanWrapper().addPropertyChangeListener(pcl);
	}

	/**
	 * Register the given custom property editor for the given type and
	 * field, or for all fields of the given type.
	 * @param requiredType type of the property, can be null if a field is
	 * given but should be specified in any case for consistency checking
	 * @param field name of the field (can also be a nested path), or
	 * null if registering an editor for all fields of the given type
	 * @param propertyEditor editor to register
	 */
	public void registerCustomEditor(Class requiredType, String field, PropertyEditor propertyEditor) {
		getBeanWrapper().registerCustomEditor(requiredType, field, propertyEditor);
	}

	/**
	 * Register the given custom property editor for all properties
	 * of the given type.
	 * @param requiredType type of the property
	 * @param propertyEditor editor to register
	 */
	public void registerCustomEditor(Class requiredType, PropertyEditor propertyEditor) {
		registerCustomEditor(requiredType, null, propertyEditor);
	}

	/**
	 * Render the field value with a custom editor, if applicable.
	 */
	public Object getFieldValue(String field) {
		Object value = super.getFieldValue(field);
		if (value != null && !hasFieldErrors(field)) {
			PropertyEditor customEditor = getBeanWrapper().findCustomEditor(null, field);
			if (customEditor != null) {
				customEditor.setValue(value);
				return customEditor.getAsText();
			}
		}
		return value;
	}

	/**
	 * Bind the given property values to this binder's target.
	 * This call can create field errors, representing basic binding
	 * errors like a required field (code "required"), or type mismatch
	 * between value and bean property (code "typeMismatch").
	 * @param pvs property values to bind
	 */
	public void bind(PropertyValues pvs) {
		// Check for missing fields
		if (requiredFields != null) {
			for (int i = 0; i < requiredFields.length; i++) {
				PropertyValue pv = pvs.getPropertyValue(requiredFields[i]);
				if (pv == null || "".equals(pv.getValue())) {
					logger.debug("Required field '" + requiredFields[i] + "' is missing or empty");
					// create field error with code "required"
					addFieldError(new FieldError(getObjectName(), requiredFields[i], "", MISSING_FIELD_ERROR_CODE, ObjectArrayUtils.toArray(requiredFields[i]), "Field '" + requiredFields[i] + "' is required"));
				}
			}
		}
		try {
			// Bind request parameters onto params
			// We ignore unknown properties
			getBeanWrapper().setPropertyValues(pvs, true, null);
		}
		catch (PropertyVetoExceptionsException ex) {
			ErrorCodedPropertyVetoException[] exs = ex.getPropertyVetoExceptions();
			for (int i = 0; i < exs.length; i++) {
				// create field with the exceptions's code, e.g. "typeMismatch"
				addFieldError(new FieldError(getObjectName(), exs[i].getPropertyChangeEvent().getPropertyName(), exs[i].getPropertyChangeEvent().getNewValue(), exs[i].getErrorCode(), null, exs[i].getLocalizedMessage()));
			}
		}
	}

	/**
	 * Close this DataBinder, which may result in throwing
	 * a BindException if it encountered any errors
	 * @throws BindException if there were any errors in the bind operation
	 */
	public Map close() throws BindException {
		if (hasErrors()) {
			throw this;
		}
		return getModel();
	}

}
