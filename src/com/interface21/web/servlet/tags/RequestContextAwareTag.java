package com.interface21.web.servlet.tags;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.interface21.web.servlet.support.RequestContext;

/**
 * Superclass for all tags that require a RequestContext.
 * The RequestContext instance provides easy access to current
 * state like WebApplicationContext, Locale, Theme, etc.
 *
 * <p>Supports a HTML escaping setting per tag instance,
 * overriding any default setting at the page or web.xml level.
 *
 * <p>Note: Works only in DispatcherServlet requests!
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.interface21.web.servlet.support.RequestContext
 * @see HtmlEscapeTag#setDefaultHtmlEscape
 * @see HtmlEscapeTag#HTML_ESCAPE_CONTEXT_PARAM
 */
public abstract class RequestContextAwareTag extends TagSupport {

	private Boolean htmlEscape;

	private RequestContext requestContext;

	/**
	 * Set HTML escaping for this tag, overriding the default
	 * HTML escaping setting for the current page.
	 * @see HtmlEscapeTag#setDefaultHtmlEscape
	 */
	public final void setHtmlEscape(boolean htmlEscape) {
		this.htmlEscape = new Boolean(htmlEscape);
	}

	/**
	 * Return the HTML escaping setting for this tag,
	 * or the default setting if not overridden.
	 * @return
	 */
	protected final boolean isHtmlEscape() {
		return (this.htmlEscape != null ? this.htmlEscape.booleanValue() :
																			HtmlEscapeTag.isDefaultHtmlEscape(this.pageContext));
	}

	/**
	 * Return the current RequestContext.
	 */
	protected final RequestContext getRequestContext() {
		return requestContext;
	}

	/**
	 * Create and set the current RequestContext.
	 * Note: Do not forget to call super.doStartTag() in subclasses!
	 */
	public int doStartTag() throws JspException {
		try {
			this.requestContext = new RequestContext((HttpServletRequest) this.pageContext.getRequest());
		}
		catch (ServletException ex) {
			throw new JspTagException(ex.getMessage());
		}
		return EVAL_BODY_INCLUDE;
	}

}
