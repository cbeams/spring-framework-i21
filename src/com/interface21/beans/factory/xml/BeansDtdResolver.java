package com.interface21.beans.factory.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * EntityResolver implementation for the Spring beans DTD,
 * to load the DTD from the Spring classpath resp. JAR file.
 *
 * <p>Fetches "spring-beans.dtd" from the classpath resource
 * "/com/interface21/beans/factory/xml/spring-beans.dtd",
 * no matter if specified as some local URL or as
 * "http://www.springframework.org/dtd/spring-beans.dtd".
 *
 * @author Juergen Hoeller
 * @since 04.06.2003
 */
public class BeansDtdResolver implements EntityResolver {

	private static final String DTD_NAME = "spring-beans";

	private static final String SEARCH_PACKAGE = "/com/interface21/beans/factory/xml/";

	protected Log logger = LogFactory.getLog(getClass());

	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		logger.debug("Trying to resolve XML entity: publicId=[" + publicId + "], systemId=[" + systemId + "]");

		if (systemId != null && systemId.indexOf(DTD_NAME) > systemId.lastIndexOf("/")) {
			String dtdFile = systemId.substring(systemId.indexOf(DTD_NAME));
			// Search for DTD
			logger.debug("Trying to locate [" + dtdFile + "] under " + SEARCH_PACKAGE);
			InputStream is = getClass().getResourceAsStream(SEARCH_PACKAGE + dtdFile);
			if (is != null) {
				logger.debug("Found beans DTD [" + systemId + "] is classpath");
				InputSource source = new InputSource(is);
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				return source;
			}
			else {
				logger.debug("Could not resolve DTD [" + systemId + "]: not found is classpath");
			}
		}
		// use the default behaviour -> download from website or whereever
		return null;
	}

}
