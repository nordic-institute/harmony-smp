package eu.europa.ec.digit.domibus.endpoint.ws.domibus;


import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eu.europa.ec.digit.domibus.common.aggregate.components.Acknowledgement;
import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayBody;
import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayHeader;
import eu.europa.ec.digit.domibus.common.aggregate.components.MessageInfoType;
import eu.europa.ec.digit.domibus.common.log.LogEvent;
import eu.europa.ec.digit.domibus.common.log.Logger;
import eu.europa.ec.digit.domibus.common.util.xml.XMLUtils;
import eu.europa.ec.digit.domibus.domain.gateway.GatewayEnvelope;
import eu.europa.ec.digit.domibus.endpoint.handler.domibus.DomibusBasicExceptionHandler;
import eu.europa.ec.digit.domibus.endpoint.ws.AbstractEndpoint;
import eu.europa.ec.digit.domibus.facade.service.message.MessageFacade;
import eu.europa.ec.digit.domibus.wsdl.endpoint.basic.DomibusBasicInterface;
import eu.europa.ec.digit.domibus.wsdl.endpoint.basic.FaultResponse;

@MTOM
@WebService (
        endpointInterface = "eu.europa.ec.digit.domibus.wsdl.endpoint.basic.DomibusBasicInterface",
        targetNamespace = "http://ec.europa.eu/digit/domibus/wsdl/endpoint/basic",
        serviceName = "DomibusBasicService",
        portName = "DomibusBasicBinding",
        wsdlLocation ="wsdl/DMS-Gateway-0.01.wsdl")
public class DomibusBasicEndpoint extends AbstractEndpoint implements DomibusBasicInterface {

    /* ---- Constants ---- */

    private final Logger log = new Logger(getClass());

    /* ---- Instance Variables ---- */

    @Autowired
    @Qualifier ("messageBasicFacade")
    private MessageFacade<GatewayEnvelope> messageFacade = null;

    @Autowired
    private DomibusBasicExceptionHandler domibusBasicExceptionHandler = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    @Override
    public Acknowledgement submit(GatewayHeader gatewayHeader, GatewayBody gatewayBody) throws FaultResponse {
        try {
            GatewayEnvelope envelope = new GatewayEnvelope(gatewayHeader, gatewayBody);
            envelope = messageFacade.submit(envelope);
            return response(envelope);

        // Generate Response
        } catch (Exception exception) {
            log.businessLog(LogEvent.BUS_SUBMIT_MESSAGE_FAILED, exception.getMessage());
            log.error(exception.getMessage(), exception);
            domibusBasicExceptionHandler.handleException(exception);
        }
        return null;
    }

    private Acknowledgement response(GatewayEnvelope envelope) {
        MessageInfoType messageInfo = new MessageInfoType();
        messageInfo.setCorrelationId(envelope.getGatewayHeader().getMessageInfo().getCorrelationId());
        messageInfo.setMessageId(envelope.getGatewayHeader().getMessageInfo().getMessageId());
        messageInfo.setTimestamp(XMLUtils.getDateTimeNow());
        Acknowledgement ack = new Acknowledgement();
        ack.setMessageInfo(messageInfo);
        return ack;
    }

    /* ---- Getters and Setters ---- */

    public MessageFacade<GatewayEnvelope> getMessageFacade() {
        return messageFacade;
    }

    public void setMessageFacade(MessageFacade<GatewayEnvelope> messageFacade) {
        this.messageFacade = messageFacade;
    }

    public DomibusBasicExceptionHandler getDomibusBasicExceptionHandler() {
        return domibusBasicExceptionHandler;
    }

    public void setDomibusBasicExceptionHandler(DomibusBasicExceptionHandler domibusBasicExceptionHandler) {
        this.domibusBasicExceptionHandler = domibusBasicExceptionHandler;
    }

}
