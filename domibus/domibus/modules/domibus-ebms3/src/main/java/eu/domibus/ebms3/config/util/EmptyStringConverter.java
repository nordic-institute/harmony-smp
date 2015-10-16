package eu.domibus.ebms3.config.util;


import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

/**
 * TODO: Insert Description here
 *
 * @author muell16
 */
public class EmptyStringConverter  implements Converter<String> {

    public String read(InputNode node) throws Exception {
        String value = node.getValue();
        if(value == null) {
            value = "";
        }
        return value;
    }

    public void write(OutputNode node, String value) throws Exception {
        node.setValue(value);
    }
}