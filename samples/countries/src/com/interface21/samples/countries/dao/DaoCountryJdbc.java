package com.interface21.samples.countries.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.context.ApplicationContextException;
import com.interface21.dao.DataAccessException;
import com.interface21.jdbc.core.BatchPreparedStatementSetter;
import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.SqlParameter;
import com.interface21.jdbc.object.MappingSqlQuery;
import com.interface21.jdbc.object.SqlUpdate;
import com.interface21.samples.countries.appli.Country;
import com.interface21.samples.countries.appli.ICountry;

/**
 * This class is the JDBC implementation of the <code>countryDao</code>. It can be
 * used directly if your database is happy with the drop/create standard statements
 * and generally with the <code>initBase</code> process.
 * <br>Otherwise, subclass it to redefine <code>initBase</code>.
 * <br>If only SQL statements have to be redefined, you can use this class directly,
 * providing the statements in the configuration like this:
 * <br><code>
 * <br>&lt;bean id="secondDaoCountry" class="com.interface21.samples.countries.dao.DaoCountryJdbc"
 * <br>	singleton="true"&gt;
 * <br>		&lt;property name="dataSource"&gt;&lt;ref bean="dataSource"/&gt;&lt;/property&gt;
 * <br>		&lt;property name="dropSql"&gt;&lt;value&gt;DROP TABLE ... &lt;/value&gt;&lt;/property&gt;
 * <br>		&lt;property name="createSql"&gt;&lt;value&gt;CREATE TABLE ... &lt;/value&gt;&lt;/property&gt;
 * <br>&lt;/bean&gt;
 * <br></code>
 * 
 * @author Jean-Pierre Pawlak
 */
public class DaoCountryJdbc implements IDaoCountry, InitializingBean {

	/** Holds a default Locale for not handled Locales. */
	public static final Locale DEFAULT_LOCALE = Locale.US;

	/** Holds a default language for not handled Locales. */
	public static final String DEFAULT_LANG = DEFAULT_LOCALE.getLanguage();

	/** Holds the INSERT SQL Statement. */
	protected static final String INSERT_SQL = "INSERT INTO countries(lang,code,name) VALUES(?,?,?)"; 
	//protected static final String INSERT_SQL = "INSERT INTO countries VALUES(?,?,?)"; 

	/** Holds the filtered SELECT SQL Statement. */
	protected static final String FILTER_SELECT_SQL = 
		"SELECT * FROM countries WHERE lang = ? and code like ? and name like ? ORDER BY code";

	/** Holds the ALL SELECT SQL Statement. */
	protected static final String SELECT_SQL = "SELECT * FROM countries WHERE lang = ? ORDER BY code";

	/** Holds the drop table ANSI SQL Statement. */
	protected static final String ANSI_DROP_SQL = "DROP TABLE countries";
	
	/** Holds the create table ANSI SQL Statement. */
	protected static final String ANSI_CREATE_SQL = "CREATE TABLE countries (lang CHAR(2), code CHAR(2), name VARCHAR(50))";

	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Holds the drop table current SQL Statement. */
	protected String dropSql = ANSI_DROP_SQL;
	
	/** Holds the create table current SQL Statement. */
	protected String createSql = ANSI_CREATE_SQL;

	/** Holds value of property dataSource. */
	private DataSource dataSource;

	/** Holds filtered Countries Query Object. */
	private FilteredCountriesQuery filteredCountriesQuery;

	/** Holds All Countries Query Object. */
	private AllCountriesQuery allCountriesQuery;
	
	/** Holds All Countries Query Object. */
	private Map countriesLists = new HashMap();

	/** Holds All Countries Query Object. */
	private Map countriesMaps = new HashMap();
	

	/**
	 * @see com.interface21.samples.countries.dao.IDaoCountry#getAllCountries(java.util.Locale)
	 */
	public final List getAllCountries(Locale locale) {
		String lang = getLang(locale);
		List countries = (List) countriesLists.get(lang);
		if (null == countries) {
			countries = (List) countriesLists.get(DEFAULT_LANG);
		}
		return countries;
	}

	/**
	 * @see com.interface21.samples.countries.dao.IDaoCountry#getFilteredCountries(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public final List getFilteredCountries(String name, String code, Locale locale) {
		// The cached value is returned if no filter are used
		if ((null == code || code.length() == 0) && (null == name || name.length() == 0)) {
			 return getAllCountries(locale);
		}
		String lang = getLang(locale);
		if (countriesLists.get(lang) == null) {
			lang = DEFAULT_LANG;
		} 
		return filteredCountriesQuery.execute(
			new Object[] {
				lang, 
				code == null ? null : code + "%", 
				name == null ? null : name + "%"});
	}

	/**
	 * @see com.interface21.samples.countries.dao.IDaoCountry#getCountry(java.lang.String, java.util.Locale)
	 */
	public final ICountry getCountry(String code, Locale locale) {
		return (ICountry) ((Map) countriesMaps.get(this.getLang(locale))).get(code);
	}

	/**
	 * This method is normally the sole which may been subclassed.
	 * Its aim is to prepare the database for inserting <code>countries</code>
	 * records. The classical way used here is deletion and creation of the table.
	 * <ul>
	 * <li>If you only have to change the syntax of SQL statements, redefine
	 * only <code>dropSql</code> and <code>createSql</code> in the subclass.</li>
	 * <li>You can also in this case not subclass and provide the statements in the configuration.</li>
	 * <li>If you have additional steps, redefine this method in the subclass.</li>
	 * </ul> 
	 * 
	 * @see com.interface21.samples.countries.dao.IDaoCountry#initBase()
	 */
	public void initBase() {
		JdbcTemplate tpl = new JdbcTemplate();
		tpl.setDataSource(this.getDataSource());

		try {
			tpl.update(dropSql);
			logger.info("'countries' table deleted");
		} catch (DataAccessException e) {
			logger.info("'countries' table didn't exist. Exception=" + e.getMessage());
		}

		tpl.update(createSql);
		logger.info("'countries' table created");
	}

