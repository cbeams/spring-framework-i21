package com.interface21.context;

/**
 * Base bean to hold minimum of application context configuration.
 * Can be subclassed to add additional properties.
 * @author Rod Johnson
 */
public class ContextOptions {

	private boolean reloadable = true;

	public ContextOptions() {
	}

	/**
	 * Can we reload this config while the application is running?
	 * <p>Note: Implementations are not obliged to support thread-safe reloading
	 * in a production environment. An implementation of reloading that cannot be
	 * guaranteed to be threadsafe but is sufficient during development is all that
	 * is required. Of course any limitations of an implementation should be documented.
	 * @return whether we can reload this config
	 */
	public boolean isReloadable() {
		return reloadable;
	}

	/**
	 * Set if we can reload this config.
	 * @param reloadable if we can reload this config.
	 */
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public String toString() {
		return getClass().getName() + ": reloadable=" + reloadable;
	}

}
