package com.interface21.validation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.VetoableChangeListener;
import java.util.Map;

import com.interface21.beans.ErrorCodedPropertyVetoException;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.PropertyVetoExceptionsException;

/**
 * Binder that allows for binding property values to a target object.
 * Supports property change listeners and vetoable change listeners.
 * @author Rod Johnson
 */
public class DataBinder extends BindException {

	public static final String MISSING_FIELD_ERROR_CODE = "required";

	private String[] requiredFields;

	/**
	 * Create a new DataBinder instance.
	 * @param target target object to bind onto
	 * @param name name of the target object
	 */
	public DataBinder(Object target, String name) {
		super(target, name);
	}

	/**
	 * Register fields that are required for each binding process.
	 * @param requiredFields array of field names
	 */
	public void setRequiredFields(String[] requiredFields) {
		this.requiredFields = requiredFields;
	}

	/**
	 * Return the fields that are required for each binding process.
	 * @return array of field names
	 */
	protected String[] getRequiredFields() {
		return requiredFields;
	}

	/**
	 * Add a VetoableChangeListener that will be notified of property updates.
	 */
	public void addVetoableChangeListener(VetoableChangeListener vcl) {
		getBeanWrapper().setEventPropagationEnabled(true);
		getBeanWrapper().addVetoableChangeListener(vcl);
	}

	/**
	 * Add a PropertyChangeListener that will be notified of property updates.
	 */
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
	 * Render the field value with the appropriate custom editor,
	 * if the type of the value matches the one of the field
	 * (i.e. if not dealing with a reported type mismatch error).
	 */
	public Object getFieldValue(String field) {
		Object value = super.getFieldValue(field);
		Class type = getBeanWrapper().getPropertyDescriptor(field).getPropertyType();
		// just treat values of the actual type, ignoring type mismatch values
		if (type.isInstance(value)) {
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
		// check for missing fields
		if (this.requiredFields != null) {
			for (int i = 0; i < this.requiredFields.length; i++) {
				PropertyValue pv = pvs.getPropertyValue(this.requiredFields[i]);
				if (pv == null || "".equals(pv.getValue())) {
					// create field error with code "required"
					addFieldError(new FieldError(getObjectName(), this.requiredFields[i], "", MISSING_FIELD_ERROR_CODE, new Object[] {requiredFields[i]}, "Field '" + requiredFields[i] + "' is required"));
				}
			}
		}
		try {
			// bind request parameters onto params, ignoring unknown properties
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
