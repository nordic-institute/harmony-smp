/*
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * 
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package eu.domibus.discovery.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provide read access resource files.
 * 
 * @author Thorsten Niedzwetzki
 * @see #openStream(String)
 */
public class FileUtils {
	
	/** Resource URLs start with this prefix and a colon */
	public static final String RESOURCE_PREFIX = "res";
	
	/** Resource URL pattern to match */
	private static final Pattern RESOURCE_URL_PATTERN = Pattern.compile("^" + RESOURCE_PREFIX + "://(.*)/(.*)$");

	
	/**
	 * Opens a URL that points to a resource or any other URL.
	 * 
	 * <p>Developers can add resource file support to their code by opening URLs using this method.</p>
	 * 
	 * @param url URL of a local or remote resource
	 * @return an input stream for reading from the URL connection. 
	 * @throws MalformedURLException if the given URL is invalid
	 * @throws IOException on any error trying opening the file (e. g. {@link FileNotFoundException})
	 * @throws IllegalArgumentException if the URL of a resource does not match the resource pattern
	 * @throws ClassNotFoundException if the full.qualified.ClassName of a resource URL cannot be found
	 * @see java.net.URL
	 */
	public static InputStream openStream(final String url)
			throws MalformedURLException, ClassNotFoundException, IOException {
		return url.startsWith(RESOURCE_PREFIX + ":") ? openResourceStream(url) : new URL(url).openStream();
	}


	/**
	 * Opens a URL that points to a resource.
	 * 
	 * <p>Example: res:full.qualified.ClassName/subfolder/filename.txt<p>
	 * 
	 * @param url resource URL to open
	 * @return open stream
	 * @throws ClassNotFoundException if the full.qualified.ClassName cannot be found
	 * @throws IllegalArgumentException if the URL does not match the resource pattern
	 * @throws FileNotFoundException if the resource file cannot be found or opened
	 * @see #RESOURCE_PREFIX
	 */
	public static InputStream openResourceStream(final String url)
			throws ClassNotFoundException, FileNotFoundException {
		final Matcher matcher = RESOURCE_URL_PATTERN.matcher(url);
		if (matcher.matches()) {
			final String fullQualifiedClassName = matcher.group(1);
			final String filePath = matcher.group(2);
			final Class<?> clazz = Class.forName(fullQualifiedClassName);
			final InputStream inputStream = clazz.getResourceAsStream(filePath);
			if (inputStream == null) {
				throw new FileNotFoundException("Resource file not found: " + url);
			}
			return inputStream;
		} else {
			throw new IllegalArgumentException("Invalid resource URL: " + url);
		}
	}

}
