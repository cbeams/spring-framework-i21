/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.BeanDefinitionStoreException;
import com.interface21.beans.factory.ListableBeanFactory;
import com.interface21.beans.factory.xml.XmlBeanFactory;
import com.interface21.jdbc.datasource.DataSourceUtils;

/**
 * Factory for creating SQLExceptionTranslator based on the
 * DatabaseProductName taken from the DatabaseMetaData.
 * Returns a SQLExceptionTranslator populated with vendor 
 * codes defined in a configuration file named "sql-error-codes.xml".
 * @author Thomas Risberg
   @version $Id$
 */
public class SQLExceptionTranslaterFactory {
	
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Name of SQL error code file, loading on the classpath. Will look
	 * in current directory (no leading /).
	 */
	public static final String SQL_ERROR_CODE_OVERRIDE_PATH = "/sql-error-codes.xml";
	public static final String SQL_ERROR_CODE_DEFAULT_PATH = "sql-error-codes.xml";
	
	/**
	* Keep track of this instance so we can return it to classes that request it.
	*/
	private static final SQLExceptionTranslaterFactory instance;
	
	static {
		instance = new SQLExceptionTranslaterFactory();
	}

	/**
	 * Factory method
	 */
	public static SQLExceptionTranslaterFactory getInstance() {
		return instance;
	}


	/**
	* Create a Map to hold error codes for all databases defined in the config file.
	*/
	private Map rdbmsErrorCodes;
	
	/**
	 * Not public to enforce Singleton design pattern.
	 */
	private SQLExceptionTranslaterFactory() {
		try {
			InputStream is = SQLExceptionTranslaterFactory.class.getResourceAsStream(SQL_ERROR_CODE_OVERRIDE_PATH);
			if (is == null) {
				is = SQLExceptionTranslaterFactory.class.getResourceAsStream(SQL_ERROR_CODE_DEFAULT_PATH);
				if (is == null) 
					throw new BeanDefinitionStoreException("Unable to locate file [" + SQL_ERROR_CODE_DEFAULT_PATH +"]",null);
			}
			ListableBeanFactory bf = new XmlBeanFactory(is);
			String[] rdbmsNames = bf.getBeanDefinitionNames(com.interface21.jdbc.core.SQLErrorCodes.class);			
			rdbmsErrorCodes = new HashMap(rdbmsNames.length);
			for (int i = 0; i < rdbmsNames.length; i++) {
				SQLErrorCodes ec = (SQLErrorCodes) bf.getBean(rdbmsNames[i]);
				if (ec.getBadSqlGrammarCodes() == null)
					ec.setBadSqlGrammarCodes(new String[0]);
				else
					Arrays.sort(ec.getBadSqlGrammarCodes());
				if (ec.getDataIntegrityViolationCodes() == null)
					ec.setDataIntegrityViolationCodes(new String[0]);
				else
					Arrays.sort(ec.getDataIntegrityViolationCodes());
				rdbmsErrorCodes.put(rdbmsNames[i], ec);
			}
		}
		catch (BeanDefinitionStoreException be) {
			logger.warn("Error loading error codes from config file.  Message = " + be.getMessage());
			rdbmsErrorCodes = new HashMap(0);
		}
	}

	/**
	 * Return SQLExceptionTranslater for the given DataSource,
	 * evaluating DatabaseProductName from DatabaseMetaData.
	 */
	public SQLExceptionTranslater getDefaultTranslater(DataSource ds) {
		logger.info("Initializing default SQL exception translater");
		Connection con = DataSourceUtils.getConnection(ds);
		if (con != null) {
			// should always be the case outside of test environments
			try {
				DatabaseMetaData dbmd = con.getMetaData();
				if (dbmd != null) {
					String dbName = dbmd.getDatabaseProductName();
					// special check for DB2
					if (dbName != null && dbName.startsWith("DB2/"))
						dbName = "DB2";
					if (dbName != null) {
						SQLErrorCodes sec = (SQLErrorCodes) rdbmsErrorCodes.get(dbName);
						if (sec != null)
							return new SQLErrorCodeSQLExceptionTranslater(sec);
					}
				}
				// could not find the database among the defined ones
			}
			catch (SQLException se) {
				// this is bad - we probably lost the connection
			}
			finally {
				DataSourceUtils.closeConnectionIfNecessary(con, ds);
			}
		}
		return new SQLStateSQLExceptionTranslater();
	}

	/**
	 * Return plain SQLErrorCodes for the given database.
	 */
	public SQLErrorCodes getErrorCodes(String dbName) {
		SQLErrorCodes sec = (SQLErrorCodes) rdbmsErrorCodes.get(dbName);
		
		// could not find the database among the defined ones
		if (sec == null) 
			sec = new SQLErrorCodes();

		return sec;
	}

}