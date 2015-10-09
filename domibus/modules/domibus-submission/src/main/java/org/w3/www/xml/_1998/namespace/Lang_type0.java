/**
 * Lang_type0.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.w3.www.xml._1998.namespace;


/**
 * Lang_type0 bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Lang_type0 extends org.apache.axis2.databinding.types.Union
        implements org.apache.axis2.databinding.ADBBean {

    public static final javax.xml.namespace.QName MY_QNAME =
            new javax.xml.namespace.QName("http://www.w3.org/XML/1998/namespace", "lang_type0", "ns4");


    public void setObject(final Object object) {

        if (object instanceof org.apache.axis2.databinding.types.Language) {
            this.localObject = object;

        } else {
            throw new RuntimeException("Invalid object type");
        }
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

        writeStartElement(null, parentQName.getNamespaceURI(), parentQName.getLocalPart(), xmlWriter);


        if (this.localObject instanceof org.apache.axis2.databinding.types.Language) {
            final String namespacePrefix = registerPrefix(xmlWriter, "http://www.w3.org/2001/XMLSchema");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":language", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "language", xmlWriter);
            }


            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(
                    (org.apache.axis2.databinding.types.Language) this.localObject));

        } else {
            throw new org.apache.axis2.databinding.ADBException("Invalid object type");
        }
        xmlWriter.writeEndElement();


    }

    private static String generatePrefix(final String namespace) {
        if ("http://www.w3.org/XML/1998/namespace".equals(namespace)) {
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


        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(MY_QNAME, new Object[]{
                org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT, this.localObject.toString()},
                                                                                    null);


    }


    /**
     * Factory class that keeps the parse method
     */
    public static class Factory {


        public static Lang_type0 fromString(final javax.xml.stream.XMLStreamReader xmlStreamReader,
                                            final String namespaceURI, final String type)
                throws org.apache.axis2.databinding.ADBException {

            Lang_type0 object = null;
            try {
                if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                    object = new Lang_type0();
                    object.setObject(xmlStreamReader, namespaceURI, type);
                } else {
                    object = new Lang_type0();
                    object.setObject(
                            backend.ecodex.org.ExtensionMapper.getTypeObject(namespaceURI, type, xmlStreamReader));
                }
                return object;
            } catch (Exception e) {
                throw new org.apache.axis2.databinding.ADBException("Error in parsing value");
            }
        }

        public static Lang_type0 fromString(final String value, final String namespaceURI) {
            final Lang_type0 object = new Lang_type0();
            boolean isValueSet = false;

            // we have to set the object with the first matching type.
            if (!isValueSet) {

                try {
                    final java.lang.reflect.Method converterMethod =
                            org.apache.axis2.databinding.utils.ConverterUtil.class
                                    .getMethod("convertToLanguage", new Class[]{String.class});
                    object.setObject(converterMethod.invoke(null, new Object[]{value}));
                    isValueSet = true;
                } catch (Exception e) {
                }

            }

            return object;
        }

        public static Lang_type0 fromString(final javax.xml.stream.XMLStreamReader xmlStreamReader,
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
         * static method to create the object
         * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
         * If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
         * Postcondition: If this object is an element, the reader is positioned at its end element
         * If this object is a complex type, the reader is positioned at the end element of its outer element
         */
        public static Lang_type0 parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            Lang_type0 object = new Lang_type0();

            int event;
            final String nillableValue = null;
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

                        final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                        object = Factory.fromString(reader, nsUri, type);


                    }

                } else {
                    // i.e this is an union type with out specific xsi:type
                    final String content = reader.getElementText();
                    if (content.indexOf(":") > -1) {
                        // i.e. this could be a qname
                        prefix = content.substring(0, content.indexOf(":"));
                        namespaceuri = reader.getNamespaceContext().getNamespaceURI(prefix);
                        object = Factory.fromString(content, namespaceuri);
                    } else {
                        object = Factory.fromString(content, "");
                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


            } catch (javax.xml.stream.XMLStreamException e) {
                throw new Exception(e);
            }

            return object;
        }

    }//end of factory class


}
           
    