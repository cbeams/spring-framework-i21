package com.interface21.web.servlet;

import junit.framework.TestCase;

import com.interface21.web.mock.MockHttpServletRequest;
import com.interface21.web.mock.MockHttpServletResponse;
import com.interface21.web.mock.MockServletContext;
import com.interface21.web.servlet.theme.AbstractThemeResolver;
import com.interface21.web.servlet.theme.CookieThemeResolver;
import com.interface21.web.servlet.theme.FixedThemeResolver;
import com.interface21.web.servlet.theme.SessionThemeResolver;

/**
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 19.06.2003
 */
public class ThemeResolverTestSuite extends TestCase {

	private static final String TEST_THEME_NAME = "test.theme";
	private static final String DEFAULT_TEST_THEME_NAME = "default.theme";

	public ThemeResolverTestSuite(String name) {
		super(name);
	}

	private void internalTest(ThemeResolver themeResolver, boolean shouldSet, String defaultName) {
		// create mocks
		MockServletContext context = new MockServletContext();
		MockHttpServletRequest request = new MockHttpServletRequest(context, "GET", "/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		// check original theme
		String themeName = themeResolver.resolveThemeName(request);
		assertEquals(themeName, defaultName);
		// set new theme name
		try {
			themeResolver.setThemeName(request, response, TEST_THEME_NAME);
			if (!shouldSet)
				fail("should not be able to set Theme name");
			// check new theme namelocale
			themeName = themeResolver.resolveThemeName(request);
			assertEquals(themeName, TEST_THEME_NAME);
			themeResolver.setThemeName(request, response, null);
			themeName = themeResolver.resolveThemeName(request);
			assertEquals(themeName, defaultName);
		} catch (IllegalArgumentException ex) {
			if (shouldSet)
				fail("should be able to set Theme name");
		}
	}

	public void testFixedThemeResolver() {
		internalTest(new FixedThemeResolver(), false, AbstractThemeResolver.DEFAULT_THEME);
	}

	public void testCookieThemeResolver() {
		internalTest(new CookieThemeResolver(), true, AbstractThemeResolver.DEFAULT_THEME);
	}

	public void testSessionThemeResolver() {
		internalTest(new SessionThemeResolver(), true,AbstractThemeResolver.DEFAULT_THEME);
	}

	public void testSessionThemeResolverWithDefault() {
		SessionThemeResolver tr = new SessionThemeResolver();
		tr.setDefaultThemeName(DEFAULT_TEST_THEME_NAME);
		internalTest(tr, true, DEFAULT_TEST_THEME_NAME);
	}
}
