package com.interface21.context.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.interface21.context.MessageSource;
import com.interface21.context.MessageSourceResolvable;
import com.interface21.context.NestingMessageSource;
import com.interface21.context.NoSuchMessageException;


/**
 * Abstract implementation of NestingMessageSource interface, making it
 * easy to implement custom MessageSources. Subclasses must implement the
 * abstract resolve() method.
 * <br/>This class does not currently implement caching, thus subclasses can
 * dynamically change messages over time.
 * NOTE: Some methods of this class are based on code from Struts 1.1b3 implementation.
 * @author Rod Johnson
 */
public abstract class AbstractNestingMessageSource implements NestingMessageSource {

	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------

	/** Parent MessageSource */
	private MessageSource parent;

	/**
	 * The default Locale for our environment.
	 */
	private Locale defaultLocale = Locale.getDefault();

	/**
	 * The set of previously created MessageFormat objects, keyed by the
	 * key computed in <code>messageKey()</code>.
	 */
	private Map formats = new HashMap();


	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------

	public AbstractNestingMessageSource() {
	}


	//---------------------------------------------------------------------
	// Implementation of MessageSource
	//---------------------------------------------------------------------

	public final void setParent(MessageSource parent) {
		this.parent = parent;
	}


	/**
	 * Try to resolve the message.Return default message if no message
	 * was found
	 * @param code code to lookup up, such as 'calculator.noRateSet'
	 * @param locale Locale in which to do lookup
	 * @param args Array of arguments that will be filled in for params within
	 * the message (params look like "{0}", "{1,date}", "{2,time}" within a message).
	 * @see <a href=http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html>java.text.MessageFormat</a>
	 * @param defaultMessage String to return if the lookup fails
	 * @return a resolved message if the lookup is successful;
	 * otherwise return the default message passed as a parameter
	 */
	public final String getMessage(String code, Object args[], String defaultMessage, Locale locale) {
		try {
			return getMessage(code, args, locale);
		} catch (NoSuchMessageException ex) {
			return defaultMessage;
		}
	}


	/**
	 * <b>Using all the attributes contained within the <code>MessageSourceResolvable</code>
	 * arg that was passed in (except for the <code>locale</code> attribute)</b>,
	 * try to resolve the message from the <code>MessageSource</code> contained within the <code>Context</code>.<p>
	 *
	 * NOTE: We must throw a <code>NoSuchMessageException</code> on this method since
	 * at the time of calling this method we aren't able to determine if the <code>defaultMessage</code>
	 * attribute is null or not.
	 * @param resolvable Value object storing 4 attributes required to properly resolve a message.
	 * @param locale Locale to be used as the "driver" to figuring out what message to return.
	 * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html">java.text.MessageFormat</a>
	 * @return message Resolved message.
	 * @throws NoSuchMessageException not found in any locale
	 */
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		String[] codes = resolvable.getCodes();
		for (int i = 0; i < codes.length; i++) {
			try {
				return getMessage(codes[i], resolvable.getArgs(), locale);
			} catch (NoSuchMessageException ex) {
				// swallow it, we'll retry the other codes
			}
		}
		if (resolvable.getDefaultMessage() == null) {
			throw new NoSuchMessageException(codes[codes.length-1], locale);
		}
		return resolvable.getDefaultMessage();
	}


	/**
	 * Try to resolve the message. Treat as an error if the message can't
	 * be found.
	 * @param code code to lookup up, such as 'calculator.noRateSet'
	 * @param locale Locale in which to do lookup
	 * @param args Array of arguments that will be filled in for params within
	 * the message (params look like "{0}", "{1,date}", "{2,time}" within a message).
	 * @see <a href=http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html">java.text.MessageFormat</a>
	 * @return message
	 * @throws NoSuchMessageException not found in any locale
	 */
	public final String getMessage(String code, Object args[], Locale locale) throws NoSuchMessageException {
		try {
			String mesg = resolve(code, locale);

			if (mesg == null) {
				if (parent != null)
					mesg = parent.getMessage(code, args, locale);
				else
					throw new NoSuchMessageException(code, locale);
			}

			// Cache MessageFormat instances as they are accessed
			if (locale == null)
				locale = defaultLocale;
			MessageFormat format = null;
			String formatKey = messageKey(locale, code);
			synchronized (formats) {
				format = (MessageFormat) formats.get(formatKey);
				if (format == null) {
					format = new MessageFormat(escape(mesg));
					formats.put(formatKey, format);
				}
			}
			return (format.format(args));
		} catch (NoSuchMessageException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new NoSuchMessageException(code, locale);
		}
	}


	/**
	 * Subclasses must implement this method to resolve a message
	 * @return the message, or null if not found
	 * @throws Exception if there's an error resolving the message.
	 * Note that failure to find a message for the code is not an error.
	 * @param code code of the message to resolve
	 * @param locale locale to resolve the code for. Subclasses
	 * are encouraged to support internationalization.
	 */
	protected abstract String resolve(String code, Locale locale) throws Exception;


	protected Locale getDefaultLocale() {
		return defaultLocale;
	}


	/**
	 * Compute and return a key to be used in caching information by a Locale.
	 * <strong>NOTE</strong> - The locale key for the default Locale in our
	 * environment is a zero length String.
	 *
	 * @param locale The locale for which a key is desired
	 */
	protected String localeKey(Locale locale) {
		if (locale == null)
			return ("");
		//        else if (locale.equals(defaultLocale))
		//            return ("");
		else
			return (locale.toString());
	}


	/**
	 * Compute and return a key to be used in caching information
	 * by Locale and message key.
	 *
	 * @param locale The Locale for which this format key is calculated
	 * @param key The message key for which this format key is calculated
	 */
	protected String messageKey(Locale locale, String key) {
		return (localeKey(locale) + "." + key);
	}


	/**
	 * Compute and return a key to be used in caching information
	 * by locale key and message key.
	 *
	 * @param localeKey The locale key for which this cache key is calculated
	 * @param key The message key for which this cache key is calculated
	 */
	protected String messageKey(String localeKey, String key) {
		return (localeKey + "." + key);
	}


	/**
	 * Escape any single quote characters that are included in the specified
	 * message string.
	 *
	 * @param string The string to be escaped
	 */
	protected String escape(String string) {
		if ((string == null) || (string.indexOf('\'') < 0))
			return (string);
		int n = string.length();
		StringBuffer sb = new StringBuffer(n);
		for (int i = 0; i < n; i++) {
			char ch = string.charAt(i);
			if (ch == '\'')
				sb.append('\'');
			sb.append(ch);
		}
		return (sb.toString());
	}


}
