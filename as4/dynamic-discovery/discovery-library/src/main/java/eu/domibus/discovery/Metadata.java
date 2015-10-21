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
package eu.domibus.discovery;

import eu.domibus.discovery.handlers.MetadataHandler;
import eu.domibus.discovery.names.NamingScheme;

/**
 * Default metadata identifiers.
 * 
 * <p>Most of these identifiers are supported by the handlers in the eu.ecodex.discovery.handlers.cpp3
 * and eu.ecodex.discovery.handlers.smp packages.</p>
 * 
 * <p>You can put given metadata into the metadata argument to the resolveMetadata method.
 * You can get resolved metadata from the same map after invoking the resolveMetadata method.</p>
 *  
 * @author Thorsten Niedzwetzki
 * @see MetadataHandler#resolveMetadata(java.util.Map, java.util.Map)
 */
public interface Metadata {

	/** Identifies one or many DNS resolvers for SML. Divided by commas and/or spaces. */
	public static final String RESOLVERS = "resolvers";
	
	/** Identifies a community or {@code null} to use no community and no environment. */
	public static final String COMMUNITY = "community";
	
	/** Identifies an environment or {@code null} to use no environment. */
	public static final String ENVIRONMENT = "environment";
	
	/** Identifies the normalization algorithm (e. g. "MD5") for DNS access. */
	public static final String NORMALISATION_ALGORITHM = "normalisationAlgorithm";

	/** Identifies the process (e. g. EPO) */
	public static final String PROCESS_ID = "processId";
	
	/** Identifies the document to exchange (e. g. Form_A) or action to invoke. */
	public static final String DOCUMENT_OR_ACTION_ID = "documentOrActionId";
	
	/** Identifies the name of the sending end entity / Corner 1  */
	public static final String SENDING_END_ENTITY_ID = "sendingEndEntityId";
	
	/** Identifies the name of the sending gateway / Corner 2 */
	public static final String FROM_PARTY_ID = "fromPartyId";
	
	/** Identifies the name of the receiving gateway / Corner 3 */
	public static final String TO_PARTY_ID = "toPartyId";
	
	/** Identifies the name of the receiving end entity / Corner 4 */
	public static final String RECEIVING_END_ENTITY_ID = "receivingEndEntityId";

	/** Identifies the endpoint address / URL of the receiving gateway / Corner 3 */
	public static final String ENDPOINT_ADDRESS = "endpointAddress";
	
	/** BusDoX SMLP scheme identifier.  Only needed for BusDoX DNS CNAME SML style */
	public static final String SCHEME_ID = "schemeId";

	/** BusDoX SMLP document type identifier.  Only needed for BusDoX DNS CNAME SML style */
	public static final String DOCUMENT_TYPE_ID = "documentTypeId";
	
	/** BusDoX SMLP metadata URI pattern.  Only needed for BusDoX DNS CNAME SML style */
	public static final String URI_PATTERN = "uriPattern";
	
	/** Identifies the countrycode of the receiving country */
	public static final String COUNTRY_CODE_OR_EU = "countryCode";
	
	/** Appends a suffix to the address to look up or {@code null} to use the default ".bdxl.e-codex.eu"*/
	public static final String SUFFIX = "suffix";

    /**
     *  Contains date in format YYYY-MM-DDTHH:MM:SS for activation of containing service
     */
    public static final String SERVICE_ACTIVATION_DATE ="ServiceActivationDate";

    /**
     *  Contains the expire date in format YYYY-MM-DDTHH:MM:SS of containing service
     */
    public static final String SERVICE_EXPIRATION_DATE ="ServiceExpirationDate";

    /**
     * Identifies transport profile for corresponding endpoint.
     */
    public static final String TRANSPORT_PROFILE_ID = "transportProfileID";
	
	/**
	 * Required: A naming scheme object
	 * 
	 * @see NamingScheme
	 */
	public static final String NAMING_SCHEME = "namingScheme";

	public static final String SIGNATURE_VALIDITY = "isSignatureValid";

}
