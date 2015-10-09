/**
 * Base64Binary.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

package org.w3.www._2005._05.xmlmime;

/**
 * Base64Binary bean class
 */
@SuppressWarnings({"unchecked", "unused"})
public class Base64Binary implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = base64Binary Namespace URI =
	 * http://www.w3.org/2005/05/xmlmime Namespace Prefix = ns1
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
        return this.localBase64Binary;
    }

    /**
     * Auto generated setter method
     *
     * @param param Base64Binary
     */
    public void setBase64Binary(final javax.activation.DataHandler param) {

        this.localBase64Binary = param;

    }

    public String toString() {

        return this.localBase64Binary.toString();

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
        return this.localContentType;
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

        String prefix = null;
        String namespace = null;

        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();
        writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

        if (serializeType) {

            final String namespacePrefix = registerPrefix(xmlWriter, "http://www.w3.org/2005/05/xmlmime");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":base64Binary", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "base64Binary", xmlWriter);
            }

        }

        if (this.localContentType != null) {
            writeAttribute("http://www.w3.org/2005/05/xmlmime", "contentType", this.localContentType.toString(),
                           xmlWriter);
        }

        if (this.localBase64Binary != null) {
            try {
                org.apache.axiom.util.stax.XMLStreamWriterUtils
                        .writeDataHandler(xmlWriter, this.localBase64Binary, null, true);
            } catch (java.io.IOException ex) {
                throw new javax.xml.stream.XMLStreamException("Unable to read data handler for base64Binary", ex);
            }
        } else {

        }

        xmlWriter.writeEndElement();

    }

    private static String generatePrefix(final String namespace) {
        if ("http://www.w3.org/2005/05/xmlmime".equals(namespace)) {
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

        elementList.add(org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT);

        elementList.add(this.localBase64Binary);

        attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2005/05/xmlmime", "contentType"));

        attribList.add(this.localContentType.toString());

        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                                                                                    attribList.toArray());

    }

    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {

        public static Base64Binary fromString(final String value, final String namespaceURI) {
            final Base64Binary returnValue = new Base64Binary();

            returnValue.setBase64Binary(org.apache.axis2.databinding.utils.ConverterUtil.convertToBase64Binary(value));

            return returnValue;
        }

        public static Base64Binary fromString(final javax.xml.stream.XMLStreamReader xmlStreamReader,
                                              final String content) {
            if (content.indexOf(":") > -1) {
                final String prefix = content.substring(0, content.indexOf(":"));
                final String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
                return Factory.fromString(content, namespaceUri);
            } else {
                return Factory.fromString(content, "");
            }
        }

        /**
         * static method to create the object Precondition: If this object is an element, the current or next start
         * element starts this object and any intervening reader events are ignorable If this object is not an element,
         * it is a complex type and the reader is at the event just after the outer start element Postcondition: If this
         * object is an element, the reader is positioned at its end element If this object is a complex type, the
         * reader is positioned at the end element of its outer element
         */
        public static Base64Binary parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final Base64Binary object = new Base64Binary();

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

                        if (!"base64Binary".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Base64Binary) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }

                    }

                }

                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();

                // handle attribute "contentType"
                final String tempAttribContentType =

                        reader.getAttributeValue("http://www.w3.org/2005/05/xmlmime", "contentType");

                if (tempAttribContentType != null) {
                    final String content = tempAttribContentType;

                    object.setContentType(org.w3.www._2005._05.xmlmime.ContentType_type0.Factory
                                                  .fromString(reader, tempAttribContentType));

                } else {

                }
                handledAttributes.add("contentType");

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
                throw new Exception(e);
            }

            return object;
        }

    }//end of factory class

}
