package eu.domibus.ebms3.consumers;

import org.apache.log4j.Logger;
import eu.domibus.common.util.ClassUtil;
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
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(final String name, final String value) {
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        }
        parameters.put(name, value);
    }

    public EbConsumer createInstance() {
        if (className == null || className.trim().equals("")) {
            return null;
        }

        EbConsumer instance = null;

        Object classByClassName = ClassUtil.createInstance(className);

        if(classByClassName instanceof EbConsumer) {
            instance = (EbConsumer)classByClassName;
        } else {
            log.error("Class " + className + " is not instance of EbConsumer");
            throw new ClassCastException();
        }

        if (instance != null) {
            instance.setParameters(parameters);
        }
        return instance;
    }
}