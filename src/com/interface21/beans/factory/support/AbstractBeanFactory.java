/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.beans.factory.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.BeansException;
import com.interface21.beans.FatalBeanException;
import com.interface21.beans.MethodInvocationException;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.BeanDefinitionStoreException;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.BeanFactoryAware;
import com.interface21.beans.factory.BeanIsNotAFactoryException;
import com.interface21.beans.factory.BeanNotOfRequiredTypeException;
import com.interface21.beans.factory.DisposableBean;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.HierarchicalBeanFactory;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;

/**
 * Abstract superclass that makes implementing a BeanFactory very easy.
 *
 * <p>This class uses the <b>Template Method</b> design pattern.
 * Subclasses must implement only the
 * <code>
 * getBeanDefinition(name)
 * </code>
 * method.
 *
 * <p>This class handles resolution of runtime bean references,
 * FactoryBean dereferencing, and management of collection properties.
 * It also allows for management of a bean factory hierarchy, 
 * implementing the HierarchicalBeanFactory interface.
 *
 * @author Rod Johnson
 * @since 15 April 2001
 * @version $Id$
 */
public abstract class AbstractBeanFactory implements HierarchicalBeanFactory {

	/**
	 * Used to dereference a FactoryBean and distinguish it from
	 * beans <i>created</i> by the factory. For example,
	 * if the bean named <code>myEjb</code> is a factory, getting
	 * <code>&myEjb</code> will return the factory, not the instance
	 * returned by the factory.
	 */
	public static final String FACTORY_BEAN_PREFIX = "&";


	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Parent bean factory, for bean inheritance support */
	private BeanFactory parentBeanFactory;

	/** Cache of singletons: bean name --> bean instance */
	private Map singletonCache = new HashMap();

	/** Map from alias to canonical bean name */
	private Map aliasMap = new HashMap();


	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------

	/**
	 * Create a new AbstractBeanFactory.
	 */
	public AbstractBeanFactory() {
	}

