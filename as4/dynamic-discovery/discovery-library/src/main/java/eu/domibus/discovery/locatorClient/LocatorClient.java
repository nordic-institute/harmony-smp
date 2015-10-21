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

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.names.NamingScheme;


/**
 * Declares all basic functionality of a locator client.
 * 
 * <p>Supports different naming schemes, e. g. to support virtual communities,
 * optional environments, different identifier normalisation algorithms (e. g. MD5).</p>
 * 
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public interface LocatorClient {
	

	/**
	 * Sets the name(s) of the SML/DNS server(s) to use in subsequent SML lookups.
	 * 
	 * @param resolver DNS server address(es) or {@code null} to use the system's default resolvers list.
	 * @throws DiscoveryException if at least one DNS resolver host name cannot be found
	 * @see #resolvePublisher(String)
	 */
	public abstract void setResolvers(final String... resolvers) throws DiscoveryException;

		
	/**
	 * Map an identifier (name) onto an service metadata publisher (SMP) domain name or address URI.
	 * 
	 * <p>Appends the name of a community and the name of an environment to the identifier.</p>
	 *
	 * @param metadata a map that contains all parts of the name for the naming scheme
	 * @param target whether to query sender or receiver identifier
	 * @return an SMP domain name (with trailing dot) or address URI
	 * @throws DiscoveryException on any errors
	 * @see NamingScheme
	 */
	public String resolvePublisher(final Map<String,Object> metadata, final Target target) throws DiscoveryException;

}
