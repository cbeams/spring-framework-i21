package com.interface21.web.servlet;

import java.util.Locale;

import junit.framework.TestCase;

import com.interface21.web.mock.MockHttpResponse;
import com.interface21.web.mock.MockHttpRequest;
import com.interface21.web.mock.MockServletContext;
import com.interface21.web.servlet.i18n.AcceptHeaderLocaleResolver;
import com.interface21.web.servlet.i18n.CookieLocaleResolver;
import com.interface21.web.servlet.i18n.SessionLocaleResolver;

/**
 * @author Juergen Hoeller
 * @since 20.03.2003
 */
public class LocaleResolverTestSuite extends TestCase {

	public LocaleResolverTestSuite(String name) {
		super(name);
	}

	private void internalTest(LocaleResolver localeResolver, boolean shouldSet) {
		// create mocks
		MockServletContext context = new MockServletContext();
		MockHttpRequest request = new MockHttpRequest(context, "GET", "/test");
		request.addPreferredLocale(Locale.UK);
		MockHttpResponse response = new MockHttpResponse();
		// check original locale
		Locale locale = localeResolver.resolveLocale(request);
		assertEquals(locale, Locale.UK);
		// set new locale
		try {
			localeResolver.setLocale(request, response, Locale.GERMANY);
			if (!shouldSet)
				fail("should not be able to set Locale");
			// check new locale
			locale = localeResolver.resolveLocale(request);
			assertEquals(locale, Locale.GERMANY);
		} catch (IllegalArgumentException ex) {
			if (shouldSet)
				fail("should be able to set Locale");
		}
	}

	public void testAcceptHeaderLocaleResolver() {
		internalTest(new AcceptHeaderLocaleResolver(), false);
	}

	public void testCookieLocaleResolver() {
		internalTest(new CookieLocaleResolver(), true);
	}

	public void testSessionLocaleResolver() {
		internalTest(new SessionLocaleResolver(), true);
	}
}
