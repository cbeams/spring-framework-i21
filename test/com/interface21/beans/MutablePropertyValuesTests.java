
package com.interface21.beans;

public class MutablePropertyValuesTests extends AbstractPropertyValuesTests {
	
		public MutablePropertyValuesTests(String name) {
			super(name);
		}
		
		public void testValid() throws Exception {
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
			pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
			pvs.addPropertyValue(new PropertyValue("age", "50"));
			testTony(pvs);
		}
		
		public void testChangesOnEquals() throws Exception {
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
			pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
			pvs.addPropertyValue(new PropertyValue("age", "50"));
			MutablePropertyValues pvs2 = pvs;
			PropertyValues changes = pvs2.changesSince(pvs);
			assertTrue("changes are empty", changes.getPropertyValues().length == 0);
		}
		
		public void testChangeOfOneField() throws Exception {
			MutablePropertyValues pvs = new MutablePropertyValues();
			pvs.addPropertyValue(new PropertyValue("forname", "Tony"));
			pvs.addPropertyValue(new PropertyValue("surname", "Blair"));
			pvs.addPropertyValue(new PropertyValue("age", "50"));
			MutablePropertyValues pvs2 = new MutablePropertyValues(pvs);
			
			PropertyValues changes = pvs2.changesSince(pvs);
			assertTrue("changes are empty, not of length " + changes.getPropertyValues().length, changes.getPropertyValues().length == 0);
			
			pvs2.addPropertyValue(new PropertyValue("forname", "Gordon"));
			
			changes = pvs2.changesSince(pvs);
			assertTrue("1 change", changes.getPropertyValues().length == 1);
			PropertyValue fn = changes.getPropertyValue("forname");
			assertTrue("change is forname",  fn != null);
			assertTrue("new value is gordon", fn.getValue().equals("Gordon"));
			
			MutablePropertyValues pvs3 = new MutablePropertyValues(pvs);
			
			changes = pvs3.changesSince(pvs);
			assertTrue("changes are empty, not of length " + changes.getPropertyValues().length, changes.getPropertyValues().length == 0);
			// add new
			pvs3.addPropertyValue(new PropertyValue("foo", "bar"));
			pvs3.addPropertyValue(new PropertyValue("fi", "fum"));
			changes = pvs3.changesSince(pvs);
			assertTrue("2 change", changes.getPropertyValues().length == 2);
			fn = changes.getPropertyValue("foo");
			assertTrue("change in foo",  fn != null);
			assertTrue("new value is bar", fn.getValue().equals("bar"));
		}

}
