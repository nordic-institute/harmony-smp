package eu.domibus.ebms3.packaging;

import eu.domibus.common.util.FileUtil;
import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.PartProperties;
import eu.domibus.ebms3.persistent.Property;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.context.MessageContext;

import java.util.*;

/**
 * @author Hamid Ben Malek
 */
public class PackagingFactory {
    private static final ThreadLocal<String> messageIDHolder = new ThreadLocal<String>();
    private static final ThreadLocal<String> endpointAddressHolder = new ThreadLocal<String>();

    public static String getCurrentEndpointAddress() {
        return PackagingFactory.endpointAddressHolder.get();
    }

    public static void setCurrentEndpointAddress(final String endpointAddress) {
        PackagingFactory.endpointAddressHolder.set(endpointAddress);
    }

    public static String getCurrentMessageID() {
        return PackagingFactory.messageIDHolder.get();
    }

    public static synchronized Messaging createMessagingElement(final MessageContext msgCtx) {
        if (msgCtx == null) {
            return null;
        }
        final MsgInfoSet mis = (MsgInfoSet) msgCtx.getProperty(Constants.MESSAGE_INFO_SET);
        if (mis == null) {
            return null;
        }
        final String pmodeName = mis.getPmode();
        final PMode pmode = Configuration.getPMode(pmodeName);
        if (pmode == null) {
            return null;
        }
        final Leg fLeg = Configuration.getLeg(mis);
        if (fLeg == null) {
            return null;
        }

        // store the pmode and and leg in the message context so that when a response
        // is received on the back channel, we could determine the toURL of the
        // AS4 receipt that will be generated for that response.
        final String m = pmode.getMep();
        if ((m != null) && (fLeg.getEndpoint() != null) &&
            (m.equalsIgnoreCase(Constants.ONE_WAY_PULL) || m.equalsIgnoreCase(Constants.TWO_WAY_SYNC))) {
            final String toURL = ((endpointAddressHolder.get() != null) && !"".equals(endpointAddressHolder.get())) ?
                                 PackagingFactory.endpointAddressHolder.get() : fLeg.getEndpoint().getAddress();
            msgCtx.setProperty(Constants.RECEIPT_TO, toURL);
        }

        final SOAPFactory factory = (SOAPFactory) msgCtx.getEnvelope().getOMFactory();
        final UserService us = fLeg.getUserService();
        Producer producer = fLeg.getProducer();
        if ((producer == null) || (producer.getParties() == null) ||
            producer.getParties().isEmpty()) {
            producer = mis.getProducer();
        }
        if ((producer != null) && (producer.getParties() != null) &&
            !producer.getParties().isEmpty() && (mis.getProducer() == null)) {
            for (final Party p : producer.getParties()) {
                mis.addFromParty(p);
            }
            mis.setFromRole(producer.getRole());
        }
        if (us != null) {
            final UserMessage userMessage =
                    PackagingFactory.createUserMessage(fLeg.getMpc(), mis, us, msgCtx.getAttachmentMap());

            final OMElement messageInfoOMElement = userMessage.getFirstGrandChildWithName(Constants.MESSAGE_INFO);
            if ((messageInfoOMElement != null) &&
                messageInfoOMElement.getChildrenWithLocalName(Constants.MESSAGE_ID).hasNext()) {
                final Iterator<OMElement> iterator = messageInfoOMElement.getChildren();

                OMElement messageIDOMElement = null;

                while (iterator.hasNext()) {
                    final OMElement omElement = iterator.next();

                    if (omElement.getLocalName().equalsIgnoreCase(Constants.MESSAGE_ID)) {
                        messageIDOMElement = omElement;
                    }
                }
                if (messageIDOMElement != null) {
                    final String messageID = messageIDOMElement.getText();

                    PackagingFactory.messageIDHolder.set(messageID);
                }
            }

            return new Messaging(factory, null, userMessage);
        }
        final String mep = Configuration.getMep(mis);
        if (((fLeg.getNumber() == 1) && mep.equalsIgnoreCase(Constants.ONE_WAY_PULL)) ||
            ((fLeg.getNumber() == 2) && mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PULL)) ||
            ((fLeg.getNumber() == 1) && mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_PUSH)) ||
            ((fLeg.getNumber() == 3) && mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_Pull)) ||
            ((fLeg.getNumber() == 1) && mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_Pull))) {
            // construct a pull request signal and put it inside an eb:Messaging
            final SignalMessage pullRequestSig = new SignalMessage(fLeg.getMpc());
            return new Messaging(factory, pullRequestSig, null);
        }
        // construct an anonymous UserMessage ...
        final UserMessage u =
                PackagingFactory.createAnonymousUserMessage(fLeg.getMpc(), mis, msgCtx.getAttachmentMap());

        final OMElement messageInfoOMElement = u.getFirstGrandChildWithName(Constants.MESSAGE_INFO);
        if ((messageInfoOMElement != null) &&
            messageInfoOMElement.getChildrenWithLocalName(Constants.MESSAGE_ID).hasNext()) {
            final Iterator<OMElement> iterator = messageInfoOMElement.getChildren();

            OMElement messageIDOMElement = null;

            while (iterator.hasNext()) {
                final OMElement omElement = iterator.next();

                if (omElement.getLocalName().equalsIgnoreCase(Constants.MESSAGE_ID)) {
                    messageIDOMElement = omElement;
                }
            }
            if (messageIDOMElement != null) {
                final String messageID = messageIDOMElement.getText();

                PackagingFactory.messageIDHolder.set(messageID);
            }
        }

        return new Messaging(factory, null, u);
    }

    public static UserMessage createRespUserMessage(final MsgInfo reqMsgInfo, final Producer producer, UserService us,
                                                    final Attachments att) {
        if (reqMsgInfo == null) {
            return null;
        }
        final MsgInfoSet mis =
                new MsgInfoSet(reqMsgInfo.getAgreementRef(), reqMsgInfo.getPmode(), reqMsgInfo.getConversationId(),
                               reqMsgInfo.getMessageId());
        if ((producer != null) && (producer.getParties() != null) &&
            !producer.getParties().isEmpty()) {
            for (final Party p : producer.getParties()) {
                mis.addFromParty(p);
            }
            mis.setFromRole(producer.getRole());
        } else {
            final Collection<Party> fromParties = reqMsgInfo.getToParties();
            for (final Party p : fromParties) {
                mis.addFromParty(p);
            }
            mis.setFromRole(reqMsgInfo.getToRole());
        }
        if (us == null) {
            final eu.domibus.ebms3.config.CollaborationInfo ci = new eu.domibus.ebms3.config.CollaborationInfo();
            ci.setService(new Service(null, reqMsgInfo.getService()));
            ci.setAction(reqMsgInfo.getAction());
            us = new UserService();
            us.setCollaborationInfo(ci);
            final Collection<Party> toP = reqMsgInfo.getFromParties();
            for (final Party p : toP) {
                us.addToParty(p);
            }
            us.getToParty().setRole(reqMsgInfo.getFromRole());
        }
        return PackagingFactory.createUserMessage(reqMsgInfo.getMpc(), mis, us, att);
    }

    public static UserMessage createUserMessage(final String mpc, final String refToMsgId, final String pmode,
                                                final UserService us, final Attachments att) {
        final MsgInfoSet mis = new MsgInfoSet(null, pmode, null, refToMsgId);
        mis.addFromParty(null, "Anonymous");
        return PackagingFactory.createUserMessage(mpc, mis, us, att);
    }

    public static UserMessage createUserMessage(final String mpc, final String refToMsgId, final Producer producer,
                                                final String pmode, final UserService us, final Attachments att) {
        final MsgInfoSet mis = new MsgInfoSet(null, pmode, null, refToMsgId);
        if (producer != null) {
            for (final Party p : producer.getParties()) {
                mis.addFromParty(p.getType(), p.getPartyId());
            }
            mis.setFromRole(producer.getRole());
        }
        return PackagingFactory.createUserMessage(mpc, mis, us, att);
    }

    public static SignalMessage createReceipt(final String refToMessageId, final OMElement[] references) {
        final MessageInfo mi = new MessageInfo(null, refToMessageId);
        final Receipt receipt = new Receipt(references);
        //return new SignalMessage(mi, null, receipt, null);
        return new SignalMessage(mi, null, receipt.getElement(), null);
    }

    public static SignalMessage createReceipt(final String refToMessageId, final OMElement nonRepudiationInfo) {
        final MessageInfo mi = new MessageInfo(null, refToMessageId);
        final Receipt receipt = new Receipt(nonRepudiationInfo);
        //return new SignalMessage(mi, null, receipt, null);
        return new SignalMessage(mi, null, receipt.getElement(), null);
    }

    public static SignalMessage createReceipt(final Date timestamp, final String refToMessageId,
                                              final OMElement nonRepudiationInfo) {
        final MessageInfo mi = new MessageInfo(timestamp, null, refToMessageId);
        final Receipt receipt = new Receipt(nonRepudiationInfo);
        //return new SignalMessage(mi, null, receipt, null);
        return new SignalMessage(mi, null, receipt.getElement(), null);
    }

    private static UserMessage createUserMessage(final String mpc, final MsgInfoSet mis, final UserService us,
                                                 final Attachments att) {
        final MessageInfo mi = new MessageInfo(mis.getMessageId(), mis.getRefToMessageId());
        final PartyInfo pi = new PartyInfo();
        for (final Party p : mis.getFromParties()) {
            pi.addFromParty(p.getPartyId(), p.getType(), null);
        }
        pi.setFromRole(mis.getFromRole());
        for (final Party p : us.getToParty().getParties()) {
            pi.addToParty(p.getPartyId(), p.getType(), null);
        }
        pi.setToRole(us.getToParty().getRole());
        final eu.domibus.ebms3.packaging.CollaborationInfo ci =
                new eu.domibus.ebms3.packaging.CollaborationInfo(mis.getAgreementRef(), mis.getPmode(),
                                                                 us.getCollaborationInfo().getService().getValue(),
                                                                 us.getCollaborationInfo().getService().getType(),
                                                                 us.getCollaborationInfo().getAction(),
                                                                 mis.getConversationId());
        eu.domibus.ebms3.packaging.MessageProperties mp = null;
        if (mis.getPropertiesMap() != null) {
            final Set<String> keys = mis.getPropertiesMap().keySet();
            if ((keys != null) && !keys.isEmpty()) {
                final Iterator<String> it = keys.iterator();
                mp = new eu.domibus.ebms3.packaging.MessageProperties();
                while (it != null && it.hasNext()) {
                    final String key = it.next();
                    mp.addProperty(key, mis.getProperty(key));
                }
            }
        }
        eu.domibus.ebms3.packaging.PayloadInfo payloadInfo = null;
        if (att != null) {
            //String spId = att.getSOAPPartContentID();
            final String[] cids = mis.getCids();
            //att.getAllContentIDs();
            //      if ( cids != null && cids.length > 0 )
            //      {
            //payloadInfo = createPayloadInfo(cids, spId, mis);
            payloadInfo = createPayloadInfo(cids, mis);
            //new eu.domibus.ebms3.packaging.PayloadInfo(cids,
            //                                             att.getSOAPPartContentID(),
            //                                             mis.hasBodyPayload());
            //      }
        }

        return new UserMessage(mpc, mi, pi, ci, mp, payloadInfo);
    }

    private static PayloadInfo createPayloadInfo(final String[] cids, final MsgInfoSet mis) {
        if (mis == null) {
            return null;
        }
        final PayloadInfo pi = new PayloadInfo();
        if (mis.hasBodyPayload()) {
            final PartProperties properties = mis.getBodyPayloadPartProperties();
            final List<String> propertyNames = new ArrayList<String>();
            final List<String> propertyValues = new ArrayList<String>();

            if (properties != null) {
                for (final eu.domibus.ebms3.persistent.Property property : properties.getPartPropertiesArray()) {
                    propertyNames.add(property.getName());
                    propertyValues.add(property.getValue());
                }
            }

            pi.addPartInfo(mis.getBodyPayloadCID(), mis.getBodyPayloadSchemaLocation(), mis.getBodyPayloadDescription(),
                           propertyNames.toArray(new String[propertyNames.size()]),
                           propertyValues.toArray(new String[propertyValues.size()]));
        }
        if ((cids != null) && (cids.length > 0)) {
            for (final String cid : cids) {
                final String payloadFile = mis.getPayload(cid);
                final boolean compressed = mis.isCompressed(payloadFile);

                final PartProperties properties = mis.getPartProperties(payloadFile);

                if (compressed) {
                    final String ct = FileUtil.mimeType(payloadFile);
                    final List<String> propertyNames = new ArrayList<String>();
                    final List<String> propertyValues = new ArrayList<String>();

                    propertyNames.add("MimeType");
                    propertyNames.add("Compressed");

                    propertyNames.add(ct);
                    propertyNames.add(null);

                    if (properties != null) {
                        for (final Property property : properties.getPartPropertiesArray()) {
                            propertyNames.add(property.getName());
                            propertyValues.add(property.getValue());
                        }
                    }

                    pi.addPartInfo(cid, mis.getSchemaLocation(payloadFile), mis.getDescription(payloadFile),
                                   propertyNames.toArray(new String[propertyNames.size()]),
                                   propertyValues.toArray(new String[propertyValues.size()]));
                } else {
                    final List<String> propertyNames = new ArrayList<String>();
                    final List<String> propertyValues = new ArrayList<String>();

                    if (properties != null) {
                        for (final Property property : properties.getPartPropertiesArray()) {
                            propertyNames.add(property.getName());
                            propertyValues.add(property.getValue());
                        }
                    }

                    pi.addPartInfo(cid, mis.getSchemaLocation(payloadFile), mis.getDescription(payloadFile),
                                   propertyNames.toArray(new String[propertyNames.size()]),
                                   propertyValues.toArray(new String[propertyValues.size()]));
                }
            }
        }
        return pi;
    }

    private static UserMessage createAnonymousUserMessage(final String mpc, final MsgInfoSet mis,
                                                          final Attachments att) {
        final MessageInfo mi = new MessageInfo(null, mis.getRefToMessageId());
        final PartyInfo pi = new PartyInfo();
        for (final Party p : mis.getFromParties()) {
            pi.addFromParty(p.getPartyId(), p.getType(), null);
        }
        pi.setFromRole(mis.getFromRole());

        pi.addToParty("Anonymous", null, null);

        final eu.domibus.ebms3.packaging.CollaborationInfo ci =
                new eu.domibus.ebms3.packaging.CollaborationInfo(mis.getAgreementRef(), mis.getPmode(), "Anonymous",
                                                                 null, "Anonymous", mis.getConversationId());
        eu.domibus.ebms3.packaging.MessageProperties mp = null;
        if (mis.getPropertiesMap() != null) {
            final Set<String> keys = mis.getPropertiesMap().keySet();
            if ((keys != null) && !keys.isEmpty()) {
                final Iterator<String> it = keys.iterator();
                mp = new eu.domibus.ebms3.packaging.MessageProperties();
                while (it != null && it.hasNext()) {
                    final String key = it.next();
                    mp.addProperty(key, mis.getProperty(key));
                }
            }
        }
        eu.domibus.ebms3.packaging.PayloadInfo payloadInfo = null;
        if (att != null) {
            final String[] cids = att.getAllContentIDs();
            if (cids != null && cids.length > 0) {
                payloadInfo = new eu.domibus.ebms3.packaging.PayloadInfo(cids, att.getRootPartContentID(),
                                                                         mis.hasBodyPayload());
            }
        }

        return new UserMessage(mpc, mi, pi, ci, mp, payloadInfo);
    }
}