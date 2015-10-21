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

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;

/**
 * This handler resolves service metadata of the receiving gateway.
 * 
 * <p>Input: The receiver's ActionBinding document node</p>
 * 
 * <p>Output: The receiver's gateway, channel, delivery channel,
 * transport and endoint elements and identifiers.</p>
 * 
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public class CPP3ResolveReceiverGateway extends CPP3BasicConfiguration {


	@Override
	public boolean canHandle(final Map<String, Node> nodes, final Map<String, Object> metadata) {
		return super.canHandle(nodes, metadata) &&
				nodes.containsKey(NODE_RECEIVER_ACTION_BINDING);
	}


	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {

		final Node resolvedReceiverActionBinding = nodes.get(NODE_RECEIVER_ACTION_BINDING);
		try {
			final Node receivingGatewayId = safeXPath(resolvedReceiverActionBinding,
					"ancestor::cpp3:PartyInfo/cpp3:PartyId/text()"); 

			final Node receiverChannelId = safeXPath(resolvedReceiverActionBinding,
					"child::cpp3:ChannelId/text()");
			final Node receiverDeliveryChannel = safeXPath(resolvedReceiverActionBinding,
					"ancestor::cpp3:PartyInfo/cpp3:DeliveryChannel[@channelId=\"{0}\"]",
					receiverChannelId.getTextContent());
			final Node receiverTransportId = safeXPath(receiverDeliveryChannel, "attribute::transportId");
			final Node receiverTransport = safeXPath(resolvedReceiverActionBinding,
					"ancestor::cpp3:PartyInfo/cpp3:Transport[@transportId=\"{0}\"]", receiverTransportId.getTextContent());
			final Node receiverEndpoint = safeXPath(receiverTransport, "child::cpp3:TransportReceiver/cpp3:Endpoint/@uri");

			metadata.put(Metadata.TO_PARTY_ID, receivingGatewayId.getTextContent());
			metadata.put(Metadata.ENDPOINT_ADDRESS, receiverEndpoint.getTextContent());
			
			nodes.put(NODE_RECEIVING_GATEWAY_ID, receivingGatewayId);
			nodes.put(NODE_RECEIVER_CHANNEL_ID, receiverChannelId);
			nodes.put(NODE_RECEIVER_DELIVERY_CHANNEL, receiverDeliveryChannel);
			nodes.put(NODE_RECEIVER_TRANSPORT_ID, receiverTransportId);
			nodes.put(NODE_RECEIVER_TRANSPORT, receiverTransport);
			nodes.put(NODE_RECEIVER_ENDPOINT, receiverEndpoint);
			
		} catch (final XPathExpressionException e) {
			throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
		}
		return true;
	}

}
