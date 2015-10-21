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
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.handlers.ReceiverLocatorHandler;
import eu.domibus.discovery.util.FileUtils;

/**
 * This metadata handler resolves the sender's and receiver's action bindings
 * from the corresponding documents if they have been downloaded before.
 * 
 * <p>It does not complain about missing document since the user can decide whether to
 * configure the sender, the receiver, or both, no none.</p>
 * 
 * <p>Input: The process and document/action identifiers if the sending or receiving
 * end entity identifer is given (or both).</p>
 * 
 * <p>Output: The senders' or receiver's action binding node (or both).</p>
 * 
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 */
public class CPP3ResolveActionBindings extends CPP3BasicConfiguration {
	private static final Logger log = Logger.getLogger(CPP3ResolveActionBindings.class);


	@Override
	public boolean canHandle(final Map<String, Node> nodes, final Map<String, Object> metadata) {
		return super.canHandle(nodes, metadata) &&
				(!metadata.containsKey(Metadata.SENDING_END_ENTITY_ID) ||
						metadata.containsKey(Metadata.PROCESS_ID) || metadata.containsKey(Metadata.DOCUMENT_OR_ACTION_ID)) &&
				(!metadata.containsKey(Metadata.RECEIVING_END_ENTITY_ID) ||
						metadata.containsKey(Metadata.PROCESS_ID) || metadata.containsKey(Metadata.DOCUMENT_OR_ACTION_ID));
	}


	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {

		final String sendingEndEntityId = (String) metadata.get(Metadata.SENDING_END_ENTITY_ID);
		final String receivingEndEntityId = (String) metadata.get(Metadata.RECEIVING_END_ENTITY_ID);
		final String processId = (String) metadata.get(Metadata.PROCESS_ID);
		final String documentOrActionId = (String) metadata.get(Metadata.DOCUMENT_OR_ACTION_ID);
		try {

			// Resolve sender ActionBinding
			
			if (nodes.containsKey(NODE_SENDER_METADATA_ROOT)) {
				final Node unresolvedSenderActionBinding = getActionBinding(
						nodes.get(NODE_SENDER_METADATA_ROOT), sendingEndEntityId, processId, documentOrActionId, "send");
				
				final Node sendingActionBindingId = unresolvedSenderActionBinding.getAttributes().getNamedItem("id");
				if (sendingActionBindingId != null) {
					log.trace(MessageFormat.format(
							"Sender action binding has ID {0}", sendingActionBindingId.getTextContent()));
				}
				
				final Node resolvedSenderActionBinding = resolveActionBinding(
						unresolvedSenderActionBinding, maxRecursions);
				nodes.put(NODE_SENDER_ACTION_BINDING, resolvedSenderActionBinding);
			}
			
			// Resolve receiver ActionBinding

			if (nodes.containsKey(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT)) {
				final Node unresolvedReceiverActionBinding = getActionBinding(
						nodes.get(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT),
						receivingEndEntityId, processId, documentOrActionId, "receive");
				
				final Node receivingActionBindingId = unresolvedReceiverActionBinding.getAttributes().getNamedItem("id");
				if (receivingActionBindingId != null) {
					log.trace("Receiving action bound to channel " + receivingActionBindingId.getNodeValue());
				}

				final Node resolvedReceiverActionBinding = resolveActionBinding(
						unresolvedReceiverActionBinding, maxRecursions);
				nodes.put(NODE_RECEIVER_ACTION_BINDING, resolvedReceiverActionBinding);
			}

		} catch (final IOException e) {
			throw new DiscoveryException("Cannot load CPP3 file: " + e.getMessage(), e);
		} catch (final IllegalArgumentException e) {
			throw new DiscoveryException("Illegal ChannelRef URI: " + e.getMessage(), e);
		} catch (final ClassNotFoundException e) {
			throw new DiscoveryException("Illegal class name in ChannelRef URI: " + e.getMessage(), e);
		} catch (final SAXException e) {
			throw new DiscoveryException("Cannot parse CPP3 file: " + e.getMessage(), e);
		} catch (final XPathExpressionException e) {
			throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
		}
		return true;
	}


