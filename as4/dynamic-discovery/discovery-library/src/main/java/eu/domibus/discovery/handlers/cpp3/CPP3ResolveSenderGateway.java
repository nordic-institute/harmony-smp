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
 * This handler resolves service metadata of the sending gateway.
 * 
 * <p>Input: The sender's resolved ActionBinding document node</p>
 * 
 * <p>Output: The sender's FromPartyId gateway metadata and document node.</p>
 * 
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public class CPP3ResolveSenderGateway extends CPP3BasicConfiguration {


	@Override
	public boolean canHandle(final Map<String, Node> nodes, final Map<String, Object> metadata) {
		return super.canHandle(nodes, metadata) &&
				nodes.containsKey(NODE_SENDER_ACTION_BINDING);
	}


	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {

		final Node resolvedSenderActionBinding = nodes.get(NODE_SENDER_ACTION_BINDING);
		try {
			final Node sendingGatewayId = safeXPath(resolvedSenderActionBinding,
					"ancestor::cpp3:PartyInfo/cpp3:PartyId/text()"); 

			final Node senderChannelId = safeXPath(resolvedSenderActionBinding,
					"child::cpp3:ChannelId/text()");
			final Node senderDeliveryChannel = safeXPath(resolvedSenderActionBinding,
					"ancestor::cpp3:PartyInfo/cpp3:DeliveryChannel[@channelId=\"{0}\"]",
					senderChannelId.getTextContent());
			final Node senderTransportId = safeXPath(senderDeliveryChannel, "attribute::transportId");
			final Node senderTransport = safeXPath(resolvedSenderActionBinding,
					"ancestor::cpp3:PartyInfo/cpp3:Transport[@transportId=\"{0}\"]", senderTransportId.getTextContent());
			final Node senderEndpoint = safeXPath(senderTransport, "child::cpp3:TransportReceiver/cpp3:Endpoint/@uri");

			metadata.put(Metadata.FROM_PARTY_ID, sendingGatewayId.getTextContent());
			metadata.put(Metadata.ENDPOINT_ADDRESS, senderEndpoint.getTextContent());
			
			nodes.put(NODE_SENDING_GATEWAY_ID, sendingGatewayId);
			nodes.put(NODE_SENDER_CHANNEL_ID, senderChannelId);
			nodes.put(NODE_SENDER_DELIVERY_CHANNEL, senderDeliveryChannel);
			nodes.put(NODE_SENDER_TRANSPORT_ID, senderTransportId);
			nodes.put(NODE_SENDER_TRANSPORT, senderTransport);
			nodes.put(NODE_SENDER_ENDPOINT, senderEndpoint);
			
		} catch (final XPathExpressionException e) {
			throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
		}
		return true;
	}
}
