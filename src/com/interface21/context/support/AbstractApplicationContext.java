/**
 * Generic framework code included with
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * This code is free to use and modify.
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.context.support;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.ApplicationEvent;
import com.interface21.context.ApplicationEventMulticaster;
import com.interface21.context.ApplicationListener;
import com.interface21.context.ContextOptions;
import com.interface21.context.MessageSource;
import com.interface21.context.NestingMessageSource;
import com.interface21.context.NoSuchMessageException;
import com.interface21.util.StringUtils;
import com.interface21.context.MessageSourceResolvable;


/**
 * Partial implementation of ApplicationContext. Doesn't mandate the
 * type of storage used for configuration, but implements common functionality.
 * <br/>This class uses the <b>template method</b> design pattern, requiring
 * concrete subclasses to implement protected abstract methods.
 * <p/>WHAT DO SUBCLASSES HAVE TO DO?
 * call refresh, i think
 * @author  Rod Johnson
 * @since January 21, 2001
 * @version $Revision$
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

	/** Name of the MessageSource bean in the bean factory */
	public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** Log4j category used by this class. Available to subclasses. */
	protected final Logger logger = Logger.getLogger(getClass().getName());

	/** Parent context */
	private ApplicationContext parent;

	/**
	 * Helper class used in event publishing.
	 * ****TODO: this could be parameterized as a JavaBean (with a distinguished name
	 * if specified), enabling a different thread usage policy for event publication.
	 */
	private ApplicationEventMulticaster eventMulticaster = new ApplicationEventMulticasterImpl();

	/** We use this to prevent reloading if it's been forbidden in the config itself */
	private boolean loaded;

	/**
	 * MessageSource helper we delegate our implementation
	 * of this interface to
	 */
	private MessageSource messageSource;

	/** System time in milliseconds this context started */
	private long startupTime;

	/** Special bean to handle configuration */
	private ContextOptions contextOptions;

	/** Default display name */
	private String displayName = getClass().getName() + ";hc=" + hashCode();

	/**
	 * Hash table of shared objects, keyed by String key passed
	 * in shared object method calls
	 */
	private HashMap sharedObjects = new HashMap();

	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/**
	 * Create a new AbstractApplicationContext with no parent.
	 */
	public AbstractApplicationContext() {
	}

	/**
	 * Create a new AbstractApplicationContext with the
	 * given parent context.
	 * @param parent parent context
	 */
	public AbstractApplicationContext(ApplicationContext parent) {
		this.parent = parent;
	}

	//---------------------------------------------------------------------
	// Implementation of ApplicationContext
	//---------------------------------------------------------------------
	/**
	 * Return a friendly name for context
	 * @return a display name for the context
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * To avoid endless constructor chaining, only concrete classes
	 * take this in their constructor, and then invoke this method
	 */
	protected void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Return the parent context, or null if there is no parent,
	 * and this is the root of the context hierarchy.
	 * @return the parent context, or null if there is no parent
	 */
	public ApplicationContext getParent() {
		return parent;
	}

	/**
	 * Subclasses may call this to set parent after constructor.
	 * Note that parent shouldn't be changed: it should only be
	 * set later if it isn't available when an object of this
	 * class is created.
	 * @param ac parent context
	 */
	protected void setParent(ApplicationContext ac) {
		this.parent = ac;
	}

	/**
	 * Load or reload configuration.
	 * @throws ApplicationContextException if the configuration was invalid or couldn't
	 * be found, or if configuration has already been loaded and reloading is
	 * forbidden
	 * DYNAMIC CLASSLOADER ISSUE...subclass to get classloader!?
	 */
	public final void refresh() throws ApplicationContextException {
		if (contextOptions != null && !contextOptions.isReloadable())
			throw new ApplicationContextException("Forbidden to reload config");

		this.startupTime = System.currentTimeMillis();

		refreshBeanFactory();

		try {
			loadOptions();
		} catch (BeansException ex) {
			throw new ApplicationContextException("Unexpected error loading context options", ex);
		}

		try {
			this.messageSource = (MessageSource) getBeanFactory().getBean(MESSAGE_SOURCE_BEAN_NAME);
		} catch (BeansException ex) {
			logger.warn("No MessageSource defined in ApplicationContext: using parent's");
			this.messageSource = this.parent;
			if (this.messageSource == null)
				logger.warn("No MessageSource defined in WebApplicationContext and no parent");
		}
		if ((this.messageSource instanceof NestingMessageSource) && this.parent != null) {
			((NestingMessageSource) this.messageSource).setParent(this.parent);
		}

		refreshListeners();
		configureAllManagedObjects();

		publishEvent(new ContextRefreshedEvent(this));
	}	// refresh


	/**
	 * The BeanFactory must be loaded before this method is called
	 */
	private void loadOptions() throws BeansException {
		if (this.contextOptions == null) {
			// Try to load from bean
			try {
				this.contextOptions = (ContextOptions) getBeanFactory().getBean(OPTIONS_BEAN_NAME);
			} catch (NoSuchBeanDefinitionException ex) {
				this.contextOptions = ContextOptions.DEFAULT_OPTIONS;
			}
		}
	}


	/**
	 * Invoke the setApplicationContext() callback on all objects
	 * in the context. This involves instantiating the objects.
	 * Only singletons will be instantiated eagerly.
	 */
	private void configureAllManagedObjects() throws ApplicationContextException {
		logger.info("Configuring singleton beans in context");
		String[] beanNames = getBeanDefinitionNames();
		logger.debug("Found " + beanNames.length + " listeners in bean factory; names=" +
		             StringUtils.arrayToDelimitedString(beanNames, ",") + "]");
		for (int i = 0; i < beanNames.length; i++) {
			String beanName = beanNames[i];

			if (isSingleton(beanName)) {

				try {
					Object bean = getBeanFactory().getBean(beanName);
					configureManagedObject(bean);
				} catch (BeansException ex) {
					throw new ApplicationContextException("Couldn't instantiate object with name '" + beanName + "'", ex);
				}
			}
		}
	}	// configureAllManagedObjects

	/**
	 * Add beans that implement listener as listeners
	 * Doesn't affect other listeners, that can be added without being beans
	 */
	private void refreshListeners() throws ApplicationContextException {
		logger.info("Refreshing listeners");
		String[] listenerNames = getBeanDefinitionNames(ApplicationListener.class);
		logger.debug("Found " + listenerNames.length + " listeners in bean factory; names=" +
		             StringUtils.arrayToDelimitedString(listenerNames, ",") + "]");
		for (int i = 0; i < listenerNames.length; i++) {
			String beanName = listenerNames[i];
			try {
				Object bean = getBeanFactory().getBean(beanName);
				ApplicationListener l = (ApplicationListener) bean;
				configureManagedObject(l);
				addListener(l);
				logger.info("Bean listener added: [" + l + "]");
			} catch (BeansException ex) {
				throw new ApplicationContextException("Couldn't load config listener with name '" + beanName + "'", ex);
			}
		}
	}	// refreshListeners


	/**
	 * Publish the given event to all listeners.
	 * @param e event to publish. The event may be application-specific,
	 * or a standard framework event
	 */
	public final void publishEvent(ApplicationEvent e) {
		this.eventMulticaster.onApplicationEvent(e);
		if (this.parent != null)
			parent.publishEvent(e);
	}

	/**
	 * Return context options. These control reloading etc.
	 * @return context options
	 */
	public final ContextOptions getOptions() {
		return this.contextOptions;
	}

	/**
	 * Return the timestamp when this context was first loaded
	 * @return the timestamp (ms) when this context was first loaded
	 */
	public final long getStartupDate() {
		return startupTime;
	}

	/**
	 * Add a listener. Any beans that are listeners are
	 * automatically added.
	 */
	protected void addListener(ApplicationListener l) {
		eventMulticaster.addApplicationListener(l);
	}

	/**
	 * This implementation only supports fully qualified URLs.
	 * @see com.interface21.context.ApplicationContext
	 */
	public InputStream getResourceAsStream(String path) throws IOException {
		try {
			// try URL
			URL url = new URL(path);
			return url.openStream();
		} catch (MalformedURLException ex) {
			// no URL -> file path
			return new FileInputStream(path);
		}
	}

	//---------------------------------------------------------------------
	// Implementation of MessageSource
	//---------------------------------------------------------------------
	/**
	 * Try to resolve the message.Return default message if no message
	 * was found
	 * @param code code to lookup up, such as 'calculator.noRateSet'
	 * @param locale Locale in which to do lookup
	 * @param args Array of arguments that will be filled in for params within
	 * the message (params look like "{0}", "{1,date}", "{2,time}" within a message).
	 * @see <a href=http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html>java.text.MessageFormat</a> for more details.
	 * @param defaultMessage String to return if the lookup fails
	 * @return a resolved message if the lookup is successful;
	 * otherwise return the default message passed as a parameter
	 */
	public String getMessage(String code, Object args[], String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, args, defaultMessage, locale);
	}


	/**
	 * Try to resolve the message. Treat as an error if the message can't
	 * be found.
	 * @param code code to lookup up, such as 'calculator.noRateSet'
	 * @param locale Locale in which to do lookup
	 * @param args Array of arguments that will be filled in for params within
	 * the message (params look like "{0}", "{1,date}", "{2,time}" within a message).
	 * @see <a href=http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html>java.text.MessageFormat</a> for more details.
	 * @return message
	 * @throws NoSuchMessageException not found in any locale
	 */
	public String getMessage(String code, Object args[], Locale locale) throws
	    NoSuchMessageException {
		return messageSource.getMessage(code, args, locale);
	}


	/**
	 * <b>Using all the attributes contained within the <code>MessageSourceResolvable</code>
	 * arg that was passed in (except for the <code>locale</code> attribute)</b>,
	 * try to resolve the message from the <code>MessageSource</code> contained within the <code>Context</code>.<p>
	 *
	 * NOTE: We must throw a <code>NoSuchMessageException</code> on this method since
	 * at the time of calling this method we aren't able to determine if the <code>defaultMessage</code>
	 * attribute is null or not.
	 * @param resolvable Value object storing 4 attributes required to properly resolve a message.
	 * @param locale Locale to be used as the "driver" to figuring out what message to return.
	 * @see <a href=http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html>java.text.MessageFormat</a> for more details.
	 * @return message Resolved message.
	 * @throws NoSuchMessageException not found in any locale
	 */
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return messageSource.getMessage(resolvable.getCode(), resolvable.getArgs(), resolvable.getDefaultMessage(), locale);
	}


	//---------------------------------------------------------------------
	// Implementation of BeanFactory
	//---------------------------------------------------------------------
	/**
	 * Try to find the bean instance in the hierarchy
	 * @see BeanFactory#getBeanInstance(String)
	 */
	public Object getBean(String name) throws BeansException {
		try {
			Object bean = getBeanFactory().getBean(name);
			configureManagedObject(bean);
			return bean;
		} catch (NoSuchBeanDefinitionException ex) {
			/*
			***** NOT NECESSARY ANYMORE BECAUSE OF BEANFACTORY INHERITANCE *****
			// Not found here: let's check parent
			// A more serious exception can just be rethrown
			if (this.parent != null)
				return parent.getBean(name);
			*/
		}
		throw new NoSuchBeanDefinitionException(name);
	}


	/**
	 * @see BeanFactory#getBeanInstance(String, Class)
	 */
	public Object getBean(String name, Class requiredType) throws BeansException {
		try {
			Object bean = getBeanFactory().getBean(name, requiredType);
			configureManagedObject(bean);
			return bean;
		} catch (NoSuchBeanDefinitionException ex) {
			// Not found here: let's check parent
			// A more serious exception can just be rethrown
			if (this.parent != null)
				return parent.getBean(name, requiredType);
		}
		throw new NoSuchBeanDefinitionException(name);
	}


	/**
	 * @see BeanFactory#isSingleton(String)
	 */
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return getBeanFactory().isSingleton(name);
	}

	/**
	 * @see ApplicationContext#sharedObject(String)
	 */
	public synchronized Object sharedObject(String key) {
		return sharedObjects.get(key);
	}

	/**
	 * @see ApplicationContext#shareObject(String, Object)
	 */
	public synchronized void shareObject(String key, Object o) {
		logger.info("Set shared object '" + key + "'");
		sharedObjects.put(key, o);
	}

	/**
	 * @see ApplicationContext#removeSharedObject(String)
	 */
	public synchronized Object removeSharedObject(String key) {
		logger.info("Removing shared object '" + key + "'");
		Object o = sharedObjects.remove(key);
		if (o == null) {
			logger.warn("Shared object '" + key + "' not present; could not be removed");
		} else {
			logger.info("Removed shared object '" + key + "'");
		}
		return o;
		//return sharedObjects.remove(key);
	}


	/**
	 * If the object is context-aware, give it a reference to this object.
	 * Note that the implementation fo the ApplicationContextAware interface
	 * must return the ApplicationContext it runs in in it's getApplicationContext()
	 * method, and null if it has not yet been associated with a context: this
	 * method relies on this to avoid initialization managed objects
	 * more than once.
	 * @param o object to invoke the setApplicationContext() method on,
	 * if it implements the ApplicationContextAware interface
	 */
	protected void configureManagedObject(Object o) throws ApplicationContextException {
		if (o instanceof ApplicationContextAware) {
			logger.debug("Setting application context on ApplicationContextAware object [" + o + "]");
			ApplicationContextAware aca = (ApplicationContextAware) o;
			if (aca.getApplicationContext() == null) {
				aca.setApplicationContext(this);
			}
		}
	}

	//---------------------------------------------------------------------
	// Implementation of ListableBeanFactory
	//---------------------------------------------------------------------
	/**
	 * @see ListableBeanFactory#getBeanDefinitionNames()
	 */
	public String[] getBeanDefinitionNames() {
		return getBeanFactory().getBeanDefinitionNames();
	}


	/**
	 * @see ListableBeanFactory#getBeanDefinitionCount()
	 */
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}


	/**
	 * @see ListableBeanFactory#getBeanDefinitionNames(Class)
	 */
	public String[] getBeanDefinitionNames(Class type) {
		return getBeanFactory().getBeanDefinitionNames(type);
	}


	/** Show information about this context */
	public String toString() {
		StringBuffer sb = new StringBuffer("ApplicationContext: displayName=**" + displayName + "'**; ");
		sb.append("class=[" + getClass().getName() + "]; ");
		sb.append("BeanFactory={" + getBeanFactory() + "}; ");
		sb.append("} MessageSource={" + this.messageSource + "}; ");
		sb.append("ContextOptions={" + this.contextOptions + "}; ");
		sb.append("Startup date=" + new Date(startupTime) + "; ");
		if (this.parent == null)
			sb.append("ROOT of ApplicationContext hierarchy");
		else
			sb.append("Parent={" + this.parent + "}");
		return sb.toString();
	}	// toString


	//---------------------------------------------------------------------
	// Abstract methods that must be implemented by subclasses
	//---------------------------------------------------------------------
	/**
	 * Subclasses must implement this method to perform the actual configuration load.
	 * @param servletConfig the app's ServletConfig to use to load the configuration.
	 * (We'll need this if it's inside the WAR.)
	 */
	protected abstract void refreshBeanFactory() throws ApplicationContextException;


	/**
	 * Unimplemented interface method. Subclasses must implement this
	 * efficiently, so that it can be called repeatedly without a performance penalty.
	 * @return this application context's default BeanFactory
	 * @see ApplicationContext#getBeanFactory()
	 */
	protected abstract ListableBeanFactory getBeanFactory();

}	// class AbstractApplicationContext
