/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.beans;

import java.beans.IndexedPropertyDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.interface21.beans.propertyeditors.ClassEditor;
import com.interface21.beans.propertyeditors.PropertiesEditor;
import com.interface21.beans.propertyeditors.PropertyValuesEditor;
import com.interface21.beans.propertyeditors.StringArrayPropertyEditor;

/**
 * Default implementation of the BeanWrapper interface
 * that should be sufficient for all normal uses. Caches
 * introspection results for efficiency.
 * <p>Note: this class never tries to load a class by name, as this can
 * pose class loading problems in J2EE applications with multiple deployment
 * modules. For example, loading a class by name
 * won't work in some application servers if the class is used in a WAR but was loaded
 * by the EJB class loader and the class to be loaded is in the WAR.
 * (This class would use the EJB classloader, which couldn't see
 * the required class.) We don't attempt to solve such problems by
 * obtaining the classloader at runtime, because this violates
 * the EJB programming restrictions.
 * @author  Rod Johnson
 * @since 15 April 2001
 * @version $Revision$
 */
public class BeanWrapperImpl implements BeanWrapper {

	/** Should JavaBeans event propagation be enabled by default? */
	public static final boolean DEFAULT_EVENT_PROPAGATION_ENABLED = false;

	// Install default property editors
	static {
		PropertyEditorManager.registerEditor(String[].class, StringArrayPropertyEditor.class);
		PropertyEditorManager.registerEditor(PropertyValues.class, PropertyValuesEditor.class);
		PropertyEditorManager.registerEditor(Properties.class, PropertiesEditor.class);
		PropertyEditorManager.registerEditor(Class.class, ClassEditor.class);

		// Register all editors in our standard package
		PropertyEditorManager.setEditorSearchPath(new String[] {
			"sun.beans.editors",
			"com.interface21.beans.propertyeditors"
		});
	}


	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------

	/**
	 * We'll create a lot of these objects, so we don't want a new logger every time.
	 */
	private static Logger logger = Logger.getLogger(BeanWrapperImpl.class);

	/** The wrapped object */
	private Object object;

	/**
	 * Cached introspections results for this object, to prevent encountering the cost
	 * of JavaBeans introspection every time.
	 * */
	private CachedIntrospectionResults cachedIntrospectionResults;

	/** Standard java.beans helper object used to propagate events */
	private VetoableChangeSupport	vetoableChangeSupport;

	/** Standard java.beans helper object used to propagate events */
	private PropertyChangeSupport	propertyChangeSupport;

	/** Should we propagate events to listeners? */
	private boolean	eventPropagationEnabled = DEFAULT_EVENT_PROPAGATION_ENABLED;

	/* Map with cached nested BeanWrappers */
	private Map nestedBeanWrappers;

	/** Map with custom PropertyEditor instances */
	private Map customEditors;


	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------

	/**
	 * Creates new BeanWrapperImpl with default event propagation (disabled)
	 * @param object object wrapped by this BeanWrapper.
	 * @throws BeansException if the object cannot be wrapped by a BeanWrapper
	 */
	public BeanWrapperImpl(Object object) throws BeansException {
		this(object, DEFAULT_EVENT_PROPAGATION_ENABLED);
	}

	/**
	 * Creates new BeanWrapperImpl, allowing specification of whether event
	 * propagation is enabled.
	 * @param object object wrapped by this BeanWrapper.
	 * @param eventPropagationEnabled whether event propagation should be enabled
	 * @throws BeansException if the object cannot be wrapped by a BeanWrapper
	 */
	public BeanWrapperImpl(Object object, boolean eventPropagationEnabled) throws BeansException {
		this.eventPropagationEnabled = eventPropagationEnabled;
		setObject(object);
	}

	/** 
	 * Creates new BeanWrapperImpl, wrapping a new instance of the specified class
	 * @param clazz class to instantiate and wrap
	 * @throws BeansException if the class cannot be wrapped by a BeanWrapper
	 */
	public BeanWrapperImpl(Class clazz) throws BeansException {
		cachedIntrospectionResults = CachedIntrospectionResults.forClass(clazz);
		setObject(BeanUtils.instantiateClass(clazz));
	}

