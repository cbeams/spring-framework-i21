package com.interface21.orm.jdo.support;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

/**
 * @author Juergen Hoeller
 * @since 30.07.2003
 */
public class JdoDaoSupportTests extends TestCase {

	public void testJdoDaoSupport() {
		MockControl pmfControl = EasyMock.controlFor(PersistenceManagerFactory.class);
		PersistenceManagerFactory pmf = (PersistenceManagerFactory) pmfControl.getMock();
		pmfControl.activate();
		final List test = new ArrayList();
		JdoDaoSupport dao = new JdoDaoSupport() {
			protected void initDao() {
				test.add("test");
			}
		};
		dao.setPersistenceManagerFactory(pmf);
		dao.afterPropertiesSet();
		assertEquals("Correct SessionFactory", dao.getPersistenceManagerFactory(), pmf);
		assertEquals("Correct HibernateTemplate", dao.getJdoTemplate().getPersistenceManagerFactory(), pmf);
		assertEquals("initDao called", test.size(), 1);
	}

}
