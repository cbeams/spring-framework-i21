package com.interface21.beans;

import java.beans.PropertyEditorSupport;
import java.beans.PropertyVetoException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import com.interface21.beans.propertyeditors.CustomBooleanEditor;
import com.interface21.beans.propertyeditors.CustomNumberEditor;
import com.interface21.beans.propertyeditors.StringTrimmerEditor;

/**
 * @author Juergen Hoeller
 * @since 10.06.2003
 */
public class CustomEditorTestSuite extends TestCase {

	public void testComplexObject() {
		TestBean t = new TestBean();
		String newName = "Rod";
		String tbString = "Kerry_34";
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			bw.registerCustomEditor(ITestBean.class, null, new TestBeanEditor());
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("age", new Integer(55)));
			pvs.addPropertyValue(new PropertyValue("name", newName));
			pvs.addPropertyValue(new PropertyValue("touchy", "valid"));
			pvs.addPropertyValue(new PropertyValue("spouse", tbString));
			bw.setPropertyValues(pvs);
			assertTrue("spouse is non-null", t.getSpouse() != null);
			assertTrue("spouse name is Kerry and age is 34", t.getSpouse().getName().equals("Kerry") && t.getSpouse().getAge() == 34);
			//assertTrue("Event source is correct", l.getEventCount() == 3);
		}
		catch (BeansException ex) {
			fail("Shouldn't throw exception when everything is valid: " + ex.getMessage());
		}
	}

	public void testCustomEditorForSingleProperty() {
		TestBean tb = new TestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(String.class, "name", new PropertyEditorSupport() {
			public void setAsText(String text) throws IllegalArgumentException {
				setValue("prefix" + text);
			}
		});
		try {
			bw.setPropertyValue("name", "value");
			bw.setPropertyValue("touchy", "value");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertEquals("prefixvalue", bw.getPropertyValue("name"));
		assertEquals("prefixvalue", tb.getName());
		assertEquals("value", bw.getPropertyValue("touchy"));
		assertEquals("value", tb.getTouchy());
	}

	public void testCustomEditorForAllStringProperties() {
		TestBean tb = new TestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(String.class, null, new PropertyEditorSupport() {
			public void setAsText(String text) throws IllegalArgumentException {
				setValue("prefix" + text);
			}
		});
		try {
			bw.setPropertyValue("name", "value");
			bw.setPropertyValue("touchy", "value");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertEquals("prefixvalue", bw.getPropertyValue("name"));
		assertEquals("prefixvalue", tb.getName());
		assertEquals("prefixvalue", bw.getPropertyValue("touchy"));
		assertEquals("prefixvalue", tb.getTouchy());
	}

	public void testCustomEditorForSingleNestedProperty() {
		TestBean tb = new TestBean();
		tb.setSpouse(new TestBean());
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(String.class, "spouse.name", new PropertyEditorSupport() {
			public void setAsText(String text) throws IllegalArgumentException {
				setValue("prefix" + text);
			}
		});
		try {
			bw.setPropertyValue("spouse.name", "value");
			bw.setPropertyValue("touchy", "value");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertEquals("prefixvalue", bw.getPropertyValue("spouse.name"));
		assertEquals("prefixvalue", tb.getSpouse().getName());
		assertEquals("value", bw.getPropertyValue("touchy"));
		assertEquals("value", tb.getTouchy());
	}

	public void testCustomEditorForAllNestedStringProperties() {
		TestBean tb = new TestBean();
		tb.setSpouse(new TestBean());
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(String.class, null, new PropertyEditorSupport() {
			public void setAsText(String text) throws IllegalArgumentException {
				setValue("prefix" + text);
			}
		});
		try {
			bw.setPropertyValue("spouse.name", "value");
			bw.setPropertyValue("touchy", "value");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertEquals("prefixvalue", bw.getPropertyValue("spouse.name"));
		assertEquals("prefixvalue", tb.getSpouse().getName());
		assertEquals("prefixvalue", bw.getPropertyValue("touchy"));
		assertEquals("prefixvalue", tb.getTouchy());
	}

	public void testBooleanPrimitiveEditor() {
		BooleanTestBean tb = new BooleanTestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);

		try {
			bw.setPropertyValue("bool1", "true");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct bool1 value", Boolean.TRUE.equals(bw.getPropertyValue("bool1")));
		assertTrue("Correct bool1 value", tb.isBool1());

		try {
			bw.setPropertyValue("bool1", "false");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct bool1 value", Boolean.FALSE.equals(bw.getPropertyValue("bool1")));
		assertTrue("Correct bool1 value", !tb.isBool1());

		try {
			bw.setPropertyValue("bool1", "argh");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			// expected
			return;
		}
		fail("Should have thrown BeansException");
	}

	public void testBooleanObjectEditorWithAllowEmpty() {
		BooleanTestBean tb = new BooleanTestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(Boolean.class, null, new CustomBooleanEditor(true));

		try {
			bw.setPropertyValue("bool2", "true");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct bool2 value", Boolean.TRUE.equals(bw.getPropertyValue("bool2")));
		assertTrue("Correct bool2 value", tb.getBool2().booleanValue());

		try {
			bw.setPropertyValue("bool2", "false");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct bool2 value", Boolean.FALSE.equals(bw.getPropertyValue("bool2")));
		assertTrue("Correct bool2 value", !tb.getBool2().booleanValue());

		try {
			bw.setPropertyValue("bool2", "");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct bool2 value", bw.getPropertyValue("bool2") == null);
		assertTrue("Correct bool2 value", tb.getBool2() == null);
	}

	public void testBooleanObjectEditorWithoutAllowEmpty() {
		BooleanTestBean tb = new BooleanTestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(Boolean.class, null, new CustomBooleanEditor(false));

		try {
			bw.setPropertyValue("bool2", "true");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct bool2 value", Boolean.TRUE.equals(bw.getPropertyValue("bool2")));
		assertTrue("Correct bool2 value", tb.getBool2().booleanValue());

		try {
			bw.setPropertyValue("bool2", "false");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct bool2 value", Boolean.FALSE.equals(bw.getPropertyValue("bool2")));
		assertTrue("Correct bool2 value", !tb.getBool2().booleanValue());

		try {
			bw.setPropertyValue("bool2", "");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			// expected
			assertTrue("Correct bool2 value", bw.getPropertyValue("bool2") != null);
			assertTrue("Correct bool2 value", tb.getBool2() != null);
			return;
		}
		fail("Should have throw BeansException");
	}

	public void testNumberEditorWithoutAllowEmpty() {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
		NumberTestBean tb = new NumberTestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(long.class, null, new CustomNumberEditor(Long.class, nf, false));
		bw.registerCustomEditor(Long.class, null, new CustomNumberEditor(Long.class, nf, false));
		bw.registerCustomEditor(int.class, null, new CustomNumberEditor(Integer.class, nf, false));
		bw.registerCustomEditor(Integer.class, null, new CustomNumberEditor(Integer.class, nf, false));
		bw.registerCustomEditor(double.class, null, new CustomNumberEditor(Double.class, nf, false));
		bw.registerCustomEditor(Double.class, null, new CustomNumberEditor(Double.class, nf, false));
		bw.registerCustomEditor(float.class, null, new CustomNumberEditor(Float.class, nf, false));
		bw.registerCustomEditor(Float.class, null, new CustomNumberEditor(Float.class, nf, false));

		try {
			bw.setPropertyValue("long1", "5");
			bw.setPropertyValue("long2", "6");
			bw.setPropertyValue("int1", "7");
			bw.setPropertyValue("int2", "8");
			bw.setPropertyValue("double1", "5,1");
			bw.setPropertyValue("double2", "6,1");
			bw.setPropertyValue("float1", "7,1");
			bw.setPropertyValue("float2", "8,1");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}

		assertTrue("Correct long1 value", new Long("5").equals(bw.getPropertyValue("long1")));
		assertTrue("Correct long1 value", tb.getLong1() == 5);
		assertTrue("Correct long2 value", new Long("6").equals(bw.getPropertyValue("long2")));
		assertTrue("Correct long2 value", new Long("6").equals(tb.getLong2()));
		assertTrue("Correct int1 value", new Integer("7").equals(bw.getPropertyValue("int1")));
		assertTrue("Correct int1 value", tb.getInt1() == 7);
		assertTrue("Correct int2 value", new Integer("8").equals(bw.getPropertyValue("int2")));
		assertTrue("Correct int2 value", new Integer("8").equals(tb.getInt2()));
		assertTrue("Correct double1 value", new Double("5.1").equals(bw.getPropertyValue("double1")));
		assertTrue("Correct double1 value", tb.getDouble1() == 5.1);
		assertTrue("Correct double2 value", new Double("6.1").equals(bw.getPropertyValue("double2")));
		assertTrue("Correct double2 value", new Double("6.1").equals(tb.getDouble2()));
		assertTrue("Correct float1 value", new Float("7.1").equals(bw.getPropertyValue("float1")));
		assertTrue("Correct float1 value", new Float("7.1").equals(new Float(tb.getFloat1())));
		assertTrue("Correct float2 value", new Float("8.1").equals(bw.getPropertyValue("float2")));
		assertTrue("Correct float2 value", new Float("8.1").equals(tb.getFloat2()));
	}

	public void testNumberEditorsWithAllowEmpty() {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
		NumberTestBean tb = new NumberTestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		bw.registerCustomEditor(long.class, null, new CustomNumberEditor(Long.class, nf, true));
		bw.registerCustomEditor(Long.class, null, new CustomNumberEditor(Long.class, nf, true));

		try {
			bw.setPropertyValue("long1", "5");
			bw.setPropertyValue("long2", "6");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct long1 value", new Long("5").equals(bw.getPropertyValue("long1")));
		assertTrue("Correct long1 value", tb.getLong1() == 5);
		assertTrue("Correct long2 value", new Long("6").equals(bw.getPropertyValue("long2")));
		assertTrue("Correct long2 value", new Long("6").equals(tb.getLong2()));

		try {
			bw.setPropertyValue("long2", "");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			fail("Should not throw BeansException: " + ex.getMessage());
		}
		assertTrue("Correct long2 value", bw.getPropertyValue("long2") == null);
		assertTrue("Correct long2 value", tb.getLong2() == null);

		try {
			bw.setPropertyValue("long1", "");
		}
		catch (PropertyVetoException ex) {
			fail("Should not throw PropertyVetoException: " + ex.getMessage());
		}
		catch (BeansException ex) {
			// expected
			assertTrue("Correct long1 value", new Long("5").equals(bw.getPropertyValue("long1")));
			assertTrue("Correct long1 value", tb.getLong1() == 5);
			return;
		}
		fail("Should have thrown BeansException");
	}

	public void testStringTrimmerEditor() {
		StringTrimmerEditor editor = new StringTrimmerEditor(false);
		editor.setAsText("test");
		assertEquals("test", editor.getValue());
		editor.setAsText(" test ");
		assertEquals("test", editor.getValue());
		editor.setAsText("");
		assertEquals("", editor.getValue());
	}

	public void testStringTrimmerEditorWithEmptyAsNull() {
		StringTrimmerEditor editor = new StringTrimmerEditor(true);
		editor.setAsText("test");
		assertEquals("test", editor.getValue());
		editor.setAsText(" test ");
		assertEquals("test", editor.getValue());
		editor.setAsText("");
		assertEquals(null, editor.getValue());
	}


	private static class TestBeanEditor extends PropertyEditorSupport {

		public void setAsText(String text) {
			TestBean tb = new TestBean();
			StringTokenizer st = new StringTokenizer(text, "_");
			tb.setName(st.nextToken());
			tb.setAge(Integer.parseInt(st.nextToken()));
			setValue(tb);
		}
	}

}
