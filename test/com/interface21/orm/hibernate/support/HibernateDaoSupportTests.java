package com.interface21.orm.hibernate.support;

import java.util.List;
import java.util.ArrayList;

import net.sf.hibernate.SessionFactory;
import org.easymock.MockControl;
import org.easymock.EasyMock;
import junit.framework.TestCase;

/**
 * @author Juergen Hoeller
 * @since 30.07.2003
 */
public class HibernateDaoSupportTests extends TestCase {

	public void testHibernateDaoSupport() {
		MockControl sfControl = EasyMock.controlFor(SessionFactory.class);
		SessionFactory sf = (SessionFactory) sfControl.getMock();
		sfControl.activate();
		final List test = new ArrayList();
		HibernateDaoSupport dao = new HibernateDaoSupport() {
			protected void initDao() {
				test.add("test");
			}
		};
		dao.setSessionFactory(sf);
		dao.afterPropertiesSet();
		assertEquals("Correct SessionFactory", dao.getSessionFactory(), sf);
		assertEquals("Correct HibernateTemplate", dao.getHibernateTemplate().getSessionFactory(), sf);
		assertEquals("initDao called", test.size(), 1);
	}

}
