package com.interface21.orm.jdo;

import com.interface21.dao.UncategorizedDataAccessException;

/**
 * JDO exception that gets thrown on system errors.
 * @author Juergen Hoeller
 * @since 03.06.2003
 */
public class JdoSystemException extends UncategorizedDataAccessException {

	public JdoSystemException(String s, Throwable ex) {
		super(s, ex);
	}
	
}
