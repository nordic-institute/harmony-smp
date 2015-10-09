/**
 * Subcode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.w3.www._2003._05.soap_envelope;


/**
 * Subcode bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Subcode implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = subcode
                Namespace URI = http://www.w3.org/2003/05/soap-envelope
                Namespace Prefix = ns3
                */


    /**
     * field for Value
     */


    protected javax.xml.namespace.QName localValue;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localValueTracker = false;

    public boolean isValueSpecified() {
        return this.localValueTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return javax.xml.namespace.QName
     */
    public javax.xml.namespace.QName getValue() {
        return this.localValue;
    }


    /**
     * Auto generated setter method
     *
     * @param param Value
     */
    public void setValue(final javax.xml.namespace.QName param) {
        this.localValueTracker = param != null;

        this.localValue = param;


    }


    /**
     * field for Subcode
     */


    protected Subcode localSubcode;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localSubcodeTracker = false;

    public boolean isSubcodeSpecified() {
        return this.localSubcodeTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.w3.www._2003._05.soap_envelope.Subcode
     */
    public Subcode getSubcode() {
        return this.localSubcode;
    }


    /**
     * Auto generated setter method
     *
     * @param param Subcode
     */
    public void setSubcode(final Subcode param) {
        this.localSubcodeTracker = param != null;

        this.localSubcode = param;


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
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix + ":subcode",
                               xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "subcode", xmlWriter);
            }


        }
        if (this.localValueTracker) {
            namespace = "http://www.w3.org/2003/05/soap-envelope";
            writeStartElement(null, namespace, "Value", xmlWriter);


            if (this.localValue == null) {
                // write the nil attribute

                throw new org.apache.axis2.databinding.ADBException("Value cannot be null!!");

            } else {


                writeQName(this.localValue, xmlWriter);

            }

            xmlWriter.writeEndElement();
        }
        if (this.localSubcodeTracker) {
            if (this.localSubcode == null) {
                throw new org.apache.axis2.databinding.ADBException("Subcode cannot be null!!");
            }
            this.localSubcode
                    .serialize(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Subcode"),
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

        if (this.localValueTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Value"));

            if (this.localValue != null) {
                elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localValue));
            } else {
                throw new org.apache.axis2.databinding.ADBException("Value cannot be null!!");
            }
        }
        if (this.localSubcodeTracker) {
            elementList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Subcode"));


            if (this.localSubcode == null) {
                throw new org.apache.axis2.databinding.ADBException("Subcode cannot be null!!");
            }
            elementList.add(this.localSubcode);
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
        public static Subcode parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final Subcode object = new Subcode();

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

                        if (!"subcode".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Subcode) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
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
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Value")
                            .equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        throw new org.apache.axis2.databinding.ADBException(
                                "The element: " + "Value" + "  cannot be null");
                    }


                    final String content = reader.getElementText();

                    final int index = content.indexOf(":");
                    if (index > 0) {
                        prefix = content.substring(0, index);
                    } else {
                        prefix = "";
                    }
                    namespaceuri = reader.getNamespaceURI(prefix);
                    object.setValue(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToQName(content, namespaceuri));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "Subcode")
                            .equals(reader.getName())) {

                    object.setSubcode(Factory.parse(reader));

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
           
    