	/**
	 * @see com.interface21.samples.countries.dao.IDaoCountry#saveCountries(java.util.List, java.util.Locale)
	 */
	public final void saveCountries(List countries, Locale locale) {
		try {
			// We try to use batch updates
			// MySql, by example supports it.
			JdbcTemplate tpl = new JdbcTemplate();
			tpl.setDataSource(this.getDataSource());
			tpl.batchUpdate(INSERT_SQL,	new BatchSetter(countries, locale));
		} catch (DataAccessException e) {
			// This is normal, by example, for HSql.
			logger.info("The database driver doesnt support batch updates. Error=" + e.getMessage());
			// We try a normal loop instead
			CountriesInsert countriesInsert = new CountriesInsert(this.getDataSource());
			String lang = locale.getLanguage();
			Iterator it = countries.iterator();
			while (it.hasNext()) {
				ICountry country = (ICountry)it.next();
				countriesInsert.insert(lang, country.getCode(), country.getName());
			}
		}
	}

	/**
	 * @see com.interface21.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public final void afterPropertiesSet() throws Exception {
		if(dataSource == null) {
			throw new ApplicationContextException("Must set dataSource bean property on " + getClass());
		}
        
		filteredCountriesQuery = new FilteredCountriesQuery(dataSource);
		allCountriesQuery = new AllCountriesQuery(dataSource);
		loadData();
	}


	/**
	 * The complete tables are read on initialisation and cached.
	 * Filtered requests will not be cached.
	 */
	private final void loadData() {
		String langs[] = {"fr", "en", "de"};
		try {
			for (int langId = 0; langId < langs.length; langId++) {
				String lang = langs[langId];
				List list = allCountriesQuery.execute(lang);
				Map map = new HashMap();
				Iterator it = list.iterator();
				while(it.hasNext()) {
					ICountry country = (ICountry)it.next();
					map.put(country.getCode(), country);
				}
				countriesLists.put(lang, list);
				countriesMaps.put(lang, map);
			}
		} catch (DataAccessException e) {
			// If it's a secondDao, it'normal: the table doesn't exist.
			// Otherwise, it's more problematic
			logger.error("loadData ERROR: " + e.getMessage());
		}
	}

	private final String getLang(Locale locale) {
		if (locale == null) {
			locale = DEFAULT_LOCALE;
		}
		return locale.getLanguage();
	}

	/**
	 * @see com.interface21.samples.countries.dao.IDaoCountry#getType()
	 */
	public final String getType() {
		return DATABASE;
	}

	// Accessors
	
	protected final DataSource getDataSource() {
		return dataSource;
	}

	public final void setDataSource(DataSource source) {
		dataSource = source;
	}

	public String getCreateSql() {
		return createSql;
	}

	public String getDropSql() {
		return dropSql;
	}

	public void setCreateSql(String string) {
		createSql = string;
	}

	public void setDropSql(String string) {
		dropSql = string;
	}


	// Embedded classes
	
	/**
	 *  callback class for JdbcTemplate, allowing batch updates.
	 */
	class BatchSetter implements BatchPreparedStatementSetter {
		private List countries;
		private String lang;
		public BatchSetter(List countries, Locale locale) {
			this.countries = countries;
			this.lang = locale.getLanguage();
		}
		public int getBatchSize() {
			return countries.size();
		}
		public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {
			ICountry country = (ICountry)countries.get(i);
			ps.setString(1, lang);
			ps.setString(2, country.getCode());
			ps.setString(3, country.getName());
		}
	}

	/**
	 * Alternative class for inserting countries 
	 * when batch updates are not supported.
	 */
	class CountriesInsert extends SqlUpdate {

		protected CountriesInsert(DataSource ds) {
			super(ds, INSERT_SQL);
			declareParameter(new SqlParameter(Types.CHAR));
			declareParameter(new SqlParameter(Types.CHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		public void insert(String lang, String code, String name) {
			Object[] objs =	new Object[] {lang, code, name};
			super.update(objs);
		}
	}

	/**
	 *  Abstract base class for all <code>Country</code> Query Objects.
	 */
	abstract class CountriesQuery extends MappingSqlQuery  {
        
		/** 
		 *  Creates a new instance of Country
		 *  @param ds the DataSource to use for the query.
		 *  @param sql Value of the SQL to use for the query. 
		 */
		protected CountriesQuery(DataSource ds, String sql) {
			super(ds, sql);
		}
        
		/** @see MappingSqlQuery#mapRow(ResultSet,int)*/
		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			Country country = new Country();
			country.setCode(rs.getString("code"));
			country.setName(rs.getString("name"));
			return country;
		}
        
	}

	/**
	 *  Filtered Countries Query Object.
	 */
	class FilteredCountriesQuery extends CountriesQuery {
        
		/** 
		 *  Creates a new instance of OwnersByNameQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected FilteredCountriesQuery(DataSource ds) {
			super(ds, FILTER_SELECT_SQL);
			declareParameter(new SqlParameter(Types.CHAR));
			declareParameter(new SqlParameter(Types.CHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}
	}

	/**
	 *  All Countries Query Object.
	 */
	class AllCountriesQuery extends CountriesQuery {
        
		/** 
		 *  Creates a new instance of OwnersByNameQuery
		 *  @param ds the DataSource to use for the query.
		 */
		protected AllCountriesQuery(DataSource ds) {
			super(ds, SELECT_SQL);
			declareParameter(new SqlParameter(Types.CHAR));
			compile();
		}
	}

}