	/**
	 * Finds an ActionBinding element in the context of the given identifiers.
	 * 
	 * @param node the root node of a CPP document that contains an ActionBinding
	 * @param partyId the party identifier to look up
	 * @param processId the process identifier to look up
	 * @param documentOrActionId the document or action identifier to look up
	 * @param sendOrReceive the direction to look up ("send" or "receive")
	 * @return the ActionBinding element in a context where all given identifiers are matched
	 * @throws XPathExpressionException on any XPath expression syntax error (should not appear)
	 * @throws XPathNullResultException if a XPath expression yields no result (document is incomplete)
	 */
	protected Node getActionBinding(
			final Node node,
			final String partyId,
			final String processId,
			final String documentOrActionId,
			final String sendOrReceive) throws XPathExpressionException, XPathNullResultException {

		final Node partyInfo = safeXPath(node,
				"descendant::cpp3:PartyInfo[cpp3:PartyId/text()=\"{0}\"]", partyId);
		final Node serviceBinding = safeXPath(partyInfo,
				"descendant::cpp3:ServiceBinding[cpp3:Service/text()=\"{0}\"]", processId);
		final Node actionBinding;
		if (sendOrReceive == null) {
			actionBinding = safeXPath(serviceBinding,
					"child::cpp3:ActionBinding[@action=\"{0}\"]", documentOrActionId);
		} else {
			actionBinding = safeXPath(serviceBinding,
					"child::cpp3:ActionBinding[@sendOrReceive=\"{0}\" and @action=\"{1}\"]",
					sendOrReceive, documentOrActionId);
		}
		return actionBinding;
	}


	/**
	 * Recursively resolves an ActionBinding element that contains a ChannelRef child element
	 * until it reaches an ActionBinding that containds a ChannelId.
	 * 
	 * <p>This method downloads files referenced by ChannelRef elements and parses them.</p>
	 * 
	 * @param actionBinding the action binding to resolve to a channel identifier
	 * @param recursions the maxium number of recursions (i. e. downloads)
	 * @return an ActionBinding element that contains a ChannelRef
	 * @throws XPathExpressionException on any XPath expression syntax error (should not appear)
	 * @throws DiscoveryException if a selected element cannot be found or if the recursion limit is exceeded
	 * @throws SAXException if a referenced document (by a ChannelRef) cannot be parsed
	 * @throws IOException if a referenced document (by a ChannelRef) cannot be found (i. e. downloaded)
	 * @throws ClassNotFoundException if the context of a referenced resource document cannot be found
	 */
	protected Node resolveActionBinding(final Node actionBinding, final int recursions)
			throws XPathExpressionException, DiscoveryException, SAXException, IOException, ClassNotFoundException {
		
		if (recursions < 0) {
			throw new DiscoveryException(
					"Aborted ChannelRef recursions, maximum exceeded: " + maxRecursions);
		}

		final Node channelId = unsafeXPath(actionBinding, "child::cpp3:ChannelId");
		if (channelId != null) {
			final Node channelIdText = safeXPath(channelId, "text()");

			final Node actionBindingId = actionBinding.getAttributes().getNamedItem("id");
			if (channelId != null) {
				log.trace(MessageFormat.format("ActionBinding {0} is bound to a local channel {1}",
						actionBindingId.getTextContent(), channelIdText.getTextContent()));
			}

			return actionBinding;
		}

		final Node channelRef = unsafeXPath(actionBinding, "child::cpp3:ChannelRef");
		if (channelRef != null) {
			final Node channelRefText = safeXPath(channelRef, "text()");

			final Node actionBindingId = actionBinding.getAttributes().getNamedItem("id");
			if (actionBindingId != null) {
				log.trace(MessageFormat.format("ActionBinding {0} is bound to a remote channel {1}",
						actionBindingId.getTextContent(), channelRefText.getTextContent()));
			}

			final Matcher m = Pattern.compile("^(.*)#(.*)$").matcher(channelRefText.getTextContent());
			if (!m.matches() || m.groupCount() != 2) {
				throw new DiscoveryException("Invalid ChannelRef: " + channelRefText.getTextContent());
			}
			final String referencedURI = m.group(1);
			final String referencedId = m.group(2);
			log.trace(MessageFormat.format("Downloading referenced metadata resource {0}", referencedURI));
			
			final Node root = documentBuilder.parse(
					FileUtils.openStream(referencedURI)).getDocumentElement();
			final Node referencedActionBinding = safeXPath(root,
					"descendant-or-self::cpp3:ActionBinding[@id=\"{0}\"]", referencedId);
			return resolveActionBinding(referencedActionBinding, recursions - 1);
		}

		throw new DiscoveryException("Neither ActionBinding/ChannelRef nor ActionBinding/ChannelId found");
	}


}
