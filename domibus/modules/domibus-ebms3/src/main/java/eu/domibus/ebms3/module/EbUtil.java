package eu.domibus.ebms3.module;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;
import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.WSUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.persistent.PartInfo;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;
import eu.domibus.ebms3.persistent.ReceivedUserMsgDAO;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 * @author Hamid Ben Malek
 */
public class EbUtil {

    private static final Logger log = Logger.getLogger(EbUtil.class);

    private static final ReceivedUserMsgDAO rum = new ReceivedUserMsgDAO();

    // Tries to figure out the MEP from a given message context while being
    // on the service side handling a request:
    public static String getMep(final MessageContext requestMsgCtx) {
        if (requestMsgCtx == null) {
            return null;
        }
        final SOAPHeader header = requestMsgCtx.getEnvelope().getHeader();
        final OMElement pullReq = XMLUtil.getGrandChildNameNS(header, Constants.PULL_REQUEST, Constants.NS);
        if (pullReq != null) {
            final String mpc = XMLUtil.getAttributeValue(pullReq, "mpc");
            final String address = requestMsgCtx.getTo().getAddress();
            final PMode pmode = Configuration.matchPMode(mpc, address);
            if (pmode != null) {
                return Configuration.getMep(pmode.getName());
            } else {
                return Constants.ONE_WAY_PULL;
            }
        } else {
            final OMElement userMessage = XMLUtil.getGrandChildNameNS(header, Constants.USER_MESSAGE, Constants.NS);
            if (userMessage == null) {
                return null;
            }
            final String pmode = XMLUtil.getGrandChildAttributeValue(userMessage, Constants.AGREEMENT_REF, "pmode");
            if (pmode == null) {
                final MsgInfo mi = (MsgInfo) requestMsgCtx.getProperty(Constants.IN_MSG_INFO);
                final String address = (String) WSUtil.getPropertyFromInMsgCtx(requestMsgCtx, Constants.TO_ADDRESS);
                final PMode pm = Configuration.match(mi, address);
                if (pm == null) {
                    return Constants.ONE_WAY_PUSH;
                } else {
                    return Configuration.getMep(pm.getName());
                }

            } else {
                return Configuration.getMep(pmode);
            }

        }
    }

    public static synchronized MsgInfo getMsgInfo() {
        final MessageContext ctx = MessageContext.getCurrentMessageContext();
        if (ctx == null) {
            throw new NullPointerException(
                    "This thread does not have a MessageContext attached. Please use the createMsgInfo(MessageContext msgCtx) method.");
        }
        return getMsgInfo(ctx);
    }

    public static synchronized MsgInfo getMsgInfo(final MessageContext ctx) {
        MsgInfo info = (MsgInfo) ctx.getProperty(Constants.IN_MSG_INFO);
        if (info == null) {
            info = EbUtil.createMsgInfo(ctx);
            ctx.setProperty(Constants.IN_MSG_INFO, info);
        }
        return info;
    }

