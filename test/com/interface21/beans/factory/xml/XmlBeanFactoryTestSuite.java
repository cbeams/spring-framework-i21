/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */

package com.interface21.beans.factory.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interface21.beans.ITestBean;
import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.TestBean;
import com.interface21.beans.factory.AbstractListableBeanFactoryTests;
import com.interface21.beans.factory.BeanDefinitionStoreException;
import com.interface21.beans.factory.BeanFactory;
import com.interface21.beans.factory.HasMap;
import com.interface21.beans.factory.support.ListableBeanFactoryImpl;
import com.interface21.beans.factory.support.RootBeanDefinition;

/**
 *
 * @author Rod Johnson
 * @version $Revision$
 */
public class XmlBeanFactoryTestSuite extends AbstractListableBeanFactoryTests {

	private XmlBeanFactory factory;

	public XmlBeanFactoryTestSuite(String name) {
		super(name);
	}

	/** Run for each test */
	protected void setUp() throws Exception {
		//this.listableBeanFactory = new XMLBeanFactory("d:\\book\\project\\i21-framework\\test\\com\\interface21\\beans\\factory\\xml\\test.xml");

		ListableBeanFactoryImpl parent = new ListableBeanFactoryImpl();
		Map m = new HashMap();
		m.put("name", "Albert");
		parent.registerBeanDefinition("father",
			new RootBeanDefinition(com.interface21.beans.TestBean.class, new MutablePropertyValues(m), false));

		// Load from classpath, NOT a file path
		InputStream is = getClass().getResourceAsStream("test.xml");
		this.factory = new XmlBeanFactory(is, parent);
	}

	/**
	 * @see BeanFactoryTests#getBeanFactory()
	 */
	protected BeanFactory getBeanFactory() {
		return factory;
	}

	/** Uses a separate factory */
	public void testRefToSeparatePrototypeInstances() throws Exception {
		InputStream is = getClass().getResourceAsStream("reftypes.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		assertTrue("6 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 6);
		TestBean emma = (TestBean) xbf.getBean("emma");
		TestBean georgia = (TestBean) xbf.getBean("georgia");
		ITestBean emmasJenks = emma.getSpouse();
		ITestBean georgiasJenks = georgia.getSpouse();
		assertTrue("Emma and georgia think they have a different boyfriend",emmasJenks != georgiasJenks);
		assertTrue("Emmas jenks has right name", emmasJenks.getName().equals("Andrew"));
			assertTrue("Emmas doesn't equal new ref", emmasJenks != xbf.getBean("jenks"));
		assertTrue("Georgias jenks has right name", emmasJenks.getName().equals("Andrew"));
		assertTrue("They are object equal", emmasJenks.equals(georgiasJenks));
		assertTrue("They object equal direct ref", emmasJenks.equals(xbf.getBean("jenks")));
	}

	public void testRefToSingleton() throws Exception {
		InputStream is = getClass().getResourceAsStream("reftypes.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		assertTrue("6 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 6);
		TestBean jen = (TestBean) xbf.getBean("jenny");
		TestBean dave = (TestBean) xbf.getBean("david");
		TestBean jenks = (TestBean) xbf.getBean("jenks");
		ITestBean davesJen = dave.getSpouse();
		ITestBean jenksJen = jenks.getSpouse();
		assertTrue("1 jen instances", davesJen == jenksJen);
	}

	public void testCircularReferences() {
		InputStream is = getClass().getResourceAsStream("reftypes.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		TestBean jenny = (TestBean) xbf.getBean("jenny");
		TestBean david = (TestBean) xbf.getBean("david");
		TestBean ego = (TestBean) xbf.getBean("ego");
		assertTrue("Correct circular reference", jenny.getSpouse() == david);
		assertTrue("Correct circular reference", david.getSpouse() == jenny);
		assertTrue("Correct circular reference", ego.getSpouse() == ego);
	}

	public void testRefSubelement() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		//assertTrue("5 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 5);
		TestBean jen = (TestBean) xbf.getBean("jenny");
		TestBean dave = (TestBean) xbf.getBean("david");
		assertTrue(jen.getSpouse() == dave);
	}

