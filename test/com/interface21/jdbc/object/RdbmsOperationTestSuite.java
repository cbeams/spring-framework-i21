/*
 * Created on 17-Feb-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.interface21.jdbc.object;

import java.sql.Types;

import junit.framework.TestCase;

import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.core.SqlParameter;
import com.interface21.jdbc.datasource.SpringMockConnection;
import com.interface21.jdbc.datasource.SpringMockDataSource;
import com.interface21.jdbc.datasource.SpringMockJdbcFactory;

/**
 * @author tcook
 */
public class RdbmsOperationTestSuite extends TestCase {

	private SpringMockDataSource mockDataSource;
	private SpringMockConnection mockConnection;

	public RdbmsOperationTestSuite(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		mockDataSource = SpringMockJdbcFactory.dataSource();
		mockConnection =
			SpringMockJdbcFactory.connection(false, mockDataSource);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		mockDataSource.verify();
		mockConnection.verify();
	}

	public void testEmptyDataSource() {
		mockDataSource.setExpectedConnectCalls(0);
		
		TestRdbmsOperation operation = new TestRdbmsOperation();
		operation.setSql("select * from mytable");
		try {
		operation.compile();
		fail("Shouldn't allow compiling without data source");
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
		
	}

	public void testEmptySql() {
		mockDataSource.setExpectedConnectCalls(0);
		
		TestRdbmsOperation operation = new TestRdbmsOperation();
		operation.setDataSource(mockDataSource);
		try {
		operation.compile();
		fail("Shouldn't allow compiling without sql statement");
 
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testSetTypeAfterCompile() {
		mockDataSource.setExpectedConnectCalls(0);
		
		TestRdbmsOperation operation = new TestRdbmsOperation();
		operation.setDataSource(mockDataSource);
		operation.setSql("select * from mytable");
		operation.compile();
		try {
		operation.setTypes(new int[] {Types.INTEGER });
		fail("Shouldn't allow setting parameters after compile");
 
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testDeclareParameterAfterCompile() {
		mockDataSource.setExpectedConnectCalls(0);
		
		TestRdbmsOperation operation = new TestRdbmsOperation();
		operation.setDataSource(mockDataSource);
		operation.setSql("select * from mytable");
		operation.compile();
		try {
		operation.declareParameter(new SqlParameter(Types.INTEGER));
		fail("Shouldn't allow setting parameters after compile");
 
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testTooFewParameters() {
		mockDataSource.setExpectedConnectCalls(0);
		
		TestRdbmsOperation operation = new TestRdbmsOperation();
		operation.setDataSource(mockDataSource);
		operation.setSql("select * from mytable");
		operation.setTypes(new int[] { Types.INTEGER });
		operation.compile();
		try {
			operation.validateParameters(null);
			fail("Shouldn't validate without enough parameters"); 
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testTooManyParameters() {
		mockDataSource.setExpectedConnectCalls(0);
		
		TestRdbmsOperation operation = new TestRdbmsOperation();
		operation.setDataSource(mockDataSource);
		operation.setSql("select * from mytable");
		operation.compile();
		try {
			operation.validateParameters(new Object[] { new Integer(1), new Integer(2) });
			fail("Shouldn't validate with too many parameters"); 
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK
		}
	}

	public void testCompileTwice() {
		mockDataSource.setExpectedConnectCalls(0);
		
		TestRdbmsOperation operation = new TestRdbmsOperation();
		operation.setDataSource(mockDataSource);
		operation.setSql("select * from mytable");
		operation.setTypes(null);
		operation.compile();
		operation.compile();
	}

}

class TestRdbmsOperation extends RdbmsOperation {
	
	protected void compileInternal()
		throws InvalidDataAccessApiUsageException {
		// empty
	}

}