    /**
     * Utility function to read all the available meta-data from the currently
     * processed UserMessage.
     * <p/>
     * It reads.
     * <p/>
     * <p/>
     * NOTE:
     * This function SHOULD only be used in the <i>InFlow</i>. Only the meta-data
     * from the message are read, the function does not retrieve the P-Mode
     * associated with the message!
     *
     * @param msgCtx The current {@link MessageContext}
     * @return MsgInfo     The meta-data read from the messsage or <code>null</code>
     *         when no data could be read
     */
    private static MsgInfo createMsgInfo(final MessageContext msgCtx) {
        if (msgCtx == null) {
            return null;
        }

        final SOAPHeader header = msgCtx.getEnvelope().getHeader();
        if (header == null) {
            return null;
        }

        final OMElement mess = XMLUtil.getGrandChildNameNS(header, Constants.MESSAGING, Constants.NS);
        if (mess == null) {
            return null;
        }

        MsgInfo msgInfo = null;

        final OMElement userMessage = EbUtil.getUserMessage(msgCtx);
        if (userMessage != null) {
            // This is a UserMessage, so meta data is available
            msgInfo = new MsgInfo();

            // Read information from the UserMessage and its child elements
            //
            msgInfo.setMpc(XMLUtil.getAttributeValue(userMessage, Constants.MPC));
            // MessageInfo element
            //
            msgInfo.setMessageId(XMLUtil.getGrandChildValue(userMessage, Constants.MESSAGE_ID));
            msgInfo.setRefToMessageId(XMLUtil.getGrandChildValue(userMessage, Constants.REF_TO_MESSAGE_ID));
            // PartyInfo/From element
            //
            final OMElement from = XMLUtil.getGrandChildNameNS(userMessage, Constants.FROM, Constants.NS);

            for (final Iterator it = from.getChildElements(); it != null && it.hasNext(); ) {
                final OMElement e = (OMElement) it.next();
                if (e.getLocalName().equals(Constants.PARTY_ID)) {
                    String partyTmp = e.getText();
                    msgInfo.addFromParty(XMLUtil.getAttributeValue(e, Constants.PARTY_ID_TYPE), ((partyTmp != null &&
                                                                                                  partyTmp.startsWith(
                                                                                                          eu.domibus.ebms3.module.Constants.ECODEX_PARTY_ID_URI_VALUE)) ?
                                                                                                 partyTmp.substring(
                                                                                                         eu.domibus.ebms3
                                                                                                                 .module
                                                                                                                 .Constants
                                                                                                                 .ECODEX_PARTY_ID_URI_VALUE
                                                                                                                 .length()) :
                                                                                                 partyTmp));
                } else if (e.getLocalName().equals(Constants.ROLE)) {
                    msgInfo.setFromRole(e.getText());
                }
            }
            // PartyInfo/To element
            //
            final OMElement to = XMLUtil.getGrandChildNameNS(userMessage, Constants.TO, Constants.NS);
            for (final Iterator it = to.getChildElements(); it != null && it.hasNext(); ) {
                final OMElement e = (OMElement) it.next();
                if (e.getLocalName().equals(Constants.PARTY_ID)) {
                    String partyTmp = e.getText();
                    msgInfo.addToParty(XMLUtil.getAttributeValue(e, Constants.PARTY_ID_TYPE), ((partyTmp != null &&
                                                                                                partyTmp.startsWith(
                                                                                                        eu.domibus.ebms3.module.Constants.ECODEX_PARTY_ID_URI_VALUE)) ?
                                                                                               partyTmp.substring(
                                                                                                       eu.domibus.ebms3
                                                                                                               .module
                                                                                                               .Constants
                                                                                                               .ECODEX_PARTY_ID_URI_VALUE
                                                                                                               .length()) :
                                                                                               partyTmp));
                } else if (e.getLocalName().equals(Constants.ROLE)) {
                    msgInfo.setToRole(e.getText());
                }
            }
            // CollaborationInfo/AgreementRef element
            //
            msgInfo.setAgreementRef(XMLUtil.getGrandChildValue(userMessage, Constants.AGREEMENT_REF));
            // @todo: Also use the type indicator of the agreement (needs update MsgInfo class)
            //          String type =
            //                  Util.getGrandChildAttributeValue(userMessage, Constants.AGREEMENT_REF,
            //                  Constants.AGREEMENT_REF_TYPE);
            msgInfo.setPmode(XMLUtil.getGrandChildAttributeValue(userMessage, Constants.AGREEMENT_REF,
                                                                 Constants.AGREEMENT_REF_PMODE));
            // CollaborationInfo/Service element
            //
            String serviceTmp = XMLUtil.getGrandChildValue(userMessage, Constants.SERVICE);
            msgInfo.setService(((serviceTmp != null &&
                                 serviceTmp.startsWith(eu.domibus.ebms3.module.Constants.ECODEX_SERVICE_URI_VALUE)) ?
                                serviceTmp.substring(
                                        eu.domibus.ebms3.module.Constants.ECODEX_SERVICE_URI_VALUE.length()) :
                                serviceTmp));
            // @todo: Also use the type indicator of the service (needs update MsgInfo class)
            //          String type =
            //                  Util.getGrandChildAttributeValue(userMessage, Constants.SERVICE,
            //                  Constants.SERVICE_TYPE);
            // CollaborationInfo/Action element
            //
            msgInfo.setAction(XMLUtil.getGrandChildValue(userMessage, Constants.ACTION));
            // CollaborationInfo/ConversationId element
            //
            msgInfo.setConversationId(XMLUtil.getGrandChildValue(userMessage, Constants.CONVERSATION_ID));
            // MessageProperties/Property elements
            //
            final OMElement msgProps =
                    XMLUtil.getGrandChildNameNS(userMessage, Constants.MESSAGE_PROPERTIES, Constants.NS);
            if (msgProps != null) {
                for (final Iterator it = msgProps.getChildElements(); it != null && it.hasNext(); ) {
                    final OMElement e = (OMElement) it.next();
                    msgInfo.addMessageProperty(XMLUtil.getAttributeValue(e, Constants.PROPERTY_NAME), e.getText());
                }
            }
            // EbmsPayload/PartInfo elements
            //
            final OMElement payloadInfo =
                    XMLUtil.getGrandChildNameNS(userMessage, Constants.PAYLOAD_INFO, Constants.NS);
            if (payloadInfo != null) {
                for (final Iterator it = payloadInfo.getChildElements(); it != null && it.hasNext(); ) {
                    final OMElement e = (OMElement) it.next();
                    final String href = XMLUtil.getAttributeValue(e, Constants.PART_INFO_HREF);
                    // Schema element
                    //
                    //@todo: Add support for attributes version and namespace
                    final String schemaLocation = XMLUtil.getGrandChildAttributeValue(e, Constants.PART_INFO_SCHEMA,
                                                                                      Constants.PART_INFO_SCHEMA_LOCATION);
                    final String desc = XMLUtil.getGrandChildValue(e, Constants.PART_INFO_DESCR);
                    final PartInfo pi = msgInfo.addPartInfo(href, schemaLocation, desc);
                    // PartProperties/Property elements
                    //
                    // @todo: Support all types of part properties
                    final List<OMElement> props = XMLUtil.getGrandChildrenName(e, Constants.PROPERTY);
                    if (props != null && props.size() > 0) {
                        for (final OMElement p : props) {
                            if (XMLUtil.getAttributeValue(p, Constants.PROPERTY_NAME) != null &&
                                XMLUtil.getAttributeValue(p, "name").equals("MimeType")) {
                                pi.setMimeType(p.getText());
                            } else if (XMLUtil.getAttributeValue(p, Constants.PROPERTY_NAME) != null &&
                                       XMLUtil.getAttributeValue(p, "name").equals("Compressed")) {
                                //                              pi.setCompressed(true);
                            } else {
                                pi.addProperty(XMLUtil.getAttributeValue(p, "name"), p.getText());
                            }
                        }
                    }
                }
            }
        } else {
            // @todo: Remove processing of the PullRequest signal (might effect other handlers)
            msgInfo = new MsgInfo();
            final OMElement signalMessage = XMLUtil.getGrandChildNameNS(header, Constants.SIGNAL_MESSAGE, Constants.NS);
            if (signalMessage != null) {
                final String mpc = XMLUtil.getGrandChildAttributeValue(signalMessage, Constants.PULL_REQUEST, "mpc");
                msgInfo.setMpc(mpc);
            }

        }

        return msgInfo;
    }

