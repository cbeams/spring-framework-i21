package com.interface21.context.support;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.factory.LBIInit;
import com.interface21.context.ACATest;
import com.interface21.context.ApplicationContext;
import com.interface21.context.AbstractApplicationContextTests;
import com.interface21.context.BeanThatListens;
 
/** 
 * Classname doesn't match XXXXTestSuite pattern, so as to avoid
 * being invoked by Ant JUnit run, as it's abstract
 * @author Rod Johnson 
 * @version $RevisionId$
 */
public class StaticApplicationContextTestSuite extends AbstractApplicationContextTests {
	
	
	protected StaticApplicationContext sac;
	
	/** Creates new SeatingPlanTest */
	public StaticApplicationContextTestSuite(String name) {
		super(name);
	}
	
	/** Run for each test */
	protected ApplicationContext createContext() throws Exception {
		StaticApplicationContext parent = new StaticApplicationContext();
		parent.addListener(parentListener) ;
		Map m = new HashMap(); 
		m.put("name", "Roderick");
		parent.registerPrototype("rod", com.interface21.beans.TestBean.class, new MutablePropertyValues(m));
		m.put("name", "Albert");
		parent.registerPrototype("father", com.interface21.beans.TestBean.class, new MutablePropertyValues(m));
		parent.rebuild();
		
		this.sac = new StaticApplicationContext(parent);
		sac.addListener(listener);
		sac.registerSingleton("beanThatListens", BeanThatListens.class, new MutablePropertyValues());
		
		sac.registerSingleton("aca", ACATest.class, new MutablePropertyValues());
		
		sac.registerPrototype("aca-prototype", ACATest.class, new MutablePropertyValues());
		
		LBIInit.createTestBeans(sac.defaultBeanFactory); 
		
		sac.rebuild();
		return sac;
	} 
	
	
	/** Overridden */
	public void testCount() {
		assertCount(11);
	}
	
	protected void tearDown() {
	}
	
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		//	junit.swingui.TestRunner.main(new String[] {PrototypeFactoryTests.class.getName() } );
	}

	public static Test suite() { 
		return new TestSuite(StaticApplicationContextTestSuite.class);
	}

	
}
