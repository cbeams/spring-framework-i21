
package com.interface21.jdbc.core.support;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.MockControl;

import com.interface21.beans.TestBean;
import com.interface21.jdbc.core.MockConnectionFactory;
import com.interface21.jdbc.core.support.JdbcBeanFactory;

import com.mockobjects.sql.MockConnection;

/**
 * @author Rod Johnson
 */
public class JdbcBeanFactoryTests extends TestCase {

	/**
	 * Constructor for JdbcBeanFactoryTest.
	 * @param arg0
	 */
	public JdbcBeanFactoryTests(String arg0) {
		super(arg0);
	}
	
	public void testValid() throws Exception {
		String sql = "SELECT NAME AS NAME, PROPERTY AS PROPERTY, VALUE AS VALUE FROM T";
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();

		String[][] results = {
			{ "one", "class", "com.interface21.beans.TestBean" },
			{ "one", "age", "53" },
		};
	
		MockConnection con = MockConnectionFactory.statement(sql, results, true, null, null);
		con.setExpectedCloseCalls(2);

		ds.getConnection();
		dsControl.setReturnValue(con);
		dsControl.activate();
		
		JdbcBeanFactory bf = new JdbcBeanFactory(ds, sql);
		assertTrue(bf.getBeanDefinitionCount() == 1);
		TestBean tb = (TestBean) bf.getBean("one");
		assertTrue(tb.getAge() == 53);
	}
	

}
