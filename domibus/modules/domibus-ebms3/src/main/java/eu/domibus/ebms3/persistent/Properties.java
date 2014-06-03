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
@Root
@Entity
@Table(name = "TB_PROPERTIES")
public class Properties extends AbstractBaseEntity {

    private static final Logger log = Logger.getLogger(Properties.class);

    @ElementMap(entry = "Property", key = "name", attribute = true, inline = true, required = false)
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> properties = new HashMap<String, String>();


    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public void addProperty(final String name, final String value) {
        properties.put(name, value);
    }

    public String getProperty(final String propertyName) {
        return properties.get(propertyName);
    }
}
