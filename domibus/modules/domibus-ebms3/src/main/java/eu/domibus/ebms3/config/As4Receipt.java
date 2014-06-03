package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Sander Fieten
 * @author Hamid Ben Malek
 */
@Root(name = "As4Receipt", strict = false)
public class As4Receipt implements java.io.Serializable {
    private static final long serialVersionUID = -8309185769038462379L;

    @Attribute(required = false)
    protected String receiptTo;

    @Attribute(required = false)
    private String deliverySemantics;

    /*
     * Because we want to include the reliability configuration within the AS4Receipt element
     * the method the receipt should be sent is now configured as an attribute of the
     * element. To be compatible with existing code the accessor method names have not been
     * changed.
     */
    @Attribute(required = false)
    protected String method;

    @Attribute(required = false)
    protected boolean nonRepudiation = false;

    @Element(name = "As4Reliability", required = false)
    private As4Reliability as4Reliability;


    public String getReceiptTo() {
        return receiptTo;
    }

    public void setReceiptTo(final String receiptTo) {
        this.receiptTo = receiptTo;
    }

    public String getValue() {
        return method;
    }

    public void setValue(final String value) {
        this.method = value;
    }

    /**
     * @return the as4Reliability
     */
    public As4Reliability getAs4Reliability() {
        return as4Reliability;
    }

    /**
     * @param as4Reliability the as4Reliability to set
     */
    public void setAs4Reliability(final As4Reliability as4Reliability) {
        this.as4Reliability = as4Reliability;
    }

    public boolean isNonRepudiation() {
        return nonRepudiation;
    }

    public void setNonRepudiation(final boolean nonRepudiation) {
        this.nonRepudiation = nonRepudiation;
    }

    public String getDeliverySemantics() {
        return deliverySemantics;
    }

    public void setDeliverySemantics(final String deliverySemantics) {
        this.deliverySemantics = deliverySemantics;
    }

}