package com.interface21.jndi.support;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.interface21.util.StringUtils;

/**
 * Simple implementation of a JNDI naming context.
 * Only supports binding plain Objects to String names.
 * Mainly targetted at test environments, but also usable for standalone applications.
 *
 * <p>This class is not intended for direct usage by applications, although it
 * can be used e.g. to override's JndiTemplate's createInitialContext method in
 * unit tests. Use SimpleNamingContextBuilder to set up a JVM-level JNDI environment.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see SimpleNamingContextBuilder
 * @see com.interface21.jndi.JndiTemplate#createInitialContext
 */
public class SimpleNamingContext implements Context {

	private final Log logger = LogFactory.getLog(getClass());

	private String root;

	private Hashtable boundObjects;

	private Hashtable environment = new Hashtable();

	/**
	 * Create a new naming context.
	 */
	public SimpleNamingContext() {
		this("");
	}

	/**
	 * Create a new naming context with the given naming root.
	 */
	public SimpleNamingContext(String root) {
		this.root = root;
		this.boundObjects = new Hashtable();
	}

	/**
	 * Create a new naming context with the given naming root,
	 * the given name/object map, and the JNDI environment entries.
	 */
	public SimpleNamingContext(String root, Hashtable boundObjects, Hashtable environment) {
		this.root = root;
		this.boundObjects = boundObjects;
		if (environment != null) {
			this.environment.putAll(environment);
		}
	}


	// Actual implementations of Context methods follow

	public NamingEnumeration list(String root) throws NamingException {
		logger.info("Listing name/class pairs under [" + root + "]");
		return new NameClassPairEnumeration(this, root);
	}

	public NamingEnumeration listBindings(String root) throws NamingException {
		logger.info("Listing bindings under [" + root + "]");
		return new BindingEnumeration(this, root);
	}

	/**
	 * Look up the object with the given name.
	 * Note: Not intended for direct use by applications.
	 * Will be used by any standard InitialContext JNDI lookups.
	 * @throws NameNotFoundException if the object could not be found
	 */
	public Object lookup(String pname) throws NameNotFoundException {
		String name = root + pname;
		logger.info("Static JNDI lookup: [" + name + "]");
		if ("".equals(name)) {
			return new SimpleNamingContext(root, boundObjects, environment);
		}
		Object found = boundObjects.get(name);
		if (found == null) {
			if (!name.endsWith("/")) {
				name = name + "/";
			}
			for (Iterator it = boundObjects.keySet().iterator(); it.hasNext();) {
				String boundName = (String) it.next();
				if (boundName.startsWith(name)) {
					return new SimpleNamingContext(name, boundObjects, environment);
				}
			}
			throw new NameNotFoundException("Name [" + root + pname + "] not bound: " + boundObjects.size() + " bindings -- [" +
			                                StringUtils.collectionToDelimitedString(boundObjects.keySet(), ",") + "]");
		}
		return found;
	}

	public Object lookupLink(String name) throws NameNotFoundException {
		return lookup(name);
	}

	/**
	 * Bind the given object to the given name.
	 * Note: Not intended for direct use by applications
	 * if setting up a JVM-level JNDI environment.
	 * Use SimpleNamingContextBuilder to set up JNDI bindings then.
	 * @see SimpleNamingContextBuilder#bind
	 */
	public void bind(String name, Object obj) {
		logger.info("Static JNDI binding: [" + root + name + "] = [" + obj + "]");
		boundObjects.put(root + name, obj);
	}

	public void unbind(String name) {
		logger.info("Static JNDI remove: [" + root + name + "]");
		boundObjects.remove(root + name);
	}

	public void rebind(String name, Object obj) {
		bind(name, obj);
	}

	public void rename(String oldName, String newName) throws NameNotFoundException {
		Object obj = lookup(oldName);
		unbind(oldName);
		bind(newName, obj);
	}

	public Context createSubcontext(String name) {
		Context subcontext = new SimpleNamingContext(root + name, boundObjects, environment);
		bind(name, subcontext);
		return subcontext;
	}

	public void destroySubcontext(String name) {
		unbind(name);
	}

	public Hashtable getEnvironment() {
		return environment;
	}

	public Object addToEnvironment(String propName, Object propVal) {
		return environment.put(propName, propVal);
	}

	public Object removeFromEnvironment(String propName) {
		return environment.remove(propName);
	}

	public void close() {
	}


	// Unsupported methods follow: no support for Name

	public NamingEnumeration list(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration listBindings(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public Object lookup(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public Object lookupLink(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public void bind(Name arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	public void unbind(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public void rebind(Name arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	public void rename(Name arg0, Name arg1) {
		throw new UnsupportedOperationException();
	}

	public Context createSubcontext(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public void destroySubcontext(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public String getNameInNamespace() {
		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(Name arg0) {
		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Name composeName(Name arg0, Name arg1) {
		throw new UnsupportedOperationException();
	}

	public String composeName(String arg0, String arg1) {
		throw new UnsupportedOperationException();
	}


	private static abstract class AbstractNamingEnumeration implements NamingEnumeration {

		private Iterator iterator;

		private AbstractNamingEnumeration(SimpleNamingContext context, String proot) throws NamingException {
			if (!"".equals(proot) && !proot.endsWith("/")) {
				proot = proot + "/";
			}
			String root = context.root + proot;
			Map contents = new HashMap();
			Iterator it = context.boundObjects.keySet().iterator();
			while (it.hasNext()) {
				String boundName = (String) it.next();
				if (boundName.startsWith(root)) {
					int startIndex = root.length();
					int endIndex = boundName.indexOf('/', startIndex);
					String strippedName = (endIndex != -1 ? boundName.substring(startIndex, endIndex) : boundName.substring(startIndex));
					if (!contents.containsKey(strippedName)) {
						try {
							contents.put(strippedName, createObject(strippedName, context.lookup(proot + strippedName)));
						}
						catch (NameNotFoundException ex) {
							// cannot happen
						}
					}
				}
			}
			if (contents.size() == 0) {
				throw new NamingException("Invalid root: [" + context.root + proot + "]");
			}
			this.iterator = contents.values().iterator();
		}

		protected abstract Object createObject(String strippedName, Object obj);

		public boolean hasMore() {
			return iterator.hasNext();
		}

		public Object next() {
			return iterator.next();
		}

		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		public Object nextElement() {
			return iterator.next();
		}

		public void close() {
		}
	}


	private static class NameClassPairEnumeration extends AbstractNamingEnumeration {

		private NameClassPairEnumeration(SimpleNamingContext context, String root) throws NamingException {
			super(context, root);
		}

		protected Object createObject(String strippedName, Object obj) {
			return new NameClassPair(strippedName, obj.getClass().getName());
		}
	}


	private static class BindingEnumeration extends AbstractNamingEnumeration {

		private BindingEnumeration(SimpleNamingContext context, String root) throws NamingException {
			super(context, root);
		}

		protected Object createObject(String strippedName, Object obj) {
			return new Binding(strippedName, obj);
		}
	}

}
