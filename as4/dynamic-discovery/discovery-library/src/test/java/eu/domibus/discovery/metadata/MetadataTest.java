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
package eu.domibus.discovery.metadata;

import static org.junit.Assert.assertEquals;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import eu.domibus.discovery.DiscoveryClient;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.PublisherClient;
import eu.domibus.discovery.locatorClient.ResourceLocatorClient;
import eu.domibus.discovery.names.ECodexNamingScheme;

/**
 * Run SML/SMP/CPP unit tests that check whether the expected service metadata are being delivered.
 * 
 * @author Thorsten Niedzwetzki
 */
public class MetadataTest {
	
	private PublisherClient discoveryClient; 

	@Before
	public void setUp() throws Exception {
		discoveryClient = new DiscoveryClient(
				new ResourceLocatorClient("res://" + getClass().getName() + "/resolver.txt"));
	}
	
	@Test
	public void smpService() throws Exception {
		
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "I-Sender");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012-nogroup");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		discoveryClient.resolveMetadata(metadata);
		
		assertEquals("I-Sender", metadata.get(Metadata.SENDING_END_ENTITY_ID));
		assertEquals("govello-1234567890123-456789012-nogroup", metadata.get(Metadata.RECEIVING_END_ENTITY_ID));
		assertEquals("EPO", metadata.get(Metadata.PROCESS_ID));
		assertEquals("Form_A", metadata.get(Metadata.DOCUMENT_OR_ACTION_ID));
		assertEquals("http://someserveringermany.de/gateway", metadata.get(Metadata.ENDPOINT_ADDRESS));
	}

	
	@Test
	public void smpServiceGroup() throws Exception {
		
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "I-Sender");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		discoveryClient.resolveMetadata(metadata);
		
		assertEquals("I-Sender", metadata.get(Metadata.SENDING_END_ENTITY_ID));
		assertEquals("govello-1234567890123-456789012", metadata.get(Metadata.RECEIVING_END_ENTITY_ID));
		assertEquals("EPO", metadata.get(Metadata.PROCESS_ID));
		assertEquals("Form_A", metadata.get(Metadata.DOCUMENT_OR_ACTION_ID));
		assertEquals("http://someserveringermany.de/gateway", metadata.get(Metadata.ENDPOINT_ADDRESS));
	}


	@Test
	public void cpp3() throws Exception {
		
		discoveryClient.setMaxRecursions(1);
		
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "OrderingBilling");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "SubmitOrder");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "c1");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "c4");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);

		assertEquals("C2", metadata.get(Metadata.FROM_PARTY_ID));
		assertEquals("C3", metadata.get(Metadata.TO_PARTY_ID));
		assertEquals("OrderingBilling", metadata.get(Metadata.PROCESS_ID));
		assertEquals("SubmitOrder", metadata.get(Metadata.DOCUMENT_OR_ACTION_ID));
		assertEquals("http://c3.example.com/msh", metadata.get(Metadata.ENDPOINT_ADDRESS));
	}

}
