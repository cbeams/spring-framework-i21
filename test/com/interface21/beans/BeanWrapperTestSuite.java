
package com.interface21.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Properties;
import java.util.StringTokenizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 *
 * @author  rod
 * @version
 */
public class BeanWrapperTestSuite extends TestCase {

	public BeanWrapperTestSuite(String name) {
		super(name);
	}

	public void testSetWrappedInstanceOfSameClass()throws Exception {
		TestBean tb = new TestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		tb.setAge(11);
		
		TestBean tb2 = new TestBean();
		bw.setWrappedInstance(tb2);
		
		bw.setPropertyValue("age", new Integer(14));
		assertTrue("2nd changed", tb2.getAge()==14);
		assertTrue("1 didn't change", tb.getAge() == 11);
	}

	public void testSetWrappedInstanceOfDifferentClass()throws Exception {
		ThrowsException tex = new ThrowsException();
		BeanWrapper bw = new BeanWrapperImpl(tex);

		TestBean tb2 = new TestBean();
		bw.setWrappedInstance(tb2);

		bw.setPropertyValue("age", new Integer(14));
		assertTrue("2nd changed", tb2.getAge()==14);
	}

	public void testNewInstanceIsIndependent() {
		TestBean t = new TestBean();
		int age = 50;
		String name = "Tony";
		t.setAge(age);
		t.setName(name);
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			assertTrue("age is OK", t.getAge() == age);
			assertTrue("name is OK", name.equals(t.getName()));
			bw.newWrappedInstance();
			TestBean t2 = (TestBean) bw.getWrappedInstance();
			assertTrue("Not ==", t2 != t);
			assertTrue("Not equals", !t2.equals(t));
			assertTrue("t2 has defaults", t2.getAge() == 0 && t2.getName() == null);
		}
		catch (BeansException ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
	}
	
	public void testGetterThrowsException() {
		GetterBean gb = new GetterBean();
		BeanWrapper bw = new BeanWrapperImpl(gb);
		bw.setEventPropagationEnabled(true);
		try {
			bw.setPropertyValue("name","tom");
			assertTrue("Set name to tom", gb.getName().equals("tom"));
		}
		catch (PropertyVetoException ex) {
			fail("Shouldn't throw PropertyVetoException, even if getter threw an exception veto");
		}
	}
	
	public void testEmptyPropertyValuesSet() {
		TestBean t = new TestBean();
		int age = 50;
		String name = "Tony";
		t.setAge(age);
		t.setName(name);
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			assertTrue("age is OK", t.getAge() == age);
			assertTrue("name is OK", name.equals(t.getName()));
			bw.setPropertyValues(new MutablePropertyValues());
			// Check its unchanged
			assertTrue("age is OK", t.getAge() == age);
			assertTrue("name is OK", name.equals(t.getName()));
		}
		catch (BeansException ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
	}

	public void testAllValid() {
		TestBean t = new TestBean();
		String newName = "tony";
		int newAge = 65;
		String newTouchy = "valid";
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			//System.out.println(bw);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("age", new Integer(newAge)));
			pvs.addPropertyValue(new PropertyValue("name", newName));
			pvs.addPropertyValue(new PropertyValue("touchy", newTouchy));
			bw.setPropertyValues(pvs);
			assertTrue("Validly set property must stick", t.getName().equals(newName));
			assertTrue("Validly set property must stick", t.getTouchy().equals(newTouchy));
			assertTrue("Validly set property must stick", t.getAge() == newAge);
		}
		catch (BeansException ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
	}

	public void testBeanWrapperUpdates() {
		TestBean t = new TestBean();
		int newAge = 33;
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			t.setAge(newAge);
			Object bwAge = bw.getPropertyValue("age");
			assertTrue("Age is an integer", bwAge instanceof Integer);
			int bwi = ((Integer) bwAge).intValue();
			assertTrue("Bean wrapper must pick up changes", bwi == newAge);
		}
		catch (Exception ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
	}

