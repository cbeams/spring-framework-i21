
package com.interface21.context.support;

import java.io.IOException;
import java.io.InputStream;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.beans.factory.xml.XmlBeanFactory;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
 
/**
 * Convenient abstract superclass for ApplicationContext implementations
 * drawing their configuration from XML documents containing bean definitions
 * understood by an XMLBeanFactory
 * @see com.interface21.beans.factory.xml.XmlBeanFactory
 * @author  Rod Johnson
 * @version $Revision$
 */
public abstract class AbstractXmlApplicationContext extends AbstractApplicationContext  {

	/** Default BeanFactory for this context */
	private ListableBeanFactory listableBeanFactory; 

	public AbstractXmlApplicationContext() {
	}
	
	public AbstractXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}
	
	protected void refreshBeanFactory() throws ApplicationContextException {
		String identifier = "application context with display name [" + getDisplayName() + "]";
		InputStream is = null;
		try {
			// Supports remote as well as local URLs
			is = getInputStreamForBeanFactory();
			listableBeanFactory = new XmlBeanFactory(is, getParent());
			logger.info("BeanFactory for application context is [" + listableBeanFactory + "]");
		}
		catch (IOException ex) {
			throw new ApplicationContextException("IOException parsing XML document for " + identifier, ex);
		} 
		catch (NoSuchBeanDefinitionException ex) {
			throw new ApplicationContextException("Cannot load configuration: missing bean definition [" + ex.getBeanName() + "]", ex);
		}
		catch (BeansException ex) {
			throw new ApplicationContextException("Cannot load configuration: problem instantiating or initializing beans", ex);
		}
		finally {
			try {
				if (is != null)
					is.close();
			}
			catch (IOException ex) {
				throw new ApplicationContextException("IOException closing stream for XML document for " + identifier, ex);
			}
		}
	}
	
	/**
	 * Return the default BeanFactory for this context
	 */
	public final ListableBeanFactory getBeanFactory() {
		return listableBeanFactory;
	}

	/**
	 * Open and return the input stream for the bean factory for this namespace. 
	 * If namespace is null, return the input stream for the default bean factory.
	 * @exception IOException if the required XML document isn't found
	 */
	protected abstract InputStream getInputStreamForBeanFactory() throws IOException;
	
}
 