	/** 
	 * Creates new BeanWrapperImpl given the cached introspection results and
	 * the given object. Used internally only.
	 * @param cachedIntrospectionResults cached results of introspection, used
	 * for efficiency in manipulating objects of this type.
	 * @param obj object to wrap
	 * @throws BeansException if a wrapper cannot be constructed
	 */
	private BeanWrapperImpl(CachedIntrospectionResults cachedIntrospectionResults, Object obj) throws BeansException {
		this.cachedIntrospectionResults = cachedIntrospectionResults;
		setObject(obj);
	}

	/**
	 * This method is included for efficiency. If an implementation
	 * caches all necessary information about the class,
	 * it might be <b>much</b> faster to instantiate a new wrapper copying
	 * the cached information, than to use introspection again.
	 * The wrapped instance is independent, as is the new BeanWrapper:
	 * only the cached introspection information is copied.
	 * @param obj new object to be wrapped by this BeanWrapper,
	 * replacing the present target object.
	 * @throws BeansException if the target cannot be changed
	 * @return a BeanWrapper for the new object, based on cached
	 * information available to this object
	 */
	public BeanWrapper newWrapper(Object obj) throws BeansException {
		if (!this.cachedIntrospectionResults.getBeanClass().equals(obj.getClass()))
			throw new FatalBeanException("Cannot create new wrapper for object of class " 
				+ obj.getClass().getName() + " using cached information for class " 
				+ cachedIntrospectionResults.getBeanClass(), null);
		return new BeanWrapperImpl(this.cachedIntrospectionResults, obj);
	}

	/**
	 * Implementation method to switch the target object, replacing the cached introspection results
	 * only if the class of the new object is different to that of the replaced object
	 * @param object new target
	 * @throws BeansException if the object cannot be changed
	 */
	private void setObject(Object object) throws BeansException {
		if (object == null)
			throw new FatalBeanException("Cannot set BeanWrapperImpl target to a null object", null);
		this.object = object;
		if (cachedIntrospectionResults == null 
				|| !cachedIntrospectionResults.getBeanClass().equals(object.getClass())) {
			cachedIntrospectionResults = CachedIntrospectionResults.forClass(object.getClass());
		}
		setEventPropagationEnabled(this.eventPropagationEnabled);
		// assert: cachedIntrospectionResults != null
	}


	//---------------------------------------------------------------------
	// Implementation of BeanWrapper
	//---------------------------------------------------------------------

	public void setWrappedInstance(Object object) throws BeansException {
		setObject(object);
	}

	public void newWrappedInstance() throws BeansException {
		this.object = BeanUtils.instantiateClass(getWrappedClass());
		vetoableChangeSupport = new VetoableChangeSupport(object);
	}

	public Class getWrappedClass() {
		return object.getClass();
	}

	public Object getWrappedInstance() {
		return object;
	}


	public void registerCustomEditor(Class requiredType, String propertyPath, PropertyEditor propertyEditor) {
		if (propertyPath != null) {
			BeanWrapperImpl bw = getBeanWrapperForNestedProperty(propertyPath);
			bw.doRegisterCustomEditor(requiredType, getFinalPath(propertyPath), propertyEditor);
		} else {
			doRegisterCustomEditor(requiredType, propertyPath, propertyEditor);
		}
	}

	public void doRegisterCustomEditor(Class requiredType, String propertyName, PropertyEditor propertyEditor) {
		if (this.customEditors == null) {
			this.customEditors = new HashMap();
		}
		if (propertyName != null) {
			// consistency check
			PropertyDescriptor descriptor = getPropertyDescriptor(propertyName);
			if (requiredType != null && !descriptor.getPropertyType().isAssignableFrom(requiredType)) {
				throw new IllegalArgumentException("Types do not match: required=" + requiredType.getName() +
				                                   ", found=" + descriptor.getPropertyType());
			}
			this.customEditors.put(propertyName, propertyEditor);
		}
		else {
			if (requiredType == null) {
				throw new IllegalArgumentException("No propertyName and no requiredType specified");
			}
			this.customEditors.put(requiredType, propertyEditor);
		}
	}

