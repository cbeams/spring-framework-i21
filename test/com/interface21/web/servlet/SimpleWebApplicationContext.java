package com.interface21.web.servlet;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.interface21.beans.BeansException;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.context.ApplicationContext;
import com.interface21.context.ApplicationContextException;
import com.interface21.context.support.StaticMessageSource;
import com.interface21.ui.context.Theme;
import com.interface21.ui.context.ThemeSource;
import com.interface21.ui.context.support.SimpleTheme;
import com.interface21.ui.context.support.UiApplicationContextUtils;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.servlet.i18n.AcceptHeaderLocaleResolver;
import com.interface21.web.servlet.mvc.Controller;
import com.interface21.web.servlet.mvc.SimpleFormController;
import com.interface21.web.servlet.support.RequestContextUtils;
import com.interface21.web.servlet.theme.AbstractThemeResolver;

/**
 * @author Juergen Hoeller
 * @since 21.05.2003
 */
public class SimpleWebApplicationContext extends StaticWebApplicationContext {

	public SimpleWebApplicationContext(ApplicationContext parent, String namespace) throws BeansException, ApplicationContextException {
		super(parent, namespace);
	}

	public void setServletContext(ServletContext servletContext) {
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("commandClass", "com.interface21.beans.TestBean"));
		pvs.addPropertyValue(new PropertyValue("formView", "form"));
		registerSingleton("/form.do", SimpleFormController.class, pvs);

		registerSingleton("/locale.do", LocaleChecker.class, null);

		addMessage("test", Locale.getDefault(), "test message");
		addMessage("test", Locale.CANADA, "Canadian & test message");

		registerSingleton(UiApplicationContextUtils.THEME_SOURCE_BEAN_NAME, DummyThemeSource.class, null);
		super.setServletContext(servletContext);
	}


	public static class LocaleChecker implements Controller {

		public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			if (!(RequestContextUtils.getWebApplicationContext(request) instanceof SimpleWebApplicationContext)) {
				throw new ServletException("Incorrect WebApplicationContext");
			}
			if (!(RequestContextUtils.getLocaleResolver(request) instanceof AcceptHeaderLocaleResolver)) {
				throw new ServletException("Incorrect LocaleResolver");
			}
			if (!Locale.CANADA.equals(RequestContextUtils.getLocale(request))) {
				throw new ServletException("Incorrect Locale");
			}
			return null;
		}
	}

	public static class DummyThemeSource implements ThemeSource {

		private StaticMessageSource messageSource;

		public DummyThemeSource() {
			messageSource = new StaticMessageSource();
			messageSource.addMessage("themetest", Locale.getDefault(), "theme test message");
		}

		public Theme getTheme(String themeName) {
			if (AbstractThemeResolver.ORIGINAL_DEFAULT_THEME_NAME.equals(themeName)) {
				return new SimpleTheme(AbstractThemeResolver.ORIGINAL_DEFAULT_THEME_NAME, messageSource);
			}
			else {
				return null;
			}
		}
	}

}
