/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.web.servlet.view.xslt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.TestCase;

/**
 * 
 * @author Rod Johnson
 * @since 26-Jul-2003
 * @version $Id$
 */
public class FormatHelperTests extends TestCase {

	/**
	 * Constructor for FormatHelperTests.
	 * @param arg0
	 */
	public FormatHelperTests(String arg0) {
		super(arg0);
	}

	/*
	 * Test for Node dateTimeElement(long, String, String)
	 */
	public void testUkDateTimeElement() {
		long t = System.currentTimeMillis();
		Element e = (Element) FormatHelper.dateTimeElement(t, Locale.UK);
		assertTrue(e.getTagName().equals("formatted-date"));
		Element monthEle = (Element) e.getElementsByTagName("month").item(0);
		// TODO finish this test case
	}

	

}
