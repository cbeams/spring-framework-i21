package com.interface21.context;

/**
 * Base bean to hold minimum of application context
 * configuration. Can be subclassed to add additional
 * properties.
 * @author Rod Johnson
 */
public class ContextOptions {
	
	/** Constant to represent default options */
	public static final ContextOptions DEFAULT_OPTIONS = new ContextOptions();
	
	/** Holds value of property reloadable. */
	private boolean reloadable = true;
	
	/** Creates new ContextOptions */
    public ContextOptions() {
    }

	/** 
	 * Can we reload this config while the application is running?
	 * <B>NB:</B> Implementations are not obliged to support thread-safe reloading
	 * in a production environment. An implementation of reloading that cannot be
	 * guaranteed to be threadsafe but is sufficient during development is all that
	 * is required. Of course any limitations of an implementation should be documented.
	 * @return whether we can reload this config
	 */
	public boolean isReloadable() {
		return reloadable;
	}
	
	/** Setter for property reloadable.
	 * @param reloadable New value of property reloadable.
	 */
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}
	
	
	/** Show the state of this object
	 */
	public String toString() {
		return getClass().getName() + ": reloadable=" + reloadable;
	}
	
}
