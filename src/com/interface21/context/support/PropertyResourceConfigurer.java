package com.interface21.context.support;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.BeansException;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.FatalBeanException;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;
import com.interface21.context.ApplicationContextException;

/**
 * Allows for configuration of individual bean properties from a property resource,
 * i.e. a properties file. Useful for custom config files targetted at system
 * administrators that override bean properties configured in the application context.
 *
 * <p>Expects configuration lines of the following form:<br>
 * beanName.property=value
 *
 * @author Juergen Hoeller
 * @since 12.03.2003
 */
public class PropertyResourceConfigurer extends ApplicationObjectSupport implements BeanFactoryPostProcessor {

	private final Log logger = LogFactory.getLog(getClass());

	private String location;

	/**
	 * Set the location of the properties file. Allows for both a URL
	 * and a (file) path, according to the respective ApplicationContext.
	 * @see com.interface21.context.ApplicationContext#getResourceAsStream
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	public void postProcessBeanFactory(ListableBeanFactoryImpl beanFactory) throws ApplicationContextException {
		if (this.location != null) {
			logger.info("Loading properties '" + this.location + "'");
			Properties prop = new Properties();
			try {
				prop.load(getApplicationContext().getResourceAsStream(this.location));
				for (Iterator it = prop.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					processKey(beanFactory, key, prop.getProperty(key));
				}
			} catch (IOException ex) {
				logger.warn("Could not load properties '" + this.location + "': " + ex.getMessage());
			}
		} else {
			logger.warn("No property resource location specified");
		}
	}

	protected void processKey(ListableBeanFactoryImpl factory, String key, String value) throws ApplicationContextException {
		try {
			int dotIndex = key.indexOf('.');
			if (dotIndex == -1) {
				throw new FatalBeanException("Invalid key (expected 'beanName.property')");
			}
			String beanName = key.substring(0, dotIndex);
			String beanProperty = key.substring(dotIndex+1);
			factory.registerAdditionalPropertyValue(beanName, new PropertyValue(beanProperty, value));
			logger.debug("Property " + key + " set to " + value);
		}
		catch (BeansException ex) {
			throw new ApplicationContextException("Could not set property '" + key + "' to value '" + value + "': " + ex.getMessage());
		}
	}

}
