package com.interface21.web.servlet.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.interface21.web.servlet.support.RequestContext;
import com.interface21.web.util.ExpressionEvaluationUtils;

/**
 * Superclass for all tags that require a RequestContext.
 * The RequestContext instance provides easy access to current
 * state like WebApplicationContext, Locale, Theme, etc.
 *
 * <p>Supports an HTML escaping setting per tag instance,
 * overriding any default setting at the page or web.xml level.
 *
 * <p>Note: Only intended for DispatcherServlet requests!
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
	 * Sets HTML escaping for this tag, overriding the default
	 * HTML escaping setting for the current page.
	 * @see HtmlEscapeTag#setDefaultHtmlEscape
	 */
	public final void setHtmlEscape(String htmlEscape) throws JspException {
		this.htmlEscape = new Boolean(ExpressionEvaluationUtils.evaluateBoolean("htmlEscape", htmlEscape, pageContext));
	}

	/**
	 * Returns the HTML escaping setting for this tag,
	 * or the default setting if not overridden.
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
	 * Delegates to doStartTagInternal for actual work.
	 */
	public final int doStartTag() throws JspException {
		try {
			this.requestContext = new RequestContext((HttpServletRequest) this.pageContext.getRequest());
			return doStartTagInternal();
		}
		catch (JspException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}
	}

	/**
	 * Called by doStartTag to perform the actual work.
	 * @return same as TagSupport.doStartTag
	 * @throws Exception any exception, every other than JspException
	 * gets wrapped in a JspException by doStartTag
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag
	 */
	protected abstract int doStartTagInternal() throws Exception;

}
