/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.beans.factory.support;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.interface21.beans.BeansException;
import com.interface21.beans.FatalBeanException;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.util.StringUtils;

/**
 * Concrete implementation of ListableBeanFactory.
 * Includes convenient methods to populate the factory from a Map
 * and a ResourceBundle, and to add bean defintions one by one.
 *
 * <p>Can be used as a standalone bean factory,
 * or as a superclass for custom bean factories.
 *
 * @author Rod Johnson
 * @since 16 April 2001
 * @version $Id$
 */
public class ListableBeanFactoryImpl extends AbstractBeanFactory implements ListableBeanFactory {
	
	/**
	 * Prefix for bean definition keys in Maps.
	 */
	public static final String DEFAULT_PREFIX = "beans.";
	
	/**
	 * Prefix for the class property of a root bean definition.
	 */
	public static final String CLASS_KEY = "class";
	
	/** 
	 * Special string added to distinguish owner.(singleton)=true
	 * Default is true.
	 */
	public static final String SINGLETON_KEY = "(singleton)";
	
	/**
	 * Reserved "property" to indicate the parent of a child bean definition.
	 */
	public static final String PARENT_KEY = "parent";
	
	/**
	 * Separator between bean name and property name.
	 * We follow normal Java conventions.
	 */
	public static final String SEPARATOR = ".";
	
	/** 
	 * Property suffix for references to other beans in the current
	 * BeanFactory: e.g. owner.dog(ref)=fido.
	 * Whether this is a reference to a singleton or a prototype
	 * will depend on the definition of the target bean.
	 */
	public static final String REF_SUFFIX = "(ref)";
	
	/**
	 * Prefix before values referencing other beans
	 */
	public static final String REF_PREFIX = "*";


	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------

	/** Map of BeanDefinition objects, keyed by prototype name */
	private Map beanDefinitionMap = new HashMap();

	/** Name of default parent bean */
	private String defaultParentBean;


	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------

	/**
	 * Create a new ListableBeanFactoryImpl.
	 */
	public ListableBeanFactoryImpl() {
		super();
	}
	
	/**
	 * Create a new ListableBeanFactoryImpl with the given parent.
	 */
	public ListableBeanFactoryImpl(BeanFactory parentBeanFactory) {
		super(parentBeanFactory);
	}

	/**
	 * Set the default parent bean for this bean factory.
	 * If a child bean definition (i.e. a definition without class
	 * attribute) handled by this factory doesn't provide a parent
	 * attribute, this default value gets used.
	 * <p>Can be used e.g. for view definition files, to define a
	 * parent with common attributes for all views.
	 */
	public void setDefaultParentBean(String defaultParentBean) {
		this.defaultParentBean = defaultParentBean;
	}

	/**
	 * Return the default parent bean for this bean factory.
	 */
	public String getDefaultParentBean() {
		return defaultParentBean;
	}


	//---------------------------------------------------------------------
	// Implementation of ListableBeanFactory
	//---------------------------------------------------------------------

	public int getBeanDefinitionCount() {
		return beanDefinitionMap.size();
	}

	public final String[] getBeanDefinitionNames() {
		Set keys = beanDefinitionMap.keySet();
		String[] names = new String[keys.size()];
		Iterator itr = keys.iterator();
		int i = 0;
		while (itr.hasNext()) {
			names[i++] = (String) itr.next();
		}
		return names;
	}

