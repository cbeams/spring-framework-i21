package com.interface21.jndi;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;

import junit.framework.TestCase;

import com.interface21.jdbc.datasource.DriverManagerDataSource;
import com.interface21.jndi.support.SimpleNamingContextBuilder;
import com.interface21.jndi.support.SimpleNamingContext;

/**
 * @author Juergen Hoeller
 */
public class SimpleNamingContextTests extends TestCase {

	public SimpleNamingContextTests(String s) {
		super(s);
	}

	public void testNamingContextBuilder() throws NamingException {
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		InitialContextFactory factory = builder.createInitialContextFactory(null);

		DataSource ds = new DriverManagerDataSource();
		builder.bind("java:comp/env/jdbc/myds", ds);
		Object obj = new Object();
		builder.bind("myobject", obj);

		Context context1 = factory.getInitialContext(null);
		assertTrue("Correct DataSource registered", context1.lookup("java:comp/env/jdbc/myds") == ds);
		assertTrue("Correct Object registered", context1.lookup("myobject") == obj);

		Hashtable env2 = new Hashtable();
		env2.put("key1", "value1");
		Context context2 = factory.getInitialContext(env2);
		assertTrue("Correct DataSource registered", context2.lookup("java:comp/env/jdbc/myds") == ds);
		assertTrue("Correct Object registered", context2.lookup("myobject") == obj);
		assertTrue("Correct environment", context2.getEnvironment() != env2);
		assertTrue("Correct key1", "value1".equals(context2.getEnvironment().get("key1")));

		Integer i = new Integer(0);
		context1.rebind("myinteger", i);
		String s = "";
		context2.bind("mystring", s);

		Context context3 = (Context) context2.lookup("");
		context3.rename("java:comp/env/jdbc/myds", "jdbc/myds");
		context3.unbind("myobject");

		assertTrue("Correct environment", context3.getEnvironment() != context2.getEnvironment());
		context3.addToEnvironment("key2", "value2");
		assertTrue("key2 added", "value2".equals(context3.getEnvironment().get("key2")));
		context3.removeFromEnvironment("key1");
		assertTrue("key1 removed", context3.getEnvironment().get("key1") == null);

		assertTrue("Correct DataSource registered", context1.lookup("jdbc/myds") == ds);
		try {
			context1.lookup("myobject");
			fail("Should have thrown NameNotFoundException");
		}
		catch (NameNotFoundException ex) {
			// expected
		}
		assertTrue("Correct Integer registered", context1.lookup("myinteger") == i);
		assertTrue("Correct String registered", context1.lookup("mystring") == s);

		assertTrue("Correct DataSource registered", context2.lookup("jdbc/myds") == ds);
		try {
			context2.lookup("myobject");
			fail("Should have thrown NameNotFoundException");
		}
		catch (NameNotFoundException ex) {
			// expected
		}
		assertTrue("Correct Integer registered", context2.lookup("myinteger") == i);
		assertTrue("Correct String registered", context2.lookup("mystring") == s);

		assertTrue("Correct DataSource registered", context3.lookup("jdbc/myds") == ds);
		try {
			context3.lookup("myobject");
			fail("Should have thrown NameNotFoundException");
		}
		catch (NameNotFoundException ex) {
			// expected
		}
		assertTrue("Correct Integer registered", context3.lookup("myinteger") == i);
		assertTrue("Correct String registered", context3.lookup("mystring") == s);

		Map bindingMap = new HashMap();
		NamingEnumeration bindingEnum = context3.listBindings("");
		while (bindingEnum.hasMoreElements()) {
			Binding binding = (Binding) bindingEnum.nextElement();
			bindingMap.put(binding.getName(), binding);
		}
		assertTrue("Correct jdbc subcontext", ((Binding) bindingMap.get("jdbc")).getObject() instanceof Context);
		assertTrue("Correct jdbc subcontext", SimpleNamingContext.class.getName().equals(((Binding) bindingMap.get("jdbc")).getClassName()));

		Context jdbcContext = (Context) context3.lookup("jdbc");
		jdbcContext.bind("mydsX", ds);
		Map subBindingMap = new HashMap();
		NamingEnumeration subBindingEnum = jdbcContext.listBindings("");
		while (subBindingEnum.hasMoreElements()) {
			Binding binding = (Binding) subBindingEnum.nextElement();
			subBindingMap.put(binding.getName(), binding);
		}

		assertTrue("Correct DataSource registered", ds.equals(((Binding) subBindingMap.get("myds")).getObject()));
		assertTrue("Correct DataSource registered", DriverManagerDataSource.class.getName().equals(((Binding) subBindingMap.get("myds")).getClassName()));
		assertTrue("Correct DataSource registered", ds.equals(((Binding) subBindingMap.get("mydsX")).getObject()));
		assertTrue("Correct DataSource registered", DriverManagerDataSource.class.getName().equals(((Binding) subBindingMap.get("mydsX")).getClassName()));
		assertTrue("Correct Integer registered", i.equals(((Binding) bindingMap.get("myinteger")).getObject()));
		assertTrue("Correct Integer registered", Integer.class.getName().equals(((Binding) bindingMap.get("myinteger")).getClassName()));
		assertTrue("Correct String registered", s.equals(((Binding) bindingMap.get("mystring")).getObject()));
		assertTrue("Correct String registered", String.class.getName().equals(((Binding) bindingMap.get("mystring")).getClassName()));

		context1.bind("jdbc/sub/subds", ds);

		Map pairMap = new HashMap();
		NamingEnumeration pairEnum = context2.list("jdbc");
		while (pairEnum.hasMore()) {
			NameClassPair pair = (NameClassPair) pairEnum.next();
			pairMap.put(pair.getName(), pair.getClassName());
		}
		assertTrue("Correct sub subcontext", SimpleNamingContext.class.getName().equals(pairMap.get("sub")));

		Context subContext = (Context) context2.lookup("jdbc/sub");
		Map subPairMap = new HashMap();
		NamingEnumeration subPairEnum = subContext.list("");
		while (subPairEnum.hasMoreElements()) {
			NameClassPair pair = (NameClassPair) subPairEnum.next();
			subPairMap.put(pair.getName(), pair.getClassName());
		}

		assertTrue("Correct DataSource registered", DriverManagerDataSource.class.getName().equals(subPairMap.get("subds")));
		assertTrue("Correct DataSource registered", DriverManagerDataSource.class.getName().equals(pairMap.get("myds")));
		assertTrue("Correct DataSource registered", DriverManagerDataSource.class.getName().equals(pairMap.get("mydsX")));

		pairMap.clear();
		pairEnum = context1.list("jdbc/");
		while (pairEnum.hasMore()) {
			NameClassPair pair = (NameClassPair) pairEnum.next();
			pairMap.put(pair.getName(), pair.getClassName());
		}
		assertTrue("Correct DataSource registered", DriverManagerDataSource.class.getName().equals(pairMap.get("myds")));
		assertTrue("Correct DataSource registered", DriverManagerDataSource.class.getName().equals(pairMap.get("mydsX")));
	}
	
	/**
	 * Demonstrates how emptyActivatedContextBuilder() method can be
	 * used repeatedly, and how it affects creating a new InitialContext()
	 * @throws Exception
	 */
	public void testCreateInitialContext() throws Exception {
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		String name = "foo";
		Object o = new Object();
		builder.bind(name, o);
		// Check it affects JNDI
		Context ctx = new InitialContext();
		assertTrue(ctx.lookup(name) == o);
		// Check it returns mutable contexts
		ctx.unbind(name);
		try {
			ctx = new InitialContext();
			ctx.lookup(name);
			fail();
		}
		catch (NamingException ex) {
			// Ok
		}
		
		// Check the same call will work again, but the context is empty
		builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		try {
			ctx = new InitialContext();
			ctx.lookup(name);
			fail();
		}
		catch (NamingException ex) {
			// Ok
		}
		Object o2 = new Object();
		builder.bind(name, o2);
		assertEquals(ctx.lookup(name), o2);
	}

}
