package com.interface21.jdbc.core.support;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.MockControl;

/**
 * @author Juergen Hoeller
 * @since 30.07.2003
 */
public class JdbcDaoSupportTests extends TestCase {

	public void testJdbcDaoSupport() throws SQLException {
		MockControl conControl = EasyMock.controlFor(Connection.class);
		Connection con = (Connection) conControl.getMock();
		MockControl dsControl = EasyMock.controlFor(DataSource.class);
		DataSource ds = (DataSource) dsControl.getMock();
		ds.getConnection();
		dsControl.setReturnValue(con, 1);
		con.getMetaData();
		conControl.setReturnValue(null, 1);
		con.close();
		conControl.setVoidCallable(1);
		conControl.activate();
		dsControl.activate();
		final List test = new ArrayList();
		JdbcDaoSupport dao = new JdbcDaoSupport() {
			protected void initDao() {
				test.add("test");
			}
		};
		dao.setDataSource(ds);
		dao.afterPropertiesSet();
		assertEquals("Correct SessionFactory", dao.getDataSource(), ds);
		assertEquals("Correct HibernateTemplate", dao.getJdbcTemplate().getDataSource(), ds);
		assertEquals("initDao called", test.size(), 1);
	}

}
