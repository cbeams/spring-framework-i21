/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
 
package com.interface21.util;

import junit.framework.TestCase;

/**
 *
 * @author Rod Johnson
 * @version $Id$
 */
public class StopWatchTests extends TestCase {

	/**
	 * Constructor for StopWatchTests.
	 * @param arg0
	 */
	public StopWatchTests(String arg0) {
		super(arg0);
	}
	
	/**
	 * Are timings off in JUnit?
	 * @throws Exception
	 */
	public void testValidUsage() throws Exception {
		StopWatch sw = new StopWatch();
		long int1 = 166L;
		long int2 = 45L;
		String name1 = "Task 1";
		String name2 = "Task 2";
		
		long fudgeFactor = 5L;
		assertFalse(sw.isRunning());
		sw.start(name1);
		Thread.sleep(int1);
		assertTrue(sw.isRunning());
		sw.stop();
		
		// TODO are timings off in JUnit? Why do these assertions sometimes fail
		// under both Ant and Eclipse?
		
		//assertTrue("Unexpected timing " + sw.getTotalTime(), sw.getTotalTime() >= int1);
		//assertTrue("Unexpected timing " + sw.getTotalTime(), sw.getTotalTime() <= int1 + fudgeFactor);
		sw.start(name2);
		Thread.sleep(int2);
		sw.stop();
		//assertTrue("Unexpected timing " + sw.getTotalTime(), sw.getTotalTime() >= int1 + int2);
		//assertTrue("Unexpected timing " + sw.getTotalTime(), sw.getTotalTime() <= int1 + int2 + fudgeFactor);
		
		assertTrue(sw.getTaskCount() == 2);
		String pp = sw.prettyPrint();
		assertTrue(pp.indexOf(name1) != -1);
		assertTrue(pp.indexOf(name2) != -1);
		
		StopWatch.TaskInfo[] tasks = sw.getTaskInfo();
		assertTrue(tasks.length == 2);
		assertTrue(tasks[0].getTaskName().equals(name1));
		assertTrue(tasks[1].getTaskName().equals(name2));
	}
	
	public void testFailureToStartBeforeGettingTimings() {
		StopWatch sw = new StopWatch();
		try {
			sw.getLastInterval();
			fail("Can't get last interval if no tests run");
		}
		catch (IllegalStateException ex) {
			// Ok
		}
	}
	
	public void testFailureToStartBeforeStop() {
		StopWatch sw = new StopWatch();
		try {
			sw.stop();
			fail("Can't stop without starting");
		}
		catch (IllegalStateException ex) {
			// Ok
		}
	}
	
	public void testRejectsStartTwice() {
		StopWatch sw = new StopWatch();
		try {
			sw.start("");
			sw.stop();
			sw.start("");
			assertTrue(sw.isRunning());
			sw.start("");
			fail("Can't start twice");
		}
		catch (IllegalStateException ex) {
			// Ok
		}
	}

}
