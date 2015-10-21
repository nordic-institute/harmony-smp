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
package eu.domibus.discovery.names;

import java.util.Map;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.locatorClient.Target;


/**
 * Gathers all name parts from the given metadata and assembles a DNS name to resolve.
 */
public interface NamingScheme {

	/**
	 * Assemble DNS name to resolve.
	 * 
	 * @param metadata metadata to gather name parts from
	 * @param target which name to assemble, sender or receiver
	 * @return full DNS name, but without trailing dot
	 * @throws DiscoveryException on missing metadata or any other error
	 */
	public String getFullName(final Map<String,Object> metadata, final Target target)
	throws DiscoveryException;

}
