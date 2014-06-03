/**
 * PayloadType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

package backend.ecodex.org._1_1;

/**
 * PayloadType bean class
 */
@SuppressWarnings({"unchecked", "unused"})
public class PayloadType implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = PayloadType Namespace URI =
	 * http://org.ecodex.backend/1_1/ Namespace Prefix = ns1
	 */

    /**
     * field for Base64Binary
     */

    protected javax.activation.DataHandler localBase64Binary;

    /**
     * Auto generated getter method
     *
     * @return javax.activation.DataHandler
     */
    public javax.activation.DataHandler getBase64Binary() {
        return localBase64Binary;
    }

    /**
     * Auto generated setter method
     *
     * @param param Base64Binary
     */
    public void setBase64Binary(final javax.activation.DataHandler param) {

        this.localBase64Binary = param;

    }

    public java.lang.String toString() {

        return localBase64Binary.toString();

    }

    /**
     * field for PayloadId This was an Attribute!
     */

    protected org.apache.axis2.databinding.types.Token localPayloadId;

    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Token
     */
    public org.apache.axis2.databinding.types.Token getPayloadId() {
        return localPayloadId;
    }

    /**
     * Auto generated setter method
     *
     * @param param PayloadId
     */
    public void setPayloadId(final org.apache.axis2.databinding.types.Token param) {

        this.localPayloadId = param;

    }

    /**
     * field for ContentType This was an Attribute!
     */

    protected org.w3.www._2005._05.xmlmime.ContentType_type0 localContentType;

    /**
     * Auto generated getter method
     *
     * @return org.w3.www._2005._05.xmlmime.ContentType_type0
     */
    public org.w3.www._2005._05.xmlmime.ContentType_type0 getContentType() {
        return localContentType;
    }

    /**
     * Auto generated setter method
     *
     * @param param ContentType
     */
    public void setContentType(final org.w3.www._2005._05.xmlmime.ContentType_type0 param) {

        this.localContentType = param;

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

        java.lang.String prefix = null;
        java.lang.String namespace = null;

        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();
        writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

        if (serializeType) {

            final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://org.ecodex.backend/1_1/");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":PayloadType", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "PayloadType", xmlWriter);
            }

        }

        if (localContentType != null) {
            writeAttribute("http://www.w3.org/2005/05/xmlmime", "contentType", localContentType.toString(), xmlWriter);
        }

        if (localPayloadId != null) {

            writeAttribute("", "payloadId",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPayloadId), xmlWriter);

        }

        if (localBase64Binary != null) {
            try {
                org.apache.axiom.util.stax.XMLStreamWriterUtils
                                          .writeDataHandler(xmlWriter, localBase64Binary, null, true);
            } catch (java.io.IOException ex) {
                throw new javax.xml.stream.XMLStreamException("Unable to read data handler for base64Binary", ex);
            }
        } else {

        }

        xmlWriter.writeEndElement();

    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://org.ecodex.backend/1_1/")) {
            return "ns1";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, final java.lang.String namespace,
                                   final java.lang.String localPart, final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        final java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
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
    private void writeAttribute(final java.lang.String prefix, final java.lang.String namespace,
                                final java.lang.String attName, final java.lang.String attValue,
                                final javax.xml.stream.XMLStreamWriter xmlWriter)
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
    private void writeAttribute(final java.lang.String namespace, final java.lang.String attName,
                                final java.lang.String attValue, final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
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
    private void writeQNameAttribute(final java.lang.String namespace, final java.lang.String attName,
                                     final javax.xml.namespace.QName qname,
                                     final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {

        final java.lang.String attributeNamespace = qname.getNamespaceURI();
        java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
        if (attributePrefix == null) {
            attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
        }
        final java.lang.String attributeValue;
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
     * method to handle Qnames
     */

    private void writeQName(final javax.xml.namespace.QName qname, final javax.xml.stream.XMLStreamWriter xmlWriter)
            throws javax.xml.stream.XMLStreamException {
        final java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
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
            final java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
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
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil
                                                                                                          .convertToString(
                                                                                                                  qnames[i]));
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
    private java.lang.String registerPrefix(final javax.xml.stream.XMLStreamWriter xmlWriter,
                                            final java.lang.String namespace)
            throws javax.xml.stream.XMLStreamException {
        java.lang.String prefix = xmlWriter.getPrefix(namespace);
        if (prefix == null) {
            prefix = generatePrefix(namespace);
            final javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
            while (true) {
                final java.lang.String uri = nsContext.getNamespaceURI(prefix);
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

        elementList.add(org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT);

        elementList.add(localBase64Binary);

        attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2005/05/xmlmime", "contentType"));

        attribList.add(localContentType.toString());

        attribList.add(new javax.xml.namespace.QName("", "payloadId"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPayloadId));

        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                                                                                    attribList.toArray());

    }

    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {

        public static PayloadType fromString(final java.lang.String value, final java.lang.String namespaceURI) {
            final PayloadType returnValue = new PayloadType();

            returnValue.setBase64Binary(org.apache.axis2.databinding.utils.ConverterUtil.convertToBase64Binary(value));

            return returnValue;
        }

        public static PayloadType fromString(final javax.xml.stream.XMLStreamReader xmlStreamReader,
                                             final java.lang.String content) {
            if (content.indexOf(":") > -1) {
                final java.lang.String prefix = content.substring(0, content.indexOf(":"));
                final java.lang.String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
                return PayloadType.Factory.fromString(content, namespaceUri);
            } else {
                return PayloadType.Factory.fromString(content, "");
            }
        }

        /**
         * static method to create the object Precondition: If this object is an element, the current or next start
         * element starts this object and any intervening reader events are ignorable If this object is not an element,
         * it is a complex type and the reader is at the event just after the outer start element Postcondition: If this
         * object is an element, the reader is positioned at its end element If this object is a complex type, the
         * reader is positioned at the end element of its outer element
         */
        public static PayloadType parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final PayloadType object = new PayloadType();

            int event;
            final java.lang.String nillableValue = null;
            final java.lang.String prefix = "";
            final java.lang.String namespaceuri = "";
            try {

                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                    final java.lang.String fullTypeName =
                            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
                    if (fullTypeName != null) {
                        java.lang.String nsPrefix = null;
                        if (fullTypeName.indexOf(":") > -1) {
                            nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                        }
                        nsPrefix = nsPrefix == null ? "" : nsPrefix;

                        final java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                        if (!"PayloadType".equals(type)) {
                            //find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (PayloadType) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }

                    }

                }

                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();

                // handle attribute "contentType"
                {
                    final java.lang.String tempAttribContentType =

                            reader.getAttributeValue("http://www.w3.org/2005/05/xmlmime", "contentType");

                    if (tempAttribContentType != null) {
                        final java.lang.String content = tempAttribContentType;

                        object.setContentType(org.w3.www._2005._05.xmlmime.ContentType_type0.Factory.fromString(reader,
                                                                                                                tempAttribContentType));

                    } else {

                    }
                    handledAttributes.add("contentType");
                }

                // handle attribute "payloadId"
                {
                    final java.lang.String tempAttribPayloadId =

                            reader.getAttributeValue(null, "payloadId");

                    if (tempAttribPayloadId != null) {
                        final java.lang.String content = tempAttribPayloadId;

                        object.setPayloadId(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToToken(tempAttribPayloadId));

                    } else {

                    }
                    handledAttributes.add("payloadId");
                }

                while (!reader.isEndElement()) {
                    if (reader.isStartElement() || reader.hasText()) {

                        if (reader.isStartElement() || reader.hasText()) {

                            object.setBase64Binary(
                                    org.apache.axiom.util.stax.XMLStreamReaderUtils.getDataHandlerFromElement(reader));

                        } // End of if for expected property start element

                        else {
                            // A start element we are not expecting indicates an invalid parameter was passed
                            throw new org.apache.axis2.databinding.ADBException(
                                    "Unexpected subelement " + reader.getName());
                        }

                    } else {
                        reader.next();
                    }
                } // end of while loop

            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }//end of factory class

}
