
package com.interface21.jndi;

import junit.framework.TestCase;


public class JndiTemplateEditorTests extends TestCase {

	
	public JndiTemplateEditorTests(String arg0) {
		super(arg0);
	}
	
	public void testNullIsIllegalArgument() {
		try {
			new JndiTemplateEditor().setAsText(null);
			fail("Null is illegal");
		}
		catch (IllegalArgumentException ex) {
			// OK
		}
	}
	
	/**
	 * There's a property string, but it's format is bogus.
	 *
	 */
//	public void testBogusFormat() {
//		try {
//			new JndiTemplateEditor().setAsText("=erever=====");
//			fail("Bogus properties");
//		}
//		catch (IllegalArgumentException ex) {
//			// OK
//		}
//	}
	
	public void testEmptyStringMeansNullEnvironment() {
		JndiTemplateEditor je = new JndiTemplateEditor();
		je.setAsText("");
		JndiTemplate jt = (JndiTemplate) je.getValue();
		assertTrue(jt.getEnvironment() == null);
	}
	
	public void testCustomEnvironment() {
		JndiTemplateEditor je = new JndiTemplateEditor();
		// These properties are meaningless for JNDI, but we don't worry about that:
		// the underlying JNDI implementation will throw exceptions when the user tries
		// to look anything up
		je.setAsText("jndiInitialSomethingOrOther=com.interface21.myjndi.CompleteRubbish\nfoo=bar");
		JndiTemplate jt = (JndiTemplate) je.getValue();
		assertTrue(jt.getEnvironment().size() == 2);
		assertTrue(jt.getEnvironment().getProperty("jndiInitialSomethingOrOther").equals("com.interface21.myjndi.CompleteRubbish"));
		assertTrue(jt.getEnvironment().getProperty("foo").equals("bar"));
	}

}
