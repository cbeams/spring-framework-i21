package com.interface21.context.support;

import com.interface21.context.ApplicationEvent;
import com.interface21.context.ApplicationListener;


/**
 * Simple listener for debug use only that logs
 * messages to the console. Normally, even for logging,
 * it's better to use a proper solution such as Log4j or 
 * Java 1.4 logging.
 * @author  Rod Johnson
 * @since January 21, 2001
 */
public class ConsoleListener implements ApplicationListener {
	
	//---------------------------------------------------------------------
	// Instance data
	//---------------------------------------------------------------------
	
	//---------------------------------------------------------------------
	// Constructors
	//---------------------------------------------------------------------
	
	public ConsoleListener() {
	}
	
	
	//---------------------------------------------------------------------
	// Implementation of WebApplicationListener
	//---------------------------------------------------------------------
	/**
	 * Ignore log events
	 */
	public void onApplicationEvent(ApplicationEvent e) {
//		if (!(e instanceof LogEvent))
			log(e.toString());
	}
	
	private void log(String s) {
		System.out.println(s);
	}
	
    
}	// class ConsoleListener
