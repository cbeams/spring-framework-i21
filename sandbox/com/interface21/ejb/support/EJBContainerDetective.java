package com.interface21.ejb.support;

import javax.ejb.EJBException;


/**
 * NOTE: THIS CLASS IS A DEMONSTRATION ONLY:
 * IT DOES NOT FORM PART OF THE FRAMEWORK PROPER.
 * IT SHOWS HOW TO AUTO-DETECT THE EJB CONTAINER,
 * ENABLING CONDITIONAL EXECUTION AT RUNTIME
 * 
 * <BR>
 * Singleton to auto-detect EJB container.
 * Does not use read-write static fields, so doesn't violate
 * EJB programming restrictions.
 * <br>Does not introduce any dependencies on vendor-specific
 * classes.
 * <p>Servers supported:
 * <ul>
 * <li>JBoss (tested with JBoss 3.0 - "Rabbit Hole")
 * <li>Orion (tested with 1.5.2)
 * </ul>
 * @author Rod Johnson
 */
public class EJBContainerDetective {

	/** Singleton instance */
	private static EJBContainerDetective Instance;

	/** Code for JBoss */
	public static final int JBOSS = 0;

	public static final int ORION = 1;

	public static final int WEBLOGIC = 2;

	public static final int UNKNOWN = 99999;

	private static final String DELIM = "/";

	/**
	 * Test classes.
	 * Add to this array to define new servers. 
	 * One string per code. 
	 * Format is server friendly name/test class
	 */
	private static String[] SERVER_INFO = new String[] { 
		"JBoss/org.jboss.Main", 							// Tested with JBoss 3.0 (Rabbit Hole)
		"Orion/com.evermind.server.ejb.SessionContainer", 	// Tested with Orion 1.5.2
		"WebLogic/weblogic.Server" 							// Tested with WebLogic 7.0 beta
		};

	// Initialize singleton isntance
	// Must be below constants! Statics are evaluated in order.
	static {
		Instance = new EJBContainerDetective();
	}

	/**
	 * Singleton design pattern
	 */
	public static EJBContainerDetective getInstance() {
		return Instance;
	}

	/** Could use server detect here also? */
	//private LogAdapter logAdapter;

	/** One of the constants in this class */
	private int detected = UNKNOWN;

	/**
	 * Private constructor to enforce singleton
	 * design pattern
	 */
	private EJBContainerDetective() {
		//this.logAdapter = LogAdapterFactory.getInstance().getLogAdapter(this);

		// Try to detect the server
		for (int i = 0; i < SERVER_INFO.length && detected == UNKNOWN; i++) {
			if (detectServer(i)) {
				detected = i;
			}
		}
		//if (detected == UNKNOWN)
		//	logAdapter.warn("Failed to detect server");
	}

	/**
	 * Try to detect the server with the given index.
	 * Tries to find test class.
	 */
	private boolean detectServer(int id) {

		int delimIndex = SERVER_INFO[id].indexOf(DELIM);
		if (delimIndex == 0) {
			throw new EJBException(
				"Server information string '"
					+ SERVER_INFO[id]
					+ "' is in a bad format. No "
					+ DELIM
					+ " separating friendly name from class");
		}
		String serverName = SERVER_INFO[id].substring(0, delimIndex);
		String testClass = SERVER_INFO[id].substring(delimIndex + 1);
		if (testClass == null) {
			throw new EJBException(
				"Server information string '"
					+ SERVER_INFO[id]
					+ "' is in a bad format. No server class specified after delimiter "
					+ DELIM);
		}
		try {
			Class.forName(testClass);
			//logAdapter.info("Identified server " + serverName + " from class '" + testClass + "'");
			return true;
		}
		catch (ClassNotFoundException ex) {
			//logAdapter.info("NOT server " + serverName + "; class '" + testClass + "' not found");
			return false;
		}
	}

	/**
	 * Return the code for the detected server
	 * @return the server id code (one of the constants
	 * defined in this class)
	 */
	public int getServer() {
		return detected;
	}

}