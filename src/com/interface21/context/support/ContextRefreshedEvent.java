package com.interface21.context.support;

import com.interface21.context.ApplicationEvent;
import com.interface21.context.ApplicationContext;

/**
 * Event raised when an ApplicationContext gets initialized or refreshed.
 * @author Juergen Hoeller
 * @since 04.03.2003
 */
public class ContextRefreshedEvent extends ApplicationEvent {

	/**
	 * Creates a new ContextRefreshedEvent.
	 * @param source the ApplicationContext
	 */
	public ContextRefreshedEvent(ApplicationContext source) {
		super(source);
	}

	public ApplicationContext getApplicationContext() {
		return (ApplicationContext) getSource();
	}

}
