/*
 * Generic framework code included with
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code.
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.web.servlet.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import com.interface21.web.util.HtmlUtils;
import com.interface21.context.NoSuchMessageException;
import com.interface21.context.MessageSource;

/**
 * Custom tag to look up a message in the scope of this page.
 * Messages are looked up using the ApplicationContext, and thus
 * should support internationalization.
 *
 * <p>Regards a HTML escaping setting, either on this tag instance,
 * the page level, or the web.xml level.
 *
 * <p>If "code" isn't set or cannot be resolved, "text" will be
 * used as default message. Thus, this tag can also be used for
 * HTML escaping of any texts.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setCode
 * @see #setText
 * @see #setHtmlEscape
 * @see HtmlEscapeTag#setDefaultHtmlEscape
 * @see HtmlEscapeTag#HTML_ESCAPE_CONTEXT_PARAM
 */
public class MessageTag extends RequestContextAwareTag {

	private String code = null;

	private String text = null;

	/**
	 * Set the message code for this tag.
	 */
	public final void setCode(String code) {
		this.code = code;
	}

	/**
	 * Set the message text for this tag.
	 */
	public final void setText(String text) {
		this.text = text;
	}

	public final int doStartTag() throws JspException {
		super.doStartTag();

		MessageSource messageSource = getMessageSource();
		if (messageSource == null) {
			throw new JspTagException("No corresponding MessageSource found");
		}
		try {
			String msg = null;
			if (this.code != null) {
				if (this.text != null) {
					msg = messageSource.getMessage(this.code, null, this.text,
					                               getRequestContext().getLocale());
				}
				else {
					msg = messageSource.getMessage(this.code, null,
					                               getRequestContext().getLocale());
				}
			}
			else {
				msg = this.text;
			}
			writeMessage(isHtmlEscape() ? HtmlUtils.htmlEscape(msg) : msg);
		}
		catch (NoSuchMessageException ex) {
			throw new JspTagException(getNoSuchMessageExceptionDescription(ex));
		}
		catch (IOException ex) {
			throw new JspTagException("Can't write message: " + ex.getMessage());
		}
		return EVAL_BODY_INCLUDE;
	}

	protected void writeMessage(String msg) throws IOException {
		this.pageContext.getOut().print(msg);
	}

	/**
	 * Use the application context itself for default message resolution.
	 */
	protected MessageSource getMessageSource() {
		return getRequestContext().getWebApplicationContext();
	}

	/**
	 * Return default exception message.
	 */
	protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
		return ex.getMessage();
	}

}
