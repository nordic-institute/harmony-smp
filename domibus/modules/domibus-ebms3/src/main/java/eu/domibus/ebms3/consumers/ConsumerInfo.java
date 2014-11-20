package eu.domibus.ebms3.consumers;

import eu.domibus.common.util.ClassUtil;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "consumer")
public class ConsumerInfo {

    private static final Logger log = Logger.getLogger(ConsumerInfo.class);

    @Attribute(name = "className")
    protected String className;

    @ElementMap(entry = "parameter", key = "name", attribute = true, inline = true, required = false)
    protected Map<String, String> parameters;

    public String getClassName() {
        return this.className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(final String name, final String value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<String, String>();
        }
        this.parameters.put(name, value);
    }

    public EbConsumer createInstance() {
        if ((this.className == null) || "".equals(this.className.trim())) {
            return null;
        }

        EbConsumer instance = null;

        final Object classByClassName = ClassUtil.createInstance(this.className);

        if (classByClassName instanceof EbConsumer) {
            instance = (EbConsumer) classByClassName;
        } else {
            ConsumerInfo.log.error("Class " + this.className + " is not instance of EbConsumer");
            throw new ClassCastException();
        }

        if (instance != null) {
            instance.setParameters(this.parameters);
        }
        return instance;
    }
}