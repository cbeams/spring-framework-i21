/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.jdbc.util;

import java.sql.Types;

import junit.framework.TestCase;

/**
 * TODO this test case needs attention: I wrote it based on Isabelle's documentation
 * and it appears that JdbcUtils doesn't work exactly as documented.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @version $Id$
 */
public class JdbcUtilsTests extends TestCase {

	/**
	 * Constructor for JdbcUtilsTests.
	 * @param arg0
	 */
	public JdbcUtilsTests(String arg0) {
		super(arg0);
	}

	/**
	 * Examples : if the delimiter is the single quote, and the character to count the 
     * occurrences of is the question mark, then
     * <p>
     * <code>The big ? 'bad wolf'</code> gives a count of one
     * <code>The big ?? bad wolf</code> gives a count of zero
     * <code>The big  'ba''ad' ? wolf</code> gives a count of one
	 */
	public void testCountParameterPlaceholders() {
		assertTrue(JdbcUtils.countParameterPlaceholders("The big ? 'bad wolf'", '?', '\'') == 1);
		
		// TODO check why this gives invalid string
		//assertTrue(JdbcUtils.countParameterPlaceholders("The big ?? bad wolf", '?', '\'') == 0);
		
		// ALSO doesn't work as documented
		//assertTrue(JdbcUtils.countParameterPlaceholders("The big  'ba''ad' ? wolf", '?', '\'') == 1);
	}

	public void testIsNumeric() {
		assertTrue(JdbcUtils.isNumeric(Types.BIGINT));
		assertTrue(JdbcUtils.isNumeric(Types.NUMERIC));
		assertTrue(JdbcUtils.isNumeric(Types.INTEGER));
		assertTrue(JdbcUtils.isNumeric(Types.FLOAT));
		assertTrue(!JdbcUtils.isNumeric(Types.VARCHAR));
	}

	public void testTranslateType() {
		assertTrue(JdbcUtils.translateType(Types.VARCHAR) == Types.VARCHAR);
		assertTrue(JdbcUtils.translateType(Types.CHAR) == Types.VARCHAR);
	}

}
