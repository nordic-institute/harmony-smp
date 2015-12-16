package eu.domibus.ebms3.receiver;

import eu.domibus.common.configuration.model.LegConfiguration;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.ObjectFactory;
import eu.domibus.submission.BackendConnector;
import eu.domibus.submission.MessageMetadata;
import java.util.List;
import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import javax.xml.soap.SOAPMessage;
import org.springframework.scheduling.annotation.Async;
import org.w3c.dom.Node;

/**
 * This interceptor is responsible for notifying the right backend
 */
public class NotifyBackendOutInterceptor extends AbstractSoapInterceptor {

    private static final Log LOG = LogFactory.getLog(NotifyBackendOutInterceptor.class);

    private JAXBContext jaxbContext;

    @Resource(name = "backends")
    private List<BackendConnector> backends;

    public void setJaxbContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public NotifyBackendOutInterceptor() {
        super(Phase.POST_STREAM);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        try {
            SOAPMessage jaxwsMessage = message.getContent(javax.xml.soap.SOAPMessage.class);
            Node messagingXml = (Node) jaxwsMessage.getSOAPHeader().getChildElements(ObjectFactory._Messaging_QNAME).next();

            Unmarshaller unmarshaller = this.jaxbContext.createUnmarshaller(); //Those are not thread-safe, therefore a new one is created each call
            @SuppressWarnings("unchecked")
            JAXBElement<Messaging> root = (JAXBElement<Messaging>) unmarshaller.unmarshal(messagingXml);

            String messageId = root.getValue().getSignalMessage().getMessageInfo().getRefToMessageId();

            this.notifyBackends(messageId);
        } catch (SOAPException | JAXBException exc) {
            LOG.error("Error notifying backends " + exc.getMessage());
        }
    }

    @Async
    private void notifyBackends(String messageId) {
        MessageMetadata metadata = new MessageMetadata(messageId, null, null, MessageMetadata.Type.INBOUND);
        for (BackendConnector backend : this.backends) {
            if (backend.isResponsible(metadata)) {
                backend.messageNotification(metadata);
                break;
            }
        }

    }

}
