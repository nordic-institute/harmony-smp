package eu.domibus.ebms3.persistent;

import org.apache.log4j.Logger;
import eu.domibus.common.persistent.AbstractBaseEntity;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "PartProperties")
@Entity
@Table(name = "TB_PARTY_PROPERTIES")
public class PartProperties extends AbstractBaseEntity {

    private static final Logger log = Logger.getLogger(PartProperties.class);

    @ElementMap(entry = "Property", key = "name", attribute = true, inline = true, required = false)
    @ElementCollection(fetch = FetchType.EAGER)
    protected Map<String, String> properties = new HashMap<String, String>();

    public Map<String, String> getPartProperties() {
        return properties;
    }

    public void setPartProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public void addProperty(final String name, final String value) {
        properties.put(name, value);
    }

    public String getProperty(final String propertyName) {
        return properties.get(propertyName);
    }

    // Only for convertion from Flex 2:
    public void setPropertyArray(final Property[] p) {
        if (p == null || p.length == 0) {
            return;
        }
        for (final Property prop : p) {
            addProperty(prop.getName(), prop.getValue());
        }

        // debugging only:
        //printProperties();
    }

    public Property[] getPartPropertiesArray() {
        if (properties == null || properties.keySet() == null) {
            return null;
        }
        final int size = properties.keySet().size();
        final Property[] res = new Property[size];
        int i = 0;
        for (final String key : properties.keySet()) {
            res[i] = new Property(key, properties.get(key));
            i++;
        }
        return res;
    }

    // Just for debugging:
    private void printProperties() {
        if (properties == null || properties.keySet() == null) {
            return;
        }
        for (final String key : properties.keySet()) {
            log.debug("[==== property " + key +
                      " has value " + properties.get(key));
        }
    }
}