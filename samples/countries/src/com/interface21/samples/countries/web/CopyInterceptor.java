package com.interface21.samples.countries.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.beans.BeanWrapper;
import com.interface21.beans.BeanWrapperImpl;
import com.interface21.beans.BeansException;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextAware;
import com.interface21.context.ApplicationContextException;
import com.interface21.samples.countries.dao.IDaoCountry;
import com.interface21.web.servlet.HandlerInterceptor;

/**
 * An interceptor that tell the views about the possibility of a copy 
 * of the countries to a database.
 * 
 * @author Jean-Pierre Pawla
 */
public class CopyInterceptor implements HandlerInterceptor, ApplicationContextAware {

	protected final Log logger = LogFactory.getLog(getClass());
	private Boolean copyAvailable;
	
	/**
	 * Makes the <code>copyAvailable</code> value available to views
	 * 
	 * @see com.interface21.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler)
		throws ServletException, IOException {
		
		request.setAttribute("copyAvailable", copyAvailable);
		return true;
	}

	/**
	 * Set <code>copyAvailable</code> to True if the <code>countriesController</code> 
	 * has a <code>secondDao</code> declared and if this one is of a DATABASE type.
	 * Otherwise <code>copyAvailable</code> is set to False.
	 * 
	 * @see com.interface21.context.ApplicationContextAware#setApplicationContext(com.interface21.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext ctx)
		throws ApplicationContextException {
		Object o = ctx.getBean("countriesController");
		if (null == o) {
			copyAvailable = Boolean.FALSE;
		} else {
			BeanWrapper bw = new BeanWrapperImpl(o);
			Object dao;
			try {
				dao = bw.getPropertyValue("secondDaoCountry");
				bw = new BeanWrapperImpl(dao);
				if (IDaoCountry.DATABASE.equals(bw.getPropertyValue("type"))) {
					copyAvailable = Boolean.TRUE;
				} else {
					copyAvailable = Boolean.FALSE;
				}
			} catch (BeansException e) {
				copyAvailable = Boolean.FALSE;
			}
		}
		if (copyAvailable.booleanValue()) {
			logger.info("The countriesController has a valid secondDao. Copy to the database is available.");
		} else {
			logger.info("The countriesController has no valid secondDao. Copy to the database is not available.");
		}
	}

}
