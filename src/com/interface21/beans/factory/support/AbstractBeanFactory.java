/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.beans.factory.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.BeansException;
import com.interface21.beans.FatalBeanException;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.BeanIsNotAFactoryException;
import com.interface21.beans.factory.BeanNotOfRequiredTypeException;
import com.interface21.beans.factory.FactoryBean;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.beans.factory.Lifecycle;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;

/**
 * Abstract superclass that makes implementing a BeanFactory very easy.
 * This class uses the <b>Template Method</b> design pattern.
 * Subclasses must implement only the
 * <code>
 * getBeanDefinition(name)
 * </code>
 * method.
 * This class handles resolution of runtime bean references,
 * FactoryBean dereferencing, and management of collection properties.
 * @author  Rod Johnson
 * @since 15 April 2001
 * @version $Id$
 */
public abstract class AbstractBeanFactory implements BeanFactory {

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

	/** parent bean factory, for bean inheritance support */
	private BeanFactory parentBeanFactory;

	/** Cache of shared instances. bean name --> bean instanced */
	private Map sharedInstanceCache = new HashMap();

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** name of default parent bean */
	protected String defaultParentBean;
	
	/** Map from alias to canonical bean name */
	private Map aliasMap = new HashMap();


	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------

	/**
	 * Creates a new AbstractBeanFactory
	 */
	public AbstractBeanFactory() {
	}

	/**
	 * Creates a new AbstractBeanFactory, with the given parent.
	 * @param parentBeanFactory  the parent bean factory, or null if none
	 * @see #getBean
	 */
	public AbstractBeanFactory(BeanFactory parentBeanFactory) {
		this.parentBeanFactory = parentBeanFactory;
	}

