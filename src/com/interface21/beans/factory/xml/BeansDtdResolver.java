package com.interface21.beans.factory.xml;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * EntityResolver implementation for the Spring beans DTD.
 * @author Juergen Hoeller
 * @since 04.06.2003
 */
public class BeansDtdResolver implements EntityResolver {

	private static final String DTD_NAME = "spring-beans";

	private static final String SEARCH_PACKAGE = "/com/interface21/beans/factory/xml/";

	private Log log = LogFactory.getLog(getClass());

	public InputSource resolveEntity(String publicId, String systemId) {
		if (systemId != null && systemId.indexOf(DTD_NAME) > systemId.lastIndexOf("/")) {
			String dtdFile = systemId.substring(systemId.indexOf(DTD_NAME));
			// Search for DTD
			log.debug("Trying to locate [" + dtdFile + "] under " + SEARCH_PACKAGE);
			InputStream in = getClass().getResourceAsStream(SEARCH_PACKAGE + dtdFile);
			if (in != null) {
				log.debug("Found DTD [" + systemId + "] in classpath");
				InputSource source = new InputSource(in);
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				return source;
			}
			else {
				log.debug("Could not resolve DTD [" + systemId + "]: not found in classpath");
			}
		}
		else {
			log.debug("Ignoring DTD [" + systemId + "]");
		}
		// use the default behaviour -> download from website
		return null;
	}

}
