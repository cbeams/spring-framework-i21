package com.interface21.beans;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


/**
 *
 * @author Rod Johnson
 * @version $RevisionId$
 */
public abstract class AbstractPropertyValuesTests extends TestCase { 


	/** Creates new SeatingPlanTest */
	public AbstractPropertyValuesTests(String name) {
		super(name);
	}

	/** Run for each test */
	protected void setUp() throws Exception {
	}

/** Must contain: forname=Tony surname=Blair age=50
 * 
 */
	
	protected void testTony(PropertyValues pvs) throws Exception {
		
		assertTrue("Contains 3", pvs.getPropertyValues().length == 3);
		assertTrue("Contains forname", pvs.contains("forname"));
		assertTrue("Contains surname", pvs.contains("surname"));
		assertTrue("Contains age", pvs.contains("age"));
		assertTrue("Doesn't contain tory", !pvs.contains("tory"));
		
		PropertyValue[] ps = pvs.getPropertyValues();
		Map m = new HashMap();
		m.put("forname", "Tony");
		m.put("surname", "Blair");
		m.put("age", "50");
		for (int i = 0; i < ps.length; i++) {
			Object val = m.get(ps[i].getName());
			assertTrue("Can't have unexpected value", val != null);
			assertTrue("Val i string", val instanceof String);
			assertTrue("val matches expected", val.equals(ps[i].getValue()));
			m.remove(ps[i].getName());
		}
		assertTrue("Map size is 0", m.size() == 0);
	}
	
	// NULL TESTS ETC.

	

}
