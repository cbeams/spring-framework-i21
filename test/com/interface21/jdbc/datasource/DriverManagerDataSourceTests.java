/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import com.mockobjects.sql.MockConnection;


import junit.framework.TestCase;

 /**
 * @author Rod Johnson
 * @version $Id$
 */
public class DriverManagerDataSourceTests extends TestCase {

	/**
	 * Constructor for DriverManagerDataSourceTests.
	 * @param arg0
	 */
	public DriverManagerDataSourceTests(String arg0) {
		super(arg0);
	}
	
	public void testValidUsage() throws Exception {
		final String url = "url";
		final String uname = "uname";
		final String pwd = "pwd";
		final MockConnection con = new MockConnection();
		// Ensure the connection is set to autocommit before it's returned to us
		con.setExpectedAutoCommit(true);
		class TestDriverManagerDataSource extends DriverManagerDataSource {
			protected Connection getConnectionFromDriverManager(String purl, String pusername, String ppassword)
				throws SQLException {
					assertTrue(purl.equals(url));
					assertTrue(pusername.equals(uname));
					assertTrue(ppassword.equals(pwd));
				return con;
			}
		}
		
		DriverManagerDataSource ds = new TestDriverManagerDataSource();
		ds.setUrl(url);
		ds.setPassword(pwd);
		ds.setUsername(uname);
		//ds.setDriverClassName("foobar");
		Connection actualCon = ds.getConnection();
		assertTrue(actualCon == con);
		
		assertTrue(ds.getUrl().equals(url));
		assertTrue(ds.getPassword().equals(pwd));
		assertTrue(ds.getUsername().equals(uname));
		
		assertTrue(ds.shouldClose(actualCon));
		con.verify();
	}
	
	public void testInvalidClassname() throws Exception {
		final String url = "url";
		final String uname = "uname";
		final String pwd = "pwd";
		String bogusClassname = "foobar";
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setUrl(url);
		ds.setPassword(pwd);
		ds.setUsername(uname);
		try {
			ds.setDriverClassName(bogusClassname);
			fail();
		}
		catch (CannotGetJdbcConnectionException ex) {
			// Check the message helpfully included the classname
			assertTrue(ex.getMessage().indexOf(bogusClassname) != -1);
			assertTrue(ex.getRootCause() instanceof ClassNotFoundException);
		}
	}

}
