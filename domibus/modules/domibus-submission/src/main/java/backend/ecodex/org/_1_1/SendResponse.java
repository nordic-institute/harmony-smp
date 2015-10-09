/**
 * SendResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */
package backend.ecodex.org._1_1;

/**
 * SendResponse bean class
 */
@SuppressWarnings({"unchecked", "unused"})
public class SendResponse implements org.apache.axis2.databinding.ADBBean {
    public static final javax.xml.namespace.QName MY_QNAME =
            new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendResponse", "ns1");
    /**
     * field for MessageID This was an Array!
     */
    protected String[] localMessageID;
    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this attribute. It will be
     * used to determine whether to include this field in the serialized XML
     */
    protected boolean localMessageIDTracker = false;

    public boolean isMessageIDSpecified() {
        return this.localMessageIDTracker;
    }

    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public String[] getMessageID() {
        return this.localMessageID;
    }

    /**
     * validate the array for MessageID
     */
    protected void validateMessageID(final String[] param) {
    }

    /**
     * Auto generated setter method
     *
     * @param param MessageID
     */
    public void setMessageID(final String[] param) {
        validateMessageID(param);
        this.localMessageIDTracker = param != null;
        this.localMessageID = param;
    }

    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addMessageID(final String param) {
        if (this.localMessageID == null) {
            this.localMessageID = new String[]{};
        }
        //update the setting tracker
        this.localMessageIDTracker = true;
        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localMessageID);
        list.add(param);
        this.localMessageID = (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                                                      final org.apache.axiom.om.OMFactory factory)
            throws org.apache.axis2.databinding.ADBException {
        final org.apache.axiom.om.OMDataSource dataSource =
                new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
        return factory.createOMElement(dataSource, MY_QNAME);
    }

