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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import eu.domibus.discovery.handlers.DefaultMetadataHandler;
import eu.domibus.discovery.handlers.MetadataHandler;
import eu.domibus.discovery.handlers.ReceiverLocatorHandler;
import eu.domibus.discovery.handlers.cpp3.CPP3ResolveActionBindings;
import eu.domibus.discovery.handlers.cpp3.CPP3ResolveReceiverGateway;
import eu.domibus.discovery.handlers.cpp3.CPP3ResolveSenderGateway;
import eu.domibus.discovery.handlers.cpp3.CPP3ResolveSenderPublisher;
import eu.domibus.discovery.handlers.smp.SMPHandler;
import eu.domibus.discovery.locatorClient.LocatorClient;

/**
 * Locate and handle SML/SMP/CPP3 using metadata handlers.
 * 
 * <p>You can add your own metadata handlers by implementing the {@link MetadataHandler} interface
 * or deriving a handler from any existing one, like the {@link DefaultMetadataHandler}.
 * Then, add your handler to the {@link #metadataHandlers} list.</p>
 * 
 * @author Thorsten Niedzwetzki
 * @see #getMetadataHandlers()
 */
public class PublisherClient {
	private static final Logger log = Logger.getLogger(PublisherClient.class);

	protected LocatorClient locatorClient;
	private final DocumentBuilder documentBuilder;
	private final XPathFactory xpathFactory;
	private final List<MetadataHandler> metadataHandlers;
	
	/** Maximum number of redirects to follow. */
	private int maxRecursions = 3;


	/**
	 * Setup up XML/DOM factories and register default metadata handlers.
	 * 
	 * @param locatorClient SML client to use for any subsequent SML queries
	 * @throws DiscoveryException on XML parser configuration errors
	 */
	public PublisherClient() throws DiscoveryException {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setCoalescing(true);
		documentBuilderFactory.setIgnoringComments(true);
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			throw new DiscoveryException("XML Parser Configuration error: " + e.getMessage(), e);
		}

		xpathFactory = XPathFactory.newInstance();

		// The order of the handlers matters, e. g. handle SML before handling SMP
		metadataHandlers = new LinkedList<MetadataHandler>();
		metadataHandlers.add(new ReceiverLocatorHandler());
		metadataHandlers.add(new SMPHandler());
		metadataHandlers.add(new CPP3ResolveSenderPublisher());
		metadataHandlers.add(new CPP3ResolveActionBindings());
		metadataHandlers.add(new CPP3ResolveSenderGateway());
		metadataHandlers.add(new CPP3ResolveReceiverGateway());
	}


	/**
	 * Set up a publisher client with a lookup cache and the given locator client.
	 * 
	 * @throws DiscoveryException on any configuration error
	 */
	public PublisherClient(final LocatorClient locatorClient) throws DiscoveryException {
		this();
		setLocatorClient(locatorClient);
	}


	/**
	 * Gain access to the chain of metadata handlers.
	 * 
	 * <p>Users can add custom metadata handlers to the list or remove existing ones.</p>
	 * 
	 * @return chain of metadata handlers
	 */
	public List<MetadataHandler> getMetadataHandlers() {
		return metadataHandlers;
	}


	public void setLocatorClient(final LocatorClient locatorClient) {
		this.locatorClient = locatorClient;
	}


	public LocatorClient getLocatorClient() {
		return locatorClient;
	}


	/**
	 * Complete service metadata using locator and publisher services.
	 * 
	 * @param metadata metadata to complete
	 * @throws DiscoveryException on any configuration, networking or parsing errors
	 */
	public void resolveMetadata(final SortedMap<String, Object> metadata) throws DiscoveryException {

		final Map<String,Node> nodes = new HashMap<String,Node>();
		int processings = 0;
		for (final MetadataHandler metadataHandler : metadataHandlers) {
			if (metadataHandler.canHandle(nodes, metadata)) {

				++processings;
				log.trace("Found matching handler #" + processings + ": " + metadataHandler.getClass().getName());
				metadataHandler.configure(locatorClient, documentBuilder, xpathFactory, maxRecursions);
				final boolean continueProcessing = metadataHandler.resolveMetadata(nodes, metadata);
				if (!continueProcessing) {
					log.trace("Metadata processing aborted as demanded by " +
							metadataHandler.getClass().getName());
					break;
				}
			}
		}

		switch (processings) {
		case 0:
			throw new DiscoveryException("No handler found for " + metadata);
		case 1:
			log.trace("Processed by one metadata handler");
			break;
		default:
			log.trace("Processed by " + processings + " metadata handlers");
			break;
		}
	}

	/**
	 * Limits the level of recursions for each handler.
	 * 
	 * <p>This applies only to handlers that resolve metadata recursively.</p>
	 * 
	 * @param maxRecursions limit on the number of metadata lookup recursions
	 */
	public void setMaxRecursions(final int maxRecursions) {
		this.maxRecursions = maxRecursions;
	}

	
	/**
	 * Get the limit of recursions.
	 * 
	 * <p>This applies only to handlers that resolve metadata recursively.</p>
	 * 
	 * @return limit on the number of metadata lookup recursions
	 */
	public int getMaxRecursions() {
		return maxRecursions;
	}

}
