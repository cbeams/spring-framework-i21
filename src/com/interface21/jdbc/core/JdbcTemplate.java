/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.interface21.dao.DataAccessException;

/**
 * <b>This is the central class in this package.</b>
 * It simplifies the use of JDBC and helps to avoid common errors. It executes
 * core JDBC workflow, leaving application code to provide SQL and extract results.
 * This class executes SQL queries or updates, initating iteration over
 * ResultSets and catching JDBC exceptions and translating them to
 * the generic, more informative, exception hierarchy defined in
 * the com.interface21.dao package.
 * <br>Code using this class need only implement callback interfaces,
 * giving them a clearly defined contract. The PreparedStatementCreator callback
 * interface creates a prepared statement given a Connection provided by this class,
 * providing SQL and any necessary parameters. The RowCallbackHandler interface
 * extracts values from each row of a ResultSet.
 * 
 * <p>The motivation and design of this class is discussed
 * in detail in
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * <br>All SQL issued by this class is logged.
 * <br>Because this class is parameterizable by the callback interfaces and the
 * SQLExceptionTranslater interface, it isn't necessary to subclass it.
 * @author  Rod Johnson
 * @see com.interface21.dao
 * @version $Id$
 * @since May 3, 2001
 */
public class JdbcTemplate {
	
	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/**
	* Create a Java 1.4-style logging category.
	*/
	protected final Logger logger = Logger.getLogger(getClass());

	/** 
	 * Used to obtain connections throughout
	 * the lifecycle of this object. This enables this class to
	 * close connections if necessary.
	 **/
	private DataSource dataSource;
	
	/** 
	 * If this variable is false, we will throw exceptions on SQL warnings
	 */
	private boolean ignoreWarnings = true;
	
	/** Factory to get instance of Helper to translate SQL exceptions */
	private SQLExceptionTranslaterFactory exceptionTranslaterFactory;

	/** Helper to translate SQL exceptions to DataAccessExceptions */
	private SQLExceptionTranslater exceptionTranslater;
	

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------
	/** 
	 * Construct a new JdbcTemplate, given a DataSource to use to obtain connections
	 * @param dataSource J2EE DataSource to use to obtain connections from
	 * @throws InvalidParameterException when dataSource is null
	 */
	public JdbcTemplate(DataSource dataSource) throws InvalidParameterException {
		if (dataSource == null) {
			throw new InvalidParameterException("dataSource", "null");
		}

		this.dataSource = dataSource;
		this.exceptionTranslaterFactory = SQLExceptionTranslaterFactory.getInstance();
		this.exceptionTranslater = this.exceptionTranslaterFactory.getDefaultTranslater(dataSource);
	}


	//-------------------------------------------------------------------------
	// Configuration properties
	//-------------------------------------------------------------------------
	/**
	 * Return whether or not we want to ignore SQLWarnings. 
	 * Default is true
	 */
	public void setIgnoreWarnings(boolean ignoreWarnings) {
		this.ignoreWarnings = ignoreWarnings;
	}
	