    public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
        serialize(parentQName, xmlWriter, false);
    }

    public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter,
                          final boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
        String prefix = null;
        String namespace = null;
        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();
        writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
        if (serializeType) {
            final String namespacePrefix = registerPrefix(xmlWriter, "http://org.ecodex.backend/1_1/");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":sendResponse", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "sendResponse", xmlWriter);
            }
        }
        if (this.localMessageIDTracker) {
            if (this.localMessageID != null) {
                namespace = "";
                for (int i = 0; i < this.localMessageID.length; i++) {
                    if (this.localMessageID[i] != null) {
                        writeStartElement(null, namespace, "messageID", xmlWriter);
                        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                                          .convertToString(this.localMessageID[i]));
                        xmlWriter.writeEndElement();
                    } else {
                        // we have to do nothing since minOccurs is zero
                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException("messageID cannot be null!!");
            }
        }
        xmlWriter.writeEndElement();
    }

    private static String generatePrefix(final String namespace) {
        if ("http://org.ecodex.backend/1_1/".equals(namespace)) {
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(String prefix, final String namespace, final String localPart,
                                   final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        final String writerPrefix = xmlWriter.getPrefix(namespace);
        if (writerPrefix != null) {
            xmlWriter.writeStartElement(namespace, localPart);
        } else {
            if (namespace.length() == 0) {
                prefix = "";
            } else if (prefix == null) {
                prefix = generatePrefix(namespace);
            }
            xmlWriter.writeStartElement(prefix, localPart, namespace);
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
    }

    /**
     * Util method to write an attribute with the ns prefix
     */
    private void writeAttribute(final String prefix, final String namespace, final String attName,
                                final String attValue, final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace, attName, attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(final String namespace, final String attName, final String attValue,
                                final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        if ("".equals(namespace)) {
            xmlWriter.writeAttribute(attName, attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeQNameAttribute(final String namespace, final String attName,
                                     final javax.xml.namespace.QName qname,
                                     final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        final String attributeNamespace = qname.getNamespaceURI();
        String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
        if (attributePrefix == null) {
            attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
        }
        final String attributeValue;
        if (attributePrefix.trim().length() > 0) {
            attributeValue = attributePrefix + ":" + qname.getLocalPart();
        } else {
            attributeValue = qname.getLocalPart();
        }
        if ("".equals(namespace)) {
            xmlWriter.writeAttribute(attName, attributeValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attributeValue);
        }
    }

    /**
     * method to handle Qnames
     */
    private void writeQName(final javax.xml.namespace.QName qname, final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        final String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix, namespaceURI);
            }
            if (prefix.trim().length() > 0) {
                xmlWriter.writeCharacters(
                        prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(final javax.xml.namespace.QName[] qnames, final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            final StringBuffer stringToWrite = new StringBuffer();
            String namespaceURI = null;
            String prefix = null;
            for (int i = 0; i < qnames.length; i++) {
                if (i > 0) {
                    stringToWrite.append(" ");
                }
                namespaceURI = qnames[i].getNamespaceURI();
                if (namespaceURI != null) {
                    prefix = xmlWriter.getPrefix(namespaceURI);
                    if ((prefix == null) || (prefix.length() == 0)) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix, namespaceURI);
                    }
                    if (prefix.trim().length() > 0) {
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil
                                                                                .convertToString(qnames[i]));
                    } else {
                        stringToWrite
                                .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                } else {
                    stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                }
            }
            xmlWriter.writeCharacters(stringToWrite.toString());
        }
    }

    /**
     * Register a namespace prefix
     */
    private String registerPrefix(final javax.xml.stream.XMLStreamWriter xmlWriter, final String namespace)
            throws javax.xml.stream.XMLStreamException {
        String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            final javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                final String uri = nsContext.getNamespaceURI(prefix);
                if (uri == null || uri.length() == 0) {
                    break;
                }
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
            }
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        return prefix;
    }

    /**
     * databinding method to get an XML representation of this object
     */
    public javax.xml.stream.XMLStreamReader getPullParser(final javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException {
        final java.util.ArrayList elementList = new java.util.ArrayList();
        final java.util.ArrayList attribList = new java.util.ArrayList();
        if (this.localMessageIDTracker) {
            if (this.localMessageID != null) {
                for (int i = 0; i < this.localMessageID.length; i++) {
                    if (this.localMessageID[i] != null) {
                        elementList.add(new javax.xml.namespace.QName("", "messageID"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToString(this.localMessageID[i]));
                    } else {
                        // have to do nothing
                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException("messageID cannot be null!!");
            }
        }
        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                                                                                    attribList.toArray());
    }

    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {
        /**
         * static method to create the object Precondition: If this object is an element, the current or next start
         * element starts this object and any intervening reader events are ignorable If this object is not an element,
         * it is a complex type and the reader is at the event just after the outer start element Postcondition: If this
         * object is an element, the reader is positioned at its end element If this object is a complex type, the
         * reader is positioned at the end element of its outer element
         */
        public static SendResponse parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final SendResponse object = new SendResponse();
            int event;
            final String nillableValue = null;
            final String prefix = "";
            final String namespaceuri = "";
            try {
                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                    final String fullTypeName =
                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                    if (fullTypeName != null) {
                        String nsPrefix = null;
                        if (fullTypeName.indexOf(":") > -1) {
                            nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                        }
                        nsPrefix = nsPrefix == null ? "" : nsPrefix;
                        final String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);
                        if (!"sendResponse".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (SendResponse) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }
                    }
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();
                reader.next();
                final java.util.ArrayList list1 = new java.util.ArrayList();
                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }
                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("", "messageID").equals(reader.getName())) {
                    // Process the array and step past its final element's end.
                    list1.add(reader.getElementText());
                    //loop until we find a start element that is not part of this array
                    boolean loopDone1 = false;
                    while (!loopDone1) {
                        // Ensure we are at the EndElement
                        while (!reader.isEndElement()) {
                            reader.next();
                        }
                        // Step out of this element
                        reader.next();
                        // Step to next element event.
                        while (!reader.isStartElement() && !reader.isEndElement()) {
                            reader.next();
                        }
                        if (reader.isEndElement()) {
                            //two continuous end elements means we are exiting the xml structure
                            loopDone1 = true;
                        } else {
                            if (new javax.xml.namespace.QName("", "messageID").equals(reader.getName())) {
                                list1.add(reader.getElementText());
                            } else {
                                loopDone1 = true;
                            }
                        }
                    }
                    // call the converter utility  to convert and set the array
                    object.setMessageID((String[]) list1.toArray(new String[list1.size()]));
                } // End of if for expected property start element
                else {
                }
                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }
                if (reader.isStartElement())
                // A start element we are not expecting indicates a trailing invalid property
                {
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                }
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new Exception(e);
            }
            return object;
        }
    }//end of factory class
}
