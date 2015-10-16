/**
 * Fault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.xmlsoap.schemas.soap.envelope;


/**
 * Fault bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Fault implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Fault
                Namespace URI = http://schemas.xmlsoap.org/soap/envelope/
                Namespace Prefix = ns2
                */


    /**
     * field for Faultcode
     */


    protected javax.xml.namespace.QName localFaultcode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localFaultcodeTracker = false;

    public boolean isFaultcodeSpecified() {
        return this.localFaultcodeTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return javax.xml.namespace.QName
     */
    public javax.xml.namespace.QName getFaultcode() {
        return this.localFaultcode;
    }


    /**
     * Auto generated setter method
     *
     * @param param Faultcode
     */
    public void setFaultcode(final javax.xml.namespace.QName param) {
        this.localFaultcodeTracker = param != null;

        this.localFaultcode = param;


    }


    /**
     * field for Faultstring
     */


    protected String localFaultstring;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localFaultstringTracker = false;

    public boolean isFaultstringSpecified() {
        return this.localFaultstringTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return java.lang.String
     */
    public String getFaultstring() {
        return this.localFaultstring;
    }


    /**
     * Auto generated setter method
     *
     * @param param Faultstring
     */
    public void setFaultstring(final String param) {
        this.localFaultstringTracker = param != null;

        this.localFaultstring = param;


    }


    /**
     * field for Faultactor
     */


    protected org.apache.axis2.databinding.types.URI localFaultactor;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localFaultactorTracker = false;

    public boolean isFaultactorSpecified() {
        return this.localFaultactorTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.URI
     */
    public org.apache.axis2.databinding.types.URI getFaultactor() {
        return this.localFaultactor;
    }


    /**
     * Auto generated setter method
     *
     * @param param Faultactor
     */
    public void setFaultactor(final org.apache.axis2.databinding.types.URI param) {
        this.localFaultactorTracker = param != null;

        this.localFaultactor = param;


    }


    /**
     * field for Detail
     */


    protected org.xmlsoap.schemas.soap.envelope.Detail localDetail;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localDetailTracker = false;

    public boolean isDetailSpecified() {
        return this.localDetailTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.xmlsoap.schemas.soap.envelope.Detail
     */
    public org.xmlsoap.schemas.soap.envelope.Detail getDetail() {
        return this.localDetail;
    }


    /**
     * Auto generated setter method
     *
     * @param param Detail
     */
    public void setDetail(final org.xmlsoap.schemas.soap.envelope.Detail param) {
        this.localDetailTracker = param != null;

        this.localDetail = param;


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
                new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
        return factory.createOMElement(dataSource, parentQName);

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


            final String namespacePrefix = registerPrefix(xmlWriter, "http://schemas.xmlsoap.org/soap/envelope/");
            if ((namespacePrefix != null) && (!namespacePrefix.trim().isEmpty())) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":Fault",
                               xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Fault", xmlWriter);
            }


        }
        if (this.localFaultcodeTracker) {
            namespace = "";
            writeStartElement(null, namespace, "faultcode", xmlWriter);


            if (this.localFaultcode == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("faultcode cannot be null!!");

            } else {


                writeQName(this.localFaultcode, xmlWriter);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localFaultstringTracker) {
            namespace = "";
            writeStartElement(null, namespace, "faultstring", xmlWriter);


            if (this.localFaultstring == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("faultstring cannot be null!!");

            } else {


                xmlWriter.writeCharacters(this.localFaultstring);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localFaultactorTracker) {
            namespace = "";
            writeStartElement(null, namespace, "faultactor", xmlWriter);


            if (this.localFaultactor == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("faultactor cannot be null!!");

            } else {


                xmlWriter.writeCharacters(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFaultactor));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localDetailTracker) {
            if (this.localDetail == null) {
                throw new org.apache.axis2.databinding.ADBException("detail cannot be null!!");
            }
            this.localDetail.serialize(new javax.xml.namespace.QName("", "detail"), xmlWriter);
        }
        xmlWriter.writeEndElement();


    }

    private static String generatePrefix(final String namespace) {
        if ("http://schemas.xmlsoap.org/soap/envelope/".equals(namespace)) {
            return "ns2";
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
            if (namespace.isEmpty()) {
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
        if (!attributePrefix.trim().isEmpty()) {
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

            if (!prefix.trim().isEmpty()) {
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
                    if ((prefix == null) || (prefix.isEmpty())) {
                        prefix = generatePrefix(namespaceURI);
                        xmlWriter.writeNamespace(prefix, namespaceURI);
                        xmlWriter.setPrefix(prefix, namespaceURI);
                    }

                    if (!prefix.trim().isEmpty()) {
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
                if (uri == null || uri.isEmpty()) {
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

        if (this.localFaultcodeTracker) {
            elementList.add(new javax.xml.namespace.QName("", "faultcode"));

            if (this.localFaultcode != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFaultcode));
            } else {
                throw new org.apache.axis2.databinding.ADBException("faultcode cannot be null!!");
            }
        }
        if (this.localFaultstringTracker) {
            elementList.add(new javax.xml.namespace.QName("", "faultstring"));

            if (this.localFaultstring != null) {
                elementList
                        .add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFaultstring));
            } else {
                throw new org.apache.axis2.databinding.ADBException("faultstring cannot be null!!");
            }
        }
        if (this.localFaultactorTracker) {
            elementList.add(new javax.xml.namespace.QName("", "faultactor"));

            if (this.localFaultactor != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localFaultactor));
            } else {
                throw new org.apache.axis2.databinding.ADBException("faultactor cannot be null!!");
            }
        }
        if (this.localDetailTracker) {
            elementList.add(new javax.xml.namespace.QName("", "detail"));


            if (this.localDetail == null) {
                throw new org.apache.axis2.databinding.ADBException("detail cannot be null!!");
            }
            elementList.add(this.localDetail);
        }

        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                                                                                    attribList.toArray());


    }


    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {


        /**
         * static method to create the object
         * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
         * If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
         * Postcondition: If this object is an element, the reader is positioned at its end element
         * If this object is a complex type, the reader is positioned at the end element of its outer element
         */
        public static Fault parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final Fault object = new Fault();

            int event;
            String nillableValue = null;
            String prefix = "";
            String namespaceuri = "";
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

                        if (!"Fault".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Fault) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


                reader.next();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("", "faultcode").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException(
                                "The element: " + "faultcode" + "  cannot be null");
                    }


                    final String content = reader.getElementText();

                    final int index = content.indexOf(":");
                    if (index > 0) {
                        prefix = content.substring(0, index);
                    } else {
                        prefix = "";
                    }
                    namespaceuri = reader.getNamespaceURI(prefix);
                    object.setFaultcode(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToQName(content, namespaceuri));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("", "faultstring").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException(
                                "The element: " + "faultstring" + "  cannot be null");
                    }


                    final String content = reader.getElementText();

                    object.setFaultstring(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("", "faultactor").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException(
                                "The element: " + "faultactor" + "  cannot be null");
                    }


                    final String content = reader.getElementText();

                    object.setFaultactor(org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(content));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("", "detail").equals(reader.getName())) {

                    object.setDetail(org.xmlsoap.schemas.soap.envelope.Detail.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

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
           
    