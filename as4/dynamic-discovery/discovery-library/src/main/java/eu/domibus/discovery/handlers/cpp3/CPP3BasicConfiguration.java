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

import java.util.Map;

import org.w3c.dom.Node;

import eu.domibus.discovery.handlers.DefaultMetadataHandler;
import eu.domibus.discovery.handlers.ReceiverLocatorHandler;
import eu.domibus.discovery.util.DefaultNamespaceContext;

/**
 * This abstract metadata handler declares the CPP3 namespace
 * and accepts CPP 3.0 documents documents by default.
 * 
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public abstract class CPP3BasicConfiguration extends DefaultMetadataHandler {
	
	protected static final String CPP30_NAMESPACE = "http://docs.oasis-open.org/ebxmlcppa/cppa-3.0";
	protected static final String CPP30_ELEMENT = "CollaborationProtocolProfile";

	protected static final String NODE_RECEIVER_ACTION_BINDING = "receiverActionBinding";
	protected static final String NODE_RECEIVING_GATEWAY_ID = "receivingGatewayId";
	protected static final String NODE_RECEIVER_CHANNEL_ID = "receiverChannelId";
	protected static final String NODE_RECEIVER_DELIVERY_CHANNEL = "receiverDeliveryId";
	protected static final String NODE_RECEIVER_TRANSPORT_ID = "receiverTransportId";
	protected static final String NODE_RECEIVER_TRANSPORT = "receiverTransport";
	protected static final String NODE_RECEIVER_ENDPOINT = "receiverEndpoint";

	protected static final String NODE_SENDER_METADATA_ROOT = "senderMetadataRoot";
	
	protected static final String NODE_SENDER_ACTION_BINDING = "senderActionBinding";
	protected static final String NODE_SENDING_GATEWAY_ID = "sendingGatewayId";
	protected static final String NODE_SENDER_CHANNEL_ID = "senderChannelId";
	protected static final String NODE_SENDER_DELIVERY_CHANNEL = "senderDeliveryChannel";
	protected static final String NODE_SENDER_TRANSPORT_ID = "senderTransportId";
	protected static final String NODE_SENDER_TRANSPORT = "senderTransport";
	protected static final String NODE_SENDER_ENDPOINT = "senderEndpoint";

	@Override
	public void configureNamespaces(final DefaultNamespaceContext namespaces) {
		super.configureNamespaces(namespaces);
		namespaces.addNamespace("cpp3", CPP30_NAMESPACE);
	}

	
	@Override
	public boolean canHandle(final Map<String,Node> nodes, final Map<String,Object> metadata) {
		final Node receiverMetadataRoot = nodes.get(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT);
		return receiverMetadataRoot != null &&
				CPP30_ELEMENT.equals(receiverMetadataRoot.getLocalName())
				&& CPP30_NAMESPACE.equals(receiverMetadataRoot.getNamespaceURI());
	}

}
