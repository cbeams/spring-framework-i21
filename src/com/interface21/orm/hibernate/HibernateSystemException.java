package com.interface21.orm.hibernate;

import net.sf.hibernate.HibernateException;

import com.interface21.dao.UncategorizedDataAccessException;

/**
 * Hibernate-specific subclass of DataAccessException, for Hibernate system
 * errors that do not match any concrete com.interface21.dao exceptions.
 * Used by HibernateTemplate.
 *
 * <p>Part of the general strategy to allow for using Hibernate within
 * application service implementations that just feature DataAccessException
 * in their interfaces. Clients of these services do not need to be aware of
 * the particular data access strategy used by the service implementations.
 *
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see HibernateTemplate
 * @see com.interface21.dao
 */
public class HibernateSystemException extends UncategorizedDataAccessException {

	public HibernateSystemException(String msg, HibernateException ex) {
		super(msg, ex);
	}

}
