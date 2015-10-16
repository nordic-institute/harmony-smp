package eu.domibus.ebms3.config;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "MessageProperties", strict = false)
public class MessageProperties implements Serializable {
    private static final long serialVersionUID = -5593316571432120737L;

    @ElementList(inline = true)
    protected List<Property> properties = new ArrayList<Property>();

    public MessageProperties() {
    }

    public MessageProperties(final List<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(final String name, final String type, final String desc, final boolean required) {
        final Property p = new Property(name, type, desc, required);
        this.properties.add(p);
    }

    public void addProperty(final Property p) {
        this.properties.add(p);
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(final List<Property> properties) {
        this.properties = properties;
    }

    /* For serialization to Flex UI */
    public Property[] getPropertiesArray() {
        if (this.properties == null) {
            return null;
        }
        final Property[] res = new Property[this.properties.size()];
        int i = 0;
        for (final Property p : this.properties) {
            res[i] = p;
            i++;
        }
        return res;
    }

    public void setPropertiesArray(final Property[] list) {
        if ((list == null) || (list.length == 0)) {
            if ((this.properties != null) && !this.properties.isEmpty()) {
                this.properties.clear();
            }
            return;
        }
        if (this.properties == null) {
            this.properties = new ArrayList<Property>();
        }
        if (!this.properties.isEmpty()) {
            this.properties.clear();
        }
        for (final Property p : list) {
            this.addProperty(p);
        }
    }
}