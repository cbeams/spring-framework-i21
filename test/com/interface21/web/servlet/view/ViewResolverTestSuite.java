package com.interface21.web.servlet.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import junit.framework.TestCase;

import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValue;
import com.interface21.beans.TestBean;
import com.interface21.context.ApplicationContextException;
import com.interface21.web.context.support.StaticWebApplicationContext;
import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.mock.MockHttpServletResponse;
import com.interface21.web.mock.MockServletContext;
import com.interface21.web.servlet.DispatcherServlet;
import com.interface21.web.servlet.View;
import com.interface21.web.servlet.i18n.AcceptHeaderLocaleResolver;
import com.interface21.web.servlet.support.RequestContext;
import com.interface21.web.servlet.theme.FixedThemeResolver;

/**
 * @author Juergen Hoeller
 * @since 18.06.2003
 */
public class ViewResolverTestSuite extends TestCase {

	public ViewResolverTestSuite(String msg) {
		super(msg);
	}

	public void testBeanNameViewResolver() throws ServletException {
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		wac.setServletContext(new MockServletContext());
		MutablePropertyValues pvs1 = new MutablePropertyValues();
		pvs1.addPropertyValue(new PropertyValue("url", "/example1.jsp"));
		wac.registerSingleton("example1", InternalResourceView.class, pvs1);
		MutablePropertyValues pvs2 = new MutablePropertyValues();
		pvs2.addPropertyValue(new PropertyValue("url", "/example2.jsp"));
		wac.registerSingleton("example2", JstlView.class, pvs2);
		BeanNameViewResolver vr = new BeanNameViewResolver();
		vr.setApplicationContext(wac);

		View view = vr.resolveViewName("example1", Locale.getDefault());
		assertTrue("Correct view class", InternalResourceView.class.equals(view.getClass()));
		assertTrue("Correct URL", "/example1.jsp".equals(((InternalResourceView) view).getUrl()));

		view = vr.resolveViewName("example2", Locale.getDefault());
		assertTrue("Correct view class", JstlView.class.equals(view.getClass()));
		assertTrue("Correct URL", "/example2.jsp".equals(((InternalResourceView) view).getUrl()));
	}

