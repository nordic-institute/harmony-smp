package eu.europa.ec.edelivery.smp.auth.cas;

import org.slf4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 * CustomContainerAttributeHandler is a custom implementation of the DefaultHandler class to parse the XML response
 * attributes from the CAS server.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public class CasContainerAttributeHandler extends DefaultHandler {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(CasContainerAttributeHandler.class);
    private final List<String> attributeContainerNames;
    private String currentContainerName;
    private Map<String, Object> attributes;
    private String currentAttribute;
    private StringBuilder value;

    public CasContainerAttributeHandler(List<String> attributeContainerNames) {
        this.attributeContainerNames = attributeContainerNames;
    }

    public void startDocument() {
        this.attributes = new HashMap<>();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) {

        if (this.currentContainerName != null) {
            this.value = new StringBuilder();
            this.currentAttribute = localName;
        } else if (attributeContainerNames.contains(localName)) {
            LOG.debug("Found attribute container [{}]", localName);
            currentContainerName = localName;
        }

    }

    public void characters(char[] chars, int start, int length) {
        if (this.currentAttribute != null) {
            this.value.append(chars, start, length);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        if (this.currentContainerName == null) {
            LOG.debug("Not in attribute container. Skip element [{}]", localName);
            return;
        }

        if (currentContainerName.equals(localName)) {
            this.currentContainerName = null;
            this.currentAttribute = null;
        } else if (currentAttribute.equals(localName)){
            Object o = this.attributes.get(this.currentAttribute);
            if (o == null) {
                this.attributes.put(this.currentAttribute, this.value.toString());
            } else {
                List<Object> items;
                if (o instanceof List) {
                    items = (List<Object>) o;
                } else {
                    // convert to list
                    items = new LinkedList();
                    items.add(o);
                    this.attributes.replace(this.currentAttribute, items);
                }
                // add new item
                LOG.debug("Add new LIST attribute  [{}], value: [{}]", localName, this.value.toString());
                items.add(this.value.toString());
            }
        }
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