	public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
		if (propertyPath != null) {
			BeanWrapperImpl bw = getBeanWrapperForNestedProperty(propertyPath);
			return bw.doFindCustomEditor(requiredType, getFinalPath(propertyPath));
		} else {
			return doFindCustomEditor(requiredType, propertyPath);
		}
	}

	public PropertyEditor doFindCustomEditor(Class requiredType, String propertyName) {
		if (this.customEditors == null) {
			return null;
		}
		if (propertyName != null) {
			// check property-specific editor first
			PropertyDescriptor descriptor = getPropertyDescriptor(propertyName);
			PropertyEditor editor = (PropertyEditor) this.customEditors.get(propertyName);
			if (editor != null) {
				// consistency check
				if (requiredType != null) {
					if (!descriptor.getPropertyType().isAssignableFrom(requiredType)) {
						throw new IllegalArgumentException("Types do not match: required=" + requiredType.getName() +
																							 ", found=" + descriptor.getPropertyType());
					}
				}
				return editor;
			} else {
				if (requiredType == null) {
					// try property type
					requiredType = descriptor.getPropertyType();
				}
			}
		}
		// no property-specific editor -> check type-specific editor
		return (PropertyEditor) this.customEditors.get(requiredType);
	}


	/**
	 * Convert the value to the required type (if necessary from a string),
	 * to create a PropertyChangeEvent.
	 * Conversions from String to any type use the setAsTest() method of
	 * the PropertyEditor class. Note that a PropertyEditor must be registered
	 * for this class for this to work. This is a standard Java Beans API. 
	 * A number of property editors are automatically registered by this class.
	 * @param target target bean
	 * @param propertyName name of the property
	 * @param oldValue previous value, if available. May be null.
	 * @param newValue proposed change value.
	 * @param requiredType type we must convert to
	 * @throws BeansException if there is an internal error
	 * @return a PropertyChangeEvent, containing the converted type of the new
	 * value.
	 */
	private PropertyChangeEvent createPropertyChangeEventWithTypeConversionIfNecessary(
							Object target, String propertyName, 
							Object oldValue, Object newValue, 
							Class requiredType) throws BeansException {
		// Only need to cast if value isn't null
		if (newValue != null) {
			// We may need to change the value of newValue
			PropertyEditor pe = findCustomEditor(requiredType, propertyName);
			if ((pe != null || !requiredType.isAssignableFrom(newValue.getClass())) && (newValue instanceof String)) {
				if (logger.isDebugEnabled())
					logger.debug("Convert: String to " + requiredType);
				if (pe == null) {
					pe = PropertyEditorManager.findEditor(requiredType);
				}
				if (logger.isDebugEnabled())
					logger.debug("Using property editor [" + pe + "]");
				if (pe != null) {
					try {
						pe.setAsText((String) newValue);
						newValue = pe.getValue();
					}
					catch (IllegalArgumentException ex) {
						throw new TypeMismatchException(
							new PropertyChangeEvent(target, propertyName, oldValue, newValue), requiredType);
					}
				}
			}
		}
		return new PropertyChangeEvent(target, propertyName, oldValue, newValue);
	}


	/**
	 * @see BeanWrapper#setPropertyValue(String, Object)
	 */
	public void setPropertyValue(String propertyName, Object value) throws PropertyVetoException, BeansException {
		setPropertyValue(new PropertyValue(propertyName, value));
	}


	/**
	 * Is the property nested? That is, does it contain the nested
	 * property separator (usually .)
	 * @param path property path
	 * @return boolean is the property nested
	 */
	private boolean isNestedProperty(String path) {
		return path.indexOf(NESTED_PROPERTY_SEPARATOR) != -1;
	}

	/**
	 * Get the last component of the path. Also works if not nested.
	 * @param nestedPath property path we know is nested
	 * @return last component of the path (the property on the target bean)
	 */
	private String getFinalPath(String nestedPath) {
		return nestedPath.substring(nestedPath.lastIndexOf(NESTED_PROPERTY_SEPARATOR) + 1);
	}

	/**
	 * Recursively navigate to return a BeanWrapper for the nested path.
	 * @param path property path, which may be nested
	 * @return a BeanWrapper for the target bean
	 */
	private BeanWrapperImpl getBeanWrapperForNestedProperty(String path) {
		int pos = path.indexOf(NESTED_PROPERTY_SEPARATOR);
		// Handle nested properties recursively
		if (pos > -1) {
			String nestedProperty = path.substring(0, pos);
			String nestedPath = path.substring(pos + 1);
			logger.debug("Navigating to property path '" + nestedPath + "' of nested property '" + nestedProperty + "'");
			BeanWrapperImpl nestedBw = getNestedBeanWrapper(nestedProperty);
			return nestedBw.getBeanWrapperForNestedProperty(nestedPath);
		}
		else {
			return this;
		}
	}

	/**
	 * Retrieve a BeanWrapper for the given nested property.
	 * Create a new one if not found in the cache.
	 * <p>Note: Caching nested BeanWrappers is necessary now,
	 * to keep registered custom editors for nested properties.
	 * @param nestedProperty property to create the BeanWrapper for
	 * @return the BeanWrapper instance, either cached or newly created
	 */
	private BeanWrapperImpl getNestedBeanWrapper(String nestedProperty) {
		if (this.nestedBeanWrappers == null) {
			this.nestedBeanWrappers = new HashMap();
		}
		// get value of bean property
		Object propertyValue = getPropertyValue(nestedProperty);
		if (propertyValue == null) {
			throw new NullValueInNestedPathException(getWrappedClass(), nestedProperty);
		}
		// lookup cached sub-BeanWrapper, create new one if not found
		BeanWrapperImpl nestedBw = (BeanWrapperImpl) this.nestedBeanWrappers.get(propertyValue);
		if (nestedBw == null) {
			logger.debug("Creating new nested BeanWrapper for property '" + nestedProperty + "'");
			nestedBw = new BeanWrapperImpl(propertyValue, false);
			this.nestedBeanWrappers.put(propertyValue, nestedBw);
		} else {
			logger.debug("Using cached nested BeanWrapper for property '" + nestedProperty + "'");
		}
		return nestedBw;
	}

	/**
	 * Set an individual field.
	 * All other setters go through this.
	 * @param pv property value to use for update
	 * @throws PropertyVetoException if a listeners throws a JavaBeans API veto
	 * @throws BeansException if there's a low-level, fatal error
	 */
	public void setPropertyValue(PropertyValue pv) throws PropertyVetoException, BeansException {

		if (isNestedProperty(pv.getName())) {
			try {
				BeanWrapper nestedBw = getBeanWrapperForNestedProperty(pv.getName());
				nestedBw.setPropertyValue(new PropertyValue(getFinalPath(pv.getName()), pv.getValue()));
				return;
			}
			catch (NullValueInNestedPathException ex) {
				// Let this through
				throw ex;
			}
			catch (FatalBeanException ex) {
				// Error in the nested path
				throw new NotWritablePropertyException(pv.getName(), getWrappedClass());
			}
		}

		if (!isWritableProperty(pv.getName()))
			throw new NotWritablePropertyException(pv.getName(), getWrappedClass());

		PropertyDescriptor pd = getPropertyDescriptor(pv.getName());
		Method writeMethod = pd.getWriteMethod();
		Method readMethod = pd.getReadMethod();
		Object oldValue = null;	// May stay null if it's not a readable property
		PropertyChangeEvent propertyChangeEvent = null;

		try {
			if (readMethod != null && eventPropagationEnabled) {
				// Can only find existing value if it's a readable property
				try {
					oldValue = readMethod.invoke(object, new Object[] { });
				}
				catch (Exception ex) {
					// The getter threw an exception, so we couldn't retrieve the old value.
					// We're not really interested in any exceptions at this point,
					// so we merely log the problem and leave oldValue null
					logger.warn("Failed to invoke getter '" + readMethod.getName()
						+ "' to get old property value before property change: getter probably threw an exception",
						ex);
				}
			}

			// Old value may still be null
			propertyChangeEvent = createPropertyChangeEventWithTypeConversionIfNecessary(
									object, pv.getName(), oldValue, pv.getValue(), pd.getPropertyType());

			// May throw PropertyVetoException: if this happens the PropertyChangeSupport
			// class fires a reversion event, and we jump out of this method, meaning
			// the change was never actually made
			if (eventPropagationEnabled) {
				vetoableChangeSupport.fireVetoableChange(propertyChangeEvent);
			}

			// Make the change
			if (logger.isDebugEnabled())
				logger.debug("About to invoke write method ["
							+ writeMethod + "] on object of class '" + object.getClass().getName() + "'");
			writeMethod.invoke(object, new Object[] { propertyChangeEvent.getNewValue() });
			if (logger.isDebugEnabled())
				logger.debug("Invoked write method [" + writeMethod + "] ok");

			// If we get here we've changed the property OK and can broadcast it
			if (eventPropagationEnabled)
				propertyChangeSupport.firePropertyChange(propertyChangeEvent);
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof PropertyVetoException)
				throw (PropertyVetoException) ex.getTargetException();
			if (ex.getTargetException() instanceof ClassCastException)
				throw new TypeMismatchException(propertyChangeEvent, pd.getPropertyType(), ex);
			throw new MethodInvocationException(ex.getTargetException(), propertyChangeEvent);
		}
		catch (IllegalAccessException ex) {
			throw new FatalBeanException("illegal attempt to set property [" + pv + "] threw exception", ex);
		}
		catch (IllegalArgumentException ex) {
			throw new TypeMismatchException(propertyChangeEvent, pd.getPropertyType(), ex);
		}
	}


	/**
	 * Bulk update from a Map.
	 * Bulk updates from PropertyValues are more powerful: this method is
	 * provided for convenience.
	 * @param map map containing properties to set, as name-value pairs.
	 * The map may include nested properties.
	 * @throws BeansException if there's a fatal, low-level exception
	 */
	public void setPropertyValues(Map map) throws BeansException {
		setPropertyValues(new MutablePropertyValues(map));
	}


	/**
	 * @see BeanWrapper#setPropertyValues(PropertyValues)
	 */
	public void setPropertyValues(PropertyValues pvs) throws BeansException {
		setPropertyValues(pvs, false, null);
	}


	/**
	 * @see BeanWrapper#setPropertyValues(PropertyValues, boolean, PropertyValuesValidator)
	 */
	public void setPropertyValues(PropertyValues propertyValues, 
					boolean ignoreUnknown, PropertyValuesValidator pvsValidator) throws BeansException {
		// Create only if needed
		PropertyVetoExceptionsException propertyVetoExceptionsException = new PropertyVetoExceptionsException(this);

		if (pvsValidator != null) {
			try {
				pvsValidator.validatePropertyValues(propertyValues);
			}
			catch (InvalidPropertyValuesException ipvex) {
				propertyVetoExceptionsException.addMissingFields(ipvex);
			}
		}

		PropertyValue[] pvs = propertyValues.getPropertyValues();
		for (int i = 0; i < pvs.length; i++) {
			try {
				// This method may throw ReflectionException, which won't be caught
				// here, if there is a critical failure such as no matching field.
				// We can attempt to deal only with less serious exceptions
				setPropertyValue(pvs[i]);
			}
			// Fatal ReflectionExceptions will just be rethrown
			catch (NotWritablePropertyException ex) {
				if (!ignoreUnknown)
					throw ex;
				// Otherwise, just ignore it and continue...
			}
			catch (PropertyVetoException ex) {
				propertyVetoExceptionsException.addPropertyVetoException(ex);
			}
			catch (TypeMismatchException ex) {
				propertyVetoExceptionsException.addTypeMismatchException(ex);
			}
			catch (MethodInvocationException ex) {
				propertyVetoExceptionsException.addMethodInvocationException(ex);
			}
		}   // for each property

		// If we encountered individual exceptions, throw the composite exception
		if (propertyVetoExceptionsException.getExceptionCount() > 0) {
			throw propertyVetoExceptionsException;
		}
	}


	/**
	 * @see BeanWrapper#getPropertyValue(String)
	 */
	public Object getPropertyValue(String propertyName) throws BeansException {
		if (isNestedProperty(propertyName)) {
			BeanWrapper nestedBw = getBeanWrapperForNestedProperty(propertyName);
			logger.debug("Final path in nested property value '" + propertyName + "' is '" 
					+ getFinalPath(propertyName) + "'");
			return nestedBw.getPropertyValue(getFinalPath(propertyName));
		}

		PropertyDescriptor pd = getPropertyDescriptor(propertyName);
		Method m = pd.getReadMethod();
		if (m == null)
			throw new FatalBeanException("Cannot get scalar property [" + propertyName + "]: not readable", null);
		try {
			return m.invoke(object, null);
		}
		catch (InvocationTargetException ex) {
			throw new FatalBeanException("getter for property [" + propertyName + "] threw exception", ex);
		}
		catch (IllegalAccessException ex) {
			throw new FatalBeanException("illegal attempt to get property [" + propertyName + "] threw exception", ex);
		}
	}

	/** 
	 * Get the value of an indexed property
	 * @param propertyName name of the property to get value of
	 * @param index index from 0 of the property
	 * @return the value of the property
	 * @throws BeansException if there's a fatal exception
	 */
	public Object getIndexedPropertyValue(String propertyName, int index) throws BeansException {
		PropertyDescriptor pd = getPropertyDescriptor(propertyName);
		if (!(pd instanceof IndexedPropertyDescriptor))
			throw new FatalBeanException("Cannot get indexed property value for [" + propertyName 
					+ "]: this property is not an indexed property", null);
		Method m = ((IndexedPropertyDescriptor) pd).getIndexedReadMethod();
		if (m == null)
			throw new FatalBeanException("Cannot get indexed property [" + propertyName 
					+ "]: not readable", null);
		try {
			return m.invoke(object, new Object[] { new Integer(index) });
		}
		catch (InvocationTargetException ex) {
			throw new FatalBeanException("getter for indexed property [" + propertyName + "] threw exception", ex);
		}
		catch (IllegalAccessException ex) {
			throw new FatalBeanException("illegal attempt to get indexed property [" 
					+ propertyName + "] threw exception", ex);
		}
	}

	/**
	 * Method getProperties.
	 * @return PropertyDescriptor[] property descriptor for the wrapped target
	 * @throws BeansException if property descriptors cannot be obtained
	 */
	public PropertyDescriptor[] getProperties() throws BeansException {
		return cachedIntrospectionResults.getBeanInfo().getPropertyDescriptors();
	}


	/**
	 * @see BeanWrapper#getPropertyDescriptor(String)
	 */
	public PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException {
		return cachedIntrospectionResults.getPropertyDescriptor(propertyName);
	}

	/**
	 * @see BeanWrapper#isReadableProperty(String)
	 */
	public boolean isReadableProperty(String propertyName) {
		try {
			return getPropertyDescriptor(propertyName).getReadMethod() != null;
		}
		catch (BeansException ex) {
			// Doesn't exist, so can't be readable
			return false;
		}
	}

	/**
	 * @see BeanWrapper#isWritableProperty(String)
	 */
	public boolean isWritableProperty(String propertyName) {
		try {
			return getPropertyDescriptor(propertyName).getWriteMethod() != null;
		}
		catch (BeansException ex) {
			// Doesn't exist, so can't be writable
			return false;
		}
	}

	/**
	 * Invoke a method
	 * @see BeanWrapper#invoke(String, Object[])
	 */
	public Object invoke(String methodName, Object[] args) throws BeansException {
		try {
			MethodDescriptor md = this.cachedIntrospectionResults.getMethodDescriptor(methodName);
			if (logger.isDebugEnabled())
				logger.debug("About to invoke method [" + methodName + "]");
			Object returnVal = md.getMethod().invoke(this.object, args);
			if (logger.isDebugEnabled())
				logger.debug("Successfully invoked method [" + methodName + "]");
			return returnVal;
		}
		catch (InvocationTargetException ex) {
			//if (ex.getTargetException() instanceof ClassCastException)
			//	throw new TypeMismatchException(propertyChangeEvent, pd.getPropertyType(), ex);
			throw new MethodInvocationException(ex.getTargetException(), methodName);
		}
		catch (IllegalAccessException ex) {
			throw new FatalBeanException("Illegal attempt to invoke method [" + methodName + "] threw exception", ex);
		}
		catch (IllegalArgumentException ex) {
			throw new FatalBeanException("Illegal argument to method [" + methodName + "] threw exception", ex);
		}
	}

	/**
	 * @see BeanWrapper#getPropertyDescriptors()
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		return cachedIntrospectionResults.getBeanInfo().getPropertyDescriptors();
	}

	//---------------------------------------------------------------------
	// Bean event support
	//---------------------------------------------------------------------
	/**
	 * @see BeanWrapper#addVetoableChangeListener(VetoableChangeListener)
	 */
	public void addVetoableChangeListener(VetoableChangeListener l) {
		if (eventPropagationEnabled)
			vetoableChangeSupport.addVetoableChangeListener(l);
	}

	/**
	 * @see BeanWrapper#removeVetoableChangeListener(VetoableChangeListener)
	 */
	public void removeVetoableChangeListener(VetoableChangeListener l) {
		if (eventPropagationEnabled)
			vetoableChangeSupport.removeVetoableChangeListener(l);
	}

	/**
	 * @see BeanWrapper#addVetoableChangeListener(String, VetoableChangeListener)
	 */
	public void addVetoableChangeListener(String propertyName, VetoableChangeListener l) {
		if (eventPropagationEnabled)
			vetoableChangeSupport.addVetoableChangeListener(propertyName, l);
	}

	/**
	 * @see BeanWrapper#removeVetoableChangeListener(String, VetoableChangeListener)
	 */
	public void removeVetoableChangeListener(String propertyName, VetoableChangeListener l) {
		if (eventPropagationEnabled)
			vetoableChangeSupport.removeVetoableChangeListener(propertyName, l);
	}

	/**
	 * @see BeanWrapper#addPropertyChangeListener(PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (eventPropagationEnabled)
			propertyChangeSupport.addPropertyChangeListener(l);
	}

	/**
	 * @see BeanWrapper#removePropertyChangeListener(PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		if (eventPropagationEnabled)
			propertyChangeSupport.removePropertyChangeListener(l);
	}

	/**
	 * @see BeanWrapper#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
		if (eventPropagationEnabled)
			propertyChangeSupport.addPropertyChangeListener(propertyName, l);
	}

	/**
	 * @see BeanWrapper#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
		if (eventPropagationEnabled)
			propertyChangeSupport.removePropertyChangeListener(propertyName, l);
	}

	/**
	 * @see BeanWrapper#isEventPropagationEnabled()
	 */
	public boolean isEventPropagationEnabled() {
		return eventPropagationEnabled;
	}

	/**
	 * Disabling event propagation improves
	 * performance
	 * @param flag whether event propagation should be enabled.
	 */
	public void setEventPropagationEnabled(boolean flag) {
		this.eventPropagationEnabled = flag;
		// Lazily initialize support for events if not already initialized
		if (eventPropagationEnabled && (vetoableChangeSupport == null || propertyChangeSupport == null)) {
			vetoableChangeSupport = new VetoableChangeSupport(object);
			propertyChangeSupport = new PropertyChangeSupport(object);
		}
	}


	//---------------------------------------------------------------------
	// Diagnostics
	//---------------------------------------------------------------------
	/**
	 * This method is expensive! Only call for diagnostics and debugging reasons,
	 * not in production
	 * @return a string describing the state of this object
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("BeanWrapperImpl: eventPropagationEnabled=" + eventPropagationEnabled 
				+ " wrapping [" + getWrappedInstance().getClass() + "]; ");
			PropertyDescriptor pds[] = getPropertyDescriptors();
			if (pds != null) {
				for (int i = 0; i < pds.length; i++) {
					Object val = getPropertyValue(pds[i].getName());
					String valStr = (val != null) ? val.toString() : "null";
					sb.append(pds[i].getName() + "={" + valStr + "}");
				}
			}
		}
		catch (Exception ex) {
			sb.append("exception encountered: " + ex);
		}
		return sb.toString();
	}   // toString

}	// class BeanWrapperImpl