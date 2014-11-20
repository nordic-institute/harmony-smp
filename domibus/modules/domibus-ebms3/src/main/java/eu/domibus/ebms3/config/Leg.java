package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Leg")
public class Leg implements Serializable {
    private static final long serialVersionUID = -5592938470192830737L;

    @Attribute
    protected int number;

    @Attribute(name = "userService", required = false)
    protected String userServiceName;

    @Element(name = "UserService", required = false)
    protected UserService userService;

    @Attribute(required = false)
    protected String messageLabel;

    @Attribute(required = false)
    protected String mpc;

    @Attribute(name = "producer", required = false)
    protected String producerName;

    @Element(name = "Producer", required = false)
    protected Producer producer;

    @Attribute(required = false)
    protected String soapAction;

    @Attribute(required = false)
    protected String wsaAction;

    @Attribute(required = false)
    protected String binding;

    @Attribute(required = false)
    protected String reliability;

    @Attribute(required = false)
    protected String security;

    @Element(name = "Endpoint", required = false)
    protected Endpoint endpoint;

    @Element(name = "ErrorAtSender", required = false)
    protected ErrorAtSender errorAtSender;

    @Element(name = "ErrorAtReceiver", required = false)
    protected ErrorAtReceiver errorAtReceiver;

    @Element(name = "Authorization", required = false)
    protected Authorization authorization;

    @Element(name = "As4Receipt", required = false)
    protected As4Receipt as4Receipt;

    @Element(name = "PayloadService", required = false)
    protected PayloadService payloadService;

    /* This is not to be serialized */
    protected PMode pmode;

    public Leg() {
    }

    public Leg(final int number, final String us, final String mpc, final String producer, final Endpoint endpoint) {
        this.number = number;
        this.userServiceName = us;
        this.mpc = mpc;
        this.producerName = producer;
        this.endpoint = endpoint;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public String getUserServiceName() {
        return this.userServiceName;
    }

    public void setUserServiceName(final String userService) {
        this.userServiceName = userService;
    }

    public UserService getUserService() {
        if (this.userService != null) {
            return this.userService;
        }
        if ((this.userServiceName == null) || "".equals(this.userServiceName.trim())) {
            return null;
        }
        if (this.pmode == null) {
            return null;
        }
        return this.pmode.getUserService(this.userServiceName);
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public String getMessageLabel() {
        return this.messageLabel;
    }

    public void setMessageLabel(final String messageLabel) {
        this.messageLabel = messageLabel;
    }

    public String getMpc() {
        return this.mpc;
    }

    public void setMpc(final String mpc) {
        this.mpc = mpc;
    }

    public String getProducerName() {
        return this.producerName;
    }

    public void setProducerName(final String producer) {
        this.producerName = producer;
    }

    public Producer getProducer() {
        if (this.producer != null) {
            return this.producer;
        }
        if ((this.producerName == null) || "".equals(this.producerName.trim())) {
            return null;
        }
        if (this.pmode == null) {
            return null;
        }
        return this.pmode.getProducer(this.producerName);
    }

    public void setProducer(final Producer producer) {
        this.producer = producer;
    }

    public String getSoapAction() {
        return this.soapAction;
    }

    public void setSoapAction(final String soapAtion) {
        this.soapAction = soapAtion;
    }

    public String getWsaAction() {
        return this.wsaAction;
    }

    public void setWsaAction(final String wsaAction) {
        this.wsaAction = wsaAction;
    }

    public String getBinding() {
        return this.binding;
    }

    public void setBinding(final String binding) {
        this.binding = binding;
    }

    public Endpoint getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(final Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public ErrorAtSender getErrorAtSender() {
        return this.errorAtSender;
    }

    public void setErrorAtSender(final ErrorAtSender errorAtSender) {
        this.errorAtSender = errorAtSender;
    }

    public ErrorAtReceiver getErrorAtReceiver() {
        return this.errorAtReceiver;
    }

    public void setErrorAtReceiver(final ErrorAtReceiver errorAtReceiver) {
        this.errorAtReceiver = errorAtReceiver;
    }

    public Authorization getAuthorization() {
        return this.authorization;
    }

    public void setAuthorization(final Authorization authorization) {
        this.authorization = authorization;
    }

    public As4Receipt getAs4Receipt() {
        return this.as4Receipt;
    }

    public void setAs4Receipt(final As4Receipt as4Receipt) {
        this.as4Receipt = as4Receipt;
    }

    public PayloadService getPayloadService() {
        return payloadService;
    }

    public void setPayloadService(PayloadService payloadService) {
        this.payloadService = payloadService;
    }

    public String getReliability() {
        return this.reliability;
    }

    public void setReliability(final String reliability) {
        this.reliability = reliability;
    }

    public String getSecurity() {
        return this.security;
    }

    public void setSecurity(final String security) {
        this.security = security;
    }

    public PMode getPmode() {
        return this.pmode;
    }

    public void setPmode(final PMode pmode) {
        this.pmode = pmode;
    }

    public String getSoapVersion() {
        if (this.endpoint == null) {
            return "1.1";
        }
        return this.endpoint.getSoapVersion();
    }

    // convenient methods...
    public String getReceiptTo() {
        if (this.as4Receipt == null) {
            return null;
        }
        return this.as4Receipt.getReceiptTo();
    }

    public String getReceiptReply() {
        if (this.as4Receipt == null) {
            return null;
        }
        return this.as4Receipt.getValue();
    }

    @Override
    public String toString() {
        return "Leg [number=" + this.number + ", userServiceName=" + this.userServiceName + ", userService=" +
               this.userService +
               ", messageLabel=" + this.messageLabel + ", mpc=" + this.mpc + ", producerName=" + this.producerName +
               ", producer=" +
               this.producer + ", soapAction=" + this.soapAction + ", wsaAction=" + this.wsaAction + ", binding=" +
               this.binding +
               ", reliability=" + this.reliability + ", security=" + this.security + ", endpoint=" + this.endpoint +
               ", errorAtSender=" + this.errorAtSender + ", errorAtReceiver=" + this.errorAtReceiver +
               ", authorization=" +
               this.authorization + ", as4Receipt=" + this.as4Receipt + ", pmode=" + this.pmode + "]";
    }


}