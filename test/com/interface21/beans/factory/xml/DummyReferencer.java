package com.interface21.beans.factory.xml;

import com.interface21.beans.TestBean;

/**
 * @author Juergen Hoeller
 * @since 21.07.2003
 */
public class DummyReferencer {

	private TestBean testBean1;

	private TestBean testBean2;

	public TestBean getTestBean1() {
		return testBean1;
	}

	public void setTestBean1(TestBean testBean1) {
		this.testBean1 = testBean1;
	}

	public TestBean getTestBean2() {
		return testBean2;
	}

	public void setTestBean2(TestBean testBean2) {
		this.testBean2 = testBean2;
	}

}
