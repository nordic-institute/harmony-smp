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
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.locatorClient.Target;
import eu.domibus.discovery.util.Normalisation;


/**
 * Gathers all name parts from the given metadata and assembles a DNS name to resolve.
 * 
 * <p>Applies normalisation where needed.</p>
 * 
 * <p>Result: end-entity-id . country-code-or-eu . [[ environment . ] community . ] suffix</p>
 * 
 */
public class ECodexNamingScheme implements NamingScheme {

	/** Default Suffix (DNS domain) for the e-CODEX project */
	public static String DEFAULT_SUFFIX = "bdxl.e-codex.eu";

	/**
	 * Appends the community and optional environment to the normalised receiver identifier and country code.
	 * 
	 * <p>Required metadata:</p>
	 * <ul>
	 * 	<li>SENDING_END_ENTITY_ID for Target.SENDER</li>
	 * 	<li>RECEIVING_END_ENTITY_ID for Target.RECEIVER</li>
	 * 	<li>COUNTRY_CODE_OR_EU containing two-letter ISO 3166 country code or "EU"</li>
	 * </ul>
	 * <p>Optional metadata:</p>
	 * <ul>
	 * 	<li>COMMUNITY</li>
	 * 	<li>ENVIRONMENT</li>
	 * 	<li>SUFFIX will be replaced by DEFAULT_SUFFIX if not given</li>
	 * </ul>
	 * 
	 * @see Metadata#SENDING_END_ENTITY_ID
	 * @see Metadata#RECEIVING_END_ENTITY_ID
	 * @see Metadata#COUNTRY_CODE_OR_EU
	 * @see Metadata#COMMUNITY
	 * @see Metadata#ENVIRONMENT
	 * @see Metadata#SUFFIX
	 * @see #DEFAULT_SUFFIX
	 * @return identifier with appended community and environment
	 */
	@Override
	public String getFullName(final Map<String, Object> metadata, final Target target)
			throws DiscoveryException{

		final StringBuilder sb = new StringBuilder();


		// Required component #1 of the e-CODEX naming scheme: The end entity identifier
		
		final String endEntityId;
		switch (target) {
		case SENDER:
			if (metadata.containsKey(Metadata.SENDING_END_ENTITY_ID)) {
				endEntityId = metadata.get(Metadata.SENDING_END_ENTITY_ID).toString();
			} else {
				throw new DiscoveryException("Missing metadata: " + Metadata.SENDING_END_ENTITY_ID);
			}
			break;
		case RECEIVER:
			if (metadata.containsKey(Metadata.RECEIVING_END_ENTITY_ID)) {
				endEntityId = metadata.get(Metadata.RECEIVING_END_ENTITY_ID).toString();
			} else {
				throw new DiscoveryException("Missing metadata: " + Metadata.RECEIVING_END_ENTITY_ID);
			}
			break;
		default:
			throw new DiscoveryException("Unknown transport target: " + target);
		}
		
		if (metadata.containsKey(Metadata.NORMALISATION_ALGORITHM)) {
			sb.append(Normalisation.normaliseName(endEntityId,
					metadata.get(Metadata.NORMALISATION_ALGORITHM).toString()));
		} else {
			sb.append(endEntityId);
		}
		sb.append('.');


		// Required component #2 of the e-CODEX naming scheme: The country code or "EU"

		if (metadata.containsKey(Metadata.COUNTRY_CODE_OR_EU)) {
			sb.append(metadata.get(Metadata.COUNTRY_CODE_OR_EU).toString());
		} else {
			throw new DiscoveryException("Missing metadata: " + Metadata.COUNTRY_CODE_OR_EU);
		}
		sb.append('.');
		

		// Optional components #3 and #4 of the e-CODEX naming scheme: Environment and Community

		if (metadata.containsKey(Metadata.COMMUNITY)) {
			if (metadata.containsKey(Metadata.ENVIRONMENT)) {
				sb.append(metadata.get(Metadata.ENVIRONMENT).toString());
				sb.append('.');
			}
			sb.append(metadata.get(Metadata.COMMUNITY).toString());
			sb.append('.');
		}

		// Required final component of the e-CODEX naming scheme: The suffix / domain part

		if (metadata.containsKey(Metadata.SUFFIX)) {
			sb.append(metadata.get(Metadata.SUFFIX).toString());
		} else {
			sb.append(DEFAULT_SUFFIX);
		}
		
		return sb.toString();
	}

}