	public void testPropertyWithLiteralValueSubelement() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		TestBean verbose = (TestBean) xbf.getBean("verbose");
		assertTrue(verbose.getName().equals("verbose"));
	}

	public void testRefSubelementsBuildCollection() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		//assertTrue("5 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 5);
		TestBean jen = (TestBean) xbf.getBean("jenny");
		TestBean dave = (TestBean) xbf.getBean("david");
		TestBean rod = (TestBean) xbf.getBean("rod");

		// Must be a list to support ordering
		// Our bean doesn't modify the collection:
		// of course it could be a different copy in a real object
		List friends = (List) rod.getFriends();
		assertTrue(friends.size() == 2);

		assertTrue("First friend must be jen, not " + friends.get(0),
			friends.get(0).equals(jen));
		assertTrue(friends.get(1).equals(dave));
		// Should be ordered
	}

	public void testRefSubelementsBuildCollectionFromSingleElement() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		//assertTrue("5 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 5);
		TestBean loner = (TestBean) xbf.getBean("loner");
		TestBean dave = (TestBean) xbf.getBean("david");
		assertTrue(loner.getFriends().size() == 1);
		assertTrue(loner.getFriends().contains(dave));
	}

	public void testBuildCollectionFromMixtureOfReferencesAndValues() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		//assertTrue("5 beans in reftypes, not " + xbf.getBeanDefinitionCount(), xbf.getBeanDefinitionCount() == 5);
		MixedCollectionBean jumble = (MixedCollectionBean) xbf.getBean("jumble");
		TestBean dave = (TestBean) xbf.getBean("david");
		assertTrue("Expected 3 elements, not " + jumble.getJumble().size(),
				jumble.getJumble().size() == 3);
		List l = (List) jumble.getJumble();
		assertTrue(l.get(0).equals(xbf.getBean("david")));
		assertTrue(l.get(1).equals("literal"));
		assertTrue(l.get(2).equals(xbf.getBean("jenny")));
	}

	/**
	 * Test that properties with name as well as id creating
	 * an alias up front
	 */
	public void testAutoAliasing() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		TestBean tb = (TestBean) xbf.getBean("aliased");
		TestBean alias = (TestBean) xbf.getBean("I have an alias");
		assertTrue(tb == alias);
	}

	public void testEmptyMap() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("emptyMap");
		assertTrue(hasMap.getMap().size() == 0);
	}

	public void testMapWithLiteralsOnly() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("literalMap");
		assertTrue(hasMap.getMap().size() == 2);
		assertTrue(hasMap.getMap().get("foo").equals("bar"));
		assertTrue(hasMap.getMap().get("fi").equals("fum"));
	}

	public void testMapWithLiteralsAndReferences() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("mixedMap");
		assertTrue(hasMap.getMap().size() == 2);
		assertTrue(hasMap.getMap().get("foo").equals("bar"));
		TestBean jenny = (TestBean) xbf.getBean("jenny");
		assertTrue(hasMap.getMap().get("jenny").equals(jenny));
	}
	
	public void testMapWithLiteralsReferencesAndList() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("mixedMapWithList");
		assertTrue(hasMap.getMap().size() == 3);
		assertTrue(hasMap.getMap().get("foo").equals("bar"));
		TestBean jenny = (TestBean) xbf.getBean("jenny");
		assertTrue(hasMap.getMap().get("jenny").equals(jenny));
		
		// Check list
		List l = (List) hasMap.getMap().get("list");
		assertNotNull(l);
		assertTrue(l.size() == 2);
		assertTrue(l.get(0).equals("zero"));
		assertTrue("List elt 1 should be equal to jenny bean, not " + l.get(1),
			l.get(1).equals(jenny));
	}
	
	public void testEmptyProps() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("emptyProps");
		assertTrue(hasMap.getMap().size() == 0);
		assertTrue(hasMap.getMap().size() == 0);
	}
	
	public void testPopulatedProps() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("props");
		assertTrue(hasMap.getMap().size() == 2);
		assertTrue(hasMap.getMap().get("foo").equals("bar"));
		assertTrue(hasMap.getMap().get("2").equals("TWO"));
	}
	
	public void testObjectArray() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("objectArray");
		assertTrue(hasMap.getObjectArray().length ==2);
		assertTrue(hasMap.getObjectArray()[0].equals("one"));
		assertTrue(hasMap.getObjectArray()[1].equals(xbf.getBean("jenny")));
	}
	
	public void testClassArray() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("classArray");
		assertTrue(hasMap.getClassArray().length ==2);
		assertTrue(hasMap.getClassArray()[0].equals(String.class));
		assertTrue(hasMap.getClassArray()[1].equals(Exception.class));
	}
	
	/*
	 * TODO address this failure
	 *
	public void testIntegerArray() throws Exception {
		InputStream is = getClass().getResourceAsStream("collections.xml");
		XmlBeanFactory xbf = new XmlBeanFactory(is);
		HasMap hasMap = (HasMap) xbf.getBean("integerArray");
		assertTrue(hasMap.getIntegerArray().length == 3);
		assertTrue(hasMap.getIntegerArray()[0].intValue() == 0);
		assertTrue(hasMap.getIntegerArray()[1].intValue() == 1);
		assertTrue(hasMap.getIntegerArray()[2].intValue() == 2);
	}
	*/
	
	public void testNoSuchXmlFile() throws Exception {
		String filename = "missing.xml";
		InputStream is = getClass().getResourceAsStream(filename);
		try {
			XmlBeanFactory xbf = new XmlBeanFactory(is);
			fail("Shouldn't create factory from missing XML");
		}
		catch (BeanDefinitionStoreException ex) {
			// Ok
			// TODO Check that the error message includes filename
		}
	}

	public void testInvalidXmlFile() throws Exception {
		String filename = "invalid.xml";
		InputStream is = getClass().getResourceAsStream(filename);
		try {
			XmlBeanFactory xbf = new XmlBeanFactory(is);
			fail("Shouldn't create factory from invalid XML");
		}
		catch (BeanDefinitionStoreException ex) {
			// Ok
			// TODO Check that the error message includes filename
		}
	}

}