	/**
	 * Create a new AbstractBeanFactory with the given parent.
	 * @param parentBeanFactory parent bean factory, or null if none
	 * @see #getBean
	 */
	public AbstractBeanFactory(BeanFactory parentBeanFactory) {
		this.parentBeanFactory = parentBeanFactory;
	}

	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}


	//---------------------------------------------------------------------
	// Implementation of BeanFactory interface
	//---------------------------------------------------------------------

	/**
	 * Return the bean name, stripping out the factory deference prefix if necessary,
	 * and resolving aliases to canonical names.
	 */
	private String transformedBeanName(String name) {
		if (name.startsWith(FACTORY_BEAN_PREFIX)) {
			name = name.substring(FACTORY_BEAN_PREFIX.length());
		}
		// Handle aliasing
		String canonicalName = (String) this.aliasMap.get(name);
		return canonicalName != null ? canonicalName : name;
	}

	/**
	 * Return whether this name is a factory dereference (beginning
	 * with the factory dereference prefix)
	 */
	private boolean isFactoryDereference(String name) {
		return name.startsWith(FACTORY_BEAN_PREFIX);
	}

	/**
	 * Return the bean with the given name,
	 * checking the parent bean factory if not found.
	 * @param name name of the bean to retrieve
	 */
	public final Object getBean(String name) {
		return getBeanInternal(name, null);
	}

	/**
	 * Return the bean with the given name,
	 * checking the parent bean factory if not found.
	 * @param name name of the bean to retrieve
	 * @param newlyCreatedBeans cache with newly created beans (name, instance)
	 * if triggered by the creation of another bean, or null else
	 * (necessary to resolve circular references)
	 */
	private Object getBeanInternal(String name, Map newlyCreatedBeans) {
		if (name == null)
			throw new NoSuchBeanDefinitionException(null, "Cannot get bean with null name");
		try {
			AbstractBeanDefinition bd = getBeanDefinition(transformedBeanName(name));
			if (bd.isSingleton()) {
				// Check for bean instance created in the current call,
				// to be able to resolve circular references
				if (newlyCreatedBeans != null && newlyCreatedBeans.containsKey(name)) {
					return newlyCreatedBeans.get(name);
				}
				return getSharedInstance(name, newlyCreatedBeans);
			}
			else {
				return createBean(name, newlyCreatedBeans);
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// not found -> check parent
			if (this.parentBeanFactory != null)
				return this.parentBeanFactory.getBean(name);
			throw ex;
		}
	}

	public final Object getBean(String name, Class requiredType) throws BeansException {
		Object bean = getBean(name);
		Class clazz = bean.getClass();
		if (!requiredType.isAssignableFrom(clazz)) {
			throw new BeanNotOfRequiredTypeException(name, requiredType, bean);
		}
		return bean;
	}

	public boolean isSingleton(String pname) throws NoSuchBeanDefinitionException {
		String name = transformedBeanName(pname);
		try {
			return getBeanDefinition(name).isSingleton();
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Not found -> check parent
			if (this.parentBeanFactory != null)
				return this.parentBeanFactory.isSingleton(name);
			throw ex;
		}
	}

	public final String[] getAliases(String pname) {
		String name = transformedBeanName(pname);
		List aliases = new ArrayList();
		for (Iterator it = this.aliasMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue().equals(name)) {
				aliases.add(entry.getKey());
			}
		}
		return (String[]) aliases.toArray(new String[aliases.size()]);
	}


	//---------------------------------------------------------------------
	// Implementation methods
	//---------------------------------------------------------------------

	/**
	 * Get a singleton instance of this bean name. Note that this method shouldn't
	 * be called too often: Callers should keep hold of instances. Hence, the whole
	 * method is synchronized here.
	 * TODO: There probably isn't any need for this to be synchronized,
	 * at least not if we pre-instantiate singletons.
	 * @param pname name that may include factory dereference prefix
	 * @param newlyCreatedBeans cache with newly created beans (name, instance)
	 * if triggered by the creation of another bean, or null else
	 * (necessary to resolve circular references)
	 */
	private final synchronized Object getSharedInstance(String pname, Map newlyCreatedBeans) throws BeansException {
		// Get rid of the dereference prefix if there is one
		String name = transformedBeanName(pname);

		Object beanInstance = this.singletonCache.get(name);
		if (beanInstance == null) {
			logger.info("Creating shared instance of singleton bean '" + name + "'");
			beanInstance = createBean(name, newlyCreatedBeans);
			this.singletonCache.put(name, beanInstance);
		}
		else {
			if (logger.isDebugEnabled())
				logger.debug("Returning cached instance of singleton bean '" + name + "'");
		}

		// Don't let calling code try to dereference the
		// bean factory if the bean isn't a factory
		if (isFactoryDereference(pname) && !(beanInstance instanceof FactoryBean)) {
			throw new BeanIsNotAFactoryException(name, beanInstance);
		}

		// Now we have the beanInstance, which may be a normal bean
		// or a FactoryBean. If it's a FactoryBean, we use it to
		// create a bean instance, unless the caller actually wants
		// a reference to the factory.
		if (beanInstance instanceof FactoryBean) {
			if (!isFactoryDereference(pname)) {
				// Configure and return new bean instance from factory
				FactoryBean factory = (FactoryBean) beanInstance;
				logger.debug("Bean with name '" + name + "' is a factory bean");
				beanInstance = factory.getObject();

				// Set pass-through properties
				if (factory.getPropertyValues() != null) {
					logger.debug("Applying pass-through properties to bean with name '" + name + "'");
					new BeanWrapperImpl(beanInstance).setPropertyValues(factory.getPropertyValues());
				}
				// Initialization is really up to factory
				//invokeInitializerIfNecessary(beanInstance);
			}
			else {
				// The user wants the factory itself
				logger.debug("Calling code asked for BeanFactory instance for name '" + name + "'");
			}
		}	// if we're dealing with a factory bean

		return beanInstance;
	}

	/**
	 * All the other methods in this class invoke this method
	 * although beans may be cached after being instantiated by this method.
	 * All bean instantiation within this class is performed by this method.
	 * Return a BeanWrapper object for a new instance of this bean.
	 * First look up BeanDefinition for the given bean name.
	 * Uses recursion to support instance "inheritance".
	 * @param name name of the bean. Must be unique in the BeanFactory
	 * @param newlyCreatedBeans cache with newly created beans (name, instance)
	 * if triggered by the creation of another bean, or null else
	 * (necessary to resolve circular references)
	 * @return a new instance of this bean
	 */
	private Object createBean(String name, Map newlyCreatedBeans) throws BeansException {
		RootBeanDefinition mergedBeanDefinition = getMergedBeanDefinition(name);
		logger.debug("Creating instance of bean '" + name + "' with merged definition [" + mergedBeanDefinition + "]");
		BeanWrapper instanceWrapper = new BeanWrapperImpl(mergedBeanDefinition.getBeanClass());
		Object bean = instanceWrapper.getWrappedInstance();

		// Cache new instance to be able resolve circular references, but ignore
		// FactoryBean instances as they can't create objects if not initialized
		if (!(bean instanceof FactoryBean)) {
			if (newlyCreatedBeans == null) {
				newlyCreatedBeans = new HashMap();
			}
			newlyCreatedBeans.put(name, bean);
		}

		PropertyValues pvs = mergedBeanDefinition.getPropertyValues();
		applyPropertyValues(instanceWrapper, pvs, name, newlyCreatedBeans);
		callLifecycleMethodsIfNecessary(bean, name, mergedBeanDefinition, instanceWrapper);
		return bean;
	}

	/**
	 * Apply the given property values, resolving any runtime references
	 * to other beans in this bean factory.
	 * Must use deep copy, so we don't permanently modify this property
	 * @param bw BeanWrapper wrapping the target object
	 * @param pvs new property values
	 * @param name bean name passed for better exception information
	 * @param newlyCreatedBeans cache with newly created beans (name, instance)
	 * if triggered by the creation of another bean, or null else
	 * (necessary to resolve circular references)
	 */
	private void applyPropertyValues(BeanWrapper bw, PropertyValues pvs, String name, Map newlyCreatedBeans) throws BeansException {
		if (pvs == null)
			return;

		MutablePropertyValues deepCopy = new MutablePropertyValues(pvs);
		PropertyValue[] pvals = deepCopy.getPropertyValues();
		
		for (int i = 0; i < pvals.length; i++) {
			PropertyValue pv = new PropertyValue(pvals[i].getName(), resolveValueIfNecessary(bw, newlyCreatedBeans, pvals[i]));
			// Update mutable copy
			deepCopy.setPropertyValueAt(pv, i);
		}
		
		// Set our (possibly massaged) deepCopy
		try {
			bw.setPropertyValues(deepCopy);
		}
		catch (FatalBeanException ex) {
			// Improve the message by showing the context
			throw new FatalBeanException("Error setting property on bean [" + name + "]", ex);
		}
	}

	/**
	 * Given a PropertyValue, return a value, resolving any references to other
	 * beans in the factory if necessary. The value could be:
	 * <li>An ordinary object or null, in which case it's left alone
	 * <li>A RuntimeBeanReference, which must be resolved
	 * <li>A ManagedList. This is a special collection that may contain
	 * RuntimeBeanReferences that will need to be resolved.
	 * <li>A ManagedMap. In this case the value may be a reference that
	 * must be resolved.
	 * If the value is a simple object, but the property takes a Collection type,
	 * the value must be placed in a list.
	 */
	private Object resolveValueIfNecessary(BeanWrapper bw, Map newlyCreatedBeans, PropertyValue pv)
	    throws BeansException {
		Object val;
		
		// Now we must check each PropertyValue to see whether it
		// requires a runtime reference to another bean to be resolved.
		// If it does, we'll attempt to instantiate the bean and set the reference.
		if (pv.getValue() != null && (pv.getValue() instanceof RuntimeBeanReference)) {
			RuntimeBeanReference ref = (RuntimeBeanReference) pv.getValue();
			val = resolveReference(pv.getName(), ref, newlyCreatedBeans);
		}	
		else if (pv.getValue() != null && (pv.getValue() instanceof ManagedList)) {
			// Convert from managed list. This is a special container that
			// may contain runtime bean references.
			// May need to resolve references
			val = resolveManagedList(pv.getName(), (ManagedList) pv.getValue(), newlyCreatedBeans);
		}
		else if (pv.getValue() != null && (pv.getValue() instanceof ManagedMap)) {
			// Convert from managed map. This is a special container that
			// may contain runtime bean references as values.
			// May need to resolve references
			ManagedMap mm = (ManagedMap) pv.getValue();
			val = resolveManagedMap(pv.getName(), mm, newlyCreatedBeans);	
		}
		else {
			// It's an ordinary property. Just copy it.
			val = pv.getValue();
		}
		
		 // If it's an array type, we may have to massage type
		 // of collection. We'll start with ManagedList.
		 // We may also have to convert array elements from Strings
		 // TODO consider refactoring into BeanWrapperImpl?
		 if (val != null && val instanceof ManagedList && bw.getPropertyDescriptor(pv.getName()).getPropertyType().isArray()) {
			 // It's an array
			 Class arrayClass = bw.getPropertyDescriptor(pv.getName()).getPropertyType();
			 Class componentType = arrayClass.getComponentType();
			 List l = (List) val;
		
			val = managedListToArray(bw, pv, val, componentType, l);
		 }
		
		return val;
	}
	
	/**
	 * Resolve a reference to another bean in the factory.
	 * @param name included for diagnostics
	 */
	private Object resolveReference(String name, RuntimeBeanReference ref, Map newlyCreatedBeans) {
		try {
			// Try to resolve bean reference
			logger.debug("Resolving reference from bean [" + name + "] to bean [" + ref.getBeanName() + "]");
			Object bean = getBeanInternal(ref.getBeanName(), newlyCreatedBeans);
			// Create a new PropertyValue object holding the bean reference
			return bean;
		}
		catch (BeansException ex) {
			throw new FatalBeanException("Can't resolve reference to bean [" + ref.getBeanName() + "] while setting properties on bean [" + name + "]", ex);
		}
	}

	/**
	 * For each element in the ManagedMap, resolve references if necessary.
	 * Allow ManagedLists as map entries.
	 */
	private ManagedMap resolveManagedMap(String name, ManagedMap mm, Map newlyCreatedBeans) {
		Iterator keys = mm.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			Object value = mm.get(key);
			if (value instanceof RuntimeBeanReference) {
				mm.put(key, resolveReference(name, (RuntimeBeanReference) value, newlyCreatedBeans));
			}
			else if (value instanceof ManagedList) {
				// An entry may be a ManagedList, in which case we may need to
				// resolve references
				mm.put(key, resolveManagedList(name, (ManagedList) value, newlyCreatedBeans));
			}
		}	// for each key in the managed map
		return mm;
	}

	/**
	 * For each element in the ManagedList, resolve reference if necessary.
	 */
	private ManagedList resolveManagedList(String name, ManagedList l, Map newlyCreatedBeans) {
		for (int j = 0; j < l.size(); j++) {
			if (l.get(j) instanceof RuntimeBeanReference) {
				l.set(j, resolveReference(name, (RuntimeBeanReference) l.get(j), newlyCreatedBeans));
			}
		}
		return l;
	}
	
	private Object managedListToArray(BeanWrapper bw, PropertyValue pv, Object val, Class componentType, List l)
	    throws NegativeArraySizeException, BeansException, BeanDefinitionStoreException {
		try {
			Object[] arr = (Object[]) Array.newInstance(componentType, l.size());
			for (int i = 0; i < l.size(); i++) {
				// TODO hack: BWI cast
				Object newval = ((BeanWrapperImpl) bw).doTypeConversionIfNecessary(bw.getWrappedInstance(), pv.getName(), null, l.get(i), componentType);
				arr[i] = newval;
			}
			val = arr;
		}
		catch (ArrayStoreException ex) {
			throw new BeanDefinitionStoreException("Cannot convert array element from String to " + componentType, ex);
		}
		return val;
	}
	
	/**
	 * Give a bean a chance to react now all its properties are set,
	 * and a chance to know about its owning bean factory (this object).
	 * This means checking whether the bean implements InitializingBean
	 * and/or BeanFactoryAware, and invoking the necessary callback(s) if it does.
	 * @param bean new bean instance we may need to initialize
	 * @param name the bean has in the factory. Used for debug output.
	 */
	private void callLifecycleMethodsIfNecessary(Object bean, String name, RootBeanDefinition rbd, BeanWrapper bw)
	    throws BeansException {

		if (bean instanceof InitializingBean) {
			logger.debug("Calling afterPropertiesSet() on bean with name '" + name + "'");
			try {
				((InitializingBean) bean).afterPropertiesSet();
			}
			catch (Exception ex) {
				throw new FatalBeanException("afterPropertiesSet() on bean with name '" + name + "' threw an exception", ex);
			}
		}
		
		if (rbd.getInitMethodName() != null) {
			logger.debug("Calling custom init method '" + rbd.getInitMethodName() + "' on bean with name '" + name + "'");
			bw.invoke(rbd.getInitMethodName(), null);
			// Can throw MethodInvocationException
		}

		if (bean instanceof BeanFactoryAware) {
			logger.debug("Calling setBeanFactory() on BeanFactoryAware bean with name '" + name + "'");
			try {
				((BeanFactoryAware) bean).setBeanFactory(this);
			}
			catch (BeansException ex) {
				throw ex;
			}
			catch (Exception ex) {
				throw new FatalBeanException("setBeanFactory() on bean with name '" + name + "' threw an exception", ex);
			}
		}
	}

	/**
	 * Make a RootBeanDefinition, even by traversing parent if the parameter is a child definition.
	 * @return a merged RootBeanDefinition with overriden properties
	 */
	protected final RootBeanDefinition getMergedBeanDefinition(String name) throws NoSuchBeanDefinitionException {
		try {
			AbstractBeanDefinition bd = getBeanDefinition(name);
			if (bd instanceof RootBeanDefinition) {
				// Remember to take a deep copy
				return new RootBeanDefinition((RootBeanDefinition) bd);
			}
			else if (bd instanceof ChildBeanDefinition) {
				ChildBeanDefinition cbd = (ChildBeanDefinition) bd;
				// Deep copy
				RootBeanDefinition rbd = new RootBeanDefinition(getMergedBeanDefinition(cbd.getParentName()));
				// Override properties
				rbd.setPropertyValues(merge(rbd.getPropertyValues(), cbd.getPropertyValues()));
				return rbd;
			}			
		}
		catch (NoSuchBeanDefinitionException ex) {
			if (this.parentBeanFactory != null) {
				if (!(this.parentBeanFactory instanceof AbstractBeanFactory))
					throw new BeanDefinitionStoreException("Parent bean factory must be of type AbstractBeanFactory to support inheritance from a parent bean definition: " +
							"offending bean name is '" + name + "'", null);
				return ((AbstractBeanFactory) this.parentBeanFactory).getMergedBeanDefinition(name);
			}
			else {
				throw ex;
			}
		}
		throw new FatalBeanException("Shouldn't happen: BeanDefinition for '" + name + "' is neither a RootBeanDefinition or ChildBeanDefinition");
	}
	
	/**
	 * Incorporate changes from overrides param into pv base param.
	 */
	private PropertyValues merge(PropertyValues pv, PropertyValues overrides) {
		MutablePropertyValues parent = new MutablePropertyValues(pv);
		for (int i = 0; i < overrides.getPropertyValues().length; i++) {
			parent.addOrOverridePropertyValue(overrides.getPropertyValues()[i]);
		}
		return parent;
	}
	
	/**
	 * Given a bean name, create an alias. This must respect prototype/
	 * singleton behaviour. We typically use this method to support
	 * names that are illegal within XML ids (used for bean names).
	 * @param name name of the bean
	 * @param alias alias that will behave the same as the bean names
	 */
	public final void registerAlias(String name, String alias) {
		logger.debug("Creating alias '" + alias + "' for bean with name '" + name + "'");
		this.aliasMap.put(alias, name);
	}

	/**
	 * Destroy all cached singletons in this factory.
	 * To be called on shutdown of a factory.
	 */
	public final void destroySingletons() {
		logger.info("Destroying singletons in factory {" + this + "}");

		for (Iterator it = this.singletonCache.keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			Object bean = this.singletonCache.get(name);
			RootBeanDefinition bd = getMergedBeanDefinition(name);

			if (bean instanceof DisposableBean) {
				logger.debug("Calling destroy() on bean with name '" + name + "'");
				try {
					((DisposableBean) bean).destroy();
				}
				catch (Exception ex) {
					logger.error("destroy() on bean with name '" + name + "' threw an exception", ex);
				}
			}

			if (bd.getDestroyMethodName() != null) {
				logger.debug("Calling custom destroy method '" + bd.getDestroyMethodName() + "' on bean with name '" + name + "'");
				BeanWrapper bw = new BeanWrapperImpl(bean);
				try {
					bw.invoke(bd.getDestroyMethodName(), null);
				}
				catch (MethodInvocationException ex) {
					logger.error(ex.getMessage(), ex.getRootCause());
				}
			}
		}
		
		this.singletonCache.clear();
	}


	//---------------------------------------------------------------------
	// Abstract method to be implemented by concrete subclasses
	//---------------------------------------------------------------------

	/**
	 * This method must be defined by concrete subclasses to implement the
	 * <b>Template Method</b> GoF design pattern.
	 * <br>Subclasses should normally implement caching, as this method is invoked
	 * by this class every time a bean is requested.
	 * @param beanName name of the bean to find a definition for
	 * @return the BeanDefinition for this prototype name. Must never return null.
	 * @throws NoSuchBeanDefinitionException if the bean definition cannot be resolved
	 */
	protected abstract AbstractBeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

}
