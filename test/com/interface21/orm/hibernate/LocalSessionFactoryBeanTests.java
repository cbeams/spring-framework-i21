package com.interface21.orm.hibernate;

import java.util.Properties;

import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.connection.UserSuppliedConnectionProvider;
import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.jdbc.datasource.DriverManagerDataSource;

/**
 * @author Juergen Hoeller
 */
public class LocalSessionFactoryBeanTests extends TestCase {

	public void testLocalSessionFactoryBeanWithDefaultConfigFile() throws HibernateException {
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
		sfb.setDataSource(new DriverManagerDataSource());
		try {
			sfb.afterPropertiesSet();
		}
		catch (HibernateException ex) {
			// expected, as the file can't be found
		}
	}

	public void testLocalSessionFactoryBeanWithDataSource() throws HibernateException {
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
		sfb.setMappingResources(new String[0]);
		sfb.setDataSource(new DriverManagerDataSource());
		sfb.afterPropertiesSet();
	}

	public void testLocalSessionFactoryBeanWithDataSourceAndProperties() throws HibernateException {
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
		sfb.setMappingResources(new String[0]);
		sfb.setDataSource(new DriverManagerDataSource());
		Properties prop = new Properties();
		prop.setProperty(Environment.CONNECTION_PROVIDER, "myClass");
		sfb.setHibernateProperties(prop);
		sfb.afterPropertiesSet();
	}

	public void testLocalSessionFactoryBeanWithValidProperties() throws HibernateException {
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
		sfb.setMappingResources(new String[0]);
		Properties prop = new Properties();
		prop.setProperty(Environment.CONNECTION_PROVIDER, UserSuppliedConnectionProvider.class.getName());
		sfb.setHibernateProperties(prop);
		sfb.afterPropertiesSet();
	}

	public void testLocalSessionFactoryBeanWithInvalidProperties() throws HibernateException {
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
		sfb.setMappingResources(new String[0]);
		Properties prop = new Properties();
		prop.setProperty(Environment.CONNECTION_PROVIDER, "myClass");
		sfb.setHibernateProperties(prop);
		try {
			sfb.afterPropertiesSet();
		}
		catch (HibernateException ex) {
			// expected, provider class not found
		}
	}

	public void testLocalSessionFactoryBeanWithInvalidMappings() throws HibernateException {
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
		sfb.setMappingResources(new String[] {"example.hbm.xml"});
		try {
			sfb.afterPropertiesSet();
		}
		catch (HibernateException ex) {
			// expected, mapping resource not found
		}
	}

	public void testLocalSessionFactoryBeanWithCustomSessionFactory() throws HibernateException {
		MockControl factoryControl = EasyMock.controlFor(SessionFactory.class);
		final SessionFactory sessionFactory = (SessionFactory) factoryControl.getMock();
		factoryControl.activate();
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean() {
			protected SessionFactory newSessionFactory(Configuration config) throws HibernateException {
				return sessionFactory;
			}
		};
		sfb.setMappingResources(new String[0]);
		sfb.setDataSource(new DriverManagerDataSource());
		sfb.afterPropertiesSet();
		assertTrue(sessionFactory.equals(sfb.getObject()));
	}

	public void testLocalSessionFactoryBeanWithEntityInterceptor() throws HibernateException {
		LocalSessionFactoryBean sfb = new LocalSessionFactoryBean() {
			protected Configuration newConfiguration() throws HibernateException {
				return new Configuration() {
					public Configuration setInterceptor(Interceptor interceptor) {
						throw new IllegalArgumentException(interceptor.toString());
					}
				};
			}
		};
		sfb.setMappingResources(new String[0]);
		sfb.setDataSource(new DriverManagerDataSource());
		MockControl interceptorControl = EasyMock.controlFor(Interceptor.class);
		Interceptor entityInterceptor = (Interceptor) interceptorControl.getMock();
		interceptorControl.activate();
		sfb.setEntityInterceptor(entityInterceptor);
		try {
			sfb.afterPropertiesSet();
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
			assertTrue("Correct exception", ex.getMessage().equals(entityInterceptor.toString()));
		}
	}

}
