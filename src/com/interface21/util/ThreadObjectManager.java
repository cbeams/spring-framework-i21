package com.interface21.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides static methods for managing key/value pairs that are bound to the
 * current thread. Supports one thread value per key without overwriting, i.e.
 * a value needs to be removed before a new one can be set for the same key.
 *
 * <p>Used by DataSourceTransactionManager to keep a JDBC transaction per
 * DataSource and thread. Does not need to be used by application developers.
 *
 * @author Juergen Hoeller
 * @since 02.05.2003
 * @see com.interface21.jdbc.datasource.DataSourceUtils#getConnection
 * @see com.interface21.transaction.datasource.DataSourceTransactionManager
 */
public class ThreadObjectManager {

	private final Log logger = LogFactory.getLog(getClass());

	private ThreadLocal threadLocal = new ThreadLocal() {
		protected Object initialValue() {
			return new HashMap();
		}
	};

	private Map getThreadObjectMap() {
		return (Map) threadLocal.get();
	}

	/**
	 * Check if there is a value for the given key bound to the current thread.
	 * @param key key to check
	 * @return if there is a value bound to the current thread
	 */
	public boolean hasThreadObject(Object key) {
		return getThreadObjectMap().containsKey(key);
	}

	/**
	 * Retrieve a value for the given key that is bound to the current thread.
	 * @param key key to check
	 * @return a value bound to the current thread, or null if none
	 */
	public Object getThreadObject(Object key) {
		Object value = getThreadObjectMap().get(key);
		if (value != null && logger.isDebugEnabled()) {
			logger.debug("Retrieved value [" + value + "] for key [" + key + "] bound to thread [" + Thread.currentThread().getName() + "]");
		}
		return value;
	}

	/**
	 * Bind the given value for the given key to the current thread.
	 * @param key key to bind the value to
	 * @param value value to bind
	 * @throws java.lang.IllegalStateException if there is already a value bound to the thread
	 */
	public void bindThreadObject(Object key, Object value) {
		if (hasThreadObject(key)) {
			throw new IllegalStateException("Already a value for key [" + key + "] bound to thread");
		}
		getThreadObjectMap().put(key, value);
		if (logger.isDebugEnabled()) {
			logger.debug("Bound value [" + value + "] for key [" + key + "] to thread [" + Thread.currentThread().getName() + "]");
		}
	}


	/**
	 * Remove a value for the key from the current thread.
	 * @param key key to check
	 * @throws java.lang.IllegalStateException if there is no value bound to the thread
	 */
	public void removeThreadObject(Object key) {
		if (!hasThreadObject(key)) {
			throw new IllegalStateException("No value for key " + key + " bound to thread");
		}
		Object value = getThreadObjectMap().remove(key);
		if (logger.isDebugEnabled()) {
			logger.debug("Removed value [" + value + "] for key [" + key + "] from thread [" + Thread.currentThread().getName() + "]");
		}
	}

}
