/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.beans.factory.support;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeansException;
import com.interface21.beans.FatalBeanException;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.PropertyValues;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.BeanNotOfRequiredTypeException;
import com.interface21.beans.factory.InitializingBean;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;


/**
 * Abstract superclass that makes implementing a BeanFactory very easy.
 * This class uses the <b>Template Method</b> design pattern.
 * Subclasses must implement a single method,
 * <code>
 * getBeanDefinition(name)
 * </code>
 * which returns an object of an inner
 * class defined here.
 * <br/>Add event listeners through BeanDefinition objects.
 * @author  Rod Johnson
 * @since 15 April 2001
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    //---------------------------------------------------------------------
    // Instance data
    //---------------------------------------------------------------------
	/** Cache of shared instances. bean name --> bean instanced */
	private HashMap						sharedInstanceCache = new HashMap();

	/** Logger available to subclasses */
	protected final Logger				logger = Logger.getLogger(getClass().getName());

    //---------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------
    /** 
     * Creates a new AbstractBeanFactory 
     */
    public AbstractBeanFactory() {
    }

    //---------------------------------------------------------------------
    // Implementation of BeanFactory interface
    //---------------------------------------------------------------------
	/**
	 * All the other methods in this class invoke this method
	 * although beans may be cached after being instantiated by this method
	 * @param name name of the bean. Must be unique in the BeanFactory
	 * @return a new instance of this bean
	 */
    private Object createBean(String name) throws BeansException {
        Object bean = getBeanWrapperForNewInstance(name).getWrappedInstance();
		invokeInitializerIfNecessary(bean);
		return bean;
    }


	/**
	 * Get a singleton instance of this bean name. Note that this method shouldn't
	 * be called too often: callers should keep hold of instances. Hence, the whole
	 * method is synchronized here.
	 * ****TODO: there probably isn't any need for this to be
	 * synchronized, at least not if we pre-instantiate singletons
	 */
	private final synchronized Object getSharedInstance(String name) throws BeansException {
       Object o = sharedInstanceCache.get(name);
	   if (o == null) {
	   	   logger.info("Cached shared instance of Singleton bean '" + name + "'");
		   o = createBean(name);
		   sharedInstanceCache.put(name, o);
	   }
	   else {
	   	if (logger.isDebugEnabled())
		   	 logger.debug("Returning cached instance of Singleton bean '" + name + "'");
	   }
	   return o;
    }
    
    
    /**
     * Return the bean with the given name
     * @param name name of the bean to retrieve
     */
    public final Object getBean(String name) {
    	BeanDefinition bd = getBeanDefinition(name);
    	return bd.isSingleton() ? getSharedInstance(name) : createBean(name);
    }
	
	/**
	 * Return a shared instance of the given bean. Analogous to getBeanInstance(name, requiredType).
	 * @name name of the instance to return
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
		return getBeanDefinition(name).isSingleton();
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
    private BeanWrapper getBeanWrapperForNewInstance(String name) throws BeansException {
        logger.debug("getBeanWrapperForNewInstance (" + name + ")");
        BeanDefinition bd = getBeanDefinition(name);
//        bd.setBeanFactory(this);
		logger.debug("getBeanWrapperForNewInstance definition is: " + bd);
        BeanWrapper instanceWrapper = null;
        if (bd instanceof RootBeanDefinition) {
            RootBeanDefinition rbd = (RootBeanDefinition) bd;
            instanceWrapper = rbd.getBeanWrapperForNewInstance();
        }
        else if (bd instanceof ChildBeanDefinition) {
            ChildBeanDefinition ibd = (ChildBeanDefinition) bd;
            instanceWrapper = getBeanWrapperForNewInstance(ibd.getParentName());
        }
        // Set our property values
        if (instanceWrapper == null)
            throw new FatalBeanException("Internal error for definition ["  + name + "]: type of definition unknown (" + bd + ")", null);
		PropertyValues pvs = bd.getPropertyValues();
        applyPropertyValues(instanceWrapper, pvs, name);
        return instanceWrapper;
    }   // getBeanWrapperForNewInstance


	/** 
	 * Apply the given property values, resolving any runtime references
	 * to other beans in this bean factory.
	 * Must use deep copy, so we don't permanently modify this property
	 * @param bw BeanWrapper wrapping the target object
	 * @param pvs new property values
	 * @param name bean name passed for better exception information
	 */
	private void applyPropertyValues(BeanWrapper bw, PropertyValues pvs, String name) throws BeansException {
		if (pvs == null)
			return;
		
		MutablePropertyValues deepCopy = new MutablePropertyValues(pvs);
		PropertyValue[] pvals = deepCopy.getPropertyValues();

		// Now we must check each PropertyValue to see whether it
		// requires a runtime reference to another bean to be resolved.
		// If it does, we'll attempt to instantiate the bean and set the reference.
		for (int i = 0; i < pvals.length; i++) {
			if (pvals[i].getValue() != null && (pvals[i].getValue() instanceof RuntimeBeanReference)) {
				RuntimeBeanReference ref = (RuntimeBeanReference) pvals[i].getValue();
				try {
					// Try to resolve bean reference
					logger.debug("Resolving reference from bean [" + name + "] to bean [" + ref.getBeanName() + "]");
					Object bean = getBean(ref.getBeanName());
					// Create a new PropertyValue object holding the bean reference
					PropertyValue pv = new PropertyValue(pvals[i].getName(), bean);
					// Update mutable copy
					deepCopy.setPropertyValueAt(pv, i);
				}
				catch (BeansException ex) {
					throw new FatalBeanException("Can't resolve reference to bean [" + ref.getBeanName() + "] while setting properties on bean [" + name + "]", ex);
				}
			}	// if this was a runtime reference to another bean
		}	// for each property

		// Set our (possibly massaged) deepCopy
		try {
			bw.setPropertyValues(deepCopy);
		}
		catch (FatalBeanException ex) {
			// Improve the message by showing the context
			throw new FatalBeanException("Error setting property on bean [" + name + "]", ex);
		}
		//catch (FatalBeanException ex) {
			// Improve the message by showing the context
		//	throw new FatalBeanException("Error setting property on bean [" + name + "]", ex);
		//}
	}	// applyPropertyValues


	/** 
	 * Give a bean a chance to react now all its properties are set
	 * This means checking whether it's an instance of InitializingBean,
	 * and invoking the necessary callback if it is
	 * Invoked from public methods just before return.
	 * @param bean new bean instance we may need to initialize
	 */
	private void invokeInitializerIfNecessary(Object bean) throws BeansException {
		// Do nothing unless the bean implements the InitializingBean interface
		if (bean instanceof InitializingBean) {
			logger.debug("configureBeanInstance calling afterPropertiesSet on bean [" + bean + "]");
			try {
				((InitializingBean) bean).afterPropertiesSet();
			}
			catch (Exception ex) {
				logger.error("afterPropertiesSet on InitializingBean [" + bean + "] threw an exception", ex);
				throw new FatalBeanException("afterPropertiesSet on bean [" + bean + "] threw an exception", ex);
			}
		}
	}	// invokeInitializerIfNecessary


	/** 
	 * Convenience method for use by subclasses.
	 * Resolves class, even by traversing parent if child definition.
	 * @return the Class of this bean
	 * @param bd the BeanDefinition we want to check. This BeanDefinition
	 * may not actually contain the class--this method may need to navigate
	 * its ancestors to find the class.
	 */
	protected final Class getBeanClass(BeanDefinition bd) {
		if (bd instanceof RootBeanDefinition)
			return ((RootBeanDefinition) bd).getBeanClass();
		else if (bd instanceof ChildBeanDefinition) {
			ChildBeanDefinition cbd = (ChildBeanDefinition) bd;
			try {
				return getBeanClass(getBeanDefinition(cbd.getParentName()));
			}
			catch (NoSuchBeanDefinitionException ex) {
				throw new RuntimeException("Shouldn't happen: BeanDefinition store corrupted: cannot resolve parent " + cbd.getParentName());
			}
		}
		throw new RuntimeException("Shouldn't happen: BeanDefinition " + bd + " is Neither a rootBeanDefinition or a ChildBeanDefinition");
	}	// getBeanClass


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
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;



}	// class AbstractBeanFactory
