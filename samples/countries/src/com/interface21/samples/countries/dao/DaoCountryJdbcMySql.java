package com.interface21.samples.countries.dao;

/**
 * MySql Subclass for <code>CountryDaoJdbC</code>.
 * 
 * @author Jean-Pierre Pawlak
 */
public class DaoCountryJdbcMySql extends DaoCountryJdbc {

	protected String DROP_SQL = "DROP TABLE IF EXISTS countries";

	// protected String CREATE_SQL = "CREATE TABLE countries (lang CHAR(2), code CHAR(2), name VARCHAR(50), PRIMARY KEY (lang,code) ) Type InnoDB";

	// We don't include indexes due to the small size of this table.

	/**
	 * @see com.interface21.samples.countries.dao.IDaoCountry#initBase()
	 */
	public void initBase() {
		this.setDropSql(DROP_SQL);
		super.initBase();
	}

}
