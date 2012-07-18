/*
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.start.soap.handler;

import eu.peppol.start.soap.SOAPHeaderObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.busdox.servicemetadata.types.DocumentIdentifierType;
import org.busdox.servicemetadata.types.ParticipantIdentifierType;
import org.busdox.servicemetadata.types.ProcessIdentifierType;
import org.busdox.transport.Identifiers.Identifiers;

/**
 * The SOAPInboundHandler class is used to handle an Inbound SOAP message.
 *
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class SOAPInboundHandler implements SOAPHandler<SOAPMessageContext> {

    /**
     * MessageIdentifier.
     */
    private static String messageId;

    /**
     * ChannelIdentifier.
     */
    private static String channelId;

    /**
     * SenderIdentifier.
     */
    private static ParticipantIdentifierType sender;

    /**
     * RecipientIdentifier.
     */
    private static ParticipantIdentifierType recipient;

    /**
     * DocumentIdentifier.
     */
    private static DocumentIdentifierType document;

    /**
     * ProcessIdentifier.
     */
    private static ProcessIdentifierType process;

    /**
     * SOAPHeaderObject instance.
     */
    public static final SOAPHeaderObject SOAPHEADER = new SOAPHeaderObject();;

    /**
     * Logger to follow this class behavior.
     */
    private final static org.apache.log4j.Logger logger4J =
            org.apache.log4j.Logger.getLogger(SOAPInboundHandler.class);

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext context) {

        try {
            SOAPMessage message = context.getMessage();
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            logger4J.debug("Inbound Envelope:\n" + new String(out.toByteArray()) +"\n");

            Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

            if (!outbound) {
                SOAPHeader header = envelope.getHeader();
                @SuppressWarnings("unchecked")
                Iterator<SOAPHeaderElement> headerElements = header.examineAllHeaderElements();

                StringBuilder log = new StringBuilder();
                log.append("Inbound Headers:");
                while (headerElements.hasNext()) {
                    SOAPElement element = headerElements.next();
                    log.append("\n\tSOAP Header:");
                    log.append("\n\t- Name: " + element.getElementName().getLocalName());
                    log.append("\n\t- Value: " + element.getValue());
                    setHeaderElement(element);
                }

                logger4J.debug(log);

                SOAPInboundHandler.SOAPHEADER.setMessageIdentifier(messageId);
                SOAPInboundHandler.SOAPHEADER.setChannelIdentifier(channelId);
                SOAPInboundHandler.SOAPHEADER.setSenderIdentifier(sender);
                SOAPInboundHandler.SOAPHEADER.setRecipientIdentifier(recipient);
                SOAPInboundHandler.SOAPHEADER.setDocumentIdentifier(document);
                SOAPInboundHandler.SOAPHEADER.setProcessIdentifier(process);
            }
        } catch (IOException ex) {
            Logger.getLogger(SOAPInboundHandler.class.getName()).log(Level.SEVERE, "Error reading the SOAP envelope", ex);
            logger4J.error("Error reading the SOAP envelop", ex);
        } catch (SOAPException ex) {
            Logger.getLogger(SOAPInboundHandler.class.getName()).log(Level.SEVERE, "Error retrieving the SOAP envelope", ex);
            logger4J.error("Error retrieving the SOAP envelope", ex);
        }
        return true;
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public void close(MessageContext mc) {}

    /**
     * Storage the value of the headers.
     * @param element Soap element.
     */
    private void setHeaderElement(SOAPElement element) {

        if (element.getElementName().getLocalName().equalsIgnoreCase(Identifiers.MESSAGEID)) {
            messageId = element.getValue();
        }
        if (element.getElementName().getLocalName().equalsIgnoreCase(Identifiers.CHANNELID)) {
            channelId = element.getValue();
        }
        if (element.getElementName().getLocalName().equalsIgnoreCase(Identifiers.RECIPIENTID)) {
            recipient = new ParticipantIdentifierType();
            recipient.setScheme(element.getAttribute(Identifiers.SCHEME_ATTR));
            recipient.setValue(element.getValue());
        }
        if (element.getElementName().getLocalName().equalsIgnoreCase(Identifiers.SENDERID)) {
            sender = new ParticipantIdentifierType();
            sender.setScheme(element.getAttribute(Identifiers.SCHEME_ATTR));
            sender.setValue(element.getValue());
        }
        if (element.getElementName().getLocalName().equalsIgnoreCase(Identifiers.DOCUMENTID)) {
            document =  new DocumentIdentifierType();
            document.setScheme(element.getAttribute(Identifiers.SCHEME_ATTR));
            document.setValue(element.getValue());
        }
        if (element.getElementName().getLocalName().equalsIgnoreCase(Identifiers.PROCESSID)) {
            process = new ProcessIdentifierType();
            process.setScheme(element.getAttribute(Identifiers.SCHEME_ATTR));
            process.setValue(element.getValue());
        }
    }
}