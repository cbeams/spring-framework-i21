
package com.interface21.jdbc.core.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.interface21.beans.BeansException;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.NoSuchBeanDefinitionException;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;
import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.RowCallbackHandler;

/**
 * ListableBeanFactory implementation that reads values from a database
 * table. Expects columns for bean name, property name and value
 * as string. Formats for each are identical to the properties format
 * recognized by ListableBeanFactoryImpl.
 * @author Rod Johnson
 * @version $Id$
 */
public class JdbcBeanFactory implements ListableBeanFactory {
	
	private ListableBeanFactory delegate;
	
	/**
	 * SQL that produces the three columns.
	 */
	private String sql;
	
	private JdbcTemplate jdbcTemplate;


	/**
	 * Create a new JdbcBeanFactory
	 * @param ds datasource to use to obtain connections. The datasource
	 * will be cached during the life of this bean factory.
	 * @param sql the first three columns must be bean name, property name and value. Any join
	 * and any other columns are permitted: e.g.
	 * SELECT BEAN_NAME, PROPERTY, VALUE FROM CONFIG WHERE CONFIG.APP_ID = 1
	 * It's also possible to perform a join. Column names are not significant--only
	 * the ordering of these first three columns.
	 */
	public JdbcBeanFactory(DataSource ds, String sql) {
		this.sql = sql;
		this.jdbcTemplate = new JdbcTemplate(ds);
		refresh();
	}
	
	/**
	 * Refresh the factory. Uses copy-on-write for thread safety
	 */
	public void refresh() {
		ListableBeanFactoryImpl newDelegate =  new ListableBeanFactoryImpl();
		final Properties props = new Properties();
		this.jdbcTemplate.query(sql, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				String beanName = rs.getString(1);
				String property = rs.getString(2);
				String value = rs.getString(3);
				// Make a properties entry by combining bean name and property
				props.setProperty(beanName + "." + property, value);
			}
		});
		
		newDelegate.registerBeanDefinitions(props, null);
		this.delegate = newDelegate;
	}

	public Object getBean(String name) {
		return delegate.getBean(name);
	}

	public Object getBean(String name, Class requiredType) throws BeansException {
		return delegate.getBean(name, requiredType);
	}

	public int getBeanDefinitionCount() {
		return delegate.getBeanDefinitionCount();
	}

	public String[] getBeanDefinitionNames() {
		return delegate.getBeanDefinitionNames();
	}

	public String[] getBeanDefinitionNames(Class type) {
		return delegate.getBeanDefinitionNames(type);
	}

	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return delegate.isSingleton(name);
	}

	public String[] getAliases(String name) {
		return null;
	}

}
