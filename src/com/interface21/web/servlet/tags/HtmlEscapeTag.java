package com.interface21.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Sets default HTML escape value for the current page. The actual value
 * can be overridden by escaping-aware tags. The default is "false".
 *
 * <p>Note: You can also set a "defaultHtmlEscape" web.xml context-param.
 * A page-level setting overrides a context-param.
 *
 * @author Juergen Hoeller
 * @since 04.03.2003
 * @see RequestContextAwareTag#setHtmlEscape
 */
public class HtmlEscapeTag extends TagSupport {

	/** ServletContext init parameter (web.xml context-param) */
	public static final String HTML_ESCAPE_CONTEXT_PARAM = "defaultHtmlEscape";

	/** PageContext attribute for page-level default */
	public static final String HTML_ESCAPE_PAGE_ATTR = "com.interface21.web.servlet.tags.HTML_ESCAPE";

	private boolean defaultHtmlEscape = false;

	/**
	 * Retrieves the default HTML escaping setting from the given PageContext,
	 * falling back to the ServletContext init parameter.
	 */
	public static boolean isDefaultHtmlEscape(PageContext pageContext) {
		Boolean defaultValue = (Boolean) pageContext.getAttribute(HTML_ESCAPE_PAGE_ATTR);
		if (defaultValue != null) {
			return defaultValue.booleanValue();
		}
		else {
			String param = pageContext.getServletContext().getInitParameter(HTML_ESCAPE_CONTEXT_PARAM);
			return Boolean.valueOf(param).booleanValue();
		}
	}

	/**
	 * Set the default value for HTML escaping,
	 * to be put in the current PageContext.
	 */
	public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
		this.defaultHtmlEscape = defaultHtmlEscape;
	}

	public int doStartTag() throws JspException {
		super.doStartTag();

		// simply add a respective PageContext attribute, for detection by other tags
		this.pageContext.setAttribute(HTML_ESCAPE_PAGE_ATTR, new Boolean(this.defaultHtmlEscape));

		return EVAL_BODY_INCLUDE;
	}

}
