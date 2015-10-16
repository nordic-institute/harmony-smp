
/**
 * CreatePartnershipRequest.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package backend.ecodex.org._1_1;


import backend.ecodex.org.ExtensionMapper;

/**
 *  CreatePartnershipRequest bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class CreatePartnershipRequest
        implements org.apache.axis2.databinding.ADBBean {

    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            "http://org.ecodex.backend/1_1/",
            "createPartnershipRequest",
            "ns2");


    /**
     * field for SenderId
     */


    protected java.lang.String localSenderId;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getSenderId() {
        return localSenderId;
    }


    /**
     * Auto generated setter method
     * @param param SenderId
     */
    public void setSenderId(java.lang.String param) {

        this.localSenderId = param;


    }


    /**
     * field for ReceiverId
     */


    protected java.lang.String localReceiverId;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getReceiverId() {
        return localReceiverId;
    }


    /**
     * Auto generated setter method
     * @param param ReceiverId
     */
    public void setReceiverId(java.lang.String param) {

        this.localReceiverId = param;


    }


    /**
     * field for Service
     */


    protected java.lang.String localService;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getService() {
        return localService;
    }


    /**
     * Auto generated setter method
     * @param param Service
     */
    public void setService(java.lang.String param) {

        this.localService = param;


    }


    /**
     * field for Action
     */


    protected java.lang.String localAction;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getAction() {
        return localAction;
    }


    /**
     * Auto generated setter method
     * @param param Action
     */
    public void setAction(java.lang.String param) {

        this.localAction = param;


    }


    /**
     * field for EndpointURL
     */


    protected java.lang.String localEndpointURL;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public java.lang.String getEndpointURL() {
        return localEndpointURL;
    }


    /**
     * Auto generated setter method
     * @param param EndpointURL
     */
    public void setEndpointURL(java.lang.String param) {

        this.localEndpointURL = param;


    }


    /**
     * field for Certificate
     */


    protected javax.activation.DataHandler localCertificate;


    /**
     * Auto generated getter method
     * @return javax.activation.DataHandler
     */
    public javax.activation.DataHandler getCertificate() {
        return localCertificate;
    }


    /**
     * Auto generated setter method
     * @param param Certificate
     */
    public void setCertificate(javax.activation.DataHandler param) {

        this.localCertificate = param;


    }


    /**
     *
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
            final javax.xml.namespace.QName parentQName,
            final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {


        org.apache.axiom.om.OMDataSource dataSource =
                new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME);
        return factory.createOMElement(dataSource, MY_QNAME);

    }

    public void serialize(final javax.xml.namespace.QName parentQName,
                          javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
        serialize(parentQName, xmlWriter, false);
    }

    public void serialize(final javax.xml.namespace.QName parentQName,
                          javax.xml.stream.XMLStreamWriter xmlWriter,
                          boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {


        java.lang.String prefix = null;
        java.lang.String namespace = null;


        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();
        writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

        if (serializeType) {


            java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://org.ecodex.backend/1_1/");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                        namespacePrefix + ":createPartnershipRequest",
                        xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                        "createPartnershipRequest",
                        xmlWriter);
            }


        }

        namespace = "";
        writeStartElement(null, namespace, "senderId", xmlWriter);


        if (localSenderId == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("senderId cannot be null!!");

        } else {


            xmlWriter.writeCharacters(localSenderId);

        }

        xmlWriter.writeEndElement();

        namespace = "";
        writeStartElement(null, namespace, "receiverId", xmlWriter);


        if (localReceiverId == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("receiverId cannot be null!!");

        } else {


            xmlWriter.writeCharacters(localReceiverId);

        }

        xmlWriter.writeEndElement();

        namespace = "";
        writeStartElement(null, namespace, "service", xmlWriter);


        if (localService == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("service cannot be null!!");

        } else {


            xmlWriter.writeCharacters(localService);

        }

        xmlWriter.writeEndElement();

        namespace = "";
        writeStartElement(null, namespace, "action", xmlWriter);


        if (localAction == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("action cannot be null!!");

        } else {


            xmlWriter.writeCharacters(localAction);

        }

        xmlWriter.writeEndElement();

        namespace = "";
        writeStartElement(null, namespace, "endpointURL", xmlWriter);


        if (localEndpointURL == null) {
            // write the nil attribute

            throw new org.apache.axis2.databinding.ADBException("endpointURL cannot be null!!");

        } else {


            xmlWriter.writeCharacters(localEndpointURL);

        }

        xmlWriter.writeEndElement();

        namespace = "";
        writeStartElement(null, namespace, "certificate", xmlWriter);


        if (localCertificate != null) {
            try {
                org.apache.axiom.util.stax.XMLStreamWriterUtils.writeDataHandler(xmlWriter, localCertificate, null, true);
            } catch (java.io.IOException ex) {
                throw new javax.xml.stream.XMLStreamException("Unable to read data handler for certificate", ex);
            }
        } else {

        }

        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(java.lang.String namespace) {
        if (namespace.equals("http://org.ecodex.backend/1_1/")) {
            return "ns2";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                   javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
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
    private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        if (xmlWriter.getPrefix(namespace) == null) {
            xmlWriter.writeNamespace(prefix, namespace);
            xmlWriter.setPrefix(prefix, namespace);
        }
        xmlWriter.writeAttribute(namespace, attName, attValue);
    }

    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeAttribute(java.lang.String namespace, java.lang.String attName,
                                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attValue);
        }
    }


    /**
     * Util method to write an attribute without the ns prefix
     */
    private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                     javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        java.lang.String attributeNamespace = qname.getNamespaceURI();
        java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
        if (attributePrefix == null) {
            attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
        }
        java.lang.String attributeValue;
        if (attributePrefix.trim().length() > 0) {
            attributeValue = attributePrefix + ":" + qname.getLocalPart();
        } else {
            attributeValue = qname.getLocalPart();
        }

        if (namespace.equals("")) {
            xmlWriter.writeAttribute(attName, attributeValue);
        } else {
            registerPrefix(xmlWriter, namespace);
            xmlWriter.writeAttribute(namespace, attName, attributeValue);
        }
    }

    /**
     *  method to handle Qnames
     */

    private void writeQName(javax.xml.namespace.QName qname,
                            javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
        java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(javax.xml.namespace.QName[] qnames,
                             javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

        if (qnames != null) {
            // we have to store this data until last moment since it is not possible to write any
            // namespace data after writing the charactor data
            java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
            java.lang.String namespaceURI = null;
            java.lang.String prefix = null;

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
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
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
    private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                java.lang.String uri = nsContext.getNamespaceURI(prefix);
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
     *
     */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException {


        java.util.ArrayList elementList = new java.util.ArrayList();
        java.util.ArrayList attribList = new java.util.ArrayList();


        elementList.add(new javax.xml.namespace.QName("",
                "senderId"));

        if (localSenderId != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSenderId));
        } else {
            throw new org.apache.axis2.databinding.ADBException("senderId cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("",
                "receiverId"));

        if (localReceiverId != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localReceiverId));
        } else {
            throw new org.apache.axis2.databinding.ADBException("receiverId cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("",
                "service"));

        if (localService != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localService));
        } else {
            throw new org.apache.axis2.databinding.ADBException("service cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("",
                "action"));

        if (localAction != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAction));
        } else {
            throw new org.apache.axis2.databinding.ADBException("action cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("",
                "endpointURL"));

        if (localEndpointURL != null) {
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEndpointURL));
        } else {
            throw new org.apache.axis2.databinding.ADBException("endpointURL cannot be null!!");
        }

        elementList.add(new javax.xml.namespace.QName("",
                "certificate"));

        elementList.add(localCertificate);


        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());


    }


    /**
     *  Factory class that keeps the parse method
     */
    public static class Factory {


        /**
         * static method to create the object
         * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
         *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
         * Postcondition: If this object is an element, the reader is positioned at its end element
         *                If this object is a complex type, the reader is positioned at the end element of its outer element
         */
        public static CreatePartnershipRequest parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            CreatePartnershipRequest object =
                    new CreatePartnershipRequest();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix = "";
            java.lang.String namespaceuri = "";
            try {

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();


                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                    java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                            "type");
                    if (fullTypeName != null) {
                        java.lang.String nsPrefix = null;
                        if (fullTypeName.indexOf(":") > -1) {
                            nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                        }
                        nsPrefix = nsPrefix == null ? "" : nsPrefix;

                        java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                        if (!"createPartnershipRequest".equals(type)) {
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (CreatePartnershipRequest) ExtensionMapper.getTypeObject(
                                    nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();


                reader.next();


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("", "senderId").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException("The element: " + "senderId" + "  cannot be null");
                    }


                    java.lang.String content = reader.getElementText();

                    object.setSenderId(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                }  // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("", "receiverId").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException("The element: " + "receiverId" + "  cannot be null");
                    }


                    java.lang.String content = reader.getElementText();

                    object.setReceiverId(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                }  // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("", "service").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException("The element: " + "service" + "  cannot be null");
                    }


                    java.lang.String content = reader.getElementText();

                    object.setService(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                }  // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("", "action").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException("The element: " + "action" + "  cannot be null");
                    }


                    java.lang.String content = reader.getElementText();

                    object.setAction(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                }  // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("", "endpointURL").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException("The element: " + "endpointURL" + "  cannot be null");
                    }


                    java.lang.String content = reader.getElementText();

                    object.setEndpointURL(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    reader.next();

                }  // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("", "certificate").equals(reader.getName())) {

                    object.setCertificate(org.apache.axiom.util.stax.XMLStreamReaderUtils.getDataHandlerFromElement(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                if (reader.isStartElement())
                    // A start element we are not expecting indicates a trailing invalid property
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());


            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }//end of factory class


}
           
    