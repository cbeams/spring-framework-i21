/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.context;

import java.io.InputStream;
import java.io.IOException;

import com.interface21.beans.factory.ListableBeanFactory;

/** 
 * Interface to provide configuration for an application. This is
 * read-only while the application is running, but may be reloaded if the
 * implementation supports this.
 * <p/>The configuration provides:
 * <ul>The ability to publish events. Implementations must provide a means
 * of registering event listeners.
 * <ul>The ability to resolves messages, supporting internationalization.
 * <ul>Bean factory methods, inherited from ListableBeanFactory. This
 * avoids the need for applications to use singletons.
 * <ul>The ability to share objects by publishing them to the context.
 * <ul>Notification of beans initialized by the context of the context,
 * enabling communication with the rest of the application, for
 * example by publishing events. The BeanFactory superinterface
 * provides no similar mechanism.
 * <ul>Inheritance from a parent context. Definitions in a descendant context
 * will always take priority. This means, for example, that a single parent
 * context can be used by an entire web application, while each servlet has its
 * own child context that is independent of that of any other servlet.
 * <li>
 * </ul>
 * @author Rod Johnson
 * @version $Revision$
 */
public interface ApplicationContext extends MessageSource, ListableBeanFactory {
	
	/** 
	 * Name of options bean. If none is supplied, DEFAULT_OPTIONS will be used
	 */
	String OPTIONS_BEAN_NAME = "ApplicationContext.options";
	
	/**
	 * Return the parent context, or null if there is no parent,
	 * and this is the root of the context hierarchy.
	 * @return the parent context, or null if there is no parent
	 */
	ApplicationContext getParent();
	
	/** Friendly name for context
	 * @return a display name for the context
	*/
	String getDisplayName();

	/**
	 * Return the timestamp when this context was first loaded
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * Return context options. These control reloading etc.
	 * ApplicationContext implementations may subclass ContextOptions to
	 * add additional properties. It must always be a bean.
	 * @return context options. Never returns null. Context may be
	 * supplied as a bean in the default bean factory. If it is
	 * not supplied, the ApplicationContext implementation must supply
	 * a context.
	 */
	ContextOptions getOptions();

	/**
	 * Load or refresh the persistent representation of the configuration, which
	 * might for example be an XML file, properties file or relational database schema.
	 * @throws ApplicationContextException if the config cannot be loaded
	 */
	void refresh() throws ApplicationContextException;

	/**
	 * Notify all listeners registered with this application of 
	 * an application event. Events may be framework events (such as RequestHandledEvent)
	 * or application-specific events.
	 * @param e event to publish
	 */
	void publishEvent(ApplicationEvent e);
	
	/**
	 * Open an InputStream to the specified resource.
	 * Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * Must support absolute file paths, e.g. "C:/test.dat".
	 * May allow for relative file paths, e.g. "/WEB-INF/test.dat".
	 * Note: Callers are responsible for closing the input stream.
	 * @param path  the path to the specified resource
	 * @return the InputStream for the specified resource
	 * @throws IOException exception when opening the specified resource
	 */
	InputStream getResourceAsStream(String path) throws IOException;

	/**
	 * Put an object available for sharing. Note that this
	 * method is not synchronized. As with Java 2 collections,
	 * it's up to calling code to ensure thread safety.
	 * Also, this doesn't work in a cluster. It's
	 * analogous to putting something in a ServletContext.
	 * @param key object key
	 * @param o object to put
	 */
	void shareObject(String key, Object o);
	
	/**
	 * Retrieve a shared object added with a call to shareObject().
	 * @return the object, or null if no object is known under
	 * this name (this is not an error).
	 */
	Object sharedObject(String key);
	
	/** 
	 * Remove a shared object added with a call to shareObject().
	 * Does nothing if the object was null.
	 * @param key the object was added with
	 * @return the object if it was found; or null.
	 */
	Object removeSharedObject(String key);
		
}	// interface ApplicationContext

