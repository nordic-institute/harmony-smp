package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "ErrorAtSender", strict = false)
public class ErrorAtSender implements java.io.Serializable {
    private static final long serialVersionUID = -8309197123450417779L;

    @Attribute(required = false)
    protected boolean notifiyProducer;

    @Attribute(required = false)
    protected boolean notifyConsumer;

    @Attribute(required = false)
    protected String reportTo;

    public ErrorAtSender() {
    }

    public ErrorAtSender(final boolean notifiyProducer, final boolean notifyConsumer, final String reportTo) {
        this.notifiyProducer = notifiyProducer;
        this.notifyConsumer = notifyConsumer;
        this.reportTo = reportTo;
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

    public String getReportTo() {
        return reportTo;
    }

    public void setReportTo(final String reportTo) {
        this.reportTo = reportTo;
    }
}