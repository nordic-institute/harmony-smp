package eu.eCODEX.submission.transformer.impl;

import backend.ecodex.org._1_1.DownloadMessageResponse;
import backend.ecodex.org._1_1.PayloadType;
import backend.ecodex.org._1_1.SendRequest;
import eu.domibus.backend.util.Converter_1_1;
import eu.domibus.backend.util.IOUtils;
import eu.domibus.common.persistent.TempStore;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.persistent.EbmsPayload;
import eu.domibus.ebms3.persistent.PartInfo;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;
import eu.domibus.ebms3.persistent.UserMsgToPush;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;
import eu.eCODEX.submission.Constants;
import eu.eCODEX.submission.transformer.MessageRetrievalTransformer;
import eu.eCODEX.submission.transformer.MessageSubmissionTransformer;
import eu.eCODEX.submission.validation.Validator;
import eu.eCODEX.submission.validation.exception.ValidationException;
import eu.eCODEX.transport.dto.BackendMessageIn;
import eu.eCODEX.transport.dto.BackendMessageOut;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axis2.databinding.types.Token;
import org.apache.log4j.Logger;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.w3.www._2005._05.xmlmime.ContentType_type0;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Implementation of the transformer-interfaces with {@link BackendMessageIn} as type
 */

//TODO: Move all files to the database. Be cautious not to break any AS4 functionality
public class BackendInterfaceMessageTransformer
        implements MessageSubmissionTransformer<BackendMessageIn>, MessageRetrievalTransformer<BackendMessageOut> {

    private static final Logger LOG = Logger.getLogger(BackendInterfaceMessageTransformer.class);

    private Validator<EbMessage> preRetrievalValidator;
    private Validator<BackendMessageIn> preSubmissionValidator;
    private Validator<EbMessage> postSubmissionValidator;
    private Validator<BackendMessageOut> postRetrievalValidator;


    @Override
    public BackendMessageOut transformFromEbMessage(final EbMessage message, final BackendMessageOut messageOut)
            throws ValidationException {
        if (!(message instanceof ReceivedUserMsg)) {
            throw new UnsupportedOperationException(
                    message + "can not be transformed, only ReceivedUserMsg is supported");
        }

        this.preRetrievalValidator.validate(message);

        final ReceivedUserMsg msg = (ReceivedUserMsg) message;
        final DownloadMessageResponse res = messageOut.getResponse();
        BackendInterfaceMessageTransformer.LOG.debug("Called SendMessageService.downloadMessage");
        XMLStreamReader reader = null;
        MessagingE messagingE = null;
        try {

            reader = StAXUtils.createXMLStreamReader(new StringReader(msg.getRawXMLMessage()));

            messagingE = MessagingE.Factory.parse(reader);

            messageOut.setMessagingE(messagingE);

        } catch (Exception e) {
            BackendInterfaceMessageTransformer.LOG.error(e);
            throw new RuntimeException(e);
        }

        
        //Remove cid: of the href
        for (final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo partInfo : messagingE.getMessaging()
                                                                                                         .getUserMessage()[0]
                .getPayloadInfo().getPartInfo()) {
            if ((partInfo.getHref() != null) && partInfo.getHref().toString().toLowerCase().startsWith("cid:")) {
                partInfo.getHref().setValue(partInfo.getHref().toString().substring("cid:".length()));
            }
        }

        for (final PartInfo p : ((ReceivedUserMsg) message).getMsgInfo().getParts()) {
            final PayloadType payloadType = new PayloadType();

            if (p.getMimeType()!=null && !p.getMimeType().isEmpty()) {
                final ContentType_type0 contentType_type0 = new ContentType_type0();
                contentType_type0.setContentType_type0(p.getMimeType());
                
                payloadType.setContentType(contentType_type0);
            }
            final DataSource ds = new ByteArrayDataSource(p.getPayloadData(), p.getMimeType());
            final DataHandler dataHandler = new DataHandler(ds);
            payloadType.setBase64Binary(dataHandler);

            if (!((p.getCid() == null) || p.getCid().isEmpty())) {
                payloadType.setPayloadId(new Token(p.getCid()));
            }
            if (p.isBody()) {

                res.setBodyload(payloadType);
            } else {
                res.addPayload(payloadType);
            }
        } 

        this.postRetrievalValidator.validate(messageOut);

        return messageOut;

    }

    @Override
    public EbMessage transformToEbMessage(final BackendMessageIn messageData) throws ValidationException {

        this.preSubmissionValidator.validate(messageData);

        final MessagingE messaging = messageData.getMessagingEnvelope();
        final SendRequest sendRequest = messageData.getSendRequest();
        
        final MsgInfoSet msgInfoSet =
                Converter_1_1.convertUserMessageToMsgInfoSet(messaging.getMessaging().getUserMessage()[0]);

        final String action =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction().toString();
        final String fromPartyid = messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                .getNonEmptyString();
        // TODO: https://secure.e-codex.eu/jira/browse/BUG-75
        String fromPartyidType = null;
        if (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType() != null) {
            BackendInterfaceMessageTransformer.LOG
                    .debug(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                                   .getType().getNonEmptyString());
            fromPartyidType =
                    messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType()
                                                                                                         .getNonEmptyString();
        }
        final String toPartyid =
                messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getNonEmptyString();
        String toPartyidType = null;
        if (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType() != null) {
            BackendInterfaceMessageTransformer.LOG
                    .debug(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType()
                                                                                                              .getNonEmptyString());
            toPartyidType =
                    messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType()
                                                                                                       .getNonEmptyString();

        }


        final String service =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService().getNonEmptyString();

        msgInfoSet.setPmode(
                Configuration.getPMode(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType));


        final String tempGroup = IOUtils.createTempGroup();
        final Collection<TempStore> attachmentData = new HashSet<TempStore>();
        int counter = 0;
        {

            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo partInfo =
                    messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter];

            final String bodyloadFileName = Constants.BODYLOAD_FILE_NAME_FORMAT;
            final EbmsPayload p = new EbmsPayload();
            TempStore ts = new TempStore();
            ts.setGroup(tempGroup);
            ts.setArtifact(bodyloadFileName);
            try {
                 ts.setBytes(IOUtils.toByteArray(sendRequest.getBodyload().getBase64Binary()));
            } catch (IOException e) {
                BackendInterfaceMessageTransformer.LOG.error(e);
                throw new RuntimeException(e);
            }
            attachmentData.add(ts);
            
            p.setQualifiedFileName(tempGroup + "/" + bodyloadFileName);

            String description = null;
            if(messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                    .getDescription()!=null)
            description = messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                    .getDescription().getNonEmptyString();

            p.setDescription(description);
            if (sendRequest.getBodyload().getContentType() != null) {
                p.setContentType(sendRequest.getBodyload().getContentType().getContentType_type0());
            }

            if ((partInfo.getPartProperties() != null) && (partInfo.getPartProperties().getProperty().length > 0)) {
                for (final Property property : partInfo.getPartProperties().getProperty()) {
                    p.addPartProperties(property.getName().getNonEmptyString(), property.getNonEmptyString());
                }
            }

            msgInfoSet.setBodyPayload(p);

            counter++;
        }

        if ((sendRequest.getPayload() != null) && (sendRequest.getPayload().length > 0)) {
            final Set<EbmsPayload> payloads = new HashSet<EbmsPayload>();

            for (final PayloadType payloadType : sendRequest.getPayload()) {
                final DataHandler dataHandler = payloadType.getBase64Binary();
                
                final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo partInfo =
                        messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter];

                final EbmsPayload p = new EbmsPayload();

                final String payloadFileName = MessageFormat.format(Constants.PAYLOAD_FILE_NAME_FORMAT, counter);

                TempStore ts = new TempStore();
                ts.setGroup(tempGroup);
                ts.setArtifact(payloadFileName);
                try {
                    ts.setBytes(IOUtils.toByteArray(dataHandler));
                } catch (IOException e) {
                    BackendInterfaceMessageTransformer.LOG.error(e);
                    throw new RuntimeException(e);
                }
                attachmentData.add(ts);

                p.setQualifiedFileName(tempGroup + "/" + payloadFileName);

                String description = null;
                if(messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                        .getDescription()!=null)
                description = messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo()[counter]
                        .getDescription().getNonEmptyString();

                p.setDescription(description);
                if (payloadType.getContentType() != null) {
                    p.setContentType(payloadType.getContentType().getContentType_type0());
                }

                p.setCid(MessageFormat.format(Constants.CID_MESSAGE_FORMAT,
                                              new String[]{payloadType.getPayloadId().toString()}));

                if ((partInfo.getPartProperties() != null) && (partInfo.getPartProperties().getProperty().length > 0)) {
                    for (final Property property : partInfo.getPartProperties().getProperty()) {
                        p.addPartProperties(property.getName().getNonEmptyString(), property.getNonEmptyString());
                    }
                }

                payloads.add(p);
                counter++;
            }
            msgInfoSet.getPayloads().setPayloads(payloads);
        }

/*            TempStore metadataTempStore = new TempStore();
            metadataTempStore.setGroup(tempGroup);
            metadataTempStore.setArtifact(Constants.METADATA_ARTIFACT_NAME);
            ByteArrayOutputStream metadataFile = new ByteArrayOutputStream();
            msgInfoSet.writeToRawXML(metadataFile);
            metadataTempStore.setBytes(metadataFile.toByteArray());

            tsd.persist(metadataTempStore);*/


        final UserMsgToPush userMsgToPush = new UserMsgToPush(tempGroup, msgInfoSet, attachmentData);

        this.postSubmissionValidator.validate(userMsgToPush);

        return userMsgToPush;
    }

    @Override
    public void setPostRetrievalValidator(Validator<BackendMessageOut> validator) {
        this.postRetrievalValidator = validator;
    }

    @Override
    public void setPreRetrievalValidator(Validator<EbMessage> validator) {
        this.preRetrievalValidator = validator;
    }

    @Override
    public void setPostSubmissionValidator(Validator<EbMessage> validator) {
        this.postSubmissionValidator = validator;
    }

    @Override
    public void setPreSubmissionValidator(Validator<BackendMessageIn> validator) {
        this.preSubmissionValidator = validator;
    }

}
