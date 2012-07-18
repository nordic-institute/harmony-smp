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

import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.XWSSecurityException;
import eu.peppol.start.soap.SOAPHeaderObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.apache.log4j.Logger;
import org.busdox.servicemetadata.types.DocumentIdentifierType;
import org.busdox.servicemetadata.types.ObjectFactory;
import org.busdox.servicemetadata.types.ParticipantIdentifierType;
import org.busdox.servicemetadata.types.ProcessIdentifierType;

/**
 * The SOAPOutboundHandler class is used to handle an Outbound SOAP message
 * in order to  include the BUSDOX defined headers.
 *
 * @author  Dante Malaga(dante@alfa1lab.com)
 *          Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class SOAPOutboundHandler implements SOAPHandler<SOAPMessageContext> {

    /**
     * Logger to follow this class behavior.
     */
    private static final Logger logger4J =  Logger.getLogger(SOAPOutboundHandler.class);

    /**
     * Holds an static SOAPHeaderObject object.
     */
    private static SOAPHeaderObject soapHeader;

    /**
     * Holds an X509 certificate object.
     */
    private X509Certificate metadataCertificate = null;

    public SOAPOutboundHandler(X509Certificate cert) {
        metadataCertificate = cert;
    }

    /**
     * @return the soapHeader
     */
    public static SOAPHeaderObject getSoapHeader() {
        return soapHeader;
    }

    /**
     * @param aSoapHeader the soapHeader to set
     */
    public static void setSoapHeader(SOAPHeaderObject aSoapHeader) {
        soapHeader = aSoapHeader;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(final SOAPMessageContext context) {

        try {
            SOAPMessage message = context.getMessage();
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            logger4J.debug("Outbound Envelope:\n" +new String(out.toByteArray()));

            Boolean isOutboundMessage = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

            if (isOutboundMessage) {
                createSOAPHeader(envelope);
            } else {
                MessageContext ctx = ((MessageContext) context);
                Subject subj = SubjectAccessor.getRequesterSubject(ctx);
                Iterator<Principal> principals = subj.getPrincipals().iterator();
                Principal principal = null;
                while (principals.hasNext()) {
                    principal = principals.next();
                }
                String principalName = principal.getName();
                String metacertPrincipal = null;
                if (metadataCertificate != null) {
                    metacertPrincipal = metadataCertificate.getSubjectX500Principal().getName();
                    metacertPrincipal = ((String[])metacertPrincipal.split(","))[0];
                    principalName = ((String[])principalName.split(","))[0];
                    if(!principalName.equals(metacertPrincipal)){
                        logger4J.warn("WARNING: Metadata Certificate of recipient "
                                + "does not match the Certificate used to sign the Response");
                        throw new RuntimeException("WARNING: Metadata Certificate of recipient "
                                + "does not match the Certificate used to sign the Response");
                    }
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SOAPOutboundHandler.class.getName()).log(Level.SEVERE, null, ex);
            logger4J.error(ex.getMessage());
        } catch (JAXBException ex) {
            java.util.logging.Logger.getLogger(SOAPOutboundHandler.class.getName()).log(Level.SEVERE, null, ex);
            logger4J.fatal("An error occurred while marshalling headers.", ex);
        } catch (SOAPException ex) {
            java.util.logging.Logger.getLogger(SOAPOutboundHandler.class.getName()).log(Level.SEVERE, null, ex);
            logger4J.fatal("An error occurred while working with SOAP objects.", ex);
        } catch (XWSSecurityException ex) {
            java.util.logging.Logger.getLogger(SOAPOutboundHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * Adds the BUSDOX headers to the header part of the given SOAP-envelope.
     *
     * @param envelope the SOAP-envelope.
     *
     * @throws SOAPException thrown if there is a problem with the
     *      SOAPHeader object.
     * @throws JAXBException thown if there is a problem marshalling the BUSDOX
     *      headers into the SOAP-envelope.
     */
    private void createSOAPHeader(final SOAPEnvelope envelope)
            throws SOAPException, JAXBException {
        logger4J.debug("Outbound Headers:"
                    + "\n\tSOAP Header:"
                    + "\n\t- Name: MessageIdentifier"
                    + "\n\t- Value: " + soapHeader.getMessageIdentifier()
                    + "\n\tSOAP Header:"
                    + "\n\t- Name: ChannelIdentifier"
                    + "\n\t- Value: " + soapHeader.getChannelIdentifier()
                    + "\n\tSOAP Header:"
                    + "\n\t- Name: RecipientIdentifier"
                    + "\n\t- Value: " + soapHeader.getRecipientIdentifier().getValue()
                    + "\n\tSOAP Header:"
                    + "\n\t- Name: SenderIdentifier"
                    + "\n\t- Value: " + soapHeader.getSenderIdentifier().getValue()
                    + "\n\tSOAP Header:"
                    + "\n\t- Name: DocumentIdentifier"
                    + "\n\t- Value: " + soapHeader.getDocumentIdentifier().getValue()
                    + "\n\tSOAP Header:"
                    + "\n\t- Name: ProcessIdentifier"
                    + "\n\t- Value: " + soapHeader.getProcessIdentifier().getValue());

        SOAPHeader header = envelope.getHeader();

        if (header == null) {
            header = envelope.addHeader();
        }

        ObjectFactory objFactory = new ObjectFactory();
        Marshaller marshaller = null;

        String channelId = soapHeader.getChannelIdentifier();
        String messageId = soapHeader.getMessageIdentifier();

        ParticipantIdentifierType recipientId = new ParticipantIdentifierType();
        recipientId.setValue(soapHeader.getRecipientIdentifier().getValue());
        recipientId.setScheme(soapHeader.getRecipientIdentifier().getScheme());

        ParticipantIdentifierType senderId = new ParticipantIdentifierType();
        senderId.setValue(soapHeader.getSenderIdentifier().getValue());
        senderId.setScheme(soapHeader.getSenderIdentifier().getScheme());

        DocumentIdentifierType documentId = new DocumentIdentifierType();
        documentId.setValue(soapHeader.getDocumentIdentifier().getValue());
        documentId.setScheme(soapHeader.getDocumentIdentifier().getScheme());

        ProcessIdentifierType processId = new ProcessIdentifierType();
        processId.setValue(soapHeader.getProcessIdentifier().getValue());
        processId.setScheme(soapHeader.getProcessIdentifier().getScheme());

        /* Proceed to put information as headers in the header block */

        marshaller = JAXBContext.newInstance(String.class).createMarshaller();
        marshaller.marshal(objFactory.createMessageIdentifier(messageId),
                new DOMResult(header));

        JAXBElement auxChannelId = objFactory.createChannelIdentifier(channelId);
        auxChannelId.setNil(true);
        marshaller.marshal(auxChannelId,
                new DOMResult(header));

        marshaller = JAXBContext.newInstance(ParticipantIdentifierType.class).createMarshaller();
        marshaller.marshal(objFactory.createRecipientIdentifier(recipientId),
                new DOMResult(header));

        marshaller.marshal(objFactory.createSenderIdentifier(senderId),
                new DOMResult(header));

        marshaller = JAXBContext.newInstance(DocumentIdentifierType.class).createMarshaller();
        marshaller.marshal(objFactory.createDocumentIdentifier(documentId),
                new DOMResult(header));

        marshaller = JAXBContext.newInstance(ProcessIdentifierType.class).createMarshaller();
        marshaller.marshal(objFactory.createProcessIdentifier(processId),
                new DOMResult(header));
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }
}