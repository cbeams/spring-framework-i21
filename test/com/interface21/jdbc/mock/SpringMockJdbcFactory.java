/*
 * SpringMockJdbcFactory.java
 *
 * Copyright (C) 2002 by Interprise Software.  All rights reserved.
 */
package com.interface21.jdbc.mock;

import java.sql.SQLException;
import java.sql.SQLWarning;

import com.mockobjects.sql.MockMultiRowResultSet;
import com.mockobjects.sql.MockResultSetMetaData;

/**
 * @task enter type comments
 * 
 * @author <a href="mailto:tcook@interprisesoftware.com">Trevor D. Cook</a>
 * @version $Id$
 */
public class SpringMockJdbcFactory {

	private SpringMockJdbcFactory() {
	}

	public static SpringMockDataSource dataSource() {
		SpringMockDataSource ds = new SpringMockDataSource();
		return ds;
	}

	public static SpringMockConnection connection(boolean mustClose) {
		return connection(mustClose, null);
	}

	public static SpringMockConnection connection(
		boolean mustClose,
		SpringMockDataSource ds) {
		SpringMockConnection con = new SpringMockConnection();

		if (ds != null) {
			ds.setupConnection(con);
		}

		return con;
	}

	public static SpringMockPreparedStatement preparedStatement(
		String sql,
		Object[] bindVariables,
//	Object[][] data,
		SQLException sex,
		SQLWarning warning) {

		return preparedStatement(sql, bindVariables, sex, warning, null);
	}

	public static SpringMockPreparedStatement preparedStatement(
		String sql,
		Object[] bindVariables,
//	Object[][] data,
		SQLException sex,
		SQLWarning warning, SpringMockConnection con) {

		SpringMockPreparedStatement ps = new SpringMockPreparedStatement();

		if (sex != null) {
			ps.setupThrowExceptionOnExecute(sex);
		} else if (warning != null) {
			ps.setupReportWarningOnExecute(warning);
		}

		if (bindVariables == null) {
			ps.setExpectingNoSetParameters();
		} else {
			ps.addExpectedSetParameters(bindVariables);
		}

		if (con != null) {
			con.addExpectedPreparedStatement(ps);
		}

		return ps;
	}

	public static MockMultiRowResultSet resultSet(Object[][] data, String[] columnNames) {
		return resultSet(data, columnNames);
	}

	public static MockMultiRowResultSet resultSet(Object[][] data, String[] columnNames, SpringMockPreparedStatement ps) {
		MockMultiRowResultSet rs = new MockMultiRowResultSet();
		rs.setExpectedCloseCalls(1);

		rs.setupRows(data);
		
		if (columnNames != null) {
			rs.setupColumnNames(columnNames);
		}

		if (ps != null) {			
			ps.addResultSet(rs);
		}

		rs.setupMetaData(new MockResultSetMetaData());
		
		return rs;
	}

}
