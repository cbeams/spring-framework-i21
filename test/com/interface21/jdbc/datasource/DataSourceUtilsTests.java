/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.easymock.EasyMock;
import org.easymock.MockControl;

import junit.framework.TestCase;

/**
 * 
 * @author Rod Johnson
 * @since 10-May-2003
 * @version $Revision$
 */
public class DataSourceUtilsTests extends TestCase {

	/**
	 * Constructor for DataSourceUtilsTest.
	 * @param arg0
	 */
	public DataSourceUtilsTests(String arg0) {
		super(arg0);
	}

	/**
	 * Test that we can invoke other methods on the proxy, but close
	 * is suppressed
	 * @throws SQLException
	 */
	public void testGetCloseSuppressingConnectionProxy() throws SQLException {
		MockControl mc = EasyMock.controlFor(Connection.class);
		Connection mockCon = (Connection) mc.getMock();
		// Test a normal method
		mockCon.isClosed();
		mc.setReturnValue(false, 1);
		mc.activate();
		
		
		Connection noclose = DataSourceUtils.getCloseSuppressingConnectionProxy(mockCon);
		assertTrue(!noclose.isClosed());
		
		// Shouldn't propagate to the mock object
		for (int i = 0; i < 10; i++)
			noclose.close();
		
		mc.verify();
	}

}
