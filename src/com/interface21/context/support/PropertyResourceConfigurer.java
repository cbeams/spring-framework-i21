package com.interface21.context.support;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.interface21.beans.BeansException;
import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.context.ApplicationEvent;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationListener;
import com.interface21.context.support.ContextRefreshedEvent;

/**
 * Allows for configuration of individual bean properties from a
 * property resource, e.g. a properties file.
 * Useful for custom config files targetted at system administrators
 * that override bean properties configured in the application context.
 *
 * Expects configuration lines of the following form:
 *   beanName.property=value
 *
 * @author Juergen Hoeller
 * @since 12.03.2003
 */
public class PropertyResourceConfigurer implements ApplicationListener {

	private Logger logger = Logger.getLogger(getClass());

	private String location;

	/**
	 * Set the location of the properties file.
	 * Allows for both a URL and an absolute or a relative file location
	 * (according to the respective ApplicationContext).
	 * @see com.interface21.context.ApplicationContext#getResourceAsStream
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	public void onApplicationEvent(ApplicationEvent e) {
		if (e instanceof ContextRefreshedEvent) {
			ApplicationContext ctx = (ApplicationContext) e.getSource();
			if (location != null) {
				Properties prop = new Properties();
				try {
					prop.load(ctx.getResourceAsStream(this.location));
					for (Iterator it = prop.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						processKey(ctx, key, prop.getProperty(key));
					}
				} catch (IOException ex) {
					logger.error("Could not load properties '" + this.location + "': " + ex.getMessage());
				}
			} else {
				logger.error("No property resource location specified");
			}
		}
	}

	protected void processKey(BeanFactory factory, String key, String value) {
		try {
			int dotIndex = key.indexOf('.');
			if (dotIndex == -1) {
				logWarning(key, value, "Invalid key (expected 'beanName.property')");
				return;
			}
			String beanName = key.substring(0, dotIndex);
			String beanProperty = key.substring(dotIndex+1);
			Object bean = factory.getBean(beanName);
			BeanWrapper bw = new BeanWrapperImpl(bean);
			bw.setPropertyValue(beanProperty, value);
			logger.debug("Property " + key + " set to " + value);
		}
		catch (BeansException ex) {
			logWarning(key, value, ex.getMessage());
		}
		catch (PropertyVetoException ex) {
			logWarning(key, value, ex.getMessage());
		}
	}

	private void logWarning(String key, String value, String msg) {
		logger.warn("Could not set property '" + key + "' to value '" + value + "': " + msg);
	}
}
