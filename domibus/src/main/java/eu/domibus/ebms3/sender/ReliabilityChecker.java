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

package eu.domibus.ebms3.sender;

import eu.domibus.common.MSHRole;
import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.configuration.model.ReplyPattern;
import eu.domibus.common.dao.ErrorLogDao;
import eu.domibus.common.exception.EbMS3Exception;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.domibus.ebms3.common.NonRepudiationConstants;
import eu.domibus.ebms3.common.dao.PModeProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wss4j.dom.WSConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

/**
 * TODO: add class description
 */
@Service
public class ReliabilityChecker {
    private static final Log LOG = LogFactory.getLog(ReliabilityChecker.class);
    private static final String XPATH_EXPRESSION_STRING = "/*/*/*[local-name() = 'Reference']/@URI  | /*/*/*[local-name() = 'Reference']/*[local-name() = 'DigestValue']";
    private static final XPath xPath = XPathFactory.newInstance().newXPath();


    @Autowired
    @Qualifier("jaxbContextEBMS")
    JAXBContext jaxbContext;
    @Autowired
    private PModeProvider pModeProvider;
    @Autowired
    private ErrorLogDao errorLogDao;

    public boolean check(SOAPMessage request, SOAPMessage response, String pmodeKey) throws EbMS3Exception {

        LegConfiguration legConfiguration = this.pModeProvider.getLegConfiguration(pmodeKey);

        assert legConfiguration != null; // we would have crashed in the security handler
        if (legConfiguration.getReliability() != null && ReplyPattern.RESPONSE.equals(legConfiguration.getReliability().getReplyPattern())) {
            ReliabilityChecker.LOG.debug("Checking reliability for outgoing message");
            Messaging messaging;

            try {
                messaging = this.jaxbContext.createUnmarshaller().unmarshal((Node) response.getSOAPHeader().getChildElements(ObjectFactory._Messaging_QNAME).next(), Messaging.class).getValue();
            } catch (JAXBException | SOAPException e) {
                ReliabilityChecker.LOG.error(e.getMessage(), e);
                return false;
            }

            SignalMessage signalMessage = messaging.getSignalMessage();

            //ReceiptionAwareness or NRR found but not expected? report if configuration=true //TODO: make configurable in domibus.properties

            //SignalMessage with Receipt expected
            if (signalMessage.getReceipt() != null && signalMessage.getReceipt().getAny().size() == 1) {

                String contentOfReceiptString = signalMessage.getReceipt().getAny().get(0);

                try {
                    if (!legConfiguration.getReliability().isNonRepudiation()) {
                        UserMessage userMessage = this.jaxbContext.createUnmarshaller().unmarshal(new StreamSource(new ByteArrayInputStream(contentOfReceiptString.getBytes())), UserMessage.class).getValue();

                        UserMessage userMessageInRequest = this.jaxbContext.createUnmarshaller().unmarshal((Node) request.getSOAPHeader().getChildElements(ObjectFactory._Messaging_QNAME).next(), Messaging.class).getValue().getUserMessage();
                        return userMessage.equals(userMessageInRequest);
                    }

                    Iterator<Element> elementIterator = response.getSOAPHeader().getChildElements(new QName(WSConstants.WSSE_NS, WSConstants.WSSE_LN));

                    if (!elementIterator.hasNext()) {
                        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0302, "Invalid NonRepudiationInformation: No security header found", null, MSHRole.SENDING);
                    }
                    Element securityHeaderResponse = elementIterator.next();

                    if (elementIterator.hasNext()) {
                        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0302, "Invalid NonRepudiationInformation: Multiple security headers found", null, MSHRole.SENDING);
                    }

                    String wsuIdOfMEssagingElement = messaging.getOtherAttributes().get(new QName(WSConstants.WSU_NS, "Id"));

                    ReliabilityChecker.LOG.debug(wsuIdOfMEssagingElement);

                    NodeList nodeList = securityHeaderResponse.getElementsByTagNameNS(WSConstants.SIG_NS, WSConstants.REF_LN);
                    boolean signatureFound = false;
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (this.compareReferenceIgnoreHashtag(node.getAttributes().getNamedItem("URI").getNodeValue(), wsuIdOfMEssagingElement)) {
                            signatureFound = true;
                            break;
                        }
                    }
                    if (!signatureFound) {
                        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0302, "Invalid NonRepudiationInformation: eb:Messaging not signed", null, MSHRole.SENDING);
                    }

                    NodeList referencesFromSecurityHeader = this.getNonRepudiationNodeList(request.getSOAPHeader().getElementsByTagNameNS(WSConstants.SIG_NS, WSConstants.SIG_LN).item(0));
                    NodeList referencesFromNonRepudiationInformation = this.getNonRepudiationNodeList(response.getSOAPHeader().getElementsByTagNameNS(NonRepudiationConstants.NS_NRR, NonRepudiationConstants.NRR_LN).item(0));

                    if (!this.compareUnorderedReferenceNodeLists(referencesFromSecurityHeader, referencesFromNonRepudiationInformation)) {
                        throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0302, "Invalid NonRepudiationInformation: non repudiation information and request message do not match", null, MSHRole.SENDING);
                    }

                    return true;

                } catch (JAXBException e) {
                    ReliabilityChecker.LOG.error("", e);
                } catch (SOAPException e) {
                    ReliabilityChecker.LOG.error("", e);
                }

            } else {
                throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0302, "There is no content inside the receipt element received by the responding gateway", signalMessage.getMessageInfo().getMessageId(), signalMessage.getMessageInfo().getMessageId(), null, MSHRole.SENDING);
            }

        }
        return false;

    }

    private NodeList getNonRepudiationNodeList(Node securityInfo) throws EbMS3Exception {
        NodeList nodes = null;
        try {
            nodes = (NodeList) ReliabilityChecker.xPath.evaluate(ReliabilityChecker.XPATH_EXPRESSION_STRING, securityInfo, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            assert false;
            // due to the fact that we use a static expression this can never occur.
            throw new RuntimeException(e);
        }

        if (nodes == null) {
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0302, "No Reference Data found in either security header or nonrepudiationinformation", null, MSHRole.SENDING);
        }

        return nodes;
    }

    private boolean compareUnorderedReferenceNodeLists(NodeList referencesFromSecurityHeader, NodeList referencesFromNonRepudiationInformation) {
        if (referencesFromSecurityHeader.getLength() != referencesFromNonRepudiationInformation.getLength()) {
            return false;
        }
        boolean found = false;
        for (int i = 0; i < referencesFromSecurityHeader.getLength(); ++i) {
            for (int j = 0; j < referencesFromNonRepudiationInformation.getLength(); ++j) {
                if (referencesFromSecurityHeader.item(i).getTextContent().equals(referencesFromNonRepudiationInformation.item(j).getTextContent())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
            found = false;
        }

        return true;
    }

    /**
     * Compares two contentIds but ignores hashtags that were used for referencing inside a document
     *
     * @param referenceId the id with an hashtag
     * @param contentId   the id of the content to match
     * @return {@code true} in case both values are equal (ignoring the hashtag), else {@link false}
     */
    private boolean compareReferenceIgnoreHashtag(String referenceId, String contentId) {
        return referenceId.substring(1).equals(contentId);
    }

}
