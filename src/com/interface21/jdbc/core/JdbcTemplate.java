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
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.interface21.dao.DataAccessException;
import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.datasource.DataSourceUtils;

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
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Yann Caroff
 * @author Thomas Risberg
 * @author Isabelle Muszynski
 * @see com.interface21.dao
 * @version $Id$
 * @since May 3, 2001
 */
public class JdbcTemplate {

	/**
	 * Constant for use as a parameter to query methods to force use of a PreparedStatement
	 * rather than a Statement, even when there are no bind parameters.
	 * For example, query(sql, JdbcTemplate.PREPARE_STATEMENT, callbackHandler)
	 * will force the use of a JDBC PreparedStatement even if the SQL
	 * passed in has no bind parameters.
	 */
	public static final PreparedStatementSetter PREPARE_STATEMENT = new PreparedStatementSetter() {
		public void setValues(PreparedStatement ps) throws SQLException {
			// do nothing
		}
	};

	//-------------------------------------------------------------------------
	// Instance data
	//-------------------------------------------------------------------------
	/**
	 * Create a Log4j logging category.
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
	 * Construct a new JdbcTemplate.
	 * Note: The DataSource has to be set before using the instance.
	 * This constructor can be used to prepare a JdbcTemplate via a BeanFactory,
	 * typically setting the DataSource via setDataSourceName.
	 * @see #setDataSource
	 * @see #setDataSourceName
	 */
	public JdbcTemplate() {
	}

	/**
	 * Construct a new JdbcTemplate, given a DataSource to use to obtain connections
	 * @param dataSource J2EE DataSource to use to obtain connections from
	 * @throws InvalidParameterException when dataSource is null
	 */
	public JdbcTemplate(DataSource dataSource) throws InvalidParameterException {
		setDataSource(dataSource);
	}


	//-------------------------------------------------------------------------
	// Configuration properties
	//-------------------------------------------------------------------------

	public void setDataSourceName(String dataSourceName) throws InvalidParameterException {
		setDataSource(DataSourceUtils.getDataSourceFromJndi(dataSourceName));
	}

	public void setDataSource(DataSource dataSource) throws InvalidParameterException {
		if (dataSource == null) {
			throw new InvalidParameterException("dataSource", "null");
		}
		this.dataSource = dataSource;
		this.exceptionTranslaterFactory = SQLExceptionTranslaterFactory.getInstance();
		this.exceptionTranslater = this.exceptionTranslaterFactory.getDefaultTranslater(dataSource);
	}

	/**
	 * Return the DataSource used by this template
	 * @return the DataSource used by this template
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

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


	//-------------------------------------------------------------------------
	// Public methods
	//-------------------------------------------------------------------------

	/**
	 * Execute a query given static SQL.
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to execute
	 * a static query with a PreparedStatement, use the overloaded query method
	 * with a the PREPARE_STATEMENT PreparedStatementSetter constant as a parameter.
	 * <br>
	 * In most cases the query() method should be preferred to the parallel
	 * doWithResultSetXXXX() method. The doWithResultSetXXXX() methods
	 * are included to allow full control over the extraction of data
	 * from ResultSets and to facilitate integration with third-party
	 * software.
	 * @param sql SQL query to execute
	 * @param callbackHandler object that will extract results
	 * @throws DataAccessException if there is any problem executing
	 * the query
	 */
	public void query(String sql, RowCallbackHandler callbackHandler) throws DataAccessException {
		doWithResultSetFromStaticQuery(sql, new RowCallbackHandlerResultSetExtracter(callbackHandler));
	}

