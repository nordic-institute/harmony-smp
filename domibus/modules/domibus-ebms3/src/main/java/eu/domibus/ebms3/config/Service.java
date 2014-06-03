package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Service", strict = false)
public class Service implements java.io.Serializable {
    private static final long serialVersionUID = -8309197955456717779L;

    @Text
    protected String value;

    @Attribute(required = false)
    protected String type;

    public Service() {
    }

    public Service(final String type, final String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Service)) {
            return false;
        }
        final Service s = (Service) obj;
        if ((type == null || type.trim().equals(""))) {
            return !(s.getType() != null && !s.getType().trim().equals("")) && value.equalsIgnoreCase(s.getValue());
        } else {
            return value.equalsIgnoreCase(s.getValue()) && type.equalsIgnoreCase(s.getType());
        }
    }
}