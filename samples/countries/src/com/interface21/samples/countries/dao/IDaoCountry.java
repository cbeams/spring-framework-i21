package com.interface21.samples.countries.dao;

import java.util.List;
import java.util.Locale;

import com.interface21.samples.countries.appli.ICountry;

/**
 * @author Jean-Pierre PAWLAK
 */
public interface IDaoCountry {

	public static final String MEMORY = "MEMORY";
	public static final String DATABASE = "DATABASE";
	
	/**
	 * Provides the list of all ICountries in the given Locale.
	 * 
	 * @param locale The Locale for which the records are intended
	 * @return The list of all countries in the given Locale
	 */
	public List getAllCountries(Locale locale);

	/**
	 * Provides a list of ICountries in the given Locale with name and code
	 * starting with the corresponding parameters. A null or empty String 
	 * in the parameter is interpreted as no filtering on that parameter.
	 * 
	 * @param name Starting of the name of the requested records 
	 * @param code Starting of the code of the requested records
	 * @param locale The Locale for which the records are intended
	 * @return The list of countries matching with parameters in the given Locale
	 */
	public List getFilteredCountries(String name, String code, Locale locale);

	/**
	 * Get a ICountry Object following the parameters.
	 * 
	 * @param code The code of the requested country
	 * @param locale The locale to use for searching the country
	 * @return the ICountry bean corresponding on the parameters
	 */
	public ICountry getCountry(String code, Locale locale);

	/**
	 * Creates a bunch of records in the table <code>countries</code>.
	 * 
	 * @param countries The list of countries to add in the table
	 * @param locale The locale on which these countries are attached
	 */
	public void saveCountries(List countries, Locale locale);

	/**
	 * Prepare the database for the copy. ie drop and re-create a void countries table.
	 */
	public void initBase();

	/**
	 * @return the type of the DAO: either MEMORY, either DATABASE
	 */
	public String getType();
		
}
