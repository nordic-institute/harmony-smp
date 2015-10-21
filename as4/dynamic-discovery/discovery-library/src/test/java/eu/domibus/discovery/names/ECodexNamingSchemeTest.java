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

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.locatorClient.Target;
import eu.domibus.discovery.names.ECodexNamingScheme;


public class ECodexNamingSchemeTest {


	@Test
	public void nonNormalisedIdentifier() throws DiscoveryException {
		
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"end-entity-id.DE.test.civil-law.community.eu");
	}

	
	@Test
	public void normalisedIdentifier() throws DiscoveryException {
		
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.test.civil-law.community.eu");
	}

	
	@Test
	public void defaultSuffix() throws DiscoveryException {
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.test.civil-law.bdxl.e-codex.eu");
	}
	
	
	@Test
	public void noCommunityNoEnvironment() throws DiscoveryException {
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.bdxl.e-codex.eu");
	}
	

	@Test
	public void noCommunityIgnoreEnvironment() throws DiscoveryException {
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.bdxl.e-codex.eu");
	}
	

	@Test
	public void communityWithoutEnvironment() throws DiscoveryException {
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.COMMUNITY, "test");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.test.bdxl.e-codex.eu");
	}
	

	@Test(expected = DiscoveryException.class)
	public void missingEndEntityId() throws DiscoveryException {
		
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.test.civil-law.community.eu");
	}
	

	@Test(expected = DiscoveryException.class)
	public void missingCountryCodeOrEU() throws DiscoveryException {
		
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.test.civil-law.community.eu");
	}
	

	@Test
	public void defaultNamingScheme() throws DiscoveryException {
		
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.RECEIVING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.RECEIVER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.test.civil-law.community.eu");
	}
	

	@Test
	public void alternativeTarget() throws DiscoveryException {
		
		final Map<String,Object> metadata = new TreeMap<String,Object>();
		metadata.put(Metadata.SENDING_END_ENTITY_ID, "end-entity-id");
		metadata.put(Metadata.COUNTRY_CODE_OR_EU, "DE");
		metadata.put(Metadata.COMMUNITY, "civil-law");
		metadata.put(Metadata.ENVIRONMENT, "test");
		metadata.put(Metadata.SUFFIX, "community.eu");
		metadata.put(Metadata.NORMALISATION_ALGORITHM, "MD5");
		metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
		
		assertEquals(
				new ECodexNamingScheme().getFullName(metadata, Target.SENDER),
				"B-1dc3b42e7c8372ddbcd870fe02ffcbf0.DE.test.civil-law.community.eu");
	}
	

}
