/**
 * Generic framework code included with
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * This code is free to use and modify.
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.context;

import java.util.Locale;

/**
 * Interface to be implemented by objects that can resolve messages.
 * This enables parameterization and internationalization of messages.
 * @author Rod Johnson
 */
public interface MessageSource {

	/**
	 * Try to resolve the message.Return default message if no message
	 * was found.
	 * @param code code to lookup up, such as 'calculator.noRateSet'.
	 * Users of this class are encouraged to base message names
	 * on the relevant fully qualified class name, thus avoiding
	 * conflict and ensuring maximum clarity.
	 * @param args Array of arguments that will be filled in for params within
	 * the message (params look like "{0}", "{1,date}", "{2,time}" within a message),
	 * or null if none.
	 * @param locale Locale in which to do lookup
	 * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html">java.text.MessageFormat</a>
	 * @param defaultMessage String to return if the lookup fails
	 * @return a resolved message if the lookup is successful;
	 * otherwise return the default message passed as a parameter
	 */
	String getMessage(String code, Object args[], String defaultMessage, Locale locale);

	/**
	 * Try to resolve the message. Treat as an error if the message can't
	 * be found.
	 * @param code code to lookup up, such as 'calculator.noRateSet'
	 * @param args Array of arguments that will be filled in for params within
	 * the message (params look like "{0}", "{1,date}", "{2,time}" within a message),
	 * or null if none.
	 * @param locale Locale in which to do lookup
	 * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html">java.text.MessageFormat</a>
	 * @return message
	 * @throws NoSuchMessageException not found in any locale
	 */
	String getMessage(String code, Object args[], Locale locale) throws NoSuchMessageException;

	/**
	 * <b>Using all the attributes contained within the <code>MessageSourceResolvable</code>
	 * arg that was passed in (except for the <code>locale</code> attribute)</b>,
	 * try to resolve the message from the <code>MessageSource</code> contained within the <code>Context</code>.<p>
	 * NOTE: We must throw a <code>NoSuchMessageException</code> on this method since
	 * at the time of calling this method we aren't able to determine if the <code>defaultMessage</code>
	 * attribute is null or not.
	 * @param resolvable Value object storing 4 attributes required to properly resolve a message.
	 * @param locale Locale to be used as the "driver" to figuring out what message to return.
	 * @see <a href="http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html">java.text.MessageFormat</a>
	 * @return message Resolved message.
	 * @throws NoSuchMessageException not found in any locale
	 */
	String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;
}

