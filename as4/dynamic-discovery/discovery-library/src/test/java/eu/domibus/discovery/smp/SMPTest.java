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
package eu.domibus.discovery.smp;

import static org.junit.Assert.assertEquals;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import eu.domibus.discovery.DiscoveryClient;
import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.locatorClient.ResourceLocatorClient;
import eu.domibus.discovery.names.ECodexNamingScheme;

/**
 * Run SML/SMP tests using local files only.
 *
 * @author Thorsten Niedzwetzki
 */
public class SMPTest {

	private DiscoveryClient discoveryClient;

	@Before
	public void setUp() throws Exception {
		discoveryClient = new DiscoveryClient(
				new ResourceLocatorClient("res://" + getClass().getName() + "/resolver.txt"));
	}

	@Test
	public void successfulLookup() throws DiscoveryException {
		
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);
		
		final String endpointAddress = metadata.get(Metadata.ENDPOINT_ADDRESS).toString();
		assertEquals("http://someserveringermany.de/gateway", endpointAddress);
	}

	@Test(expected = DiscoveryException.class)
	public void unknownCommunity() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "no-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);
	}

	@Test(expected = DiscoveryException.class)
	public void unknownEnvironment() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "unknown");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);
	}

	@Test(expected = DiscoveryException.class)
	public void noCommunity() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);
	}

	@Test(expected = DiscoveryException.class)
	public void noEnvironment() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);
	}
	
	@Test(expected = DiscoveryException.class)
	public void unknownCountryCode() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "XX");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		discoveryClient.resolveMetadata(metadata);
	}
	
	@Test(expected = DiscoveryException.class)
	public void noCountryCode() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		discoveryClient.resolveMetadata(metadata);
	}
	
	@Test(expected = DiscoveryException.class)
	public void defaultSuffix() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		discoveryClient.resolveMetadata(metadata);
	}
	
	@Test(expected = DiscoveryException.class)
	public void unknownSuffix() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "somewhere.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		discoveryClient.resolveMetadata(metadata);
	}
}
