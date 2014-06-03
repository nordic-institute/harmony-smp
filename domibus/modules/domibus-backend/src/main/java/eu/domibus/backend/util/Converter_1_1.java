/*
 * 
 */
package eu.domibus.backend.util;

import backend.ecodex.org._1_1.Code;
import backend.ecodex.org._1_1.ListPendingMessagesResponse;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.log4j.Logger;
import eu.domibus.backend.db.model.Message;
import eu.domibus.backend.service._1_1.exception.DownloadMessageServiceException;
import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.persistent.Payloads;
import eu.domibus.ebms3.persistent.Properties;
import eu.domibus.ebms3.submit.MsgInfoSet;
import eu.domibus.logging.persistent.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class Converter.
 */
public class Converter_1_1 {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(Converter_1_1.class);

    /**
     * Convert user message to msg info set.
     *
     * @param userMessage the user message
     * @return the eu.domibus.ebms3.submit. msg info set
     */
    public static MsgInfoSet convertUserMessageToMsgInfoSet(final UserMessage userMessage) {
        final MsgInfoSet msgInfoSet = new MsgInfoSet();
        if (userMessage.getCollaborationInfo() != null) {
            if (userMessage.getCollaborationInfo().getAgreementRef() != null) {
                msgInfoSet.setAgreementRef(userMessage.getCollaborationInfo().getAgreementRef().getNonEmptyString());
            }
            if (userMessage.getCollaborationInfo().getConversationId() != null) {
                msgInfoSet.setConversationId(userMessage.getCollaborationInfo().getConversationId().toString());
            }
        }
        if (userMessage.getMessageInfo() != null && userMessage.getMessageInfo().getRefToMessageId() != null) {
            if (userMessage.getMessageInfo().getRefToMessageId() != null) {
                msgInfoSet.setRefToMessageId(userMessage.getMessageInfo().getRefToMessageId().getNonEmptyString());
            }
        }
        if (userMessage.getPartyInfo() != null) {
            if (userMessage.getPartyInfo().getFrom() != null) {
                final Producer producer = new Producer();
                if (userMessage.getPartyInfo().getFrom().getRole() != null) {
                    producer.setRole(userMessage.getPartyInfo().getFrom().getRole().getNonEmptyString());
                }
                final Set<Party> parties = new HashSet<Party>();
                if (userMessage.getPartyInfo().getFrom().getPartyId() != null) {
                    for (final PartyId partyId : userMessage.getPartyInfo().getFrom().getPartyId()) {
                        final Party party = new Party();
                        party.setPartyId(partyId.getNonEmptyString());
                        if (partyId.getType() != null) {
                            party.setType(partyId.getType().getNonEmptyString());
                        }

                        parties.add(party);
                    }
                }
                producer.setParties(parties);
                msgInfoSet.setProducer(producer);
            }
        }
        if (userMessage.getMessageProperties() != null && userMessage.getMessageProperties().getProperty() != null) {
            final Properties properties = new Properties();
            for (final Property property : userMessage.getMessageProperties().getProperty()) {
                 if(property.getName().getNonEmptyString().equalsIgnoreCase(eu.domibus.backend.module.Constants.ENDPOINT_ADDRESS_MESSAGE_PROPERTY)){
                	eu.domibus.ebms3.packaging.PackagingFactory.setCurrentEndpointAddress(property.getNonEmptyString());
                }
                else{
                    properties.addProperty(property.getName().getNonEmptyString(), property.getNonEmptyString());
                }
            }

            msgInfoSet.setProperties(properties);
        }
        if (userMessage.getPayloadInfo() != null && userMessage.getPayloadInfo().getPartInfo() != null) {
            final Payloads payloads = new Payloads();

            boolean first = true;
            for (final PartInfo partInfo : userMessage.getPayloadInfo().getPartInfo()) {
                if (first) {
                    msgInfoSet.setBodyPayload(
                            (partInfo.getDescription() != null) ? partInfo.getDescription().getNonEmptyString() : "");
                    first = false;
                } else {
                    payloads.addPayload((partInfo.getHref() != null) ? partInfo.getHref().toString() : "",
                                        (partInfo.getDescription() != null) ?
                                        partInfo.getDescription().getNonEmptyString() : "");
                }
            }

            msgInfoSet.setPayloads(payloads);
        } else {
            msgInfoSet.setPayloads(new Payloads());
        }

        msgInfoSet.setLegNumber(1);

        return msgInfoSet;
    }

