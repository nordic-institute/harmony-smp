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

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.PublisherClient;
import eu.domibus.discovery.locatorClient.LocatorClient;

/**
 * Interface to be implemented by any XML service metadata handlers.
 * 
 * <p>Developers can provide custom metadata handlers by implementing this interface
 * and by adding their custom handlers to the PublisherClient's list of metadataHandlers.</p>
 * 
 * @author Thorsten Niedzwetzki
 * @see DefaultMetadataHandler
 * @see PublisherClient#getMetadataHandlers()
 */
public interface MetadataHandler {

	/**
	 * <p>Equips the metadata handler with an SML client and XML/DOM-related factories.</p>
	 * 
	 * <p>The handler can use the SML client for additional SML requests.
	 * It can use the XML/DOM-related factories to use evaluate XPath expressions
	 * and download additional documents.</p>
	 * 
	 * <p>Custom handlers can ignore parameters they do not need.</p>
	 * 
	 * @param locatorClient an SML client for SML requests
	 * @param documentBuilder to evaluate more XML documents using DOM
	 * @param xpathFactory to evaluate XPath expressions
	 */
	public void configure(
			final LocatorClient locatorClient,
			final DocumentBuilder documentBuilder,
			final XPathFactory xpathFactory,
			final int maxRecursions);
	
	/**
	 * Decides whether this handler can handle the document.
	 * 
	 * @return {@code true}, if the handler can handle this document, or {@code false} otherwise
	 */
	public boolean canHandle(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata);

	/**
	 * Extracts all service metadata from the document.
	 * The String parameters may be taken into account when parsing the document. 
	 * 
	 * @param sendingEndEntityId identifier of the original sender (corner 1 of the four-corner-model)
	 * @param receivingEndEntityId identifier of the ultimate recipient (corner 4 of the four-corner-model)
	 * @param processId identifier of the enquired process or service
	 * @param documentOrActionId identifier of the enquired document or action
	 * @param nodes nodes taken from the metadata file to pass to subsequent handlers
	 * @param metadata all service metadata extracted from the document
	 * @param documentRoot root node of the downloaded document containing service metadata
	 * @return {@code false}, if handling should stop here
	 * @throws DiscoveryException encapsulates any errors
	 * @see DiscoveryException#getCause()
	 * @see ServiceMetadata
	 */
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException;

}