	/**
	 * Execute a query given static SQL.
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to execute
	 * a static query with a PreparedStatement, use the overloaded query method
	 * with a NOP PreparedStatement setter as a parameter.
	 * @param sql SQL query to execute
	 * @param rse object that will extract all rows of results
	 * @throws DataAccessException if there is any problem executing
	 * the query
	 */
	public void doWithResultSetFromStaticQuery(String sql, ResultSetExtracter rse) throws DataAccessException {
		if (sql == null)
			throw new InvalidDataAccessApiUsageException("SQL may not be null");
		if (containsBindVariables(sql))
			throw new InvalidDataAccessApiUsageException("Cannot execute '" + sql + "' as a static query: it contains bind variables");

		Connection con = null;
		Statement s = null;
		ResultSet rs = null;
		try {
			con = DataSourceUtils.getConnection(this.dataSource);
			s = con.createStatement();
			rs = s.executeQuery(sql);

			if (logger.isInfoEnabled())
				logger.info("Executing static SQL query '" + sql + "' using a java.sql.Statement");

			rse.extractData(rs);

			SQLWarning warning = s.getWarnings();
			rs.close();
			s.close();

			throwExceptionOnWarningIfNotIgnoringWarnings(warning);
		}
		catch (SQLException ex) {
			throw this.exceptionTranslater.translate("JdbcTemplate.query(sql)", sql, ex);
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(con, this.dataSource);
		}
	} 	// doWithResultSetFromStaticQuery


	/**
	 * Query using a prepared statement.
	 * @param psc Callback handler that can create a PreparedStatement
	 * given a Connection
	 * @param callbackHandler object that will extract results,
	 * one row at a time
	 * @throws DataAccessException if there is any problem
	 */
	public void query(PreparedStatementCreator psc, RowCallbackHandler callbackHandler) throws DataAccessException {
		doWithResultSetFromPreparedQuery(psc, new RowCallbackHandlerResultSetExtracter(callbackHandler));
	}


	/**
	 * Query using a prepared statement. Most other query methods use
	 * this method.
	 * @param psc Callback handler that can create a PreparedStatement
	 * given a Connection
	 * @param rse object that will extract results.
	 * @throws DataAccessException if there is any problem
	 */
	public void doWithResultSetFromPreparedQuery(PreparedStatementCreator psc, ResultSetExtracter rse) throws DataAccessException {
		Connection con = null;
		ResultSet rs = null;
		try {
			con = DataSourceUtils.getConnection(this.dataSource);
			PreparedStatement ps = psc.createPreparedStatement(con);
			if (logger.isInfoEnabled())
				logger.info("Executing SQL query using PreparedStatement: [" + psc + "]");
			rs = ps.executeQuery();

			rse.extractData(rs);

			SQLWarning warning = ps.getWarnings();
			rs.close();
			ps.close();
			throwExceptionOnWarningIfNotIgnoringWarnings(warning);
		}
		catch (SQLException ex) {
			throw this.exceptionTranslater.translate("JdbcTemplate.query(psc) with PreparedStatementCreator [" + psc + "]", null, ex);
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(con, this.dataSource);
		}
	} 	// doWithResultSetFromPreparedQuery


