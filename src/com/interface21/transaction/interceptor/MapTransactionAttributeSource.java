/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.transaction.TransactionUsageException;

/**
 * Simple implementation of TransactionAttributeSource that
 * allows attributes to be stored in a map.
 * @since 24-Apr-2003
 * @version $Id$
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class MapTransactionAttributeSource implements TransactionAttributeSource {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	/** Map from method to TransactionAttribute */
	protected Map methodMap = new HashMap();

	public TransactionAttribute getTransactionAttribute(MethodInvocation invocation) {
		return (TransactionAttribute) this.methodMap.get(invocation.getMethod());
	}

	/**
	 * Set a name/attribute map, consisting of "FQCN.method" method names
	 * (e.g. "com.mycompany.mycode.MyClass.myMethod") and TransactionAttribute
	 * instances.
	 */
	public void setMethodMap(Map methodMap) {
		Iterator it = methodMap.keySet().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			Object attr =  methodMap.get(name);
			if (!(attr instanceof TransactionAttribute)) {
				throw new IllegalArgumentException("methodMap values must be of type TransactionAttribute");
			}
			addTransactionalMethod(name, (TransactionAttribute) attr);
		}
	}

	/**
	 * Add an attribute for a transactional method.
	 * @param method method
	 * @param attr attribute associated with the method
	 */
	public void addTransactionalMethod(Method method, TransactionAttribute attr) {
		logger.info("Adding transactional method " + method + " with attr " + attr);
		this.methodMap.put(method, attr);
	}

	public void addTransactionalMethod(String name, TransactionAttribute attr) {
		int lastDotIndex = name.lastIndexOf(".");
		if (lastDotIndex == -1)
			throw new TransactionUsageException("'" + name + "' is not a valid method name: format is FQN.methodName");
		String className = name.substring(0, lastDotIndex);
		String methodName = name.substring(lastDotIndex + 1);

		logger.debug("Transactional method: " + className + "/" + methodName +
				" with transaction attribute string " + attr);

		try {
			Class clazz = Class.forName(className);

			// TODO address method overloading? At present this will
			// simply match all methods that have the given name.
			// Consider EJB syntax (int, String) etc.?
			Method[] methods = clazz.getDeclaredMethods();
			List matchingMethods = new ArrayList();
			for (int i = 0; i < methods.length; i++) {
				if (isMatch(methods[i].getName(), methodName)) {
					matchingMethods.add(methods[i]);
				}
			}
			if (matchingMethods.isEmpty())
				throw new TransactionUsageException("Couldn't find method '" + methodName + "' on " + clazz);

			// register all matching methods
			for (Iterator it = matchingMethods.iterator(); it.hasNext();) {
				addTransactionalMethod((Method) it.next(), attr);
			}
		}
		catch (ClassNotFoundException ex) {
			throw new TransactionUsageException("Class '" + className + "' not found");
		}
	}

	/**
	 * Return if the given method name matches the mapped name.
	 * The default implementation checks for direct and "xxx*" matches.
	 * Can be overridden in subclasses.
	 * @param methodName the method name of the class
	 * @param mappedName the name in the descriptor
	 * @return if the names match
	 */
	protected boolean isMatch(String methodName, String mappedName) {
		return methodName.equals(mappedName) ||
		    (mappedName.endsWith("*") && methodName.startsWith(mappedName.substring(0, mappedName.length()-1)));
	}

}
