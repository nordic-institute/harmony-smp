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
package eu.domibus.discovery.locatorClient;

import java.util.Map;
import java.util.Properties;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.names.ECodexNamingScheme;
import eu.domibus.discovery.names.NamingScheme;
import eu.domibus.discovery.util.FileUtils;

/**
 * This service metadata locator client looks up a service metadata publisher (SMP) URL
 * using a properties file.
 * 
 * <p>The format of the properties file is "service metadata identifier = service metadata URL".</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public class ResourceLocatorClient implements LocatorClient {
	
	/** Maps receivingEndEntityIds to publisher URIs */
	private final Properties publishers;

	
	public ResourceLocatorClient() {
		publishers = new Properties();
	}

	
	public ResourceLocatorClient(final String... resolvers) throws DiscoveryException {
		this();
		setResolvers(resolvers);
	}


	@Override
	public void setResolvers(final String... resolvers) throws DiscoveryException {
		publishers.clear();
		for (final String resolver : resolvers) {
			try {
				publishers.load(FileUtils.openStream(resolver));
			} catch (final Exception e) {
				throw new DiscoveryException("Cannot open resolver properties file: " + e.getMessage(), e);
			}
		}
	}

	
	@Override
	public String resolvePublisher(final Map<String, Object> metadata, final Target target) throws DiscoveryException {
		if (!metadata.containsKey(Metadata.NAMING_SCHEME)) {
			metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		}
		final NamingScheme namingScheme = (NamingScheme) metadata.get(Metadata.NAMING_SCHEME);
		final String fullName = namingScheme.getFullName(metadata, target) + ".";
		final String publisher = (String) publishers.get(fullName);
		if (publisher == null) {
			throw new DiscoveryException("No mock publishing URI for " + fullName);
		}
		return publisher;
	}


}
