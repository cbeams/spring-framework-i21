package com.interface21.web.servlet.view;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.ServletException;

import com.interface21.context.support.MessageSourceResourceBundle;
import com.interface21.web.servlet.support.RequestContextUtils;

/**
 * Specialization of InternalResourceView for JSTL pages,
 * i.e. JSP pages that use the JSP Standard Tag Library.
 *
 * <p>Exposes JSTL-specific request attributes specifying locale
 * and resource bundle for JSTL's formatting and message tags,
 * using Spring's locale and message source.
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 */
public class JstlView extends InternalResourceView {

	public static final String REQUEST_SCOPE_PREFIX = ".request";

	protected void exposeModelsAsRequestAttributes(Map model, HttpServletRequest request) throws ServletException {
		super.exposeModelsAsRequestAttributes(model, request);

		// add JSTL locale and LocalizationContext request attributes
		Locale jstlLocale = RequestContextUtils.getLocale(request);
		ResourceBundle bundle = new MessageSourceResourceBundle(getApplicationContext(), jstlLocale);
		LocalizationContext jstlContext = new LocalizationContext(bundle, jstlLocale);

		// for JSTL implementations that stick to the config names (e.g. Resin's)
		request.setAttribute(Config.FMT_LOCALIZATION_CONTEXT, jstlContext);
		request.setAttribute(Config.FMT_LOCALE, jstlLocale);

		// for JSTL implementations that append the scope to the config names (e.g. Jakarta's)
		request.setAttribute(Config.FMT_LOCALIZATION_CONTEXT + REQUEST_SCOPE_PREFIX, jstlContext);
		request.setAttribute(Config.FMT_LOCALE + REQUEST_SCOPE_PREFIX, jstlLocale);
	}
}
