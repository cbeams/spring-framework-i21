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

package com.interface21.jdbc.object;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.datasource.DataSourceUtils;
import com.interface21.jdbc.core.SQLExceptionTranslater;
import com.interface21.jdbc.core.SQLExceptionTranslaterFactory;
import com.interface21.jdbc.core.SQLStateSQLExceptionTranslater;
import com.interface21.jdbc.core.SqlParameter;

/**
 * Superclass for object abstractions of RDBMS stored procedures.
 * This class is abstract and its execute methods are protected, preventing use other than through
 * a subclass that offers tighter typing.
 * <br>The inherited sql property is the name of the stored procedure in the RDBMS.
 * Note that JDBC 3.0 introduces named parameters, although the other features provided
 * by this class are still necessary in JDBC 3.0.
 * @author Rod Johnson
 * @version $Id$
 */
public abstract class StoredProcedure extends RdbmsOperation {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	/** 
	 * Call string as defined in java.sql.CallableStatement.
	 * String of form {call add_invoice(?, ?, ?)} 
	 * or {? = call get_invoice_count(?)} if isFunction is set to true 
	 * Updated after each parameter is added.
	 */
	private String callString;

	/** 
	 * Flag used to indicate that this call is for a function and to 
	 * use the {? = call get_invoice_count(?)} syntax. 
	 */ 
	private boolean isFunction = false;
	
	/** Factory to get instance of Helper to translate SQL exceptions */
	private SQLExceptionTranslaterFactory exceptionTranslaterFactory;
	/** Helper to translate SQL exceptions to DataAccessExceptions */
	private SQLExceptionTranslater exceptionTranslater;
	
	
	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	/**
	 * Allow use as a bean
	 */
	protected StoredProcedure() {
		this.exceptionTranslater = new SQLStateSQLExceptionTranslater();
	}
	
	/**
	 * Set the exception translater used in this class.
	 * As in the JdbcTemplate class, this can be parameterized
	 * @see com.interface21.jdbc.core.SQLExceptionTranslater
	 */
	public void setExceptionTranslater(SQLExceptionTranslater exceptionTranslater) {
		this.exceptionTranslater = exceptionTranslater;
	}

	/**
	 * Create a new object wrapper for a stored procedure.
	 * @param ds DataSource to use throughout the lifetime
	 * of this object to obtain connections
	 * @param name name of the stored procedure in the database.
	 */
	protected StoredProcedure(DataSource ds, String name) {
		setDataSource(ds);
		setSql(name);
		this.exceptionTranslaterFactory = SQLExceptionTranslaterFactory.getInstance();
		this.exceptionTranslater = this.exceptionTranslaterFactory.getDefaultTranslater(ds);
	}


	//---------------------------------------------------------------------
	// Overriden methods
	//---------------------------------------------------------------------
	/**
	 * Overridden method.
	 * Add a parameter.
	 * <b>NB: Calls to addParameter must be made in the same
	 * order as they appear in the database's stored procedure parameter
	 * list.</b> Names are purely used to help mapping
	 * @param p Parameter object (as defined in the Parameter
	 * inner class)
	 */
	public void declareParameter(SqlParameter p) throws InvalidDataAccessApiUsageException {
		if (p.getName() == null)
			throw new InvalidDataAccessApiUsageException("Parameters to stored procedures must have names as well as types");
		super.declareParameter(p);
	}


	/**
	 * Override of NOP RdbmsOperation.compileInternal() to 
	 * ensure that the call string is up to date before invoking
	 * the RDBMS stored procedure.
	 */
	protected void compileInternal() {
		List parameters = getDeclaredParameters();
		int firstParameter = 0;
		if (isFunction) {
			callString = "{? = call " + getSql() + "(";
			firstParameter = 1;
		} 
		else {
			callString = "{call " + getSql() + "(";
		}
		for (int i = firstParameter; i < parameters.size(); i++) {
			if (i > firstParameter)
				callString += ", ";
			callString += "?";
		}
		callString += ")}";
		logger.info("Compiled stored procedure. Call string is [" + callString + "]");
	}
	
	
	//---------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------
	/** 
	 * Execute the stored procedure. Subclasses should define a strongly typed
	 * execute method (with a meaningful name) that invokes this method, populating
	 * the input map and extracting typed values from the output map. Subclass
	 * execute methods will often take domain objects as arguments and return values.
	 * Alternatively, they can return void.
	 * @param inParams map of input parameters, keyed by name as in parameter
	 * declarations. Output parameters need not (but can be) included in this map.
	 * It is legal for map entries to be null, and this will produce the correct
	 * behavior using a NULL argument to the stored procedure.
	 * @return map of output params, keyed by name as in parameter declarations.
	 * Output parameters will appear here, with their values after the 
	 * stored procedure has been called.
	 */
	protected Map execute(final Map inParams) {
		return execute(new ParameterMapper() {
			public Map createMap(Connection con) throws SQLException {
				return inParams;
			}
		});
	}