	/**
	 * Query given SQL to create a prepared statement from SQL and a
	 * PreparedStatementSetter implementation that knows how to bind values
	 * to the query.
	 * @param sql SQL to execute
	 * @param pss object that knows how to set values on the prepared statement.
	 * If this is null, the SQL will be assumed to contain no bind parameters.
	 * Even if there are no bind parameters, this object may be used to
	 * set fetch size and other performance options.
	 * @param callbackHandler object that will extract results
	 * @throws DataAccessException if the query fails
	 */
	public void query(final String sql, final PreparedStatementSetter pss, RowCallbackHandler callbackHandler) throws DataAccessException {
		if (sql == null)
			throw new InvalidDataAccessApiUsageException("SQL may not be null");

		if (pss == null) {
			// Check there are no bind parameters, in which case pss could not be null
			if (containsBindVariables(sql))
				throw new InvalidDataAccessApiUsageException("SQL '" + sql + "' requires at least one bind variable, but PreparedStatementSetter parameter was null");
			query(sql, callbackHandler);
		}
		else {
			// Wrap it in a new PreparedStatementCreator
			query(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql);
					pss.setValues(ps);
					return ps;
				}

				public String getSql() {
					return sql;
				}
			}, callbackHandler);
		}
	}

	/**
	 * Return whether the given SQL String contains bind variables
	 */
	private boolean containsBindVariables(String sql) {
		return sql.indexOf("?") != -1;
	}


	/**
	 * Helper class for inserts with auto-generated keys.
	 * Contains the new key value and the number of affected rows
	 **/
	public static class InsertRetval {

		/**
		 * Default constructor
		 **/
		public InsertRetval() {
		}

		/**
		 * Constructor
		 * @param rowsAffected the number of rows inserted (will be 0 or 1)
		 * @param key the newly generated primary key used for this insert
		 **/
		public InsertRetval(int rowsAffected, Object key) {
			this.rowsAffected = rowsAffected;
			this.key = key;
		}

		/**
		 * Retrieve the number of affected rows (will be 0 or 1)
		 * @return the number of affected rows
		 */
		public int getRowsAffected() {
			return rowsAffected;
		}

		/**
		 * Set the number of affected rows
		 * @param rowsAffected the number of affected rows, should be 0 or 1
		 * @exception IllegalArgumentException if rowsAffected is not 0 or 1
		 **/
		public void setRowsAffected(int rowsAffected) {
			if (rowsAffected != 0 && rowsAffected != 1)
				throw new IllegalArgumentException("Insert can only affect zero or one rows");
			this.rowsAffected = rowsAffected;
		}

		/**
		 * Retrieve the newly generated key
		 * @return the key
		 **/
		public Object getKey() {
			return key;
		}

		/**
		 * Set the newly generated key
		 * @param key the key
		 **/
		public void setKey(Object key) {
			this.key = key;
		}

		private int rowsAffected;
		private Object key;
	}

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
	 * Issue a single SQL insert using a key generator
	 * @param sql static SQL to execute
	 * @param binder the callback interface for binding the generated key value into the prepared statement
	 * @param keygen the key generator instance to use
	 * @param keyClass the class of the key to return {@link DataFieldMaxValueIncrementer#nextValue  nextValue}
	 * @return the number of rows affected
	 * @throws IllegalArgumentException if the key class is of an unsupported type
	 * @throws DataAccessException if there is any problem.
	 */
	public InsertRetval update(final String sql, KeyBinder binder, DataFieldMaxValueIncrementer keygen, Class keyClass) throws DataAccessException {
		if (logger.isInfoEnabled())
			logger.info("Running SQL update '" + sql + "'");
		return update(PreparedStatementCreatorFactory.newPreparedStatementCreator(sql), binder, keygen, keyClass);
	}

	/**
	 * Issue an update using a PreparedStatementCreator to provide SQL and any required
	 * parameters
	 * @param psc helper: callback object that provides SQL and any necessary parameters
	 * @return the number of rows affected
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	public int update(PreparedStatementCreator psc) throws DataAccessException {
		return update(new PreparedStatementCreator[]{psc})[0];
	}

	/**
	 * Issue an insert using a PreparedStatementCreator to provide SQL, any required
	 * parameters and a key generator
	 * @param psc helper: callback object that provides SQL and any necessary parameters
	 * @param binder the callback interface for binding the generated key value into the prepared statement
	 * @param keygen the key generator instance to use
	 * @param keyClass the class of the key to return {@link DataFieldMaxValueIncrementer#nextValue  nextValue}
	 * @return the number of rows affected
	 * @throws IllegalArgumentException if the key class is of an unsupported type
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	public InsertRetval update(PreparedStatementCreator psc, KeyBinder binder, DataFieldMaxValueIncrementer keygen, Class keyClass) throws DataAccessException {
		return update(new PreparedStatementCreator[]{psc}, binder, keygen, keyClass)[0];
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
			DataSourceUtils.closeConnectionIfNecessary(con, this.dataSource);
		}
	}


	/**
	 * Issue multiple inserts using multiple PreparedStatementCreators to provide SQL, any required
	 * parameters and a key generator
	 * @param pscs array of helpers: callback object that provides SQL and any necessary parameters
	 * @param binder the callback interface for binding the generated key value into the prepared statement
	 * @param keygen the key generator instance to use
	 * @param keyClass the class of the key to return {@link DataFieldMaxValueIncrementer#nextValue  nextValue}
	 * @return an array of the number of rows affected by each statement
	 * @throws IllegalArgumentException if the key class is of an unsupported type
	 * @throws DataAccessException if there is any problem issuing the update
	 */
	public InsertRetval[] update(PreparedStatementCreator[] pscs, KeyBinder binder, DataFieldMaxValueIncrementer keygen, Class keyClass) throws DataAccessException {
		Connection con = null;
		int index = 0;
		try {
			con = DataSourceUtils.getConnection(this.dataSource);
			InsertRetval[] retvals = new InsertRetval[pscs.length];
			for (index = 0; index < retvals.length; index++) {
				retvals[index] = new InsertRetval();
				PreparedStatement ps = pscs[index].createPreparedStatement(con);
				binder.bind(ps, retvals[index].key = keygen.nextValue(keyClass));
				retvals[index].rowsAffected = ps.executeUpdate();
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
			DataSourceUtils.closeConnectionIfNecessary(con, this.dataSource);
		}
	}

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
	 * Issue an insert using a PreparedStatementSetter to set bind parameters,
	 * with given SQL. Simpler than using a PreparedStatementCreator
	 * as this method will create the PreparedStatement: the
	 * PreparedStatementSetter has only to set parameters.
	 * @param sql SQL, containing bind parameters
	 * @param pss helper that sets bind parameters. If this is null
	 * we run an update with static SQL
	 * @param binder the callback interface for binding the generated key value into the prepared statement
	 * @param keygen the key generator instance to use
	 * @param keyClass the class of the key to return {@link DataFieldMaxValueIncrementer#nextValue  nextValue}
	 * @return the number of rows affected and the newly generated key
	 * @throws DataAccessException if there is any problem issuing the update
	 * @throws IllegalArgumentException if the key class is of an unsupported type
	 */
	public InsertRetval update(final String sql, final PreparedStatementSetter pss, KeyBinder binder, DataFieldMaxValueIncrementer keygen, Class keyClass)
	    throws DataAccessException {
		if (pss == null) {
			return update(sql, binder, keygen, keyClass);
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
		}, binder, keygen, keyClass);
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
	public int[] batchUpdate(String sql, BatchPreparedStatementSetter setter) throws DataAccessException {
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
			DataSourceUtils.closeConnectionIfNecessary(con, this.dataSource);
		}
	}


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


	/**
	 * Adapter to enable use of a RowCallbackHandler inside a
	 * ResultSetExtracter. Uses a ReadOnlyResultSet to
	 * ensure that the underlying ResultSet isn't used illegally
	 * (for example, for navigation).
	 */
	private final class RowCallbackHandlerResultSetExtracter implements ResultSetExtracter {

		/**
		 * RowCallbackHandler to use to extract data
		 */
		private RowCallbackHandler callbackHandler;

		/**
		 * Construct a new ResultSetExtracter that will use the given
		 * RowCallbackHandler to process each row.
		 */
		public RowCallbackHandlerResultSetExtracter(RowCallbackHandler callbackHandler) {
			this.callbackHandler = callbackHandler;
		}

		/**
		 * @see com.interface21.jdbc.core.ResultSetExtracter#extractData(java.sql.ResultSet)
		 */
		public void extractData(ResultSet rs) throws SQLException {
			ReadOnlyResultSet rors = new ReadOnlyResultSet(rs);
			while (rs.next()) {
				callbackHandler.processRow(rors);
			}
			//	Since rors is a wrapper around rs, calling the close() method is
			// forbidden. Since rs is already closed, we only need to make it
			// null.
			rors = null;
		}
	}

}
