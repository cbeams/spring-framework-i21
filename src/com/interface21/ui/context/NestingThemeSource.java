package com.interface21.ui.context;

/**
 * Subinterface of ThemeSource to be implemented by objects that
 * can resolve messages hierarchically.
 * @author Rod Johnson
 * @author Jean-Pierre Pawlak
 * @version $RevisionId$
 */
public interface NestingThemeSource extends ThemeSource {		

	/**
	 * Set the parent that will be used to try to resolve theme messages
	 * that this object can't resolve.
	 * @param parent parent ThemeSource that will be used to
	 * resolve messages that this object can't resolve.
	 * May be null, in which case no further resolution is possible.
	 */
	void setParent(ThemeSource parent);
}

