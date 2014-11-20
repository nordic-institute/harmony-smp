/*
 * 
 */
package eu.domibus.backend.util;


import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.config.Producer;
import eu.domibus.ebms3.packaging.PackagingFactory;
import eu.domibus.ebms3.persistent.Payloads;
import eu.domibus.ebms3.persistent.Properties;
import eu.domibus.ebms3.submit.MsgInfoSet;
import eu.eCODEX.submission.Constants;
import org.apache.log4j.Logger;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

import java.util.HashSet;
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
     * @return the org.holodeck.ebms3.submit. msg info set
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
        if ((userMessage.getMessageInfo() != null) && (userMessage.getMessageInfo().getRefToMessageId() != null)) {
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
        if ((userMessage.getMessageProperties() != null) &&
            (userMessage.getMessageProperties().getProperty() != null)) {
            final Properties properties = new Properties();
            for (final Property property : userMessage.getMessageProperties().getProperty()) {
                if (property.getName().getNonEmptyString()
                            .equalsIgnoreCase(Constants.ENDPOINT_ADDRESS_MESSAGE_PROPERTY)) {
                    PackagingFactory.setCurrentEndpointAddress(property.getNonEmptyString());
                } else {
                    properties.addProperty(property.getName().getNonEmptyString(), property.getNonEmptyString());
                }
            }

            msgInfoSet.setProperties(properties);
        }
        if ((userMessage.getPayloadInfo() != null) && (userMessage.getPayloadInfo().getPartInfo() != null)) {
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


}