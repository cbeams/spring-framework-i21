/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.interface21.dao.DataAccessException;
import com.interface21.dao.DataIntegrityViolationException;


/**
 * Implementation of SQLExceptionTranslater that uses the
 * SQLState code in the SQL exception.
 * Can't diagnose all problems, but is portable between
 * databases.
 * @author Rod Johnson
 * @version $Id$
 */
public class SQLStateSQLExceptionTranslater implements SQLExceptionTranslater {
	
	/** Set of String 2-digit codes that indicate bad SQL */
	private static Set BAD_SQL_CODES = new HashSet();
	
	/** Set of String 2-digit codes that indicate RDBMS integrity violation */
	private static Set INTEGRITY_VIOLATION_CODES = new HashSet();
	
	// Populate reference data
	static {
		BAD_SQL_CODES.add("07");
		BAD_SQL_CODES.add("42");
		BAD_SQL_CODES.add("65");				// Oracle throws on unknown identifier
		BAD_SQL_CODES.add("S0");                // MySQL uses this - from ODBC error codes?
		
		INTEGRITY_VIOLATION_CODES.add("22");	// Integrity constraint violation
		INTEGRITY_VIOLATION_CODES.add("23");	// Integrity constraint violation
		INTEGRITY_VIOLATION_CODES.add("27");	// Triggered data change violation
		INTEGRITY_VIOLATION_CODES.add("44");	// With check violation
	}
	
	
	/**
	* Create a logging category that is available
	* to subclasses. 
	*/
	protected final Logger logger = Logger.getLogger(getClass().getName());


	/**
	 * @see SQLExceptionTranslater#translate(String,String,SQLException)
	 */
	public DataAccessException translate(String task, String sql, SQLException sqlex) {
		logger.warn("Translating SQLException with SQLState='" + sqlex.getSQLState() + "' and errorCode=" + sqlex.getErrorCode() + 
						" and message=" + sqlex.getMessage() + "; sql was '" + sql + "'");
			
		String sqlstate = sqlex.getSQLState();
		if (sqlstate != null && sqlstate.length() >= 2) {
			String classCode = sqlstate.substring(0, 2);
			if (BAD_SQL_CODES.contains(classCode))
				throw new BadSqlGrammarException("(" + task + "): SQL grammatical error '" + sql + "'", sql, sqlex);
			if (INTEGRITY_VIOLATION_CODES.contains(classCode))
				throw new DataIntegrityViolationException("(" + task + "): data integrity violated by SQL '" + sql + "'", sqlex);
		}
		
		// We couldn't identify it more precisely
		return new UncategorizedSQLException("(" + task + "): encountered SQLException [" + sqlex.getMessage() + "]", sql, sqlex);
	}

}
