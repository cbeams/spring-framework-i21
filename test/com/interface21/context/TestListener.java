package com.interface21.context;


/**
 * Listener that maintains a global count
 * of events.
 * @author  Rod Johnson
 * @since January 21, 2001
 */
public class TestListener implements ApplicationListener {
	
	// static?
	private int eventCount;
	
	public int getEventCount() {
		return eventCount;
	}
	
	public void zeroCounter() {
		eventCount = 0;
	}
	
	public TestListener() {
	}
	
	
	//---------------------------------------------------------------------
	// Implementation of WebApplicationListener
	//---------------------------------------------------------------------
	/**
	 * Ignore log events
	 */
	public void onApplicationEvent(ApplicationEvent e) {
		System.out.println(e);
		++eventCount;
	}
    
}	// class ConsoleListener
