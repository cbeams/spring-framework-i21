package com.interface21.context;

/**
 * Subclass of ApplicationListener to be implemented by
 * listeners that can broadcast events to other listeners.
 * @author  Rod Johnson
 */
public interface ApplicationEventMulticaster extends ApplicationListener {

	/**
	 * Add a listener to be notified of all events
	 * @param l listener to add
 	*/
    void addApplicationListener(ApplicationListener l);

	/**
	 * Remove a listener in the notification list]
	 * @param l listener to remove
	 */
    void removeApplicationListener(ApplicationListener l);
	
	/**
	 * Remove all listeners registered with this multicaster.
	 * It will perform no action on event notification until more
	 * listeners are registered.
	 */
	void removeAllListeners();

}