	/**
	 * Execute the stored procedure. All parameters
	 * must have been added before any calls are made
	 * to this method.
	 * <br>This method is provided as a more powerful alternative to the
	 * execute(Map) method for special cases where we need the Connection
	 * to populate the input map. We might need to do this to use proprietary
	 * features of the target database: for example, to use RDBMS-specific
	 * types.
	 * <br/>Maps, using parameter names given in addParameter(),
	 * are used to hold input and output parameters.
	 * @param inParams Map of input parameters
	 * @return map of output parameters. In/out parameters
	 * will appear here, with their value after the stored
	 * procedure has been called.
	 */
	protected Map execute(ParameterMapper mapper) throws InvalidDataAccessApiUsageException {
		if (!isCompiled())
			throw new InvalidDataAccessApiUsageException("Stored procedure must be compiled before execution");
		
		Connection con = null;
		DataSource ds = getDataSource();
		try {
			con = ds.getConnection();
			
			Map inParams = mapper.createMap(con);
			
			CallableStatement call = con.prepareCall(this.callString);
			processInputParameters(inParams, call);

			// Execute the stored procedure
			call.execute();

			logger.info("Executing stored procedure [" + callString + "]");

			// Now get output parameters. There need not be any.
			Map outParams = extractOutputParameters(call);
			call.close();
			return outParams;
		}
		catch (SQLException ex) {
			//throw new UncategorizedSQLException("Call to stored procedure '" + getSql() + "' failed", ex);
			throw this.exceptionTranslater.translate("Call to stored procedure '" + getSql() + "'", this.callString, ex);
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(con, ds);
		}
	} 	// execute
	

	/** 
	 * Set and register input parameters
	 * @param inParams parameters (including output parameters) to the stored procedure
	 * @param call CallableStatement representing the stored procedure
	 * @throws SQLException
	 */
	private void processInputParameters(Map inParams, CallableStatement call) throws SQLException, InvalidDataAccessApiUsageException {
		List parameters = getDeclaredParameters();
		for (int i = 0; i < parameters.size(); i++) {
			SqlParameter p = (SqlParameter) parameters.get(i);
			if (!inParams.containsKey(p.getName()) && !(p instanceof OutputParameter) )
				throw new InvalidDataAccessApiUsageException("Required input parameter '" + p.getName() + "' is missing");
			// The value may still be null
			Object in = inParams.get(p.getName());
			if (!(p instanceof OutputParameter)) {
				// Input parameters must be supplied
				if (in != null)					
					call.setObject(i + 1, in, p.getSqlType());
				else
					call.setNull(i + 1, p.getSqlType());
			}
			else {
				// It's an output parameter.
				// It need not (but may be) supplied by the caller.
				call.registerOutParameter(i + 1, p.getSqlType());
				if (in != null) {
					call.setObject(i + 1, in, p.getSqlType());
				}
			}
		}
	}	// processInputParameters
	

	/**
	 * Extract output parameters from the completed stored procedure.
	 * @param outParams parameters to the stored procedure
	 * @param call JDBC wrapper for the stored procedure
	 * @throws SQLException
	 * @return 
	 */
	private Map extractOutputParameters(CallableStatement call) throws SQLException {
		Map outParams = new HashMap();
		List parameters = getDeclaredParameters();
		for (int i = 0; i < parameters.size(); i++) {
			SqlParameter p = (SqlParameter) parameters.get(i);
			if (p instanceof OutputParameter) {
				Object out = call.getObject(i + 1);
				outParams.put(p.getName(), out);
			}
		}
		return outParams;
	}
	
	
	//---------------------------------------------------------------------
	// Inner classes
	//---------------------------------------------------------------------
	/**
	 * Implement this interface when parameters need to be customized based 
	 * on the connection. We might need to do this to make
	 * use of proprietary features, available only with a specific
	 * Connection type.
	 */
	protected interface ParameterMapper {
		
		/**
		 * @param con JDBC connection. This is useful (and the purpose
		 * of this interface) if we need to do something RDBMS-specific
		 * with a proprietary Connection implementation. This class conceals
		 * such proprietary details. However, it is best to avoid using
		 * such proprietary RDBMS features if possible.
		 * @return input parameters, keyed by name
		 */
		Map createMap(Connection con) throws SQLException;
	}


	/** 
	 * Subclass of SqlParameter to represent an output parameter.
	 * No additional properties: instanceof will be used to check
	 * for such types.
	 * Output parameters--like all stored procedure parameters--
	 * must have names.
	 **/
	public static class OutputParameter extends SqlParameter {
		
		/**
		 * Create a new OutputParameter, supplying name and SQL type
		 * @param name name of the parameter, as used in input and
		 * output maps
		 * @param type SQL type of the parameter, as defined
		 * in a constant in the java.sql.Types class.
		 */
		public OutputParameter(String name, int type) {
			super(name, type);
		}
	}

	/**
	 * @return boolean
	 */
	public boolean isFunction() {
		return isFunction;
	}

	/**
	 * Sets the isFunction.
	 * @param isFunction The isFunction to set
	 */
	public void setFunction(boolean isFunction) {
		this.isFunction = isFunction;
	}

}	// class StoredProcedure
