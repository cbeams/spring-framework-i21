package com.interface21.orm.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

/**
 * Callback interface for Hibernate code. To be used with HibernateTemplate's execute
 * method, assumably often as anonymous classes within a method implementation.
 *
 * <p>The typical implementation will call Session.load/find/save/update to perform
 * some operations on persistent objects. It can also perform direct JDBC operations
 * via Hibernate's Session.connection() method, returning the active JDBC conneciton. 
 *
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see HibernateTemplate
 */
public interface HibernateCallback {

	/**
	 * Gets called by HibernateTemplate.execute with an active Hibernate Session.
	 * Does not need to care about activating or closing the Session, or taking
	 * part in transactions.
	 *
	 * <p>Allows for returning a result object created within the callback, i.e.
	 * a business object or a collection of business objects. Note that there's
	 * special support for List results: see HibernateTemplate.executeFind.
	 * A thrown RuntimeException is treated as application exception, it gets
	 * propagated to the caller of the template.
	 *
	 * @param session active Hibernate session
	 * @return a result object, or null if none
	 * @throws HibernateException in case of Hibernate errors
	 * @throws RuntimeException in case of an application exception,
	 * propagating the exception to the caller
	 * @see HibernateTemplate#execute
	 * @see HibernateTemplate#executeFind
	 */
	Object doInHibernate(Session session) throws HibernateException, RuntimeException;

}
