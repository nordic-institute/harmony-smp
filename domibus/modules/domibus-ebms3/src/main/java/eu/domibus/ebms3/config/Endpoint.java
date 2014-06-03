package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Endpoint", strict = false)
public class Endpoint implements java.io.Serializable {
    private static final long serialVersionUID = -8309197123450417779L;

    @Attribute(required = false)
    protected String address;

    @Attribute(required = false)
    protected String soapVersion = "1.2";

    public Endpoint() {
    }

    public Endpoint(final String address, final String soapVersion) {
        this.address = address;
        this.soapVersion = soapVersion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getSoapVersion() {
        return soapVersion;
    }

    public void setSoapVersion(final String version) {
        this.soapVersion = version;
    }
}