/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.submission.webService.impl;

import eu.domibus.common.MessageStatus;
import eu.domibus.common.configuration.model.Identifier;
import eu.domibus.common.exception.ConfigurationException;
import eu.domibus.common.model.logging.ErrorLogEntry;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.*;
import eu.domibus.discovery.DiscoveryClient;
import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.names.ECodexNamingScheme;
import eu.domibus.submission.AbstractBackendConnector;
import eu.domibus.submission.MessageMetadata;
import eu.domibus.submission.discovery.PartyIndentifierResolverService;
import eu.domibus.submission.transformer.MessageRetrievalTransformer;
import eu.domibus.submission.transformer.MessageSubmissionTransformer;
import eu.domibus.submission.transformer.exception.TransformationException;
import eu.domibus.submission.validation.exception.ValidationException;
import eu.domibus.submission.webService.MessageErrorsRequest;
import eu.domibus.submission.webService.MessageStatusRequest;
import eu.domibus.submission.webService.generated.*;
import eu.domibus.submission.webService.generated.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPBinding;
import java.util.Collection;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Future;

@SuppressWarnings("ValidExternallyBoundObject")
@javax.jws.WebService(
        serviceName = "BackendService_1_1",
        portName = "BACKEND_PORT",
        targetNamespace = "http://org.ecodex.backend/1_1/",
        endpointInterface = "eu.domibus.submission.webService.generated.BackendInterface")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class BackendWebServiceImpl extends AbstractBackendConnector<Messaging, UserMessage> implements BackendInterface {

    private static final Log LOG = LogFactory.getLog(BackendWebServiceImpl.class);
    private static final ObjectFactory WEBSERVICE_OF = new ObjectFactory();
    private static final eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory EBMS_OBJECT_FACTORY = new eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory();

    @Autowired
    private MessageRetrievalTransformer<UserMessage> messageRetrievalTransformer;
    @Autowired
    private MessageSubmissionTransformer<Messaging> messageSubmissionTransformer;

    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private DiscoveryClient discoveryClient;
    @Autowired
    private PartyIndentifierResolverService partyIndentifierResolverService;

    public BackendWebServiceImpl() {
        super("DefaultWebService");
    }
    public BackendWebServiceImpl(String name) {
        super(name);
    }


    @SuppressWarnings("ValidExternallyBoundObject")
    @Override
    @Transactional
    public SendResponse sendMessage(final SendRequest sendRequest, final Messaging ebMSHeaderInfo) throws SendMessageFault {
        BackendWebServiceImpl.LOG.debug("Transforming incomming message");

        final PayloadType bodyload = sendRequest.getBodyload();
        for (final PartInfo partInfo : ebMSHeaderInfo.getUserMessage().getPayloadInfo().getPartInfo()) {
            boolean foundPayload = false;
            final String href = partInfo.getHref();
            BackendWebServiceImpl.LOG.debug("Looking for payload: " + href);
            for (final PayloadType payload : sendRequest.getPayload()) {
                BackendWebServiceImpl.LOG.debug("comparing with payload id: " + payload.getPayloadId());
                if (payload.getPayloadId().equals(href)) {

                    this.copyPartProperties(payload, partInfo);
                    partInfo.setInBody(false);
                    partInfo.setBinaryData(payload.getValue());
                    foundPayload = true;
                    break;
                }
            }
            if (!foundPayload) {
                //can only be in bodyload, href MAY be null!
                if (href == null && bodyload.getPayloadId() == null ||
                        href != null && href.equals(bodyload.getPayloadId())) {

                    this.copyPartProperties(bodyload, partInfo);
                    partInfo.setInBody(true);
                    partInfo.setBinaryData(bodyload.getValue());
                } else {
                    throw new SendMessageFault("no payload found for PartInfo with href " + partInfo.getHref());
                }
            }


        }
        if (ebMSHeaderInfo.getUserMessage().getPartyInfo().getTo().getPartyId().isEmpty()) {
            enrichMessageWithSMPData(ebMSHeaderInfo);
        }
        ebMSHeaderInfo.getUserMessage().getMessageInfo().setTimestamp(new Date());
        final String messageId;
        try {
            messageId = this.submit(ebMSHeaderInfo);
        } catch (final ValidationException | TransformationException e) {
            BackendWebServiceImpl.LOG.error("", e);
            throw new SendMessageFault("Message submission failed", e);
        }
        BackendWebServiceImpl.LOG.debug("Received message from backend to send, assigning messageID" + messageId);
        final SendResponse response = BackendWebServiceImpl.WEBSERVICE_OF.createSendResponse();
        response.getMessageID().add(messageId);
        return response;
    }

    private void enrichMessageWithSMPData(Messaging ebMSHeaderInfo) {
        String endpointAddress = "";

        final SortedMap<String, Object> metadata = new TreeMap<String, Object>();
        //Service@Type is ignored because as of now it is not supported by the SMP datamodel
        metadata.put(Metadata.PROCESS_ID, ebMSHeaderInfo.getUserMessage().getCollaborationInfo().getService().getValue());
        metadata.put(Metadata.DOCUMENT_OR_ACTION_ID, ebMSHeaderInfo.getUserMessage().getCollaborationInfo().getAction());

        for (Property property : ebMSHeaderInfo.getUserMessage().getMessageProperties().getProperty()) {
            switch (property.getName()) {
                case "originalSender":
                    metadata.put(Metadata.SENDING_END_ENTITY_ID, property.getValue());
                    break;
                case "finalRecipient":
                    metadata.put(Metadata.RECEIVING_END_ENTITY_ID, property.getValue());
                    break;
            }
        }

        //should be configurable
        metadata.put(Metadata.COMMUNITY, "civil-law");
        metadata.put(Metadata.ENVIRONMENT, "test");

        metadata.put(Metadata.TRANSPORT_PROFILE_ID, "ebms3-as4");
        metadata.put(Metadata.SUFFIX, "community.eu");

        //!!!
        metadata.put(Metadata.COUNTRY_CODE_OR_EU, "*");
        metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());


        try {
            discoveryClient.resolveMetadata(metadata);
            endpointAddress = (String) metadata.get(Metadata.ENDPOINT_ADDRESS);

        } catch (DiscoveryException e) {
            LOG.error("", e);
        }

            Collection<Identifier> identifiers = partyIndentifierResolverService.resolveByEndpoint(endpointAddress);
            PartyId partyId = new PartyId();
            for (Identifier identifier : identifiers) {
                partyId.setType(identifier.getPartyIdType().getValue());
                partyId.setValue(identifier.getPartyId());
                ebMSHeaderInfo.getUserMessage().getPartyInfo().getTo().getPartyId().add(partyId);
         }

    }

    private void copyPartProperties(final PayloadType payload, final PartInfo partInfo) {
        final PartProperties partProperties = new PartProperties();
        Property prop;

        // add all partproperties WEBSERVICE_OF the backend message
        if (partInfo.getPartProperties() != null) {
            for (final Property property : partInfo.getPartProperties().getProperties()) {
                prop = new Property();

                prop.setName(property.getName());
                prop.setValue(property.getValue());
                partProperties.getProperties().add(prop);
            }
        }

        // in case there was no property with name {@value Property.MIME_TYPE} and xmime:contentType attribute was set
        //noinspection SuspiciousMethodCalls
        if (!partProperties.getProperties().contains(Property.MIME_TYPE) && payload.getContentType() != null) {
            prop = new Property();
            prop.setName(Property.MIME_TYPE);
            prop.setValue(payload.getContentType());
            partProperties.getProperties().add(prop);
        }


        partInfo.setPartProperties(partProperties);
    }


    @Override
    public ListPendingMessagesResponse listPendingMessages(final Object listPendingMessagesRequest) {
        final ListPendingMessagesResponse response = BackendWebServiceImpl.WEBSERVICE_OF.createListPendingMessagesResponse();
        final Collection<String> pending = this.messageRetriever.listPendingMessages();
        response.getMessageID().addAll(pending);
        return response;
    }

    @Override
    public void downloadMessage(final DownloadMessageRequest downloadMessageRequest, final Holder<DownloadMessageResponse> downloadMessageResponse, final Holder<Messaging> ebMSHeaderInfo) throws DownloadMessageFault {
        UserMessage userMessage = null;
        try {
            if (downloadMessageRequest.getMessageID() != null && !downloadMessageRequest.getMessageID().isEmpty()) {
                userMessage = this.downloadMessage(downloadMessageRequest.getMessageID(), null);
            }
        } catch (final ValidationException e) {
            BackendWebServiceImpl.LOG.error("", e);
            throw new DownloadMessageFault("Downloading message failed", e);
        }
        final Messaging result = BackendWebServiceImpl.EBMS_OBJECT_FACTORY.createMessaging();
        result.setUserMessage(userMessage);
        ebMSHeaderInfo.value = result;
        downloadMessageResponse.value = BackendWebServiceImpl.WEBSERVICE_OF.createDownloadMessageResponse();

        for (final PartInfo partInfo : result.getUserMessage().getPayloadInfo().getPartInfo()) {
            final PayloadType payloadType = BackendWebServiceImpl.WEBSERVICE_OF.createPayloadType();
            payloadType.setValue(partInfo.getBinaryData());
            if (partInfo.isInBody()) {
                partInfo.setHref("#bodyload");
                payloadType.setPayloadId("#bodyload");
                downloadMessageResponse.value.setBodyload(payloadType);
                continue;
            }
            payloadType.setPayloadId(partInfo.getHref());
            downloadMessageResponse.value.getPayload().add(payloadType);
        }
    }

    @Override
    public MessageStatus getMessageStatus(final MessageStatusRequest messageStatusRequest) {
        return this.messageRetriever.getMessageStatus(messageStatusRequest.getMessageID());
    }

    @Override
    public Collection<ErrorLogEntry> getMessageErrors(final MessageErrorsRequest messageErrorsRequest) {
        return this.messageRetriever.getErrorsForMessage(messageErrorsRequest.getMessageID());
    }

    @Override
    public MessageSubmissionTransformer<Messaging> getMessageSubmissionTransformer() {
        return this.messageSubmissionTransformer;
    }

    @Override
    public MessageRetrievalTransformer<UserMessage> getMessageRetrievalTransformer() {
        return this.messageRetrievalTransformer;
    }

    /**
     * As incomming messages can only be pulled, this implementation is never responsible for message delivery
     *
     * @param metadata
     * @return false
     */
    @Override
    public boolean isResponsible(final MessageMetadata metadata) {
        return false; //TODO: is it possible to have messages we are responsible for?
    }

    /**
     * @param metadata
     * @return
     */
    @SuppressWarnings("ValidExternallyBoundObject")
    @Override
    public Future<Boolean> deliverMessage(final MessageMetadata metadata) {
        throw new UnsupportedOperationException("This method should never be called");
    }
}
