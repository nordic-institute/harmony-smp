/**
 * Fault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.w3.www._2003._05.soap_envelope;


/**
 * Fault bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Fault implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Fault
                Namespace URI = http://www.w3.org/2003/05/soap-envelope
                Namespace Prefix = ns3
                */


    /**
     * field for Code
     */


    protected org.w3.www._2003._05.soap_envelope.Faultcode localCode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localCodeTracker = false;

    public boolean isCodeSpecified() {
        return this.localCodeTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.w3.www._2003._05.soap_envelope.Faultcode
     */
    public org.w3.www._2003._05.soap_envelope.Faultcode getCode() {
        return this.localCode;
    }


    /**
     * Auto generated setter method
     *
     * @param param Code
     */
    public void setCode(final org.w3.www._2003._05.soap_envelope.Faultcode param) {
        this.localCodeTracker = param != null;

        this.localCode = param;


    }


    /**
     * field for Reason
     */


    protected org.w3.www._2003._05.soap_envelope.Faultreason localReason;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localReasonTracker = false;

    public boolean isReasonSpecified() {
        return this.localReasonTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.w3.www._2003._05.soap_envelope.Faultreason
     */
    public org.w3.www._2003._05.soap_envelope.Faultreason getReason() {
        return this.localReason;
    }


    /**
     * Auto generated setter method
     *
     * @param param Reason
     */
    public void setReason(final org.w3.www._2003._05.soap_envelope.Faultreason param) {
        this.localReasonTracker = param != null;

        this.localReason = param;


    }


    /**
     * field for Node
     */


    protected org.apache.axis2.databinding.types.URI localNode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localNodeTracker = false;

    public boolean isNodeSpecified() {
        return this.localNodeTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.URI
     */
    public org.apache.axis2.databinding.types.URI getNode() {
        return this.localNode;
    }


    /**
     * Auto generated setter method
     *
     * @param param Node
     */
    public void setNode(final org.apache.axis2.databinding.types.URI param) {
        this.localNodeTracker = param != null;

        this.localNode = param;


    }


    /**
     * field for Role
     */


    protected org.apache.axis2.databinding.types.URI localRole;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localRoleTracker = false;

    public boolean isRoleSpecified() {
        return this.localRoleTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.URI
     */
    public org.apache.axis2.databinding.types.URI getRole() {
        return this.localRole;
    }


    /**
     * Auto generated setter method
     *
     * @param param Role
     */
    public void setRole(final org.apache.axis2.databinding.types.URI param) {
        this.localRoleTracker = param != null;

        this.localRole = param;


    }


    /**
     * field for Detail
     */


    protected org.w3.www._2003._05.soap_envelope.Detail localDetail;

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
     * @return org.w3.www._2003._05.soap_envelope.Detail
     */
    public org.w3.www._2003._05.soap_envelope.Detail getDetail() {
        return this.localDetail;
    }


    /**
     * Auto generated setter method
     *
     * @param param Detail
     */
    public void setDetail(final org.w3.www._2003._05.soap_envelope.Detail param) {
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


            final String namespacePrefix = registerPrefix(xmlWriter, "http://www.w3.org/2003/05/soap-envelope");
            if ((namespacePrefix != null) && (!namespacePrefix.trim().isEmpty())) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":Fault",
                               xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Fault", xmlWriter);
            }


        }
        if (this.localCodeTracker) {
            if (this.localCode == null) {
                throw new org.apache.axis2.databinding.ADBException("Code cannot be null!!");
            }
            this.localCode.serialize(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Code"),
                                     xmlWriter);
        }
        if (this.localReasonTracker) {
            if (this.localReason == null) {
                throw new org.apache.axis2.databinding.ADBException("Reason cannot be null!!");
            }
            this.localReason
                    .serialize(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Reason"),
                               xmlWriter);
        }
        if (this.localNodeTracker) {
            namespace = "http://www.w3.org/2003/05/soap-envelope";
            writeStartElement(null, namespace, "Node", xmlWriter);


            if (this.localNode == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("Node cannot be null!!");

            } else {


                xmlWriter.writeCharacters(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localNode));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localRoleTracker) {
            namespace = "http://www.w3.org/2003/05/soap-envelope";
            writeStartElement(null, namespace, "Role", xmlWriter);


            if (this.localRole == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("Role cannot be null!!");

            } else {


                xmlWriter.writeCharacters(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localRole));

            }

            xmlWriter.writeEndElement();
        }
        if (this.localDetailTracker) {
            if (this.localDetail == null) {
                throw new org.apache.axis2.databinding.ADBException("Detail cannot be null!!");
            }
            this.localDetail
                    .serialize(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Detail"),
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

        if (this.localCodeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Code"));


            if (this.localCode == null) {
                throw new org.apache.axis2.databinding.ADBException("Code cannot be null!!");
            }
            elementList.add(this.localCode);
        }
        if (this.localReasonTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Reason"));


            if (this.localReason == null) {
                throw new org.apache.axis2.databinding.ADBException("Reason cannot be null!!");
            }
            elementList.add(this.localReason);
        }
        if (this.localNodeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Node"));

            if (this.localNode != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localNode));
            } else {
                throw new org.apache.axis2.databinding.ADBException("Node cannot be null!!");
            }
        }
        if (this.localRoleTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Role"));

            if (this.localRole != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localRole));
            } else {
                throw new org.apache.axis2.databinding.ADBException("Role cannot be null!!");
            }
        }
        if (this.localDetailTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Detail"));


            if (this.localDetail == null) {
                throw new org.apache.axis2.databinding.ADBException("Detail cannot be null!!");
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
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Code")
                            .equals(reader.getName())) {

                    object.setCode(org.w3.www._2003._05.soap_envelope.Faultcode.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Reason")
                            .equals(reader.getName())) {

                    object.setReason(org.w3.www._2003._05.soap_envelope.Faultreason.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Node")
                            .equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException(
                                "The element: " + "Node" + "  cannot be null");
                    }


                    final String content = reader.getElementText();

                    object.setNode(org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(content));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Role")
                            .equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException(
                                "The element: " + "Role" + "  cannot be null");
                    }


                    final String content = reader.getElementText();

                    object.setRole(org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(content));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Detail")
                            .equals(reader.getName())) {

                    object.setDetail(org.w3.www._2003._05.soap_envelope.Detail.Factory.parse(reader));

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
           
    