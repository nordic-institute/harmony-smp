/**
 * Envelope.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.w3.www._2003._05.soap_envelope;


/**
 * Envelope bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Envelope implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Envelope
                Namespace URI = http://www.w3.org/2003/05/soap-envelope
                Namespace Prefix = ns3
                */


    /**
     * field for Header
     */


    protected Header localHeader;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localHeaderTracker = false;

    public boolean isHeaderSpecified() {
        return this.localHeaderTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.w3.www._2003._05.soap_envelope.Header
     */
    public Header getHeader() {
        return this.localHeader;
    }


    /**
     * Auto generated setter method
     *
     * @param param Header
     */
    public void setHeader(final Header param) {
        this.localHeaderTracker = param != null;

        this.localHeader = param;


    }


    /**
     * field for Body
     */


    protected Body localBody;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localBodyTracker = false;

    public boolean isBodySpecified() {
        return this.localBodyTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.w3.www._2003._05.soap_envelope.Body
     */
    public Body getBody() {
        return this.localBody;
    }


    /**
     * Auto generated setter method
     *
     * @param param Body
     */
    public void setBody(final Body param) {
        this.localBodyTracker = param != null;

        this.localBody = param;


    }


    /**
     * field for ExtraAttributes
     * This was an Attribute!
     * This was an Array!
     */


    protected org.apache.axiom.om.OMAttribute[] localExtraAttributes;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.om.OMAttribute[]
     */
    public org.apache.axiom.om.OMAttribute[] getExtraAttributes() {
        return this.localExtraAttributes;
    }


    /**
     * validate the array for ExtraAttributes
     */
    protected void validateExtraAttributes(final org.apache.axiom.om.OMAttribute[] param) {

        if ((param != null) && (param.length > 1)) {
            throw new RuntimeException();
        }

        if ((param != null) && (param.length < 1)) {
            throw new RuntimeException();
        }

    }


    /**
     * Auto generated setter method
     *
     * @param param ExtraAttributes
     */
    public void setExtraAttributes(final org.apache.axiom.om.OMAttribute[] param) {

        validateExtraAttributes(param);


        this.localExtraAttributes = param;
    }


    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.apache.axiom.om.OMAttribute
     */
    public void addExtraAttributes(final org.apache.axiom.om.OMAttribute param) {
        if (this.localExtraAttributes == null) {
            this.localExtraAttributes = new org.apache.axiom.om.OMAttribute[]{};
        }


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localExtraAttributes);
        list.add(param);
        this.localExtraAttributes =
                (org.apache.axiom.om.OMAttribute[]) list.toArray(new org.apache.axiom.om.OMAttribute[list.size()]);

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


            final String namespacePrefix = registerPrefix(xmlWriter, "http://www.w3.org/2003/05/soap-envelope");
            if ((namespacePrefix != null) && (!namespacePrefix.trim().isEmpty())) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":Envelope", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Envelope", xmlWriter);
            }


        }

        if (this.localExtraAttributes != null) {
            for (int i = 0; i < this.localExtraAttributes.length; i++) {
                writeAttribute(this.localExtraAttributes[i].getNamespace().getName(),
                               this.localExtraAttributes[i].getLocalName(),
                               this.localExtraAttributes[i].getAttributeValue(), xmlWriter);
            }
        }
        if (this.localHeaderTracker) {
            if (this.localHeader == null) {
                throw new org.apache.axis2.databinding.ADBException("Header cannot be null!!");
            }
            this.localHeader
                    .serialize(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Header"),
                               xmlWriter);
        }
        if (this.localBodyTracker) {
            if (this.localBody == null) {
                throw new org.apache.axis2.databinding.ADBException("Body cannot be null!!");
            }
            this.localBody.serialize(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Body"),
                                     xmlWriter);
        }
        xmlWriter.writeEndElement();


    }

    private static String generatePrefix(final String namespace) {
        if ("http://www.w3.org/2003/05/soap-envelope".equals(namespace)) {
            return "ns3";
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

        if (this.localHeaderTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Header"));


            if (this.localHeader == null) {
                throw new org.apache.axis2.databinding.ADBException("Header cannot be null!!");
            }
            elementList.add(this.localHeader);
        }
        if (this.localBodyTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Body"));


            if (this.localBody == null) {
                throw new org.apache.axis2.databinding.ADBException("Body cannot be null!!");
            }
            elementList.add(this.localBody);
        }
        for (int i = 0; i < this.localExtraAttributes.length; i++) {
            attribList.add(org.apache.axis2.databinding.utils.Constants.OM_ATTRIBUTE_KEY);
            attribList.add(this.localExtraAttributes[i]);
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
        public static Envelope parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final Envelope object = new Envelope();

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

                        if (!"Envelope".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Envelope) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


                // now run through all any or extra attributes
                // which were not reflected until now
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    if (!handledAttributes.contains(reader.getAttributeLocalName(i))) {
                        // this is an anyAttribute and we create
                        // an OMAttribute for this
                        final org.apache.axiom.om.OMFactory factory =
                                org.apache.axiom.om.OMAbstractFactory.getOMFactory();
                        final org.apache.axiom.om.OMAttribute attr =
                                factory.createOMAttribute(reader.getAttributeLocalName(i),
                                                          factory.createOMNamespace(reader.getAttributeNamespace(i),
                                                                                    reader.getAttributePrefix(i)),
                                                          reader.getAttributeValue(i));

                        // and add it to the extra attributes

                        object.addExtraAttributes(attr);


                    }
                }


                reader.next();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Header")
                            .equals(reader.getName())) {

                    object.setHeader(Header.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Body")
                            .equals(reader.getName())) {

                    object.setBody(Body.Factory.parse(reader));

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
           
    