    /**
     * Convert message list to list pending messages response.
     *
     * @param messages the messages
     * @return the list pending messages response
     */
    public static ListPendingMessagesResponse convertMessageListToListPendingMessagesResponse(
            final List<Message> messages) {
        final ListPendingMessagesResponse listPendingMessagesResponse = new ListPendingMessagesResponse();

        if (messages != null) {
            final String[] ids = new String[messages.size()];

            for (int i = 0; i < messages.size(); i++) {
                ids[i] = Integer.toString(messages.get(i).getIdMessage());
            }

            listPendingMessagesResponse.setMessageID(ids);
        }

        return listPendingMessagesResponse;
    }

    /**
     * Convert file to messaging e.
     *
     * @param messageFile the message file
     * @return the org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704. messaging e
     * @throws DownloadMessageServiceException
     *          the download message service exception
     */
    public static MessagingE convertFileToMessagingE(final File messageFile) throws DownloadMessageServiceException {
        if (messageFile == null || !messageFile.exists()) {
            log.error("Error loading message file");

            final DownloadMessageServiceException downloadMessageServiceException =
                    new DownloadMessageServiceException("Error loading message file", Code.ERROR_DOWNLOAD_003);
            throw downloadMessageServiceException;
        }

        XMLStreamReader xmlReader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(messageFile);

            xmlReader = StAXUtils.createXMLStreamReader(fileInputStream);

            return MessagingE.Factory.parse(xmlReader);
        } catch (Exception e) {
            log.error("Error loading message file", e);

            final DownloadMessageServiceException downloadMessageServiceException =
                    new DownloadMessageServiceException("Error loading message file", Code.ERROR_DOWNLOAD_003);
            throw downloadMessageServiceException;
        } finally {
            if (xmlReader != null) {
                try {
                    xmlReader.close();
                } catch (XMLStreamException e) {
                    log.warn("Error closing message file");
                }
            }
            if (fileInputStream != null) {
                IOUtils.closeQuietly(fileInputStream);
            }
        }
    }

    /**
     * Convert user message to msg info set.
     *
     * @param userMessage the user message
     * @return the eu.domibus.ebms3.submit. msg info set
     */
    public static MessageInfo convertUserMessageToMessageInfo(final UserMessage userMessage, final String messageId,
                                                              final String service, final String action,
                                                              final String status) {
        final MessageInfo messageInfo = new MessageInfo();
        if (userMessage.getCollaborationInfo() != null) {
            if (userMessage.getCollaborationInfo().getAgreementRef() != null &&
                userMessage.getCollaborationInfo().getAgreementRef().getPmode() != null) {
                messageInfo
                        .setPmode(userMessage.getCollaborationInfo().getAgreementRef().getPmode().getNonEmptyString());
            }
            if (userMessage.getCollaborationInfo().getConversationId() != null) {
                messageInfo.setConversationId(userMessage.getCollaborationInfo().getConversationId().toString());
            }
        }
        if (userMessage.getPartyInfo() != null) {
            if (userMessage.getPartyInfo().getFrom() != null) {
                if (userMessage.getPartyInfo().getFrom().getRole() != null) {
                    messageInfo.setFromRole(userMessage.getPartyInfo().getFrom().getRole().getNonEmptyString());
                }
                if (userMessage.getPartyInfo().getFrom().getPartyId() != null) {
                    String sender = null;

                    for (final PartyId partyId : userMessage.getPartyInfo().getFrom().getPartyId()) {
                        final Party party = new Party();
                        party.setPartyId(partyId.getNonEmptyString());
                        if (partyId.getType() != null) {
                            party.setType(partyId.getType().getNonEmptyString());
                        }

                        if (sender == null) {
                            sender = "";
                        } else {
                            sender += "\n";
                        }

                        if (party.getPartyId() != null) {
                            sender += party.getPartyId();
                        }
                    }

                    messageInfo.setSender(sender);
                }
            }

            if (userMessage.getPartyInfo().getTo() != null) {
                if (userMessage.getPartyInfo().getTo().getRole() != null) {
                    messageInfo.setToRole(userMessage.getPartyInfo().getTo().getRole().getNonEmptyString());
                }
                if (userMessage.getPartyInfo().getTo().getPartyId() != null) {
                    String recipient = null;

                    for (final PartyId partyId : userMessage.getPartyInfo().getTo().getPartyId()) {
                        final Party party = new Party();
                        party.setPartyId(partyId.getNonEmptyString());
                        if (partyId.getType() != null) {
                            party.setType(partyId.getType().getNonEmptyString());
                        }

                        if (recipient == null) {
                            recipient = "";
                        } else {
                            recipient += "\n";
                        }

                        if (party.getPartyId() != null) {
                            recipient += party.getPartyId();
                        }
                    }

                    messageInfo.setRecipient(recipient);
                }
            }
        }

        messageInfo.setMessageId(messageId);
        messageInfo.setService(service);
        messageInfo.setAction(action);
        messageInfo.setStatus(status);

        return messageInfo;
    }
}