    public static String[] parseModules(final String modules) {
        final String[] mod;
        if (modules != null && !modules.trim().equals("")) {
            final StringTokenizer st = new StringTokenizer(modules, ",");
            mod = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                mod[i] = st.nextToken().trim();
                i++;
            }
        } else {
            mod = new String[]{"domibus-ebms3"};
        }
        return mod;
    }

    public static boolean isLocal(final String mshURL, final ConfigurationContext configCtx) {
        if (mshURL == null) {
            return false;
        }
        String host = null;
        log.debug("mshURL=" + mshURL);
        if (mshURL.startsWith("http")) {
            try {
                //                host = mshURL.substring(8, mshURL.indexOf("/"));
                final URL url = new URL(mshURL);
                host = url.getHost();
                log.debug("========= host is: " + host);
            } catch (MalformedURLException mue) {
                log.error("Problem while parsing msh URL: " + mshURL + "Maybe an unknown protocol was used", mue);
            }
        } else if (mshURL.indexOf("@") > 0) {
            host = mshURL.substring(mshURL.indexOf("@") + 1);
        }
        if (host != null && host.equals("localhost")) {
            return true;
        }

        try {
            final InetAddress addr = InetAddress.getLocalHost();
            final String hostname = addr.getHostName();
            if (host != null && host.equalsIgnoreCase(hostname)) {
                return true;
            }
            final byte[] ipAddr = addr.getAddress();
            String ipAddrStr = "";
            for (int i = 0; i < ipAddr.length; i++) {
                if (i > 0) {
                    ipAddrStr += ".";
                }
                ipAddrStr += ipAddr[i] & 0xFF;
            }
            if (host != null && host.equals(ipAddrStr)) {
                return true;
            }

        } catch (final UnknownHostException e) {
            log.error("Cannot get localhost address", e);
        }
        if (configCtx == null) {
            return false;
        }
        String localNames = null;
        if (configCtx.getAxisConfiguration().getParameter("LOCAL_MACHINE") != null) {
            localNames = (String) configCtx.getAxisConfiguration().getParameter("LOCAL_MACHINE").getValue();
            if (localNames != null) {
                final StringTokenizer st = new StringTokenizer(localNames, ",");
                while (st.hasMoreTokens()) {
                    final String token = st.nextToken().trim();
                    if (host != null && host.equalsIgnoreCase(token)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * Get the <code>/soapenv:Envelope/soapenv:Header/eb:Messaging</code>
     * element.
     *
     * @param msgCtx message context of the SOAP message
     * @return the Messaging element or {@code null} if there is none.
     */
    public static OMElement getMessaging(final MessageContext msgCtx) {
        if (msgCtx == null) {
            return null;
        }

        final SOAPEnvelope envelope = msgCtx.getEnvelope();
        if (envelope == null) {
            return null;
        }

        final SOAPHeader header = envelope.getHeader();
        if (header == null) {
            return null;
        }

        // Find the eb:Messaging header as a child element of the soapenv:Header element.
        final OMElement messaging = XMLUtil.getFirstChildWithNameNS(header, Constants.MESSAGING, Constants.NS);
        return messaging;
    }

    /**
     * Get the <code>/soapenv:Envelope/soapenv:Header/eb:Messaging/eb:UserMessage</code>
     * element.
     *
     * @param msgCtx message context of the SOAP message
     * @return the UserMessage element or {@code null} if there is none.
     */
    public static OMElement getUserMessage(final MessageContext msgCtx) {
        final OMElement messaging = getMessaging(msgCtx);
        if (messaging == null) {
            return null;
        }

        // Find the eb:UserMessage header as a child element of the eb:Messaging element.
        final OMElement userMessage = XMLUtil.getFirstChildWithNameNS(messaging, Constants.USER_MESSAGE, Constants.NS);
        return userMessage;
    }

    /**
     * Get the <code>/soapenv:Envelope/soapenv:Header/eb:Messaging/eb:SignalMessage</code>
     * element.
     *
     * @param msgCtx message context of the SOAP message
     * @return the SignalMessage element or {@code null} if there is none.
     */
    public static OMElement getSignalMessage(final MessageContext msgCtx) {
        final OMElement messaging = getMessaging(msgCtx);
        if (messaging == null) {
            return null;
        }

        // Find the eb:SignalMessage header as a child element of the eb:Messaging element.
        final OMElement signalMessage =
                XMLUtil.getFirstChildWithNameNS(messaging, Constants.SIGNAL_MESSAGE, Constants.NS);
        return signalMessage;
    }

    /**
     * Checks if the currently processed message contains an ebMS UserMessage.
     * An ebMS message contains a UserMessage if there is an element
     * <code>/soapenv:Envelope/soapenv:Header/eb:Messaging/eb:UserMessage</code>.
     * To check whether there exists an element <code>//eb:UserMessage</code> is
     * wrong since a signal message may contain an receipt that itself contains
     * the copy of a user message.  Such a signal message is no user message.
     *
     * @param msgCtx The message context of the processed message
     * @return true when this message is an ebMS UserMessage
     *         false otherwise
     */
    public static boolean isUserMessage(final MessageContext msgCtx) {
        return getUserMessage(msgCtx) != null;
    }

    /**
     * Get the ID of the UserMessage.
     *
     * @param msgCtx of a message containing a UserMessage.
     * @return the ID of the UserMessage or {@code null} if the message does not contain a UserMessage.
     */
    public static String getUserMessageId(final MessageContext msgCtx) {
        final OMElement userMessage = getUserMessage(msgCtx);
        if (userMessage == null) {
            return null;
        }

        final OMElement messageInfo =
                XMLUtil.getFirstChildWithNameNS(userMessage, Constants.MESSAGE_INFO, Constants.NS);
        if (messageInfo == null) {
            return null;
        }

        final OMElement messageId = XMLUtil.getFirstChildWithNameNS(messageInfo, Constants.MESSAGE_ID, Constants.NS);
        if (messageId == null) {
            return null;
        }

        return messageId.getText();
    }

    /**
     * Get the PMode for a message from the database.
     *
     * @param messageId id of the user message
     * @return PMode for the message or {@code null} if there is none.
     */
    public static PMode getPModeForReceivedMessage(final String messageId) {
        final List<ReceivedUserMsg> messages = rum.findByMessageId(messageId);
        ReceivedUserMsg message = messages.get(0);
        return Configuration.getPModeO(message.getAction(), message.getService(), message.getFromParty(), null,
                                       message.getToParty(), null);
    }

    /**
     * Find any PMode that matches the given arguments in its first leg.
     * Returns {@code null} if there is no matching PMode.
     *
     * @param service         service identifier to match exactly
     * @param action          action identifier to match exactly
     * @param endpointAddress endpoint address to match exactly
     * @return the first matching recipient's identifier or {@code null} if there is no matching PMode
     * @throws ConfigurationException on any PMode configuration errors (e.g. missing binding)
     */
    public static PMode findPMode(final String service, final String action, final String endpointAddress)
            throws ConfigurationException {

        for (final Entry<String, PMode> entry : Constants.pmodes.entrySet()) {
            final PMode pMode = entry.getValue();
            if (pMode == null) {
                throw new ConfigurationException("Empty PMode " + entry.getKey());
            }

            final Binding binding = pMode.getBinding();
            if (binding == null) {
                throw new ConfigurationException("Missing binding for PMode " + pMode.getName());
            }

            final MEP mep = binding.getMep();
            if (mep == null) {
                throw new ConfigurationException("Missing MEP for binding " + binding.getName());
            }

            final List<Leg> legs = binding.getMep().getLegs();
            if (legs == null) {
                throw new ConfigurationException("Missing MEP legs in binding " + binding.getName());
            }

            Leg legOne = null;
            for (final Leg leg : legs) {
                if (leg.getNumber() == 1) {
                    legOne = leg;
                    break;
                }
            }
            if (legOne == null) {
                throw new ConfigurationException("Missing leg number=\"1\" of MEP in binding " + binding.getName());
            }

            final UserService userService = legOne.getUserService();
            if (userService == null) {
                throw new ConfigurationException(
                        "Missing user service of leg number=\"1\" of MEP in binding " + binding.getName());
            }

            final Producer producer = legOne.getProducer();
            if (producer == null) {
                throw new ConfigurationException(
                        "Missing producer of leg number=\"1\" of MEP in binding " + binding.getName());
            }

            final Collection<Party> producerParties = producer.getParties();
            if (producerParties == null) {
                throw new ConfigurationException("Missing parties for producer " + producer.getName());
            }

            final CollaborationInfo collaborationInfo = userService.getCollaborationInfo();
            if (collaborationInfo == null) {
                throw new ConfigurationException("Missing collaboration info in user service " + userService.getName());
            }

            final Service pModeService = collaborationInfo.getService();
            if (service == null) {
                throw new ConfigurationException(
                        "Missing service in collaboration info of user service " + userService.getName());
            }

            final String pModeServiceName = pModeService.getValue();
            if (pModeServiceName == null) {
                throw new ConfigurationException(
                        "Missing service name in collaboration info of user service " + userService.getName());
            }

            // Step 1: Check for matching service name

            if (!pModeServiceName.equals(service)) {
                continue;  // try next PMode
            }

            final String pModeActionName = collaborationInfo.getAction();
            if (pModeActionName == null) {
                throw new ConfigurationException(
                        "Missing action name in collaboration info of user service " + userService.getName());
            }

            // Step 2: Check for matching action name

            if (!pModeActionName.equals(action)) {
                continue;  // try next PMode
            }

            final Endpoint pModeEndpoint = legOne.getEndpoint();
            if (pModeEndpoint == null) {
                throw new ConfigurationException(
                        "Missing endpoint of leg number=\"1\" of MEP in binding " + binding.getName());
            }

            final String pModeEndpointAddress = pModeEndpoint.getAddress();
            if (pModeEndpointAddress == null) {
                throw new ConfigurationException(
                        "Missing endpoint address of leg number=\"1\" of MEP in binding " + binding.getName());
            }

            // Step 3: Check for matching endpoint address

            if (!pModeEndpointAddress.equals(endpointAddress)) {
                continue;  // try next PMode
            }

            return pMode;
        }

        return null;  // no error, just did not find any perfect match
    }
}