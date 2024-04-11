/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
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
