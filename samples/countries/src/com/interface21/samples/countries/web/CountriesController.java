package com.interface21.samples.countries.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.samples.countries.appli.ICountry;
import com.interface21.samples.countries.dao.IDaoCountry;
import com.interface21.util.PagedListSourceProvider;
import com.interface21.util.RefreshablePagedListHolder;
import com.interface21.validation.BindException;
import com.interface21.web.bind.BindUtils;
import com.interface21.web.servlet.ModelAndView;
import com.interface21.web.servlet.mvc.multiaction.MultiActionController;
import com.interface21.web.servlet.support.RequestContextUtils;

/**
 * @author Jean-Pierre Pawlak
 */
public class CountriesController extends MultiActionController {
	static final private String COUNTRIES_ATTR = "countries";

	private IDaoCountry daoCountry;
	private IDaoCountry secondDaoCountry;

	// handlers
    
	/**
	 * Custom handler for home
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render the response
	 */
	public ModelAndView handleHome(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		return new ModelAndView("homeView");
	}

	/**
	 * Custom handler for countries main paged list
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render the response
	 */
	public ModelAndView handleMain(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RefreshablePagedListHolder listHolder = (RefreshablePagedListHolder) request.getSession(true).getAttribute(COUNTRIES_ATTR);
		if (null == listHolder) {
			listHolder = new RefreshablePagedListHolder();
			listHolder.setSourceProvider(new CountriesProvider());
			listHolder.setFilter(new CountriesFilter());
			request.getSession(true).setAttribute(COUNTRIES_ATTR, listHolder);
		}
		BindException ex = BindUtils.bind(request, listHolder, "countries");
		listHolder.setLocale(RequestContextUtils.getLocale(request));
		boolean forceRefresh = request.getParameter("forceRefresh") != null;
		listHolder.refresh(forceRefresh);
		return new ModelAndView("countries_mainView", ex.getModel());
	}

	/**
	 * Custom handler for countries detail page
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render the response
	 */
	public ModelAndView handleDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Locale locale = RequestContextUtils.getLocale(request);
		ICountry country = daoCountry.getCountry(request.getParameter("code"), locale);
		return new ModelAndView("countries_detailView", "country", country);
	}

	/**
	 * Custom handler for countries Excel document
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render the response
	 */
	public ModelAndView handleExcel(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RefreshablePagedListHolder listHolder = (RefreshablePagedListHolder) request.getSession(true).getAttribute(COUNTRIES_ATTR);
		if (null == listHolder) {
			return handleMain(request, response);
		}
		return new ModelAndView("countries_excelView", "countries", listHolder);
	}

	/**
	 * Custom handler for countries PDF document
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render the response
	 */
	public ModelAndView handlePdf(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RefreshablePagedListHolder listHolder = (RefreshablePagedListHolder) request.getSession(true).getAttribute(COUNTRIES_ATTR);
		if (null == listHolder) {
			return handleMain(request, response);
		}
		return new ModelAndView("countries_pdfView", "countries", listHolder);
	}

	/**
	 * Custom handler for copy countries from memory to database
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render the response
	 */
	public ModelAndView handleCopy(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		boolean copyMade = false;
		try {
			if (null != secondDaoCountry) {
				secondDaoCountry.initBase(); 
				Locale locs[] = {Locale.US, Locale.FRANCE, Locale.GERMANY};
				for (int i = 0; i < locs.length; i++ ) {
					secondDaoCountry.saveCountries(daoCountry.getAllCountries(locs[i]), locs[i]);
				}
				copyMade = true;
			}
		} finally {
			return new ModelAndView("copyView", "copyMade", Boolean.valueOf(copyMade));
		}
	}

	// Accessors
	public void setDaoCountry(IDaoCountry daoCountry) {
		this.daoCountry = daoCountry;
	}

	public void setSecondDaoCountry(IDaoCountry country) {
		secondDaoCountry = country;
	}

	// Embedded classes
	private class CountriesProvider implements PagedListSourceProvider {
		public List loadList(Locale loc, Object filter) {
			CountriesFilter cf = (CountriesFilter) filter;
			return daoCountry.getFilteredCountries(cf.getName(), cf.getCode(), loc);
		}
	}

	public static class CountriesFilter {

		private String name;
		private String code;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public boolean equals(Object obj) {
			return (obj instanceof CountriesFilter ? equals((CountriesFilter) obj) : false);
		}

		public boolean equals(CountriesFilter cf) {
			if (cf == this) return true;
			boolean result = (name == null ? cf.name == null : name.equals(cf.name));
			if (result) {
				result = (code == null ? cf.code == null : code.equals(cf.code));
			}
			return result;
		}

		public int hashCode() {
			int hash = 17;
			hash = 37 * hash + (name == null ? 0 : name.hashCode());
			hash = 37 * hash + (code == null ? 0 : code.hashCode());
			return hash;
		}
	}

}