	public void testValidNullUpdate() {
		TestBean t = new TestBean();
		t.setName("Frank");	// we need to change it back
		t.setSpouse(t);
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			assertTrue("name is not null to start off", t.getName() != null);
			bw.setPropertyValue("name", null);
			assertTrue("name is now null", t.getName() == null);
			// Now test with non-string
			assertTrue("spouse is not null to start off", t.getSpouse() != null);
			bw.setPropertyValue("spouse", null);
			assertTrue("spouse is now null", t.getSpouse() == null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Shouldn't throw exception when everything is valid");
		}
	}
	
	
	
	
	public static class PropsTest {
		public Properties props;
		public String name;
		public String[] sa;
		
		public void setProperties(Properties p) {
			props = p;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setStringArray(String[] sa) {
			this.sa = sa;
		}
	}
	
	
	/**
	 * Test default conversion of properties
	 */
	public void testPropertiesProperty() throws Exception {
		PropsTest pt = new PropsTest();
		BeanWrapper bw = new BeanWrapperImpl(pt);
		bw.setPropertyValue("name", "ptest");
		
		// Note format...
		String ps = "peace=war\nfreedom=slavery";
		bw.setPropertyValue("properties", ps);
		
		assertTrue("name was set", pt.name.equals("ptest"));
		assertTrue("props non null", pt.props != null);
		String freedomVal = pt.props.getProperty("freedom");
		String peaceVal = pt.props.getProperty("peace");
		assertTrue("peace==war", peaceVal.equals("war"));
		assertTrue("Freedom==slavery", freedomVal.equals("slavery"));
	}
	
	public void testStringArrayProperty() throws Exception {
		PropsTest pt = new PropsTest();
		BeanWrapper bw = new BeanWrapperImpl(pt);
		bw.setPropertyValue("stringArray", "foo,fi,fi,fum");
		
		assertTrue("stringArray was set", pt.sa != null);
		assertTrue("stringArray length = 4", pt.sa.length == 4);
		assertTrue("correct values", pt.sa[0].equals("foo") && pt.sa[1].equals("fi") && pt.sa[2].equals("fi") && pt.sa[3].equals("fum"));
		
		bw.setPropertyValue("stringArray", "one");
		assertTrue("stringArray length = 1", pt.sa.length == 1);
		assertTrue("stringArray elt is ok", pt.sa[0].equals("one"));
	}
	
	

	public void testIndividualAllValid() {
		TestBean t = new TestBean();
		String newName = "tony";
		int newAge = 65;
		String newTouchy = "valid";
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			bw.setPropertyValue("age", new Integer(newAge));
			bw.setPropertyValue(new PropertyValue("name", newName));
			bw.setPropertyValue(new PropertyValue("touchy", newTouchy));
			assertTrue("Validly set property must stick", t.getName().equals(newName));
			assertTrue("Validly set property must stick", t.getTouchy().equals(newTouchy));
			assertTrue("Validly set property must stick", t.getAge() == newAge);
		}
		catch (BeansException ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
		catch (PropertyVetoException ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
	}

	public void test2Invalid() {
		TestBean t = new TestBean();
		String newName = "tony";
		String invalidTouchy = ".valid";
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			//System.out.println(bw);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("age", "foobar"));
			pvs.addPropertyValue(new PropertyValue("name", newName));
			pvs.addPropertyValue(new PropertyValue("touchy", invalidTouchy));
			bw.setPropertyValues(pvs);
			fail("Should throw exception when everything is valid");
		}
		catch (PropertyVetoExceptionsException ex) {
			assertTrue("Must contain 2 exceptions", ex.getExceptionCount() == 2);
			// Test validly set property matches
			assertTrue("Validly set property must stick", t.getName().equals(newName));
			assertTrue("Invalidly set property must retain old value", t.getAge() == 0);
			assertTrue("New value of dodgy setter must be available through exception",
				ex.getPropertyVetoException("touchy").getPropertyChangeEvent().getNewValue().equals(invalidTouchy));
		}
		catch (Exception ex) {
			fail("Shouldn't throw exception other than pvee");
		}
	}

	public void testTypeMismatch() {
		TestBean t = new TestBean();
		String newName = "tony";
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			//System.out.println(bw);
			bw.setPropertyValue("age", "foobar");
			fail("Should throw exception on type mismatch");
		}
		catch (TypeMismatchException ex) {
			//System.out.println("As expected: " + ex);
		}
		catch (Exception ex) {
			fail("Shouldn't throw exception other than Type mismatch");
		}
	}
	
	
	public void testSetPropertyValuesIgnoresInvalidNestedOnRequest() {
		ITestBean rod = new TestBean();
		
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("name", "rod"));
		pvs.addPropertyValue(new PropertyValue("graceful.rubbish", "tony"));
		pvs.addPropertyValue(new PropertyValue("more.garbage", new Object()));
		BeanWrapper bw = new BeanWrapperImpl(rod);
		bw.setPropertyValues(pvs, true, null);
		
