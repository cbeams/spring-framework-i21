package com.interface21.web.servlet;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.servlet.mvc.SimpleFormController;
import com.interface21.web.servlet.mvc.Controller;
import com.interface21.web.servlet.i18n.AcceptHeaderLocaleResolver;
import com.interface21.web.servlet.support.RequestContextUtils;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.beans.BeansException;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;

/**
 * @author Juergen Hoeller
 * @since 21.05.2003
 */
class SimpleWebApplicationContext extends StaticWebApplicationContext {

	public SimpleWebApplicationContext(ApplicationContext parent, String namespace) throws BeansException, ApplicationContextException {
		super(parent, namespace);
	}

	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("commandClass", "com.interface21.beans.TestBean"));
		pvs.addPropertyValue(new PropertyValue("formView", "form"));
		registerSingleton("/form.do", SimpleFormController.class, pvs);

		registerSingleton("/locale.do", LocaleChecker.class, null);

		addMessage("test", Locale.getDefault(), "test message");
		addMessage("test", Locale.CANADA, "Canadian & test message");
	}


	public static class LocaleChecker implements Controller, LocaleResolverAware {

		private LocaleResolver localeResolver;

		public void setLocaleResolver(LocaleResolver localeResolver) {
			this.localeResolver = localeResolver;
		}

		public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			if (!(RequestContextUtils.getWebApplicationContext(request) instanceof SimpleWebApplicationContext)) {
				throw new ServletException("Incorrect WebApplicationContext");
			}
			if (!Locale.CANADA.equals(RequestContextUtils.getLocale(request))) {
				throw new ServletException("Incorrect Locale");
			}
			if (!(localeResolver instanceof AcceptHeaderLocaleResolver)) {
				throw new ServletException("Incorrect LocaleResolver");
			}
			return null;
		}

	}

}
