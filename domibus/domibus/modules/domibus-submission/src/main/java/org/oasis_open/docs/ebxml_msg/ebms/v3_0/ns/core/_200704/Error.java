/**
 * Error.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;


/**
 * Error bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Error implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Error
                Namespace URI = http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/
                Namespace Prefix = ns4
                */


    /**
     * field for Description
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description localDescription;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localDescriptionTracker = false;

    public boolean isDescriptionSpecified() {
        return this.localDescriptionTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description getDescription() {
        return this.localDescription;
    }


    /**
     * Auto generated setter method
     *
     * @param param Description
     */
    public void setDescription(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description param) {
        this.localDescriptionTracker = param != null;

        this.localDescription = param;


    }


    /**
     * field for ErrorDetail
     */


    protected org.apache.axis2.databinding.types.Token localErrorDetail;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localErrorDetailTracker = false;

    public boolean isErrorDetailSpecified() {
        return this.localErrorDetailTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getErrorDetail() {
        return this.localErrorDetail;
    }


    /**
     * Auto generated setter method
     *
     * @param param ErrorDetail
     */
    public void setErrorDetail(final org.apache.axis2.databinding.types.Token param) {
        this.localErrorDetailTracker = param != null;

        this.localErrorDetail = param;


    }


    /**
     * field for Category
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.Token localCategory;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getCategory() {
        return this.localCategory;
    }


    /**
     * Auto generated setter method
     *
     * @param param Category
     */
    public void setCategory(final org.apache.axis2.databinding.types.Token param) {

        this.localCategory = param;


    }


    /**
     * field for RefToMessageInError
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.Token localRefToMessageInError;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getRefToMessageInError() {
        return this.localRefToMessageInError;
    }


    /**
     * Auto generated setter method
     *
     * @param param RefToMessageInError
     */
    public void setRefToMessageInError(final org.apache.axis2.databinding.types.Token param) {

        this.localRefToMessageInError = param;


    }


    /**
     * field for ErrorCode
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.Token localErrorCode;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getErrorCode() {
        return this.localErrorCode;
    }


    /**
     * Auto generated setter method
     *
     * @param param ErrorCode
     */
    public void setErrorCode(final org.apache.axis2.databinding.types.Token param) {

        this.localErrorCode = param;


    }


    /**
     * field for Origin
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.Token localOrigin;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getOrigin() {
        return this.localOrigin;
    }


    /**
     * Auto generated setter method
     *
     * @param param Origin
     */
    public void setOrigin(final org.apache.axis2.databinding.types.Token param) {

        this.localOrigin = param;


    }


    /**
     * field for Severity
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.Token localSeverity;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getSeverity() {
        return this.localSeverity;
    }


    /**
     * Auto generated setter method
     *
     * @param param Severity
     */
    public void setSeverity(final org.apache.axis2.databinding.types.Token param) {

        this.localSeverity = param;


    }


    /**
     * field for ShortDescription
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.Token localShortDescription;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getShortDescription() {
        return this.localShortDescription;
    }


    /**
     * Auto generated setter method
     *
     * @param param ShortDescription
     */
    public void setShortDescription(final org.apache.axis2.databinding.types.Token param) {

        this.localShortDescription = param;


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


            final String namespacePrefix =
                    registerPrefix(xmlWriter, "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":Error",
                               xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Error", xmlWriter);
            }


        }

        if (this.localCategory != null) {

            writeAttribute("", "category",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCategory),
                           xmlWriter);


        }

        if (this.localRefToMessageInError != null) {

            writeAttribute("", "refToMessageInError", org.apache.axis2.databinding.utils.ConverterUtil
                    .convertToString(this.localRefToMessageInError), xmlWriter);


        }

        if (this.localErrorCode != null) {

            writeAttribute("", "errorCode",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localErrorCode),
                           xmlWriter);


        } else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localErrorCode is null");
        }

        if (this.localOrigin != null) {

            writeAttribute("", "origin",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localOrigin),
                           xmlWriter);


        }

        if (this.localSeverity != null) {

            writeAttribute("", "severity",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSeverity),
                           xmlWriter);


        } else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localSeverity is null");
        }

        if (this.localShortDescription != null) {

            writeAttribute("", "shortDescription",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localShortDescription),
                           xmlWriter);


        }
        if (this.localDescriptionTracker) {
            if (this.localDescription == null) {
                throw new org.apache.axis2.databinding.ADBException("Description cannot be null!!");
            }
            this.localDescription.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "Description"), xmlWriter);
        }
        if (this.localErrorDetailTracker) {
            namespace = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/";
            writeStartElement(null, namespace, "ErrorDetail", xmlWriter);


            if (this.localErrorDetail == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("ErrorDetail cannot be null!!");

            } else {


                xmlWriter.writeCharacters(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localErrorDetail));

            }

            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();


    }

    private static String generatePrefix(final String namespace) {
        if ("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/".equals(namespace)) {
            return "ns4";
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

        if (this.localDescriptionTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "Description"));


            if (this.localDescription == null) {
                throw new org.apache.axis2.databinding.ADBException("Description cannot be null!!");
            }
            elementList.add(this.localDescription);
        }
        if (this.localErrorDetailTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "ErrorDetail"));

            if (this.localErrorDetail != null) {
                elementList
                        .add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localErrorDetail));
            } else {
                throw new org.apache.axis2.databinding.ADBException("ErrorDetail cannot be null!!");
            }
        }
        attribList.add(new javax.xml.namespace.QName("", "category"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCategory));

        attribList.add(new javax.xml.namespace.QName("", "refToMessageInError"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localRefToMessageInError));

        attribList.add(new javax.xml.namespace.QName("", "errorCode"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localErrorCode));

        attribList.add(new javax.xml.namespace.QName("", "origin"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localOrigin));

        attribList.add(new javax.xml.namespace.QName("", "severity"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSeverity));

        attribList.add(new javax.xml.namespace.QName("", "shortDescription"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localShortDescription));


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
        public static Error parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final Error object = new Error();

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

                        if (!"Error".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Error) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


                // handle attribute "category"
                final String tempAttribCategory =

                        reader.getAttributeValue(null, "category");

                if (tempAttribCategory != null) {
                    final String content = tempAttribCategory;

                    object.setCategory(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToToken(tempAttribCategory));

                } else {

                }
                handledAttributes.add("category");

                // handle attribute "refToMessageInError"
                final String tempAttribRefToMessageInError =

                        reader.getAttributeValue(null, "refToMessageInError");

                if (tempAttribRefToMessageInError != null) {
                    final String content = tempAttribRefToMessageInError;

                    object.setRefToMessageInError(org.apache.axis2.databinding.utils.ConverterUtil
                                                          .convertToToken(tempAttribRefToMessageInError));

                } else {

                }
                handledAttributes.add("refToMessageInError");

                // handle attribute "errorCode"
                final String tempAttribErrorCode =

                        reader.getAttributeValue(null, "errorCode");

                if (tempAttribErrorCode != null) {
                    final String content = tempAttribErrorCode;

                    object.setErrorCode(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToToken(tempAttribErrorCode));

                } else {

                    throw new org.apache.axis2.databinding.ADBException("Required attribute errorCode is missing");

                }
                handledAttributes.add("errorCode");

                // handle attribute "origin"
                final String tempAttribOrigin =

                        reader.getAttributeValue(null, "origin");

                if (tempAttribOrigin != null) {
                    final String content = tempAttribOrigin;

                    object.setOrigin(org.apache.axis2.databinding.utils.ConverterUtil.convertToToken(tempAttribOrigin));

                } else {

                }
                handledAttributes.add("origin");

                // handle attribute "severity"
                final String tempAttribSeverity =

                        reader.getAttributeValue(null, "severity");

                if (tempAttribSeverity != null) {
                    final String content = tempAttribSeverity;

                    object.setSeverity(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToToken(tempAttribSeverity));

                } else {

                    throw new org.apache.axis2.databinding.ADBException("Required attribute severity is missing");

                }
                handledAttributes.add("severity");

                // handle attribute "shortDescription"
                final String tempAttribShortDescription =

                        reader.getAttributeValue(null, "shortDescription");

                if (tempAttribShortDescription != null) {
                    final String content = tempAttribShortDescription;

                    object.setShortDescription(org.apache.axis2.databinding.utils.ConverterUtil
                                                       .convertToToken(tempAttribShortDescription));

                } else {

                }
                handledAttributes.add("shortDescription");


                reader.next();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "Description").equals(reader.getName())) {

                    object.setDescription(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "ErrorDetail").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException(
                                "The element: " + "ErrorDetail" + "  cannot be null");
                    }


                    final String content = reader.getElementText();

                    object.setErrorDetail(org.apache.axis2.databinding.utils.ConverterUtil.convertToToken(content));

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
           
    