		assertTrue("Set valid and ignored invalid", rod.getName().equals("rod"));
		
		try {
			// Don't ignore: should fail
			bw.setPropertyValues(pvs, false, null);
			fail("Shouldn't have ignored invalid updates");
		}
		catch (NotWritablePropertyException ex) {
			// OK: but which exception??
		}
	}
	
	public void testGetNestedProperty() {
		ITestBean rod = new TestBean("rod", 31);
		ITestBean kerry = new TestBean("kerry", 35);
		rod.setSpouse(kerry);
		kerry.setSpouse(rod);
		BeanWrapper bw = new BeanWrapperImpl(rod);
		Integer KA = (Integer) bw.getPropertyValue("spouse.age");
		assertTrue("kerry is 35", KA.intValue() == 35);
		Integer RA = (Integer) bw.getPropertyValue("spouse.spouse.age");
		assertTrue("rod is 31, not" + RA, RA.intValue() == 31);
		ITestBean spousesSpouse = (ITestBean) bw.getPropertyValue("spouse.spouse");
		assertTrue("spousesSpouse = initial point", rod == spousesSpouse);
	}
	
	
	
	public void testGetNestedPropertyNullValue() throws Exception {
		ITestBean rod = new TestBean("rod", 31);
		ITestBean kerry = new TestBean("kerry", 35);
		rod.setSpouse(kerry);
		
		BeanWrapper bw = new BeanWrapperImpl(rod);
		try {
			bw.getPropertyValue("spouse.spouse.age");
			fail("Shouldn't have succeded with null path");
		}
		catch (NullValueInNestedPathException ex) {
			// ok
			assertTrue("it was the spouse property htat was null, not " + ex.getPropertyName(), ex.getPropertyName().equals("spouse"));
		}
	}
	
	public void testSetNestedProperty() throws Exception {
		ITestBean rod = new TestBean("rod", 31);
		ITestBean kerry = new TestBean("kerry", 0);
		
		BeanWrapper bw = new BeanWrapperImpl(rod);
		bw.setPropertyValue("spouse", kerry);
		
		assertTrue("nested set worked", rod.getSpouse() == kerry);
		assertTrue("no back relation", kerry.getSpouse() == null);
		bw.setPropertyValue(new PropertyValue("spouse.spouse", rod));
		assertTrue("nested set worked", kerry.getSpouse() == rod);
		assertTrue("kerry age not set", kerry.getAge()==0);
		bw.setPropertyValue(new PropertyValue("spouse.age", new Integer(35)));
		assertTrue("Set primitive on spouse", kerry.getAge()==35);
	}
	
	public void testSetNestedPropertyNullValue() throws Exception {
		ITestBean rod = new TestBean("rod", 31);
		
		BeanWrapper bw = new BeanWrapperImpl(rod);
		try {
			bw.setPropertyValue("spouse.age", new Integer(31));
			fail("Shouldn't have succeded with null path");
		}
		catch (NullValueInNestedPathException ex) {
			// ok
			assertTrue("it was the spouse property that was null, not " + ex.getPropertyName(), ex.getPropertyName().equals("spouse"));
		}
	}
	
	
	public void testSetNestedPropertyPolymorphic() throws Exception {
		ITestBean rod = new TestBean("rod", 31);
		ITestBean kerry = new Employee();
		
		BeanWrapper bw = new BeanWrapperImpl(rod);
		bw.setPropertyValue("spouse", kerry);
		bw.setPropertyValue("spouse.age", new Integer(35));
		bw.setPropertyValue("spouse.name", "Kerry");
		bw.setPropertyValue("spouse.company", "Lewisham");
		assertTrue("kerry name is Kerry", kerry.getName().equals("Kerry"));
		
		assertTrue("nested set worked", rod.getSpouse() == kerry);
		assertTrue("no back relation", kerry.getSpouse() == null);
		bw.setPropertyValue(new PropertyValue("spouse.spouse", rod));
		assertTrue("nested set worked", kerry.getSpouse() == rod);
		
		BeanWrapper kbw = new BeanWrapperImpl(kerry);
		assertTrue("spouse.spouse.spouse.spouse.company=Lewisham", 
			"Lewisham".equals(kbw.getPropertyValue("spouse.spouse.spouse.spouse.company")));
	}
	
	

	public void testEventPropagation() {
		TestBean t = new TestBean();
		String newName = "tony";
		ConsoleListener l = new ConsoleListener();
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			bw.setEventPropagationEnabled(true);
			bw.addPropertyChangeListener(l);
			//System.out.println(bw);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("age", new Integer(55)));
			pvs.addPropertyValue(new PropertyValue("name", newName));
			pvs.addPropertyValue(new PropertyValue("touchy", "valid"));
			bw.setPropertyValues(pvs);
			assertTrue("Must have fired 3 events", l.getEventCount() == 3);
			//assertTrue("Event source is correct", l.getEventCount() == 3);
		}
		catch (Exception ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
	}

	public void testEventPropagationAndVeto() {
		TestBean t = new TestBean();
		String newName = "tony";
		ConsoleListener l = new ConsoleListener();
		AgistListener v = new AgistListener();
		try {
			BeanWrapper bw = new BeanWrapperImpl(t, true);
			bw.addPropertyChangeListener(l);
			bw.addVetoableChangeListener(v);
			//System.out.println(bw);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("age", new Integer(67)));
			pvs.addPropertyValue(new PropertyValue("name", newName));
			pvs.addPropertyValue(new PropertyValue("touchy", "valid"));
			bw.setPropertyValues(pvs);
			fail("Veto must have fired");
			//assertTrue("Event source is correct", l.getEventCount() == 3);
		}
		catch (PropertyVetoExceptionsException ex) {
			assertTrue("Must have fired 2 events", l.getEventCount() == 2);
			assertTrue("VetoableChangeListener must have seen 4 events, not " + v.getEventCount(), v.getEventCount() == 4);
			assertTrue("Error count = 1", ex.getExceptionCount() == 1);
		//	assertTrue(ex.getPropertyVetoExceptions()[0].getPropertyVetoException().
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Should throw PropertyVetoExceptionsException only on veto");
		}
	}

	public void testEventPropagationAndNonFiringVeto() {
		TestBean t = new TestBean();
		String newName = "tony";
		ConsoleListener l = new ConsoleListener();
		AgistListener v = new AgistListener();
		try {
			BeanWrapper bw = new BeanWrapperImpl(t, true);
			bw.addPropertyChangeListener(l);
			bw.addVetoableChangeListener(v);
			//System.out.println(bw);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("age", new Integer(55)));
			pvs.addPropertyValue(new PropertyValue("name", newName));
			pvs.addPropertyValue(new PropertyValue("touchy", "valid"));
			bw.setPropertyValues(pvs);
			assertTrue("Must have fired 3 events", l.getEventCount() == 3);
			assertTrue("VetoableChangeListener must have seen 3 events, not " + v.getEventCount(), v.getEventCount() == 3);
			//assertTrue("Event source is correct", l.getEventCount() == 3);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Shouldn't veto without good cause");
		}
	}
	
	
	/**
	 * Tests we don't lose existing listeners but can't add new ones
	 * or remove during disabled period
	 */
	public void testEventPropagationDisableBehavior() throws Exception {
		TestBean t = new TestBean();
		String newName = "tony";
		ConsoleListener l = new ConsoleListener();
		
		BeanWrapper bw = new BeanWrapperImpl(t, true);
		bw.addPropertyChangeListener(l);

			//System.out.println(bw);
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(new PropertyValue("age", new Integer(55)));
		bw.setPropertyValues(pvs);
		assertTrue("Must have fired 1 events", l.getEventCount() == 1);

		bw.setEventPropagationEnabled(false);
		bw.setPropertyValue("age", new Integer(23));
		assertTrue("Must have fired no more events", l.getEventCount() == 1);
		// should have no effect
		bw.removePropertyChangeListener(l);
		
		// Re-enable. Should get our listener working again.
		bw.setEventPropagationEnabled(true);
		bw.setPropertyValue("age", new Integer(43));
		assertTrue("Must have fired 1 more event", l.getEventCount() == 2);
		//assertTrue("Event source is correct", l.getEventCount() == 3);

	}

	public void testEventPropagationDisabled() {
		TestBean t = new TestBean();
		String newName = "tony";
		ConsoleListener l = new ConsoleListener();
		try {
			BeanWrapper bw = new BeanWrapperImpl(t);
			bw.setEventPropagationEnabled(false);
			bw.addPropertyChangeListener(l);
			//System.out.println(bw);
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("age", new Integer(55)));
			pvs.addPropertyValue(new PropertyValue("name", newName));
			pvs.addPropertyValue(new PropertyValue("touchy", "valid"));
			bw.setPropertyValues(pvs);
			assertTrue("Must have fired 0 events, not " + l.getEventCount(), l.getEventCount() == 0);
		}
		catch (Exception ex) {
			fail("Shouldn't throw exception when everything is valid");
		}
	}
	
	public void testNullObject() {
		try {
			BeanWrapper bw = new BeanWrapperImpl((Object) null);
			fail ("Must throw an exception when constructed with null object");
		}
		catch (BeansException ex) {
			// We should get here
			//ex.printStackTrace();
		}
	}

	public void testInvokeValidVoidMethod() {
		TestBean tb = new TestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		Object ret = bw.invoke("setName", new Object[] { "invoked" });
		assertTrue("valid void method returned null", ret == null);
		assertTrue("invoke set name", tb.getName().equals("invoked"));
	}
	
	public void testInvokeMethodThrowingException() {
		ThrowsException te = new ThrowsException();
		BeanWrapper bw = new BeanWrapperImpl(te);
		
		Exception ex = new Exception();
		try {
			
			bw.invoke("doSomething", new Object[] { ex });
			fail("Should've thrown exception");
		}
		catch (MethodInvocationException mie) {
			assertTrue("Threw in exception", mie.getRootCause()==ex);
		}
		
		RuntimeException rex = new RuntimeException();
		try {

			bw.invoke("doSomething", new Object[] { rex });
			fail("Should've thrown exception");
		}
		catch (MethodInvocationException mie) {
			assertTrue("Threw in exception", mie.getRootCause()==rex);
		}

	}

	public static class ThrowsException {
		public void doSomething(Throwable t) throws Throwable { 
			throw t;
		}
	}

	public void testInvokeValidGetterMethod() {
		TestBean tb = new TestBean();
		tb.setAge(23);
		BeanWrapper bw = new BeanWrapperImpl(tb);
		Object ret = bw.invoke("getAge", null);
		assertTrue("valid int method returned non null", ret != null);
		Integer Age = (Integer) ret;
		assertTrue("invoke got correct age", Age.intValue() == tb.getAge());
	}
	
	public void testInvokeInvalidMethod() {
		TestBean tb = new TestBean();
		BeanWrapper bw = new BeanWrapperImpl(tb);
		try {
			Object ret = bw.invoke("setxName", new Object[] { "invoked" });
			fail("Should have thrown exception trying to invoke null method");
		}
		catch (BeansException ex) {
			// expected
		}
	}


	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		//	junit.swingui.TestRunner.main(new String[] {PrototypeFactoryTests.class.getName() } );
	}

	public static Test suite() {
		return new TestSuite(BeanWrapperTestSuite.class);
	}


	private static class GetterBean {

		private String name;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			if (this.name == null)
				throw new RuntimeException("name property must be set");
			return name;
		}
	}

	private static class ConsoleListener implements PropertyChangeListener {
		int events;

		public void propertyChange(PropertyChangeEvent e) {
			++events;
		}

		public int getEventCount() {
			return events;
		}
	}

	private static class AgistListener implements VetoableChangeListener {
		int events;

		public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
			++events;
			//System.out.println("VetoableChangeEvent: old value=[" + e.getOldValue() + "] new value=[" + e.getNewValue() + "]");
			if ("age".equals(e.getPropertyName())) {
				//if (e.getPropertyName().equals("age")
				Integer newValue = (Integer) e.getNewValue();
				if (newValue.intValue() > 65) {
					//System.out.println("Yeah! got another old bugger and vetoed it");
					throw new PropertyVetoException("You specified " + newValue.intValue() + "; that's too bloody old", e);
				}
			}
		}

		public int getEventCount() {
			return events;
		}
	}

}
