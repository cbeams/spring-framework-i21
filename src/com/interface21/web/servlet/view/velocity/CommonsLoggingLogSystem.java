package com.interface21.web.servlet.view.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 * Velocity LogSystem implementation for Jakarta Commons Logging.
 * Used by VelocityConfigurer to redirect log output.
 * @author Juergen Hoeller
 * @since 07.08.2003
 * @see VelocityConfigurer
 */
public class CommonsLoggingLogSystem implements LogSystem {

	private Log logger = LogFactory.getLog(getClass());

	public void init(RuntimeServices runtimeServices) {
	}

	public void logVelocityMessage(int i, String msg) {
		switch (i) {
			case ERROR_ID:
				logger.error(msg);
				break;
			case WARN_ID:
				logger.warn(msg);
				break;
			case INFO_ID:
				logger.info(msg);
				break;
			case DEBUG_ID:
				logger.debug(msg);
				break;
		}
	}

}