	public void testInternalResourceViewResolverWithoutPrefixes() throws ServletException, IOException {
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		wac.setServletContext(new MockServletContext());
		InternalResourceViewResolver vr = new InternalResourceViewResolver();
		vr.setApplicationContext(wac);
		vr.setRequestContextAttribute("rc");

		View view = vr.resolveViewName("example1", Locale.getDefault());
		assertTrue("Correct view class", InternalResourceView.class.equals(view.getClass()));
		assertTrue("Correct URL", "example1".equals(((InternalResourceView) view).getUrl()));

		view = vr.resolveViewName("example2", Locale.getDefault());
		assertTrue("Correct view class", InternalResourceView.class.equals(view.getClass()));
		assertTrue("Correct URL", "example2".equals(((InternalResourceView) view).getUrl()));

		ServletContext sc = new MockServletContext();
		HttpServletRequest request = new MockHttpServletRequest(sc, "GET", "/example");
		HttpServletResponse response = new MockHttpServletResponse();
		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
		request.setAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE, new FixedThemeResolver());
		Map model = new HashMap();
		TestBean tb = new TestBean();
		model.put("tb", tb);
		view.render(model, request, response);
		assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
		assertTrue("Correct rc attribute", request.getAttribute("rc") instanceof RequestContext);
	}

	public void testInternalResourceViewResolverWithPrefixes() throws ServletException {
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		wac.setServletContext(new MockServletContext());
		InternalResourceViewResolver vr = new InternalResourceViewResolver();
		vr.setPrefix("/WEB-INF/");
		vr.setSuffix(".jsp");
		vr.setApplicationContext(wac);

		View view = vr.resolveViewName("example1", Locale.getDefault());
		assertTrue("Correct view class", InternalResourceView.class.equals(view.getClass()));
		assertTrue("Correct URL", "/WEB-INF/example1.jsp".equals(((InternalResourceView) view).getUrl()));

		view = vr.resolveViewName("example2", Locale.getDefault());
		assertTrue("Correct view class", InternalResourceView.class.equals(view.getClass()));
		assertTrue("Correct URL", "/WEB-INF/example2.jsp".equals(((InternalResourceView) view).getUrl()));
	}

	public void testInternalResourceViewResolverWithJstl() throws ServletException, IOException {
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		wac.setServletContext(new MockServletContext());
		InternalResourceViewResolver vr = new InternalResourceViewResolver();
		vr.setViewClass(JstlView.class);
		vr.setApplicationContext(wac);

		View view = vr.resolveViewName("example1", Locale.getDefault());
		assertTrue("Correct view class", JstlView.class.equals(view.getClass()));
		assertTrue("Correct URL", "example1".equals(((InternalResourceView) view).getUrl()));

		view = vr.resolveViewName("example2", Locale.getDefault());
		assertTrue("Correct view class", JstlView.class.equals(view.getClass()));
		assertTrue("Correct URL", "example2".equals(((InternalResourceView) view).getUrl()));

		ServletContext sc = new MockServletContext();
		MockHttpServletRequest request = new MockHttpServletRequest(sc, "GET", "/example");
		Locale locale = !Locale.GERMAN.equals(Locale.getDefault()) ? Locale.GERMAN : Locale.ENGLISH;
		request.addPreferredLocale(locale);
		HttpServletResponse response = new MockHttpServletResponse();
		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
		Map model = new HashMap();
		TestBean tb = new TestBean();
		model.put("tb", tb);
		view.render(model, request, response);
		assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
		assertTrue("Correct rc attribute", request.getAttribute("rc") == null);
		assertTrue("Correct JSTL attributes", request.getAttribute(Config.FMT_LOCALIZATION_CONTEXT) instanceof LocalizationContext);
		assertTrue("Correct JSTL attributes", locale.equals(request.getAttribute(Config.FMT_LOCALE)));
		assertTrue("Correct JSTL attributes", request.getAttribute(Config.FMT_LOCALIZATION_CONTEXT + JstlView.REQUEST_SCOPE_PREFIX) instanceof LocalizationContext);
		assertTrue("Correct JSTL attributes", locale.equals(request.getAttribute(Config.FMT_LOCALE + JstlView.REQUEST_SCOPE_PREFIX)));
	}

	public void testXmlViewResolver() throws ServletException, IOException {
		StaticWebApplicationContext wac = new StaticWebApplicationContext() {
			protected InputStream getResourceByPath(String path) throws IOException {
				return ViewResolverTestSuite.class.getResourceAsStream(path);
			}
		};
		wac.setServletContext(new MockServletContext());
		XmlViewResolver vr = new XmlViewResolver();
		vr.setLocation("/com/interface21/web/servlet/view/views.xml");
		vr.setApplicationContext(wac);

		View view = vr.resolveViewName("example1", Locale.getDefault());
		assertTrue("Correct view class", InternalResourceView.class.equals(view.getClass()));
		assertTrue("Correct URL", "/example1.jsp".equals(((InternalResourceView) view).getUrl()));

		view = vr.resolveViewName("example2", Locale.getDefault());
		assertTrue("Correct view class", JstlView.class.equals(view.getClass()));
		assertTrue("Correct URL", "/example2new.jsp".equals(((InternalResourceView) view).getUrl()));

		ServletContext sc = new MockServletContext();
		HttpServletRequest request = new MockHttpServletRequest(sc, "GET", "/example");
		HttpServletResponse response = new MockHttpServletResponse();
		request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
		request.setAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE, new FixedThemeResolver());
		Map model = new HashMap();
		TestBean tb = new TestBean();
		model.put("tb", tb);
		view.render(model, request, response);
		assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
		assertTrue("Correct test1 attribute", "testvalue1".equals(request.getAttribute("test1")));
		assertTrue("Correct test2 attribute", "testvalue2".equals(request.getAttribute("test2")));
	}

	public void testXmlViewResolverDefaultLocation() {
		StaticWebApplicationContext wac = new StaticWebApplicationContext() {
			protected InputStream getResourceByPath(String path) throws IOException {
				assertTrue("Correct default location", XmlViewResolver.DEFAULT_LOCATION.equals(path));
				return null;
			}
		};
		wac.setServletContext(new MockServletContext());
		XmlViewResolver vr = new XmlViewResolver();
		try {
			vr.setApplicationContext(wac);
			fail("Should have thrown ApplicationContextException");
		}
		catch (ApplicationContextException ex) {
			// expected
		}
	}

	public void testXmlViewResolverWithoutCache() {
		StaticWebApplicationContext wac = new StaticWebApplicationContext() {
			protected InputStream getResourceByPath(String path) throws IOException {
				assertTrue("Correct default location", XmlViewResolver.DEFAULT_LOCATION.equals(path));
				return null;
			}
		};
		wac.setServletContext(new MockServletContext());
		XmlViewResolver vr = new XmlViewResolver();
		vr.setCache(false);
		try {
			vr.setApplicationContext(wac);
		}
		catch (ApplicationContextException ex) {
			fail("Should not have thrown ApplicationContextException: " + ex.getMessage());
		}
		try {
			vr.resolveViewName("example1", Locale.getDefault());
			fail("Should have thrown ServletException");
		}
		catch (ServletException ex) {
			// expected
		}
	}

}
