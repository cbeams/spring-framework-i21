package com.interface21.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;

import com.interface21.jdbc.datasource.SpringMockPreparedStatement;
import com.interface21.jdbc.datasource.SpringMockStatement;
import com.mockobjects.sql.CommonMockMultiRowResultSet;
import com.mockobjects.sql.MockConnection;
import com.mockobjects.sql.MockMultiRowResultSet;

/**
 * Factory for mock objects used in JDBC testing.
 * Addresses some limitations of standard mock objects from
 * mockobjects.com, such as lack of support for warnings.
 * @author Rod Johnson
 * @since 08-Jan-03
 */
public abstract class MockConnectionFactory {

	/**
	* Constructor for SimpleMockConnection with Statement
	*/
	public static MockConnection statement(
		String sql,
		Object[][] data,
		boolean mustClose,
		SQLException sex,
		SQLWarning warnings) {

		MockConnection mc = new MockConnection();
		mc.setExpectedCloseCalls(mustClose ? 2 : 0);

		SpringMockStatement s = new SpringMockStatement();
		s.setExpectedQueryString(sql);
		
		if (sex != null) {
			s.setupThrowExceptionOnExecute(sex);
		}
		else {

			if (warnings != null)
				s.setupReportWarningOnExecute(warnings);
		}
		
		mc.setupStatement(s);
		CommonMockMultiRowResultSet rs = new MockMultiRowResultSet();
		rs.setupRows(data);
		s.addResultSet(rs);
		return mc;
	}

	/**
	* Constructor for prepared statements
	*/
	public static MockConnection preparedStatement(
		String sql,
		Object[] bindVariables,
		Object[][] data,
		boolean mustClose) {

		return preparedStatement(
			sql,
			bindVariables,
			data,
			mustClose,
			null,
			null);
	}

	public static MockConnection preparedStatement(
		String sql,
		Object[] bindVariables,
		Object[][] data,
		boolean mustClose,
		SQLException sex,
		SQLWarning warnings) {

		MockConnection mc = new MockConnection();
		mc.setExpectedCloseCalls(mustClose ? 2 : 0);

		// What about PS?
		SpringMockPreparedStatement ps = new SpringMockPreparedStatement();
		ps.setExpectedCloseCalls(1);

		if (sex != null) {
			ps.setupThrowExceptionOnExecute(sex);
		}
		else {

			if (warnings != null)
				ps.setupReportWarningOnExecute(warnings);

		}

		//ps.setExpectedQueryString(sql);
		if (bindVariables == null) {
			ps.setExpectingNoSetParameters();
		}
		else {
			ps.addExpectedSetParameters(bindVariables);
		}
		mc.addExpectedPreparedStatement(ps);
		CommonMockMultiRowResultSet rs = new MockMultiRowResultSet();
		rs.setupRows(data);
		ps.addResultSet(rs);

		return mc;
	}

	/**
	* Update: Java really needs named constructors!
	*/
	public static MockConnection updateWithPreparedStatement(
		String sql,
		Object[] bindVariables,
		int updateCount,
		boolean mustClose) {

		return updateWithPreparedStatement(
			sql,
			bindVariables,
			updateCount,
			mustClose,
			null,
			null);
	}

	public static MockConnection updateWithPreparedStatement(
		String sql,
		Object[] bindVariables,
		int updateCount,
		boolean mustClose,
		SQLException sex,
		SQLWarning warnings) {

		MockConnection mc = new MockConnection();
		mc.setExpectedCloseCalls(mustClose ? 2 : 0);

		// What about PS?
		SpringMockPreparedStatement ps = new SpringMockPreparedStatement();
		//ps.setExpectedQueryString(sql);
		if (bindVariables == null) {
			ps.setExpectingNoSetParameters();
		}
		else {
			ps.addExpectedSetParameters(bindVariables);
		}

		if (sex != null) {
			ps.setupThrowExceptionOnExecute(sex);
		}
		else {
			if (warnings != null)
			throw new RuntimeException("Warnings not supported");
				ps.setupReportWarningOnExecute(warnings);
		}

		mc.addExpectedPreparedStatement(ps);
		ps.setupUpdateCount(updateCount);
		ps.setExpectedCloseCalls(1);

		return mc;
	}
	
	/**
	 * Create a new Connection that will return a given mock
	 * prepared statement. This allows us to create the PreparedStatement
	 * as a separate mock object and script it using EasyMock
	 * @param sql
	 * @param mockPs
	 * @return MockConnection
	 */
	public static MockConnection update(
		String sql,
		PreparedStatement mockPs) {

		MockConnection mc = new MockConnection();
		mc.setExpectedCloseCalls(2);

		mc.addExpectedPreparedStatement(mockPs);

		return mc;
	}

}
