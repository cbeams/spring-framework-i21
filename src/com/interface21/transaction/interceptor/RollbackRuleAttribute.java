/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.transaction.interceptor;

//import attrib4j.Attribute;

/**
 * Rule determining whether or not a given exception 
 * (and any subclasses) should
 * cause a rollback. Multiple such rules can be applied to
 * determine whether a transaction should commit or rollback
 * after an exception has been thrown.
 *
 * @since 09-Apr-2003
 * @version $Id$
 * @author Rod Johnson
 */
public class RollbackRuleAttribute
// TODO currently need to implement Attrib4j tag interface for metadata operation.
// This restriction should soon disappear.
//implements Attribute 
{
	
	public static final RollbackRuleAttribute ROLLBACK_ON_RUNTIME_EXCEPTIONS = new RollbackRuleAttribute("java.lang.RuntimeException");
	
	// Could hold exception, resolving classname
	// but would always require FQN.
	// This way does multiple string comparisons,
	// but how often do we decide whether to roll back
	// a transaction following an exception?
	private final String exceptionName;

	/**
	 * Construct a new RollbackRule for the given exception name.
	 * This can be a substring, with no wildcard support at present.
	 * A value of "ServletException" would match ServletException and
	 * subclasses, for example.
	 * @param exceptionName
	 */
	public RollbackRuleAttribute(String exceptionName) {
		this.exceptionName = exceptionName;
	}
	
	
	/**
	 * Return the depth to the superclass matching.
	 * 0 means t matches.
	 * Return -1 if there's no match.
	 * Otherwise, return depth.
	 * Lowest depth wins.
	 * @param t
	 * @return int
	 */
	public int getDepth(Throwable t) {
		return getDepth(t.getClass(), 0);
	}
	
	
	private int getDepth(Class exceptionClass, int depth) {
		if (exceptionClass.getName().indexOf(this.exceptionName) != -1)
			// Found it!
			return depth;
			
		// If we've gone as far as we can go and haven't found it...
		if (exceptionClass.equals(Throwable.class))
			return -1;
			
		return getDepth(exceptionClass.getSuperclass(), depth + 1);
	}
	
	public String toString() {
		return "RollbackRule: pattern='" + this.exceptionName + "'";
	}
}
