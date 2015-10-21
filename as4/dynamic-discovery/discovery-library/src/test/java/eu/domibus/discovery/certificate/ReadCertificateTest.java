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
package eu.domibus.discovery.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
 * Run tests about certificate extraction
 *
 * @author Thorsten Niedzwetzki
 */
public class ReadCertificateTest {

	
	private DiscoveryClient discoveryClient;

	
	@Before
	public void setUp() throws Exception {
		discoveryClient = new DiscoveryClient(
				new ResourceLocatorClient("res://" + getClass().getName() + "/resolver.txt"));
		discoveryClient.getMetadataHandlers().add(new SMPCertificateHandler());
		discoveryClient.getMetadataHandlers().add(new CPPCertificateHandler());
		discoveryClient.getMetadataHandlers().add(new CertificateHandler());
	}

	
	@Test
	public void extractSMPReceiverSubjectCertificateCommonNameField() throws DiscoveryException {
		
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
		
		final String certificateCommonName = metadata.get(
				CertificateHandler.RECEIVER_CERTIFICATE_SUBJECT_CN).toString();
		assertEquals("Thorsten Niedzwetzki", certificateCommonName);
	}


	@Test
	public void extractSMPSenderSubjectCertificateCommonNameField() throws DiscoveryException {
		
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
		
		// SMLP cannot provide sender certificate
		assertEquals(null, metadata.get(CertificateHandler.SENDER_CERTIFICATE_SUBJECT_CN));
	}


	@Test
	public void extractCPPReceiverSubjectCertificateCommonNameField() throws DiscoveryException {
		
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "OrderingBilling");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "SubmitOrder");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "c1");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "c4");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);

		final String receiverCertificateCommonName =
				(String) metadata.get(CertificateHandler.RECEIVER_CERTIFICATE_SUBJECT_CN);
		assertNotNull(metadata.get(CertificateHandler.RECEIVER_CERTIFICATE_SUBJECT_CN));
		assertEquals("Thorsten Niedzwetzki", receiverCertificateCommonName);
	}

	@Test
	public void extractCPPSenderSubjectCertificateCommonNameField() throws DiscoveryException {
		
		final SortedMap<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.PROCESS_ID, "OrderingBilling");
		metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, "SubmitOrder");
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "c1");
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "c4");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());

		discoveryClient.resolveMetadata(metadata);

		
		final String senderCertificateCommonName =
				(String) metadata.get(CertificateHandler.SENDER_CERTIFICATE_SUBJECT_CN);
		assertNotNull(senderCertificateCommonName);
		assertEquals("Thorsten Niedzwetzki", senderCertificateCommonName);
	}

}
