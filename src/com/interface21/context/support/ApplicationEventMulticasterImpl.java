package com.interface21.context.support;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.interface21.context.ApplicationEvent;
import com.interface21.context.ApplicationEventMulticaster;
import com.interface21.context.ApplicationListener;


/**
 * Concrete implementation of ApplicationEventMulticaster
 * Doesn't permit multiple instances of the same listener.
 * <br/>Note that this class doesn't try to do anything clever to
 * ensure thread safety if listeners are added or removed at runtime.
 * A technique such as Copy-on-Write (Lea:137) could be used to ensure this,
 * but the assumption in this version of this framework is that listeners
 * will be added at application configuration time and not
 * added or removed as the application runs.
 * <br/>All listeners are invoked in the calling thread. This allows the
 * danger of a rogue listener blocking the entire application, but adds minimal
 * overhead.
 * <br/>An alternative implementation could be more sophisticated in both
 * these respects.
 * @author Rod Johnson
 */
public class ApplicationEventMulticasterImpl implements ApplicationEventMulticaster {

    //---------------------------------------------------------------------
	// Instance data
    //---------------------------------------------------------------------
    /**
     * Set of listeners
     */
    private Set   eventListeners = new HashSet();


    //---------------------------------------------------------------------
    // Constructors
    //---------------------------------------------------------------------
    /** Creates new ApplicationEventMulticasterImpl */
	public ApplicationEventMulticasterImpl() {
    }


    //---------------------------------------------------------------------
	// Implementation of ApplicationEventMulticaster
	//---------------------------------------------------------------------
	/**
	 * @see ApplicationEventMulticaster#addApplicationListener(ApplicationListener)
	 */
    public void addApplicationListener(ApplicationListener l) {
        eventListeners.add(l);
    }

	/**
	 * @see ApplicationEventMulticaster#removeApplicationListener(ApplicationListener)
	 */
    public void removeApplicationListener(ApplicationListener l) {
        eventListeners.remove(l);
    }


	/**
	 * @see ApplicationListener#onApplicationEvent(ApplicationEvent)
	 */
    public void onApplicationEvent(ApplicationEvent e) {
       Iterator i = eventListeners.iterator();
       while (i.hasNext()) {
           ApplicationListener l = (ApplicationListener) i.next();
           l.onApplicationEvent(e);
       }
    }

	/**
	 * @see ApplicationEventMulticaster#removeAllListeners()
	 */
	public void removeAllListeners() {
		eventListeners.clear();
	}

}	// class ApplicationEventMulticasterImpl
