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
import com.interface21.context.support.ApplicationObjectSupport;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.servlet.handler.SimpleUrlHandlerMapping;
import com.interface21.web.servlet.i18n.SessionLocaleResolver;
import com.interface21.web.servlet.mvc.SimpleControllerHandlerAdapter;
import com.interface21.web.servlet.mvc.SimpleFormController;
import com.interface21.web.servlet.support.RequestContextUtils;
import com.interface21.web.servlet.view.ResourceBundleViewResolver;
import com.interface21.core.Ordered;

/**
 * @author Juergen Hoeller
 * @since 21.05.2003
 */
class ComplexWebApplicationContext extends StaticWebApplicationContext {

	public ComplexWebApplicationContext(ApplicationContext parent, String namespace) throws BeansException, ApplicationContextException {
		super(parent, namespace);
	}

	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);

		registerSingleton(ControllerServlet.LOCALE_RESOLVER_BEAN_NAME, SessionLocaleResolver.class, null);

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("mappings", "/form.do=localeHandler\n/locale.do=localeHandler"));
		registerSingleton("myUrlMapping1", SimpleUrlHandlerMapping.class, pvs);

		pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("mappings", "/form.do=localeHandler\n/unknown.do=unknownHandler"));
		pvs.addPropertyValue(new PropertyValue("order", "2"));
		registerSingleton("myUrlMapping2", SimpleUrlHandlerMapping.class, pvs);

		pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("mappings", "/form.do=formHandler"));
		pvs.addPropertyValue(new PropertyValue("order", "1"));
		registerSingleton("myUrlMapping3", SimpleUrlHandlerMapping.class, pvs);

		registerSingleton("myDummyAdapter", MyDummyAdapter.class, null);
		registerSingleton("myHandlerAdapter", MyHandlerAdapter.class, null);
		registerSingleton("standardHandler", SimpleControllerHandlerAdapter.class, null);

		pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("basename", "com.interface21.web.servlet.complexviews"));
		registerSingleton(ControllerServlet.VIEW_RESOLVER_BEAN_NAME, ResourceBundleViewResolver.class, pvs);

		pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("commandClass", "com.interface21.beans.TestBean"));
		pvs.addPropertyValue(new PropertyValue("formView", "form"));
		registerSingleton("formHandler", SimpleFormController.class, pvs);

		registerSingleton("localeHandler", ComplexLocaleChecker.class, null);
		registerSingleton("unknownHandler", String.class, null);

		addMessage("test", Locale.getDefault(), "test message");
		addMessage("test", Locale.CANADA, "Canadian & test message");
	}


	public static interface MyHandler {

		public void doSomething(HttpServletRequest request) throws ServletException;
	}

	public static class MyHandlerAdapter extends ApplicationObjectSupport implements HandlerAdapter, Ordered {

		public int getOrder() {
			return 99;
		}

		public boolean supports(Object handler) {
			return handler != null && MyHandler.class.isAssignableFrom(handler.getClass());
		}

		public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object delegate)
		    throws IOException, ServletException {
			((MyHandler) delegate).doSomething(request);
			return null;
		}

		public long getLastModified(HttpServletRequest request, Object delegate) {
			return -1;
		}
	}

	public static class MyDummyAdapter extends ApplicationObjectSupport implements HandlerAdapter {

		public boolean supports(Object handler) {
			return handler != null && MyHandler.class.isAssignableFrom(handler.getClass());
		}

		public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object delegate)
		    throws IOException, ServletException {
			throw new ServletException("dummy");
		}

		public long getLastModified(HttpServletRequest request, Object delegate) {
			return -1;
		}
	}

	public static class ComplexLocaleChecker implements MyHandler, LocaleResolverAware {

		private LocaleResolver localeResolver;

		public void setLocaleResolver(LocaleResolver localeResolver) {
			this.localeResolver = localeResolver;
		}

		public void doSomething(HttpServletRequest request) throws ServletException {
			if (!(RequestContextUtils.getWebApplicationContext(request) instanceof ComplexWebApplicationContext)) {
				throw new ServletException("Incorrect WebApplicationContext");
			}
			if (!Locale.CANADA.equals(RequestContextUtils.getLocale(request))) {
				throw new ServletException("Incorrect Locale");
			}
			if (!(localeResolver instanceof SessionLocaleResolver)) {
				throw new ServletException("Incorrect LocaleResolver");
			}
		}

	}

}
