package com.interface21.samples.countries.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.interface21.beans.factory.InitializingBean;
import com.interface21.samples.countries.appli.Country;
import com.interface21.samples.countries.appli.ICountry;

/**
 * @author Jean-Pierre PAWLAK
 *
 */
public class DaoCountryMem implements IDaoCountry, InitializingBean {

	public static final Locale DEFAULT_LOCALE = Locale.US;
	public static final String DEFAULT_LANG = DEFAULT_LOCALE.getLanguage();

	private Map countriesLists = new HashMap();
	private Map countriesMaps = new HashMap();

	/**
	 * @see com.interface21.samples.pagedlist.dao.IDaoCountry#getAllCountries(java.util.Locale)
	 */
	public List getAllCountries(Locale inLocale) {
		String lang = getLang(inLocale);
		List countries = (List) countriesLists.get(lang);
		if (null == countries) {
			countries = (List) countriesLists.get(DEFAULT_LANG);
		}
		return countries;
	}

	/**
	 * @see com.interface21.samples.pagedlist.dao.IDaoCountry#getFilteredCountries(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public List getFilteredCountries(String name, String code, Locale locale) {
		List allCountries = getAllCountries(locale);
		List countries = new ArrayList();
		Iterator it = allCountries.iterator();
		while (it.hasNext()) {
			Country country = (Country) it.next();
			if ((null == name || country.getName().startsWith(name)) &&
			    (null == code || country.getCode().startsWith(code))
			) {
				countries.add(country);
			}
		}
		return countries;
	}

	/**
	 * @see com.interface21.samples.pagedlist.dao.IDaoCountry#getCountry(java.lang.String, java.util.Locale)
	 */
	public ICountry getCountry(String code, Locale locale) {
		return (ICountry) ((Map) countriesMaps.get(this.getLang(locale))).get(code);
	}

	/**
	 * @see com.interface21.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {
		loadData();
	}

	private String getLang(Locale locale) {
		if (locale == null) {
			locale = DEFAULT_LOCALE;
		}
		return locale.getLanguage();
	}

	private void loadData() {
		String countries[] = Locale.getISOCountries();
		String langs[] = {"fr", "en", "de"};
		for (int langId = 0; langId < langs.length; langId++) {
			String lang = langs[langId];
			Locale crtLoc = new Locale(lang, "");
			List list = new ArrayList();
			Map map = new HashMap();
			Country country = null;
			for (int j = 0; j < countries.length; j++) {
				String countryCode = countries[j];
				Locale countryLoc = new Locale("en", countryCode);
				String name = countryLoc.getDisplayCountry(crtLoc);
				if (!name.equals(countryCode)) {
					country = new Country(countryCode, name);
					list.add(country);
					map.put(countryCode, country);
				}
			}
			countriesLists.put(lang, list);
			countriesMaps.put(lang, map);
		}
	}

}

