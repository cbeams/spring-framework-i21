/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.support.XmlBeanFactory;
import com.interface21.beans.factory.BeanDefinitionStoreException;

/**
 * Factory for creating SQLExceptionTranslator based on the
 * DatabaseProductName taken from the DatabaseMetaData.
 * Returns a SQLExceptionTranslator populated with vendor 
 * codes defined in a configuration file named "sql-error-codes.xml".
 * @author Thomas Risberg
 */
public class SQLExceptionTranslaterFactory {
	/**
	* Keep track of this instance so we can return it to classes that request
	* it.
	*/
	private static final SQLExceptionTranslaterFactory instance;
	/**
	* Create a HashMap to hold error codes for all databases defined in the
	* config file.
	*/
	private HashMap rdbmsErrorCodes;
	/**
	* Create a logging category 
	*/
	protected final Logger logger = Logger.getLogger(getClass().getName());

	SQLExceptionTranslaterFactory() {
		String errorCodeXml = "/sql-error-codes.xml";
		try {
			java.io.InputStream is = SQLExceptionTranslaterFactory.class.getResourceAsStream(errorCodeXml);
			if (is == null) {
				throw new BeanDefinitionStoreException("Unable to locate file [" + errorCodeXml +"]",null);
			}
			ListableBeanFactory bf = new XmlBeanFactory(is);
			String[] rdbmsNames = bf.getBeanDefinitionNames(com.interface21.jdbc.core.SQLErrorCodes.class);			
			rdbmsErrorCodes = new HashMap(rdbmsNames.length);
			for (int i = 0; i < rdbmsNames.length; i++) {
				SQLErrorCodes ec = (SQLErrorCodes) bf.getBean(rdbmsNames[i]);
				if (ec.getBadSqlGrammarCodes() == null)
					ec.setBadSqlGrammarCodes(new String[0]);
				else
					java.util.Arrays.sort(ec.getBadSqlGrammarCodes());
				if (ec.getDataIntegrityViolationCodes() == null)
					ec.setDataIntegrityViolationCodes(new String[0]);
				else
					java.util.Arrays.sort(ec.getDataIntegrityViolationCodes());
				rdbmsErrorCodes.put(rdbmsNames[i], ec);
			}
		}
		catch (BeanDefinitionStoreException be) {
			logger.warn("Error loading error codes from config file.  Message = " + be.getMessage());
			rdbmsErrorCodes = new HashMap(0);
		}
	}
		
	static {
		instance = new SQLExceptionTranslaterFactory();
	}

	/**
	 * 
	 */
	public static SQLExceptionTranslaterFactory getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	public SQLExceptionTranslater getDefaultTranslater(DataSource ds) {
		String dbName = null;
		Connection con = null;
		DatabaseMetaData dbmd = null;
		try {
			con = DataSourceUtils.getConnection(ds);
			if (con != null) 
				dbmd = con.getMetaData();
			if (dbmd != null)
				dbName = dbmd.getDatabaseProductName();
			if (dbName != null && dbName.startsWith("DB2/"))
				dbName = "DB2";
		}
		catch (SQLException se) {
			// this is bad - we probably lost the connection
			return new SQLStateSQLExceptionTranslater();
		}
		finally {
			DataSourceUtils.closeConnectionIfNecessary(ds, con);
		}
		SQLErrorCodes sec = null;
		if (dbName != null)
			sec = (SQLErrorCodes) rdbmsErrorCodes.get(dbName);
		
		// could not find the database among the defined ones
		if (sec == null) 
			return new SQLStateSQLExceptionTranslater();
			
		SQLErrorCodeSQLExceptionTranslater set = new SQLErrorCodeSQLExceptionTranslater(sec);
		return set;
	}

	/**
	 * 
	 */
	public SQLErrorCodes getErrorCodes(String dbName) {
		
		SQLErrorCodes sec = (SQLErrorCodes) rdbmsErrorCodes.get(dbName);
		
		// could not find the database among the defined ones
		if (sec == null) 
			sec = new SQLErrorCodes();

		return sec;
	}
}