package com.interface21.context.support;

import com.interface21.context.ApplicationEvent;
import com.interface21.context.ApplicationContext;

/**
 * Event raised when an ApplicationContext gets closed.
 * @author Juergen Hoeller
 * @since 12.08.2003
 */
public class ContextClosedEvent extends ApplicationEvent {

	/**
	 * Creates a new ContextClosedEvent.
	 * @param source the ApplicationContext
	 */
	public ContextClosedEvent(ApplicationContext source) {
		super(source);
	}

	public ApplicationContext getApplicationContext() {
		return (ApplicationContext) getSource();
	}

}
