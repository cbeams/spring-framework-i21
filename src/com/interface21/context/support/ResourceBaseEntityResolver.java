package com.interface21.context.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.InputSource;

import com.interface21.beans.factory.xml.BeansDtdResolver;
import com.interface21.context.ApplicationContext;

/**
 * EntityResolver implementation that tries to resolve entity references
 * relative to the resource base of the application context, if applicable.
 * Extends BeansDtdResolver to also provide DTD lookup in the classpath.
 *
 * <p>Allows to use standard XML entities to include XML snippets into an
 * application context definition, for example to split a large XML file
 * into various modules. The include paths can be relative to the
 * application context's resource base as usual, instead of relative
 * to the JVM working directory (the XML parser's default).
 *
 * <p>Note: In addition to relative paths, every URL that specifies a
 * file in the current system root, i.e. the JVM working directory,
 * will be interpreted relative to the application context too.
 *
 * @author Juergen Hoeller
 * @since 31.07.2003
 * @see com.interface21.context.ApplicationContext#getResourceBasePath
 * @see com.interface21.context.ApplicationContext#getResourceAsStream
 */
public class ResourceBaseEntityResolver extends BeansDtdResolver {

	private ApplicationContext applicationContext;

	public ResourceBaseEntityResolver(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		InputSource source = super.resolveEntity(publicId, systemId);
		if (source == null && systemId != null) {
			String resourcePath = null;
			try {
				String givenUrl = new URL(systemId).toString();
				String systemRootUrl = new File("").toURL().toString();
				// try relative to resource base if currently in system root
				if (givenUrl.startsWith(systemRootUrl)) {
					resourcePath = givenUrl.substring(systemRootUrl.length());
				}
			}
			catch (MalformedURLException ex) {
				// no URL -> try relative to resource base
				resourcePath = systemId;
			}
			if (resourcePath != null) {
				logger.debug("Trying to locate entity [" + systemId + "] as application context resource [" + resourcePath + "]");
				InputStream is = this.applicationContext.getResourceAsStream(resourcePath);
				logger.info("Found entity [" + systemId + "] as application context resource [" + resourcePath + "]");
				source = new InputSource(is);
				source.setPublicId(publicId);
				source.setSystemId(systemId);
			}
		}
		return source;
	}

}
