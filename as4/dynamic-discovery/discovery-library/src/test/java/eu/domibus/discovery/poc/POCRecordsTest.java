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
package eu.domibus.discovery.poc;

import static org.junit.Assert.assertEquals;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import eu.domibus.discovery.DiscoveryClient;
import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.PublisherClient;
import eu.domibus.discovery.locatorClient.ResourceLocatorClient;
import eu.domibus.discovery.names.ECodexNamingScheme;


/**
 * Run SML/CPP tests from the Proof of Concept using local files only.
 *
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public class POCRecordsTest {

	private PublisherClient discoveryClient;

	@Before
	public void setUp() throws Exception {
		discoveryClient = new DiscoveryClient(
				new ResourceLocatorClient("res://" + getClass().getName() + "/resolver.txt"));
	}

	@Test
	public void smpGovello() throws DiscoveryException {
		
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "EPO");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "Form_A");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "any-sender-id");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "govello-1234567890123-456789012");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		discoveryClient.resolveMetadata(metadata);

		final String endpointAddress = metadata.get(Metadata.ENDPOINT_ADDRESS).toString();
		assertEquals("http://someserveringermany.de/gateway", endpointAddress);
	}

	@Test
	public void cppSubmitOrderFrom1To4() throws DiscoveryException {
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
		final String endpointAddress = metadata.get(Metadata.ENDPOINT_ADDRESS).toString();
		assertEquals("http://c3.example.com/msh", endpointAddress);
	}

	@Test
	public void cppAcceptOrderFrom4To1() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "OrderingBilling");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "AcceptOrder");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "c4");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "c1");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);

		final String endpointAddress = metadata.get(Metadata.ENDPOINT_ADDRESS).toString();
		assertEquals("http://c2.example.com/msh", endpointAddress);
	}

	@Test(expected = DiscoveryException.class)
	public void cppRejectOrderFrom4To1ShouldFailIsCommentedOut() throws DiscoveryException {
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "OrderingBilling");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "RejectOrder");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "c4");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "c1");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);
	}

}
