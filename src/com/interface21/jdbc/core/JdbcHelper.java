
package com.interface21.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJBException;
import javax.sql.DataSource;


/**
 * Utility class to use for JDBC queries from J2EE applications.
 * This avoids the need for writing raw SQL.
 * <br/>Connections are obtained using JNDI data sources,
 * so this class will only work within a J2EE application.
 * It is probably best used from a stateless session bean; however
 * it could also be used from within a web application.
 * @author  Rod Johnson
 * @since May 30, 2001
 */
public class JdbcHelper {
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** Column extractor to use */
	private ColumnExtractor columnExtractor;
	
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/** Creates a new JDBCHelper */
	public JdbcHelper(DataSource dataSource) {
		columnExtractor = new DefaultColumnExtractor();
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//---------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------
	public int runSQLFunction(String sql) {
		Integer I = (Integer) runSQLFunction(sql, Integer.class);
		return I.intValue();
	}
	
	/**
	 * 
	 */
	public int runSQLFunction(String sql, int[] types, Object[] args) {
	    //PreparedStatementCreator psc = PreparedStatementCreatorFactory.newPreparedStatementCreator(sql, types, args);
		Integer I = (Integer) runSQLFunction(sql, Integer.class, types, args);
		return I.intValue();
	}
	
	public Object runSQLFunction(final String sql, final Class requiredType, int[] types, Object[] args) throws EJBException {
		FunctionHandler fh = new FunctionHandler(sql, requiredType);
		PreparedStatementCreator psc = PreparedStatementCreatorFactory.newPreparedStatementCreator(sql, types, args);
		jdbcTemplate.query(psc, fh);
		return fh.getFunctionValue();
	}
	
	/**
	 * Run the given SQL function.
	 * @param sql SQL function, such as SELECT MAX(USER_ID) FROM USERS.
	 * Must return only one row.
	 * @param requiredType the class we need to extract the function result as.
	 * @throws EJBException if there is a problem executing the function
	 */
	public Object runSQLFunction(final String sql, final Class requiredType) throws EJBException {
		return runSQLFunction(sql, requiredType, null, null);		
	}	// runSQLFunction
	
	
	/**
	 * Run the given SQL SELECT to return a ResultSet of an array of IDs.
	 * @param sql SQL function, such as SELECT MAX(USER_ID) FROM USERS.
	 * Must return only one row.
	 * @param requiredType the class we need to extract the function result as.
	 * @throws EJBException if there is a problem executing the function
	 */
	public int[] getIDs(final String sql, final Object[] params) throws EJBException {
		Object[] os = getIDs(sql, Integer.class, params);
		int[] is = new int[os.length];
		for (int i = 0; i < is.length; i++)
			is[i] = ((Integer) os[i]).intValue();
		return is;
	}	// getIDs		
	
	
	public Object[] getIDs(final String sql, final Class requiredType, final Object[] params) throws EJBException {
		class IDsHandler extends RowCountCallbackHandler implements PreparedStatementCreator {			
			private List l = new LinkedList();
			
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql);
				// Don't prepare if no params
				if (params != null)
					for (int i = 0; i < params.length; i++) {
						ps.setObject(i + 1, params[i]);
					}
				return ps;
			}
			
			protected void processRow(ResultSet rs, int rowNum) throws SQLException {
				l.add(columnExtractor.extractColumn(1, requiredType, rs));
			}
			
			public Object[] getIDs() {
				return l.toArray();
			}
			
			public String getSql() {
				return sql;
			}
		}
		
		IDsHandler idsh = new IDsHandler();
		jdbcTemplate.query(idsh, idsh);
		return idsh.getIDs();
	}	// getIDs	
	
	public JdbcTemplate getTemplate() {
		return jdbcTemplate;
	}
	
	
	private class FunctionHandler extends RowCountCallbackHandler {
			private Object	obj;
			private Class requiredType;
			private String sql;
			
			public FunctionHandler(String sql, Class requiredType) {
				this.requiredType = requiredType;
				this.sql = sql;
			}
			
			/* This method is
			 * invoked for each row
			 * @param rowNum number of the current row (starting from 0)
			 */
			protected void processRow(ResultSet rs, int rowNum) throws SQLException {
				if (rowNum > 0)
					throw new EJBException("runSQLFunction retrieved more than one row for sql [" + sql + "]: probably not a valid SQL function");
				obj = columnExtractor.extractColumn(1, requiredType, rs);
			}
			
			public Object getFunctionValue() {
				return obj;
			}
		}
	
}	// class JDBCHelper

