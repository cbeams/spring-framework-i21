/*
 * Copyright (c) 2003 JTeam B.V.
 * www.jteam.nl
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JTeam B.V. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with JTeam.
 */
package com.interface21.util;

import junit.framework.TestCase;

public class PathMatcherTestSuite extends TestCase {

    public void testPathMatcher() {

        // test exact matching
        assertTrue(PathMatcher.match("test", "test"));
        assertTrue(PathMatcher.match("/test", "/test"));
        assertFalse(PathMatcher.match("/test.jpg", "test.jpg"));
        assertFalse(PathMatcher.match("test", "/test"));
        assertFalse(PathMatcher.match("/test", "test"));

        // test matching with ?'s
        assertTrue(PathMatcher.match("t?st", "test"));
        assertTrue(PathMatcher.match("??st", "test"));
        assertTrue(PathMatcher.match("tes?", "test"));
        assertTrue(PathMatcher.match("te??", "test"));
        assertTrue(PathMatcher.match("?es?", "test"));
        assertFalse(PathMatcher.match("tes?", "tes"));
        assertFalse(PathMatcher.match("tes?", "testt"));
        assertFalse(PathMatcher.match("tes?", "tsst"));

        // test matchin with *'s
        assertTrue(PathMatcher.match("*", "test"));
        assertTrue(PathMatcher.match("test*", "test"));
        assertTrue(PathMatcher.match("test*", "testTest"));
        assertTrue(PathMatcher.match("*test*", "AnothertestTest"));
        assertTrue(PathMatcher.match("*test", "Anothertest"));
        assertTrue(PathMatcher.match("*.*", "test."));
        assertTrue(PathMatcher.match("*.*", "test.test"));
        assertTrue(PathMatcher.match("*.*", "test.test.test"));
        assertTrue(PathMatcher.match("test*aaa", "testblaaaa"));
        assertFalse(PathMatcher.match("test*", "tst"));
        assertFalse(PathMatcher.match("test*", "tsttest"));
        assertFalse(PathMatcher.match("*test*", "tsttst"));
        assertFalse(PathMatcher.match("*test", "tsttst"));
        assertFalse(PathMatcher.match("*.*", "tsttst"));
        assertFalse(PathMatcher.match("test*aaa", "test"));
        assertFalse(PathMatcher.match("test*aaa", "testblaaab"));


        // test matching with ?'s and /'s
        assertTrue(PathMatcher.match("/?", "/a"));
        assertTrue(PathMatcher.match("/?/a", "/a/a"));
        assertTrue(PathMatcher.match("/a/?", "/a/b"));
        assertTrue(PathMatcher.match("/??/a", "/aa/a"));
        assertTrue(PathMatcher.match("/a/??", "/a/bb"));
        assertTrue(PathMatcher.match("/?", "/a"));


        // test matching with **'s
        assertTrue(PathMatcher.match("/**", "/testing/testing"));
        assertTrue(PathMatcher.match("/*/**", "/testing/testing"));
        assertTrue(PathMatcher.match("/**/*", "/testing/testing"));
        assertTrue(PathMatcher.match("/bla/**/bla", "/bla/testing/testing/bla"));
        assertTrue(PathMatcher.match("/bla/**/bla", "/bla/testing/testing/bla/bla"));
        assertTrue(PathMatcher.match("/**/test", "/bla/bla/test"));
        assertTrue(PathMatcher.match("/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla"));
        assertTrue(PathMatcher.match("/bla*bla/test", "/blaXXXbla/test"));
        assertTrue(PathMatcher.match("/*bla/test", "/XXXbla/test"));
        assertFalse(PathMatcher.match("/bla*bla/test", "/blaXXXbl/test"));
        assertFalse(PathMatcher.match("/*bla/test", "XXXblab/test"));
        assertFalse(PathMatcher.match("/*bla/test", "XXXbl/test"));

        assertFalse(PathMatcher.match("/????", "/bala/bla"));
        assertFalse(PathMatcher.match("/**/*bla", "/bla/bla/bla/bbb"));

        assertTrue(PathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/"));
        assertTrue(PathMatcher.match("/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing"));
        assertTrue(PathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing"));
        assertTrue(PathMatcher.match("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg"));

        assertTrue(PathMatcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/"));
        assertTrue(PathMatcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing"));
        assertTrue(PathMatcher.match("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing"));
        assertFalse(PathMatcher.match("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing"));

        assertFalse(PathMatcher.match("/x/x/x/", "/x/x/**/bla"));


        assertTrue(PathMatcher.match("", ""));
        assertTrue(PathMatcher.match("", ""));
        assertTrue(PathMatcher.match("", ""));
        assertTrue(PathMatcher.match("", ""));
        assertTrue(PathMatcher.match("", ""));
        assertTrue(PathMatcher.match("", ""));

    }
}