	/**
	 * Note that this method is slow. Don't invoke it too often:
	 * it's best used only in application initialization.
	 */
	public final String[] getBeanDefinitionNames(Class type) {
		Set keys = beanDefinitionMap.keySet();
		List matches = new LinkedList();
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			String name = (String) itr.next();
			Class clazz = getMergedBeanDefinition(name).getBeanClass();
			if (type.isAssignableFrom(clazz)) {
				matches.add(name);
			}
		}
		return (String[]) matches.toArray(new String[matches.size()]);
	}
	
	
	//---------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------

	/**
	 * Subclasses or users should call this method to register new bean definitions
	 * with this class. All other registration methods in this class use this method.
	 * <p>This method isn't guaranteed to be threadsafe. It should be called
	 * before any bean instances are accessed.
	 * @param prototypeName name of the bean instance to register
	 * @param beanDefinition definition of the bean instance to register
	 */
	public final void registerBeanDefinition(String prototypeName, AbstractBeanDefinition beanDefinition) {
		beanDefinitionMap.put(prototypeName, beanDefinition);
	}

	/**
	 * Ensure that even potentially unreferenced singletons are instantiated.
	 * Subclasses or callers should invoke this if they want this behavior.
	 */
	public void preInstantiateSingletons() {
		// Ensure that unreferenced singletons are instantiated
		logger.info("Instantiating singletons in factory [" + this + "]");
		String[] beanNames = getBeanDefinitionNames();
		for (int i = 0; i < beanNames.length; i++) {
			AbstractBeanDefinition bd = getBeanDefinition(beanNames[i]);
			if (bd.isSingleton()) {
				getBean(beanNames[i]);
			}
		}
	}
	
	/**
	 * Register valid bean definitions in a properties file.
	 * Ignore ineligible properties
	 * @param m Map name -> property (String or Object). Property values
	 * will be strings if coming from a Properties file etc. Property names
	 * (keys) <b>must</b> be strings. Class keys must be Strings.
	 * <code>
	 * employee.class=MyClass              // special property
	 * //employee.abstract=true              // this prototype can't be instantiated directly
	 * employee.group=Insurance Services   // real property
	 * employee.usesDialUp=false           // default unless overriden
	 *
	 * employee.manager(ref)=tony		   // reference to another prototype defined in the same file
	 *									   // cyclic and unresolved references will be detected
	 * salesrep.parent=employee
	 * salesrep.department=Sales and Marketing
	 *
	 * techie.parent=employee
	 * techie.department=Software Engineering
	 * techie.usesDialUp=true              // overridden property
	 * </code>
	 * @param prefix The match or filter within the keys
	 * in the map: e.g. 'beans.'
	 * @return the number of bean definitions found
	 * @throws BeansException if there is an error trying to register a definition
	 */
	public final int registerBeanDefinitions(Map m, String prefix) throws BeansException {
		if (prefix == null)
			prefix = "";		
		int beanCount = 0;				
		
		Set keys = m.keySet();
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			if (key.startsWith(prefix)) {
				// Key is of form prefix<name>.property
				String nameAndProperty = key.substring(prefix.length());
				int sepIndx = nameAndProperty.indexOf(SEPARATOR);
				if (sepIndx != -1) {
					String beanName = nameAndProperty.substring(0, sepIndx);
					logger.debug("Found bean name '" + beanName + "'");
					if (beanDefinitionMap.get(beanName) == null) {
						// If we haven't already registered it...
						registerBeanDefinition(beanName, m, prefix + beanName);
						++beanCount;
					}
				}
				else {
					// Ignore it: it wasn't a valid bean name and property,
					// although it did start with the required prefix
					logger.debug("Invalid bean name and property '" + nameAndProperty + "'");
				}
			}	// if the key started with the prefix we're looking for
		}	// while there are more keys
		
		return beanCount;
	}
	
	/**
	 * Get all property values, given a prefix (which will be stripped)
	 * and add the bean they define to the factory with the given name
	 * @param beanName name of the bean to define
	 * @param m Map containing string pairs
	 * @param prefix prefix of each entry, which will be stripped
	 */
	private void registerBeanDefinition(String beanName, Map m, String prefix) throws BeansException {
		String className = null;
		String parent = null;
		boolean singleton = true;
		
		MutablePropertyValues pvs = new MutablePropertyValues();
		Set keys = m.keySet();
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			if (key.startsWith(prefix + SEPARATOR)) {
				String property = key.substring(prefix.length() + SEPARATOR.length());
				if (property.equals(CLASS_KEY)) {
					className = (String) m.get(key);
				}
				else if (property.equals(SINGLETON_KEY)) {
					String val = (String) m.get(key);
					singleton = val == null || !val.toUpperCase().equals("FALSE");
				}
				else if (property.equals(PARENT_KEY)) {
					parent = (String) m.get(key);
				}
				else if (property.endsWith(REF_SUFFIX)) {
					// This isn't a real property, but a reference to another prototype
					// Extract property name: property is of form dog(ref)
					property = property.substring(0, property.length() - REF_SUFFIX.length());
					String ref = (String) m.get(key);					
					
					// It doesn't matter if the referenced bean hasn't yet been registered:
					// this will ensure that the reference is resolved at rungime
					// Default is not to use singleton
					Object val = new RuntimeBeanReference(ref);
					pvs.addPropertyValue(new PropertyValue(property, val));
				}
				else{
					// Normal bean property
					Object val = m.get(key);
					if (val instanceof String) {
						String sval = (String) val;
						// If it starts with unescaped prefix...
						if (sval.startsWith(REF_PREFIX)) {
							// Expand reference
							String targetName = ((String) val).substring(1);
							if (sval.startsWith("**")) {
								val = targetName;
							}
							else {
								val = new RuntimeBeanReference(targetName);
							}
						}
					}
					
					pvs.addPropertyValue(new PropertyValue(property, val));
				}
			}
		}
		logger.debug(pvs.toString());

		if (parent == null)
			parent = defaultParentBean;

		if (className == null && parent == null)
			throw new FatalBeanException("Invalid bean definition. Classname or parent must be supplied for bean with name '" + beanName + "'", null);
		
		try {
			
			AbstractBeanDefinition beanDefinition = null;
			if (className != null) {
				// Load the class using a special class loader if one is available.
				// Otherwise rely on the thread context classloader.
				Class clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
				beanDefinition = new RootBeanDefinition(clazz, pvs, singleton);
			}
			else {
				beanDefinition = new ChildBeanDefinition(parent, pvs, singleton);
			}
			registerBeanDefinition(beanName, beanDefinition);
		}
		catch (ClassNotFoundException ex) {
			throw new FatalBeanException("Cannot find class '" + className + "' for bean with name '" + beanName + "'", ex);
		}
	}
	
	/**
	 * Register bean definitions in a ResourceBundle. Similar syntax
	 * as for a Map. This method is useful to enable standard
	 * Java internationalization support.
	 */
	public final int registerBeanDefinitions(ResourceBundle rb, String prefix) throws BeansException {
		// Simply create a map and call overloaded method
		Map m = new HashMap();
		Enumeration keys = rb.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			m.put(key, rb.getObject(key));
		}
		return registerBeanDefinitions(m, prefix);
	}

	/**
	 * Register additional property value for a specific bean.
	 * This is intended for bean factory post processing, i.e. overriding
	 * certain property values after parsing the original bean definitions.
	 * @param beanName name of the bean
	 * @param pv property name and value
	 * @throws BeansException if the property values of the specified bean are immutable
	 * @see com.interface21.context.support.BeanFactoryPostProcessor
	 */
	public void registerAdditionalPropertyValue(String beanName, PropertyValue pv) throws BeansException {
		AbstractBeanDefinition bd = getBeanDefinition(beanName);
		if (!(bd.getPropertyValues() instanceof MutablePropertyValues)) {
			throw new FatalBeanException("Cannot modify immutable property values for bean [" + beanName + "]");
		}
		MutablePropertyValues pvs = (MutablePropertyValues) bd.getPropertyValues();
		pvs.addPropertyValue(pv);
	}


	//---------------------------------------------------------------------
	// Implementation of superclass protected abstract methods
	//---------------------------------------------------------------------

	/**
	 * @see AbstractBeanFactory#getBeanDefinition(String)
	 */
	protected final AbstractBeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		AbstractBeanDefinition bd = (AbstractBeanDefinition) beanDefinitionMap.get(beanName);
		if (bd == null)
			throw new NoSuchBeanDefinitionException(beanName, toString());
		return bd;
	}


	public String toString() {
		return getClass().getName() + " with defined beans [" + StringUtils.arrayToDelimitedString(getBeanDefinitionNames(), ",") + "]";
	}
	
}
