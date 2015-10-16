/**
 * Schema.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;


/**
 * Schema bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Schema implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Schema
                Namespace URI = http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/
                Namespace Prefix = ns4
                */


    /**
     * field for Location
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.URI localLocation;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.URI
     */
    public org.apache.axis2.databinding.types.URI getLocation() {
        return this.localLocation;
    }


    /**
     * Auto generated setter method
     *
     * @param param Location
     */
    public void setLocation(final org.apache.axis2.databinding.types.URI param) {

        this.localLocation = param;


    }


    /**
     * field for Version
     * This was an Attribute!
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString localVersion;


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString getVersion() {
        return this.localVersion;
    }


    /**
     * Auto generated setter method
     *
     * @param param Version
     */
    public void setVersion(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString param) {

        this.localVersion = param;


    }


    /**
     * field for Namespace
     * This was an Attribute!
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString localNamespace;


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString getNamespace() {
        return this.localNamespace;
    }


    /**
     * Auto generated setter method
     *
     * @param param Namespace
     */
    public void setNamespace(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString param) {

        this.localNamespace = param;


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
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":Schema",
                               xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Schema", xmlWriter);
            }


        }

        if (this.localLocation != null) {

            writeAttribute("", "location",
                           org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLocation),
                           xmlWriter);


        } else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localLocation is null");
        }


        if (this.localVersion != null) {
            writeAttribute("", "version", this.localVersion.toString(), xmlWriter);
        }


        if (this.localNamespace != null) {
            writeAttribute("", "namespace", this.localNamespace.toString(), xmlWriter);
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


        attribList.add(new javax.xml.namespace.QName("", "location"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLocation));

        attribList.add(new javax.xml.namespace.QName("", "version"));

        attribList.add(this.localVersion.toString());

        attribList.add(new javax.xml.namespace.QName("", "namespace"));

        attribList.add(this.localNamespace.toString());


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
        public static Schema parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final Schema object = new Schema();

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

                        if (!"Schema".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Schema) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


                // handle attribute "location"
                final String tempAttribLocation =

                        reader.getAttributeValue(null, "location");

                if (tempAttribLocation != null) {
                    final String content = tempAttribLocation;

                    object.setLocation(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(tempAttribLocation));

                } else {

                    throw new org.apache.axis2.databinding.ADBException("Required attribute location is missing");

                }
                handledAttributes.add("location");

                // handle attribute "version"
                final String tempAttribVersion =

                        reader.getAttributeValue(null, "version");

                if (tempAttribVersion != null) {
                    final String content = tempAttribVersion;

                    object.setVersion(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString.Factory
                                              .fromString(reader, tempAttribVersion));

                } else {

                }
                handledAttributes.add("version");

                // handle attribute "namespace"
                final String tempAttribNamespace =

                        reader.getAttributeValue(null, "namespace");

                if (tempAttribNamespace != null) {
                    final String content = tempAttribNamespace;

                    object.setNamespace(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.NonEmptyString.Factory
                                                .fromString(reader, tempAttribNamespace));

                } else {

                }
                handledAttributes.add("namespace");


                reader.next();


            } catch (javax.xml.stream.XMLStreamException e) {
                throw new Exception(e);
            }

            return object;
        }

    }//end of factory class


}
           
    