
package com.interface21.beans.propertyeditors;

import java.beans.PropertyEditor;
import java.util.Locale;
import java.util.Properties;

import junit.framework.TestCase;

import com.interface21.beans.TestBean;

/**
 * Test the conversion of Strings to java.util.Properties objects,
 * and other property editors.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @version $Id$
 */
public class PropertyEditorTestSuite extends TestCase {

	public void testOneProperty() {
		String s = "foo=bar";
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText(s);
		Properties p= (Properties) pe.getValue();
		assertTrue("contains one entry", p.entrySet().size() == 1);
		assertTrue("foo=bar", p.get("foo").equals("bar"));
	}
	
	public void testTwoProperties() {
		String s = "foo=bar with whitespace\n" +
			"me=mi";
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText(s);
		Properties p= (Properties) pe.getValue();
		assertTrue("contains two entries", p.entrySet().size() == 2);
		assertTrue("foo=bar with whitespace", p.get("foo").equals("bar with whitespace"));
		assertTrue("me=mi", p.get("me").equals("mi"));
	}
	
	public void testHandlesEqualsInValue() {
		String s = "foo=bar\n" +
			"me=mi\n" +
			"x=y=z";
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText(s);
		Properties p= (Properties) pe.getValue();
		assertTrue("contains two entries", p.entrySet().size() == 3);
		assertTrue("foo=bar", p.get("foo").equals("bar"));
		assertTrue("me=mi", p.get("me").equals("mi"));
		assertTrue("x='y=z'", p.get("x").equals("y=z"));
	}
	
	public void testHandlesEmptyProperty() {
		String s = "foo=bar\nme=mi\nx=";
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText(s);
		Properties p= (Properties) pe.getValue();
		assertTrue("contains two entries", p.entrySet().size() == 3);
		assertTrue("foo=bar", p.get("foo").equals("bar"));
		assertTrue("me=mi", p.get("me").equals("mi"));
		assertTrue("x='y=z'", p.get("x").equals(""));
	}
	
	public void testHandlesEmptyPropertyWithoutEquals() {
		String s = "foo\nme=mi\nx=x";
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText(s);
		Properties p= (Properties) pe.getValue();
		assertTrue("contains three entries", p.entrySet().size() == 3);
		assertTrue("foo is empty", p.get("foo").equals(""));
		assertTrue("me=mi", p.get("me").equals("mi"));
	}
	
	/**
	 * Comments begin with #
	 *
	 */
	public void testIgnoresCommentLinesAndEmptyLines() {
		String s = "#Ignore this comment\n" +
			"foo=bar\n" +
			"#Another=comment more junk \n" +
			"me=mi\n" +
			"x=x\n" +
			"\n";
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText(s);
		Properties p= (Properties) pe.getValue();
		assertTrue("contains three entries", p.entrySet().size() == 3);
		assertTrue("foo is bar", p.get("foo").equals("bar"));
		assertTrue("me=mi", p.get("me").equals("mi"));
	}
	
	
	/**
	 * We'll typically align by indenting with tabs or
	 * spaces. These should be ignored if at the beginning of a line.
	 * We must ensure that comment lines beginning with whitespace
	 * are still ignored: the standard syntax doesn't allow this.
	 */
	public void testIgnoresLeadingSpacesAndTabs() {
		String s = "    #Ignore this comment\n" +
			"\t\tfoo=bar\n" +
			"\t#Another comment more junk \n" +
			" me=mi\n" +
			"x=x\n" +
			"\n";
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText(s);
		Properties p= (Properties) pe.getValue();
		//p.list(System.out);
		assertTrue("contains 3 entries, not " + p.size(), p.size() == 3);
		assertTrue("foo is bar", p.get("foo").equals("bar"));
		assertTrue("me=mi", p.get("me").equals("mi"));
	}
	
	public void testNull() {
		PropertiesEditor pe= new PropertiesEditor();
		try {
			pe.setAsText(null);
			fail("Should reject null");
		}
		catch (IllegalArgumentException ex) {
			// OK
		}
	}
	
	public void testEmptyString() {
		PropertiesEditor pe= new PropertiesEditor();
		pe.setAsText("");
		Properties p= (Properties) pe.getValue();
		assertTrue("empty string means empty properties", p.isEmpty());
	}

	public void testClassEditor() {
		PropertyEditor classEditor = new ClassEditor();
		classEditor.setAsText("com.interface21.beans.TestBean");
		assertEquals(TestBean.class, classEditor.getValue());
		assertEquals("com.interface21.beans.TestBean", classEditor.getAsText());
	}

	public void testLocaleEditor() {
		PropertyEditor localeEditor = new LocaleEditor();
		localeEditor.setAsText("en_CA");
		assertEquals(Locale.CANADA, localeEditor.getValue());
		assertEquals("en_CA", localeEditor.getAsText());
	}

}
