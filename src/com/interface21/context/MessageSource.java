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
 * This enables parameterization and internationalization of
 * messages.
 * @author Rod Johnson
 */
public interface MessageSource {		
	
	/**
	 * Try to resolve the message.Return default message if no message
	 * was found
	 * @param code code to lookup up, such as 'calculator.noRateSet'.
	 * Users of this class are encouraged to base message names
	 * on the relevant fully qualified class name, thus avoiding
	 * conflict and ensuring maximum clarity.
	 * @param locale Locale in which to do lookup
	 * @param defaultMessage String to return if the lookup fails
	 * @return a resolved message if the lookup is successful;
	 * otherwise return the default message passed as a parameter
	 */
	String getMessage(String code, Locale locale, String defaultMessage);
	
	/**
	 * Try to resolve the message. Treat as an error if the message can't
	 * be found.
	 * @param code code to lookup up, such as 'calculator.noRateSet'
	 * @param locale Locale in which to do lookup
	 * @return message
	 * @throws NoSuchMessageException not found in any locale
	 */
	String getMessage(String code, Locale locale) throws NoSuchMessageException;

}

