/*
 * Created on 17-Feb-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.interface21.jdbc.object;

import java.sql.Types;

import com.mockobjects.sql.MockResultSet;
import junit.framework.TestCase;

import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.mock.SpringMockConnection;
import com.interface21.jdbc.mock.SpringMockDataSource;
import com.interface21.jdbc.mock.SpringMockJdbcFactory;
import com.interface21.jdbc.mock.SpringMockPreparedStatement;

/**
 * @author tcook
 */
public class SqlFunctionTestSuite extends TestCase {

	private static final String FUNCTION = "select count(id) from mytable";
	private static final String FUNCTION_INT = "select count(id) from mytable where myparam = ?";
	private static final String FUNCTION_MIXED = "select count(id) from mytable where myparam = ? and mystring = ?";
	
	private SpringMockDataSource mockDataSource;
	private SpringMockConnection mockConnection;

	public SqlFunctionTestSuite(String name) {
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

	public void testFunction() {
		SpringMockPreparedStatement mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				FUNCTION,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MockResultSet mockResultSet = 
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(14) }
			}, null, mockPreparedStatement);
		mockResultSet.setExpectedNextCalls(2);

		SqlFunction function = new SqlFunction();
		function.setDataSource(mockDataSource);
		function.setSql(FUNCTION);
		function.compile();
		
		int count = function.run();
		assertTrue("Function returned value 14", count == 14);
	}

	public void testTooManyRows() {
		SpringMockPreparedStatement mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				FUNCTION,
				null,
				null,
				null,
				mockConnection);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MockResultSet mockResultSet = 
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(14) },  { new Integer(28) }
			}, null, mockPreparedStatement);
		mockResultSet.setExpectedNextCalls(2);

		SqlFunction function = new SqlFunction(mockDataSource, FUNCTION);
		function.compile();
		
		try {
			int count = function.run();
			fail("Shouldn't continue when too many rows returned");
		} catch (InvalidDataAccessApiUsageException idaauex) {
			// OK 
	}
	}

	public void testFunctionInt() {
		SpringMockPreparedStatement mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
				FUNCTION_INT,
				new Object[] { new Integer(1)},
				null,
				null,
				mockConnection);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MockResultSet mockResultSet = 
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(14) }
			}, null, mockPreparedStatement);
		mockResultSet.setExpectedNextCalls(2);

		SqlFunction function = new SqlFunction(mockDataSource, FUNCTION_INT, new int[] { Types.INTEGER } );
		function.compile();
		
		int count = function.run(1);
		assertTrue("Function returned value 14", count == 14);
	}

	public void testFunctionMixed() {
		SpringMockPreparedStatement mockPreparedStatement =
			SpringMockJdbcFactory.preparedStatement(
		FUNCTION_MIXED,
				new Object[] { new Integer(1), "rod" },
				null,
				null,
				mockConnection);
		mockPreparedStatement.setExpectedExecuteCalls(1);
		mockPreparedStatement.setExpectedCloseCalls(1);

		MockResultSet mockResultSet = 
				SpringMockJdbcFactory
				.resultSet(new Object[][] { { new Integer(14) }
			}, null, mockPreparedStatement);
		mockResultSet.setExpectedNextCalls(2);

		SqlFunction function = new SqlFunction(mockDataSource, FUNCTION_MIXED, new int[] { Types.INTEGER, Types.VARCHAR} );
		function.compile();
		
		int count = function.run(new Object[] {new Integer(1), "rod"});
		assertTrue("Function returned value 14", count == 14);
	}


}
