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
package eu.domibus.discovery.handlers.cpp3;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.locatorClient.Target;
import eu.domibus.discovery.util.FileUtils;

/**
 * This handler resolves the document root node of the sender's gateway collaboration profile.
 * 
 * <p>This handler performs additional DNS and HTTP requests.</p>
 *
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public class CPP3ResolveSenderPublisher extends CPP3BasicConfiguration {
	private static final Logger log = Logger.getLogger(CPP3ResolveSenderPublisher.class);


	@Override
	public boolean canHandle(final Map<String, Node> nodes, final Map<String, Object> metadata) {
		return super.canHandle(nodes, metadata) && metadata.containsKey(Metadata.SENDING_END_ENTITY_ID);
	}


	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {

		final String senderMetadataURI = locatorClient.resolvePublisher(metadata, Target.SENDER);
		final Node senderMetadataRoot;
		try {
			senderMetadataRoot = documentBuilder.parse(
					FileUtils.openStream(senderMetadataURI)).getDocumentElement();
		} catch (final IOException e) {
			throw new DiscoveryException("Cannot load CPP3 file: " + e.getMessage(), e);
		} catch (final ClassNotFoundException e) {
			throw new DiscoveryException("Cannot find class of resource: " + e.getMessage(), e);
		} catch (final SAXException e) {
			throw new DiscoveryException("Cannot parse CPP3 file: " + e.getMessage(), e);
		}
		
		log.trace("Sender metadata format: " + senderMetadataRoot.getLocalName());
		if (!CPP30_ELEMENT.equals(senderMetadataRoot.getLocalName())
				|| !CPP30_NAMESPACE.equals(senderMetadataRoot.getNamespaceURI())) {
			throw new DiscoveryException("Collaboration sender profile: Unsupported format: {" +
					senderMetadataRoot.getNamespaceURI() + "}" + senderMetadataRoot.getLocalName());
		}

		nodes.put(NODE_SENDER_METADATA_ROOT, senderMetadataRoot);

		return true;
	}
	
}
