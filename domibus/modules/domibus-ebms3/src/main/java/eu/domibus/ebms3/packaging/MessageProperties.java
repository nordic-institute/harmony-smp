package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;

/**
 * @author Hamid Ben Malek
 */
public class MessageProperties extends Element {
    private static final long serialVersionUID = 6574712004406476903L;

    public MessageProperties() {
        super(Constants.MESSAGE_PROPERTIES, Constants.NS, Constants.PREFIX);
    }

    public MessageProperties(final String propertyName, final String propertyValue) {
        this();
        addProperty(propertyName, propertyValue);
    }

    public MessageProperties(final String[] propertyNames, final String[] propertyValues) {
        this();
        if (propertyNames == null || propertyNames.length == 0) {
            return;
        }
        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyValues != null && propertyValues.length > i) {
                addProperty(propertyNames[i], propertyValues[i]);
            }
        }
    }

    public void addProperty(final String name, final String value) {
        if (name == null) {
            return;
        }
        final Element child = addElement(Constants.PROPERTY, Constants.PREFIX);
        child.setText(value);
        child.addAttribute("name", name);
    }
}