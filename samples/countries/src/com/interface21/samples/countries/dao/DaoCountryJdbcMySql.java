package com.interface21.samples.countries.dao;

import com.interface21.jdbc.core.JdbcTemplate;

/**
 * @author Jean-Pierre Pawlak
 */
public class DaoCountryJdbcMySql extends AbstractDaoCountryJdbc {

	/**
	 * The table could define lang + code as primary key and innoDB type.
	 * <br>Index may also be created, but for a so small table, the interest is not clear.
	 * @see com.interface21.samples.countries.dao.IDaoCountry#initBase()
	 */
	public void initBase() {
		JdbcTemplate tpl = new JdbcTemplate();
		tpl.setDataSource(this.getDataSource());
		tpl.update("DROP TABLE IF EXISTS countries");
		tpl.update("CREATE TABLE countries (lang CHAR(2), code CHAR(2), name VARCHAR(50)");
	}
}
