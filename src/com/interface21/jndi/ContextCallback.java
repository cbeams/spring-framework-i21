/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. However, please
 * acknowledge the source and include the above URL in each
 * class using or derived from this code. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */

package com.interface21.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Callback interface to be implemented by classes that
 * need to perform an operation (such as a lookup) in a 
 * JNDI context. This callback approach is valuable in
 * simplifying error handling, which is performed
 * by the JndiTemplate class. This is a similar approach to
 * that used by the JdbcTemplate class.
 * @see com.interface21.jndi.JndiTemplate
 * @see com.interface21.jdbc.core.JdbcTemplate
 * @author Rod Johnson
 */
public interface ContextCallback {
    
	/**
	 * Do something with the given JNDI context.
	 * Implementations don't need to worry about error handling
	 * or cleanup, as the JndiTemplate class will handle this.
	 * @param ctx  the current JNDI context
	 * @throws NamingException  Implementations don't need
	 * to catch naming exceptions
	 * @return  a result object, or null
	 */
  Object doInContext(Context ctx) throws NamingException;

}

