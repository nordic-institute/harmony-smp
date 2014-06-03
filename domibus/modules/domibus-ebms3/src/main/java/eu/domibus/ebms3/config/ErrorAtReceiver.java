package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "ErrorAtReceiver", strict = false)
public class ErrorAtReceiver implements java.io.Serializable {
    private static final long serialVersionUID = -8309197098760417779L;

    @Attribute(required = false)
    protected boolean notifiyProducer;

    @Attribute(required = false)
    protected boolean notifyConsumer;

    public ErrorAtReceiver() {
    }

    public ErrorAtReceiver(final boolean notifiyProducer, final boolean notifyConsumer) {
        this.notifiyProducer = notifiyProducer;
        this.notifyConsumer = notifyConsumer;
    }

    public boolean isNotifiyProducer() {
        return notifiyProducer;
    }

    public void setNotifiyProducer(final boolean notifiyProducer) {
        this.notifiyProducer = notifiyProducer;
    }

    public boolean isNotifyConsumer() {
        return notifyConsumer;
    }

    public void setNotifyConsumer(final boolean notifyConsumer) {
        this.notifyConsumer = notifyConsumer;
    }
}