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
package eu.domibus.discovery.handlers;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.locatorClient.LocatorClient;
import eu.domibus.discovery.locatorClient.Target;
import eu.domibus.discovery.util.FileUtils;
import eu.domibus.discovery.util.Normalisation;

/**
 * This metadata handler uses a {@link LocatorClient} to resolve, download and parse
 * the gateway service metadata file of the receiving site. (Corner 3 of the four-corner-model)
 * 
 * <p>Input: TO_PARTY_ID or RECEIVING_END_ENTITY_ID metadata,
 * optionally RESOLVERS, COMMUNITY, ENVIRONMENT and NORMALIZATION_ALGORITHM metadata
 * to configure the locatorClient</p>
 * 
 * <p>Output: The root of the service service metadata document (NODE_RECEIVER_METADATA_ROOT)</p>
 * 
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public class ReceiverLocatorHandler extends DefaultMetadataHandler {

	/** Nodes map key to use for the service metadata document root. */
	public static final String NODE_RECEIVER_METADATA_ROOT = "receiverMetadataRoot";

	/**
	 * Default pattern for SMP servers according to SML standard.
	 * Applied to BusDoX CNAME domain names only.
	 * 
	 * <table>
	 *  <tr><th>Placeholder</th><th>Replaced by</th></tr>
	 *  <tr><td>{0}</td><td>Normalised recipient identifier</td></tr>
	 *  <tr><td>{1}</td><td>Scheme Identifier</td></tr>
	 *  <tr><td>{2}</td><td>Resolved domain</td></tr>
	 *  <tr><td>{3}</td><td>Recipient identifier</td></tr>
	 *  <tr><td>{4}</td><td>Document type identifier</td></tr>
	 * </table>
	 */
	protected static final String DEFAULT_METADATA_URI_PATTERN = "http://{0}.{1}.{2}/{3}/services/{4}";
	

	@Override
	public boolean canHandle(final Map<String,Node> nodes, final Map<String,Object> metadata) {
		final boolean hasToPartyId = metadata.containsKey(Metadata.TO_PARTY_ID);
		final boolean hasReceivingEndEntityId = metadata.containsKey(Metadata.RECEIVING_END_ENTITY_ID);
		return hasToPartyId || hasReceivingEndEntityId;
	}


	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {
		
		// Set resolvers if specified
		if (metadata.containsKey(Metadata.RESOLVERS)) {
			locatorClient.setResolvers(metadata.get(Metadata.RESOLVERS).toString());
		}

		nodes.put(NODE_RECEIVER_METADATA_ROOT, loadMetadata(locateMetadata(metadata)));

		return true;
	}


	/**
	 * Applies the URI pattern if the metadata locator returns a domain name only.
	 * 
	 * <p>Uses the default metadata URI pattern if no URI pattern given.</p>
	 * 
	 * <p>If the metadata locator returns an URI, this method returns it unmodified.<p>
	 * 
	 * @param metadata contains the URI pattern and different identifiers
	 * @return the metadata URI, URI pattern applied if necessary
	 * @throws DiscoveryException on any LocatorClient error
	 * @see LocatorClient
	 * @see Metadata#URI_PATTERN
	 * @see #DEFAULT_METADATA_URI_PATTERN
	 * @see Metadata#RECEIVING_END_ENTITY_ID
	 * @see Metadata#SCHEME_ID
	 * @see Metadata#DOCUMENT_TYPE_ID
	 */
	private String locateMetadata(final Map<String,Object> metadata) throws DiscoveryException {
		
		if (locatorClient == null) {
			throw new DiscoveryException("No DNS locator configured.");
		}

		final String receiverMetadataLocation = locatorClient.resolvePublisher(metadata, Target.RECEIVER);
		final String receivingEndEntityId = (String) metadata.get(Metadata.RECEIVING_END_ENTITY_ID);
		
		// The receiverMetadataLocation is a domain name. => Build URI by pattern
		if (receiverMetadataLocation.endsWith(".")) {
			final Object[] arguments = {
					Normalisation.normaliseName(receivingEndEntityId),
					metadata.get(Metadata.SCHEME_ID),
					receiverMetadataLocation.substring(0, receiverMetadataLocation.length() - 1),
					receivingEndEntityId,
					metadata.get(Metadata.DOCUMENT_TYPE_ID)
			};
			final String metadataURIPattern; 
			if (metadata.containsKey(Metadata.URI_PATTERN)) {
				metadataURIPattern = (String) metadata.get(Metadata.URI_PATTERN);
			} else {
				metadataURIPattern = DEFAULT_METADATA_URI_PATTERN;
			}
			return MessageFormat.format(metadataURIPattern, arguments);
		} else {
			// The receiverMetadataLocation is a URL. --> No need to apply the URI pattern
			return receiverMetadataLocation;
		}
	}

	
	/**
	 * Loads service metadata using the specified URI.
	 * 
	 * @param receiverMetadataURI URI pointing to service metadata
	 * @return DOM Node of the metadata document
	 * @throws DiscoveryException on any error
	 */
	private Node loadMetadata(final String receiverMetadataURI) throws DiscoveryException {
		try {
			return documentBuilder.parse(FileUtils.openStream(receiverMetadataURI)).getDocumentElement();
		} catch (final IOException e) {
			throw new DiscoveryException("Cannot load " + receiverMetadataURI, e);
		} catch (final ClassNotFoundException e) {
			throw new DiscoveryException("Cannot find class of resource " + e.getMessage(), e);
		} catch (final SAXException e) {
			throw new DiscoveryException("Cannot parse " + receiverMetadataURI, e);
		}
	}

}
