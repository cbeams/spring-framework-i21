/**
 * Extension for Spring Framework based on Generic framework code included with
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jdbc.core;

/**
 * JavaBean for holding JDBC Error Codes - loaded through BeanFactory
 * implementation. Used by the SQLExceptionTranslater.
 * @author trisberg
 */
public class SQLErrorCodes {
	private String[] badSqlGrammarCodes = new String[0];
	private String[] dataIntegrityViolationCodes = new String[0];

	/**
	 * Returns the badSqlGrammarCodes.
	 * @return String[]
	 */
	public String[] getBadSqlGrammarCodes() {
		return badSqlGrammarCodes;
	}

	/**
	 * Returns the dataIntegrityViolationCodes.
	 * @return String[]
	 */
	public String[] getDataIntegrityViolationCodes() {
		return dataIntegrityViolationCodes;
	}

	/**
	 * Sets the badSqlGrammarCodes.
	 * @param badSqlGrammarCodes The badSqlGrammarCodes to set
	 */
	public void setBadSqlGrammarCodes(String[] badSqlGrammarCodes) {
		this.badSqlGrammarCodes = badSqlGrammarCodes;
	}

	/**
	 * Sets the dataIntegrityViolationCodes.
	 * @param dataIntegrityViolationCodes The dataIntegrityViolationCodes to set
	 */
	public void setDataIntegrityViolationCodes(String[] dataIntegrityViolationCodes) {
		this.dataIntegrityViolationCodes = dataIntegrityViolationCodes;
	}

}