	/**
	 * Return whether or not we ignore SQLWarnings
	 * @return whether or not we ignore SQLWarnings.
	 * Default is true.
	 */
	public boolean getIgnoreWarnings() {
		return ignoreWarnings;
	}
	
	
	/**
	 * Set the exception translater used in this class.
	 * If no custom translater is provided, a default is used
	 * which examines the SQLException's SQLState code.
	 * @param exceptionTranslater custom exception translator.
	 */
	public void setExceptionTranslater(SQLExceptionTranslater exceptionTranslater) {
		this.exceptionTranslater = exceptionTranslater;
	}
	
	
	/**
	 * Return the DataSource used by this template
	 * @return the DataSource used by this template
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	//-------------------------------------------------------------------------
	// Public methods
	//-------------------------------------------------------------------------
	/**
	 * Execute a query given static SQL.
	 * Still uses a prepared statement.
	 * @param sql SQL query to execute
	 * @param callbackHandler object that will extract results
	 * @throws DataAccessException if there is any problem executing
	 * the query
	 */
	public void query(String sql, RowCallbackHandler callbackHandler) throws DataAccessException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ReadOnlyResultSet rors = null;
		try {
			con = DataSourceUtils.getConnection(this.dataSource);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			rors = new ReadOnlyResultSet(rs);
			
			if (logger.isInfoEnabled())
				logger.info("Executing static SQL query '" + sql + "'");

			while (rs.next()) {
				callbackHandler.processRow(rors);
			}
			
			SQLWarning warning = ps.getWarnings();
			rs.close();
			
			// Since rors is a wrapper around rs, calling the close() method is 
			// forbidden. Since rs is already closed, we only need to make it 
			// null.
			rors = null;
			
			ps.close();
			
			throwExceptionOnWarningIfNotIgnoringWarnings(warning);
		}
		catch (SQLException ex) {
			throw this.exceptionTranslater.translate("JdbcTemplate.query(sql)", sql, ex);
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(this.dataSource, con);
		}
	} 	// query
	

	/**
	 * Query using a prepared statement. Most other query methods use
	 * this method.
	 * @param psc Callback handler that can create a PreparedStatement
	 * given a Connection
	 * @param callbackHandler object that will extract results,
	 * one row at a time
	 * @throws DataAccessException if there is any problem
	 */
	public void query(PreparedStatementCreator psc, RowCallbackHandler callbackHandler) throws DataAccessException {
		Connection con = null;
		ResultSet rs = null;
		ReadOnlyResultSet rors = null;
		try {
			con = DataSourceUtils.getConnection(this.dataSource);
			PreparedStatement ps = psc.createPreparedStatement(con);
			if (logger.isInfoEnabled())
				logger.info("Executing SQL query using PreparedStatement: [" + psc + "]");
			rs = ps.executeQuery();
			rors = new ReadOnlyResultSet(rs);

			while (rs.next()) {
				if (logger.isDebugEnabled())
					logger.debug("Processing row of ResultSet");
				callbackHandler.processRow(rors);
			}
			
			SQLWarning warning = ps.getWarnings();
			rs.close();
			
			// Since rors is a wrapper around rs, calling the close() method is 
			// forbidden. Since rs is already closed, we only need to make it 
			// null.
			rors = null;

			ps.close();
			throwExceptionOnWarningIfNotIgnoringWarnings(warning);
		}
		catch (SQLException ex) {
			throw this.exceptionTranslater.translate("JdbcTemplate.query(psc) with PreparedStatementCreator [" + psc + "]", null, ex);
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(this.dataSource, con);
		}
	} 	// query


	/**
	 * Issue a single SQL update.
	 * @param sql static SQL to execute
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem. 
	 */
	public int update(final String sql) throws DataAccessException {
		if (logger.isInfoEnabled())
			logger.info("Running SQL update '" + sql + "'");
		return update(PreparedStatementCreatorFactory.newPreparedStatementCreator(sql));
	}

	/**
	 * Issue an update using a PreparedStatementCreator to provide SQL and any required
	 * parameters
	 * @param psc helper: callback object that provides SQL and any necessary parameters
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	public int update(PreparedStatementCreator psc) throws DataAccessException {
		return update(new PreparedStatementCreator[] { psc })[0];
	}

	/**
	 * Issue multiple updates using multiple PreparedStatementCreators to provide SQL and any required
	 * parameters
	 * @param pscs array of helpers: callback object that provides SQL and any necessary parameters
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	public int[] update(PreparedStatementCreator[] pscs) throws DataAccessException {
		Connection con = null;
		int index = 0;
		try {
			con = DataSourceUtils.getConnection(this.dataSource);
			int[] retvals = new int[pscs.length];
			for (index = 0; index < retvals.length; index++) {
				PreparedStatement ps = pscs[index].createPreparedStatement(con);
				retvals[index] = ps.executeUpdate();
				if (logger.isInfoEnabled())
					logger.info("JDBCTemplate: update affected " + retvals[index] + " rows");
				ps.close();
			}
			
			// Don't worry about warnings, as we're more likely to get exception on updates
			// (for example on data truncation)
			return retvals;
		}
		catch (SQLException ex) {
			throw this.exceptionTranslater.translate("processing update " +
				(index + 1) + " of " + pscs.length + "; update was [" + pscs[index] + "]", null, ex);
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(this.dataSource, con);
		}
	}	// update[]
	
	
	/**
	 * Issue an update using a PreparedStatementSetter to set bind parameters,
	 * with given SQL. Simpler than using a PreparedStatementCreator
	 * as this method will create the PreparedStatement: the
	 * PreparedStatementSetter has only to set parameters.
	 * @param sql SQL, containing bind parameters
	 * @param pss helper that sets bind parameters. If this is null
	 * we run an update with static SQL
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 * TODO add a similar query method
	 */
	public int update(final String sql, final PreparedStatementSetter pss) throws DataAccessException {
		if (pss == null) {
			return update(sql);
		}
		
		return update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql);
				pss.setValues(ps);
				return ps;
			}
			public String getSql() {
				return sql;
			}
		});
	}
	
	
	/**
	 * Issue multiple updates using JDBC 2.0 batch updates and PreparedStatementSetters to 
	 * set values on a PreparedStatement created by this method
	 * @param sql defining PreparedStatement that will be reused.
	 * All statements in the batch will use the same SQL.
	 * @param setter object to set parameters on the
	 * PreparedStatement created by this method
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	public int[] batchUpdate(String sql, BulkPreparedStatementSetter setter) throws DataAccessException {
		Connection con = null;
		try {
			con = DataSourceUtils.getConnection(this.dataSource);
			PreparedStatement ps = con.prepareStatement(sql);
			int batchSize = setter.getBatchSize();
			for (int i = 0; i < batchSize; i++) {
				setter.setValues(ps, i);
				ps.addBatch();
			}
			
			int[] retvals = ps.executeBatch();
			
			ps.close();
			return retvals;
		}
		catch (SQLException ex) {
			throw this.exceptionTranslater.translate("processing batch update " +
				" with size=" + setter.getBatchSize() + "; update was [" + sql + "]", sql, ex);
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(this.dataSource, con);
		}
	}	// batchUpdate
	
	
	/**
	 * Convenience method to throw a JdbcSqlWarningException if we're
	 * not ignoring warnings
	 * @param warning warning from current statement. May be null,
	 * in which case this method does nothing.
	 */
	private void throwExceptionOnWarningIfNotIgnoringWarnings(SQLWarning warning) throws SQLWarningException {
		if (warning != null) {
			if (this.ignoreWarnings) {
				logger.warn("SQLWarning ignored: " + warning); 
			}
			else {
				throw new SQLWarningException("Warning not ignored", warning);
			}
		}
	}

} 	// class JdbcTemplate