	/**
	 * Returns the parent bean factory, or null if none.
	 */
	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}


	//---------------------------------------------------------------------
	// Implementation of BeanFactory interface
	//---------------------------------------------------------------------

	/**
	 * All the other methods in this class invoke this method
	 * although beans may be cached after being instantiated by this method
	 * @param name name of the bean. Must be unique in the BeanFactory
	 * @param newlyCreatedBeans cache with newly created beans (name, instance)
	 * if triggered by the creation of another bean, or null else
	 * (necessary to resolve circular references)
	 * @return a new instance of this bean
	 */
	private Object createBean(String name, Map newlyCreatedBeans) throws BeansException {
		if (newlyCreatedBeans == null) {
			newlyCreatedBeans = new HashMap();
		}
		Object bean = getBeanWrapperForNewInstance(name, newlyCreatedBeans).getWrappedInstance();
		callLifecycleMethodsIfNecessary(bean, name);
		return bean;
	}

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
	 * Get a singleton instance of this bean name. Note that this method shouldn't
	 * be called too often: callers should keep hold of instances. Hence, the whole
	 * method is synchronized here.
	 * TODO: there probably isn't any need for this to be
	 * synchronized, at least not if we pre-instantiate singletons.
	 * @param pname name that may include factory dereference prefix
	 * @param newlyCreatedBeans cache with newly created beans (name, instance)
	 * if triggered by the creation of another bean, or null else
	 * (necessary to resolve circular references)
	 */
	private final synchronized Object getSharedInstance(String pname, Map newlyCreatedBeans) throws BeansException {
		// Get rid of the dereference prefix if there is one
		String name = transformedBeanName(pname);

		Object beanInstance = this.sharedInstanceCache.get(name);
		if (beanInstance == null) {
			logger.info("Cached shared instance of Singleton bean '" + name + "'");
			if (newlyCreatedBeans == null) {
				newlyCreatedBeans = new HashMap();
			}
			beanInstance = createBean(name, newlyCreatedBeans);
			this.sharedInstanceCache.put(name, beanInstance);
		}
		else {
			if (logger.isDebugEnabled())
				logger.debug("Returning cached instance of Singleton bean '" + name + "'");
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
			throw new NoSuchBeanDefinitionException(null);
		if (newlyCreatedBeans != null && newlyCreatedBeans.containsKey(name)) {
			return newlyCreatedBeans.get(name);
		}
		try {
			AbstractBeanDefinition bd = getBeanDefinition(transformedBeanName(name));
			return bd.isSingleton() ? getSharedInstance(name, newlyCreatedBeans) : createBean(name, newlyCreatedBeans);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// not found -> check parent
			if (this.parentBeanFactory != null)
				return this.parentBeanFactory.getBean(name);
			throw ex;
		}
	}

	/**
	 * Return a shared instance of the given bean. Analogous to getBeanInstance(name, requiredType).
	 * @param name name of the instance to return
	 * @param requiredType type the bean must match
	 * @return a shared instance of the given bean
	 * @throws BeanNotOfRequiredTypeException if the bean  not of the required type
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 */
	public final Object getBean(String name, Class requiredType) throws BeansException {
		Object bean = getBean(name);
		Class clazz = bean.getClass();
		if (!requiredType.isAssignableFrom(clazz))
			throw new BeanNotOfRequiredTypeException(name, requiredType, bean);
		return bean;
	}

	/**
	 * @see BeanFactory#isSingleton(String)
	 */
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		try {
			return getBeanDefinition(name).isSingleton();
		}
		catch (NoSuchBeanDefinitionException ex) {
			// not found -> check parent
			if (this.parentBeanFactory != null)
				return this.parentBeanFactory.isSingleton(name);
			throw ex;
		}
	}


	//---------------------------------------------------------------------
	// Implementation methods
	//---------------------------------------------------------------------

	/**
	 * All bean instantiation within this class is performed by this method.
	 * Return a BeanWrapper object for a new instance of this bean.
	 * First look up BeanDefinition for the given bean name.
	 * Uses recursion to support instance "inheritance".
	 */
	private BeanWrapper getBeanWrapperForNewInstance(String name, Map newlyCreatedBeans) throws BeansException {
		logger.debug("getBeanWrapperForNewInstance (" + name + ")");
		AbstractBeanDefinition bd = getBeanDefinition(name);
		logger.debug("getBeanWrapperForNewInstance definition is: " + bd);
		BeanWrapper instanceWrapper = null;
		if (bd instanceof RootBeanDefinition) {
			RootBeanDefinition rbd = (RootBeanDefinition) bd;
			instanceWrapper = rbd.getBeanWrapperForNewInstance();
		}
		else if (bd instanceof ChildBeanDefinition) {
			ChildBeanDefinition ibd = (ChildBeanDefinition) bd;
			instanceWrapper = getBeanWrapperForNewInstance(ibd.getParentName(), newlyCreatedBeans);
		}
		// Set our property values
		if (instanceWrapper == null)
			throw new FatalBeanException("Internal error for definition [" + name + "]: type of definition unknown (" + bd + ")", null);
		// cache new instance to be able resolve circular references
		newlyCreatedBeans.put(name, instanceWrapper.getWrappedInstance());
		PropertyValues pvs = bd.getPropertyValues();
		applyPropertyValues(instanceWrapper, pvs, name, newlyCreatedBeans);
		return instanceWrapper;
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
			ManagedList l = (ManagedList) pv.getValue();
			for (int j = 0; j < l.size(); j++) {
				if (l.get(j) instanceof RuntimeBeanReference) {
					l.set(j, resolveReference(pv.getName(), (RuntimeBeanReference) l.get(j), newlyCreatedBeans));
				}
			}
			val = l;
		}
		else if (pv.getValue() != null && (pv.getValue() instanceof ManagedMap)) {
			// Convert from managed map. This is a special container that
			// may contain runtime bean references as values.
			// May need to resolve references
			ManagedMap mm = (ManagedMap) pv.getValue();
			Iterator keys = mm.keySet().iterator();
			while (keys.hasNext()) {
				Object key = keys.next();
				Object value = mm.get(key);
				if (value instanceof RuntimeBeanReference) {
					mm.put(key, resolveReference(pv.getName(), (RuntimeBeanReference) value, newlyCreatedBeans));
				}
			}	// for each key in the managed map
			val = mm;
		}
		else {
			// It's an ordinary property. Just copy it.
			val = pv.getValue();
		}
		
		return val;
	}
	
	/**
	 * Resolve a reference to another bean in the factory
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
	 * Give a bean a chance to react now all its properties are set,
	 * and a chance to know about its owning bean factory (this object).
	 * This means checking whether the bean implements InitializingBean
	 * and/or Lifecycle, and invoking the necessary callback(s) if it does.
	 * @param bean new bean instance we may need to initialize
	 * @param name the bean has in the factory. Used for debug output.
	 */
	private void callLifecycleMethodsIfNecessary(Object bean, String name) throws BeansException {
		if (bean instanceof InitializingBean) {
			logger.debug("configureBeanInstance calling afterPropertiesSet on bean with name '" + name + "'");
			try {
				((InitializingBean) bean).afterPropertiesSet();
			}
			catch (BeansException ex) {
				throw ex;
			}
			catch (Exception ex) {
				throw new FatalBeanException("afterPropertiesSet on with name '" + name + "' threw an exception", ex);
			}
		}

		if (bean instanceof Lifecycle) {
			logger.debug("configureBeanInstance calling setBeanFactory() on Lifecycle bean with name '" + name + "'");
			try {
				((Lifecycle) bean).setBeanFactory(this);
			}
			catch (BeansException ex) {
				throw ex;
			}
			catch (Exception ex) {
				throw new FatalBeanException("Lifecycle method on bean with name '" + name + "' threw an exception", ex);
			}
		}
	}

	/**
	 * Convenience method for use by subclasses.
	 * Resolves class, even by traversing parent if child definition.
	 * @return the Class of this bean
	 * @param bd the BeanDefinition we want to check. This BeanDefinition
	 * may not actually contain the class--this method may need to navigate
	 * its ancestors to find the class.
	 */
	protected final Class getBeanClass(AbstractBeanDefinition bd) {
		if (bd instanceof RootBeanDefinition)
			return ((RootBeanDefinition) bd).getBeanClass();
		else if (bd instanceof ChildBeanDefinition) {
			ChildBeanDefinition cbd = (ChildBeanDefinition) bd;
			try {
				return getBeanClass(getBeanDefinition(cbd.getParentName()));
			}
			catch (NoSuchBeanDefinitionException ex) {
				throw new FatalBeanException("Shouldn't happen: BeanDefinition store corrupted: cannot resolve parent " + cbd.getParentName());
			}
		}
		throw new FatalBeanException("Shouldn't happen: BeanDefinition " + bd + " is Neither a rootBeanDefinition or a ChildBeanDefinition");
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

	public final String[] getAliases(String name) {
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
