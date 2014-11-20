/**
 * Messaging.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;


/**
 * Messaging bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class Messaging implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = Messaging
                Namespace URI = http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/
                Namespace Prefix = ns4
                */


    /**
     * field for SignalMessage
     * This was an Array!
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[] localSignalMessage;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localSignalMessageTracker = false;

    public boolean isSignalMessageSpecified() {
        return this.localSignalMessageTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[]
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[] getSignalMessage() {
        return this.localSignalMessage;
    }


    /**
     * validate the array for SignalMessage
     */
    protected void validateSignalMessage(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param SignalMessage
     */
    public void setSignalMessage(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[] param) {

        validateSignalMessage(param);

        this.localSignalMessageTracker = param != null;

        this.localSignalMessage = param;
    }


    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage
     */
    public void addSignalMessage(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage param) {
        if (this.localSignalMessage == null) {
            this.localSignalMessage = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[]{};
        }


        //update the setting tracker
        this.localSignalMessageTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localSignalMessage);
        list.add(param);
        this.localSignalMessage = (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[]) list
                .toArray(new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[list.size()]);

    }


    /**
     * field for UserMessage
     * This was an Array!
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[] localUserMessage;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localUserMessageTracker = false;

    public boolean isUserMessageSpecified() {
        return this.localUserMessageTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[]
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[] getUserMessage() {
        return this.localUserMessage;
    }


    /**
     * validate the array for UserMessage
     */
    protected void validateUserMessage(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param UserMessage
     */
    public void setUserMessage(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[] param) {

        validateUserMessage(param);

        this.localUserMessageTracker = param != null;

        this.localUserMessage = param;
    }


    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage
     */
    public void addUserMessage(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage param) {
        if (this.localUserMessage == null) {
            this.localUserMessage = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[]{};
        }


        //update the setting tracker
        this.localUserMessageTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localUserMessage);
        list.add(param);
        this.localUserMessage = (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[]) list
                .toArray(new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[list.size()]);

    }


    /**
     * field for ExtraElement
     * This was an Array!
     */


    protected org.apache.axiom.om.OMElement[] localExtraElement;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localExtraElementTracker = false;

    public boolean isExtraElementSpecified() {
        return this.localExtraElementTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.om.OMElement[]
     */
    public org.apache.axiom.om.OMElement[] getExtraElement() {
        return this.localExtraElement;
    }


    /**
     * validate the array for ExtraElement
     */
    protected void validateExtraElement(final org.apache.axiom.om.OMElement[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param ExtraElement
     */
    public void setExtraElement(final org.apache.axiom.om.OMElement[] param) {

        validateExtraElement(param);

        this.localExtraElementTracker = param != null;

        this.localExtraElement = param;
    }


    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.apache.axiom.om.OMElement
     */
    public void addExtraElement(final org.apache.axiom.om.OMElement param) {
        if (this.localExtraElement == null) {
            this.localExtraElement = new org.apache.axiom.om.OMElement[]{};
        }


        //update the setting tracker
        this.localExtraElementTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localExtraElement);
        list.add(param);
        this.localExtraElement =
                (org.apache.axiom.om.OMElement[]) list.toArray(new org.apache.axiom.om.OMElement[list.size()]);

    }


    /**
     * field for Id
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.Id localId;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.Id
     */
    public org.apache.axis2.databinding.types.Id getId() {
        return this.localId;
    }


    /**
     * Auto generated setter method
     *
     * @param param Id
     */
    public void setId(final org.apache.axis2.databinding.types.Id param) {

        this.localId = param;


    }


    /**
     * field for MustUnderstandS11
     * This was an Attribute!
     */


    protected org.xmlsoap.schemas.soap.envelope.MustUnderstandS11_type0 localMustUnderstandS11;


    /**
     * Auto generated getter method
     *
     * @return org.xmlsoap.schemas.soap.envelope.MustUnderstandS11_type0
     */
    public org.xmlsoap.schemas.soap.envelope.MustUnderstandS11_type0 getMustUnderstandS11() {
        return this.localMustUnderstandS11;
    }


    /**
     * Auto generated setter method
     *
     * @param param MustUnderstandS11
     */
    public void setMustUnderstandS11(final org.xmlsoap.schemas.soap.envelope.MustUnderstandS11_type0 param) {

        this.localMustUnderstandS11 = param;


    }


    /**
     * field for MustUnderstand
     * This was an Attribute!
     */


    protected boolean localMustUnderstand = org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean("0");


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getMustUnderstand() {
        return this.localMustUnderstand;
    }


    /**
     * Auto generated setter method
     *
     * @param param MustUnderstand
     */
    public void setMustUnderstand(final boolean param) {

        this.localMustUnderstand = param;


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
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":Messaging", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "Messaging", xmlWriter);
            }


        }

        if (this.localId != null) {

            writeAttribute("", "id", org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localId),
                           xmlWriter);


        }


        if (this.localMustUnderstandS11 != null) {
            writeAttribute("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstandS11",
                           this.localMustUnderstandS11.toString(), xmlWriter);
        }


        writeAttribute("http://www.w3.org/2003/05/soap-envelope", "mustUnderstand",
                       org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMustUnderstand),
                       xmlWriter);


        if (this.localSignalMessageTracker) {
            if (this.localSignalMessage != null) {
                for (int i = 0; i < this.localSignalMessage.length; i++) {
                    if (this.localSignalMessage[i] != null) {
                        this.localSignalMessage[i].serialize(new javax.xml.namespace.QName(
                                "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "SignalMessage"),
                                                             xmlWriter);
                    } else {

                        // we don't have to do any thing since minOccures is zero

                    }

                }
            } else {

                throw new org.apache.axis2.databinding.ADBException("SignalMessage cannot be null!!");

            }
        }
        if (this.localUserMessageTracker) {
            if (this.localUserMessage != null) {
                for (int i = 0; i < this.localUserMessage.length; i++) {
                    if (this.localUserMessage[i] != null) {
                        this.localUserMessage[i].serialize(new javax.xml.namespace.QName(
                                "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "UserMessage"),
                                                           xmlWriter);
                    } else {

                        // we don't have to do any thing since minOccures is zero

                    }

                }
            } else {

                throw new org.apache.axis2.databinding.ADBException("UserMessage cannot be null!!");

            }
        }
        if (this.localExtraElementTracker) {

            if (this.localExtraElement != null) {
                for (int i = 0; i < this.localExtraElement.length; i++) {
                    if (this.localExtraElement[i] != null) {
                        this.localExtraElement[i].serialize(xmlWriter);
                    } else {

                        // we have to do nothing since minOccures zero

                    }
                }
            } else {
                throw new org.apache.axis2.databinding.ADBException("extraElement cannot be null!!");
            }
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

        if (this.localSignalMessageTracker) {
            if (this.localSignalMessage != null) {
                for (int i = 0; i < this.localSignalMessage.length; i++) {

                    if (this.localSignalMessage[i] != null) {
                        elementList.add(new javax.xml.namespace.QName(
                                "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "SignalMessage"));
                        elementList.add(this.localSignalMessage[i]);
                    } else {

                        // nothing to do

                    }

                }
            } else {

                throw new org.apache.axis2.databinding.ADBException("SignalMessage cannot be null!!");

            }

        }
        if (this.localUserMessageTracker) {
            if (this.localUserMessage != null) {
                for (int i = 0; i < this.localUserMessage.length; i++) {

                    if (this.localUserMessage[i] != null) {
                        elementList.add(new javax.xml.namespace.QName(
                                "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "UserMessage"));
                        elementList.add(this.localUserMessage[i]);
                    } else {

                        // nothing to do

                    }

                }
            } else {

                throw new org.apache.axis2.databinding.ADBException("UserMessage cannot be null!!");

            }

        }
        if (this.localExtraElementTracker) {
            if (this.localExtraElement != null) {
                for (int i = 0; i < this.localExtraElement.length; i++) {
                    if (this.localExtraElement[i] != null) {
                        elementList.add(new javax.xml.namespace.QName("", "extraElement"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToString(this.localExtraElement[i]));
                    } else {

                        // have to do nothing

                    }

                }
            } else {
                throw new org.apache.axis2.databinding.ADBException("extraElement cannot be null!!");
            }
        }
        attribList.add(new javax.xml.namespace.QName("", "id"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localId));

        attribList.add(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstandS11"));

        attribList.add(this.localMustUnderstandS11.toString());

        attribList.add(new javax.xml.namespace.QName("http://www.w3.org/2003/05/soap-envelope", "mustUnderstand"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localMustUnderstand));


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
        public static Messaging parse(final javax.xml.stream.XMLStreamReader reader) throws Exception {
            final Messaging object = new Messaging();

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

                        if (!"Messaging".equals(type)) {
                            //find namespace for the prefix
                            final String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (Messaging) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


                // handle attribute "id"
                final String tempAttribId =

                        reader.getAttributeValue(null, "id");

                if (tempAttribId != null) {
                    final String content = tempAttribId;

                    object.setId(org.apache.axis2.databinding.utils.ConverterUtil.convertToID(tempAttribId));

                } else {

                }
                handledAttributes.add("id");

                // handle attribute "mustUnderstandS11"
                final String tempAttribMustUnderstandS11 =

                        reader.getAttributeValue("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstandS11");

                if (tempAttribMustUnderstandS11 != null) {
                    final String content = tempAttribMustUnderstandS11;

                    object.setMustUnderstandS11(org.xmlsoap.schemas.soap.envelope.MustUnderstandS11_type0.Factory
                                                        .fromString(reader, tempAttribMustUnderstandS11));

                } else {

                }
                handledAttributes.add("mustUnderstandS11");

                // handle attribute "mustUnderstand"
                final String tempAttribMustUnderstand =

                        reader.getAttributeValue("http://www.w3.org/2003/05/soap-envelope", "mustUnderstand");

                if (tempAttribMustUnderstand != null) {
                    final String content = tempAttribMustUnderstand;

                    object.setMustUnderstand(org.apache.axis2.databinding.utils.ConverterUtil
                                                     .convertToBoolean(tempAttribMustUnderstand));

                } else {

                }
                handledAttributes.add("mustUnderstand");


                reader.next();

                final java.util.ArrayList list1 = new java.util.ArrayList();

                final java.util.ArrayList list2 = new java.util.ArrayList();

                final java.util.ArrayList list3 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "SignalMessage").equals(reader.getName())) {


                    // Process the array and step past its final element's end.
                    list1.add(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage.Factory
                                      .parse(reader));

                    //loop until we find a start element that is not part of this array
                    boolean loopDone1 = false;
                    while (!loopDone1) {
                        // We should be at the end element, but make sure
                        while (!reader.isEndElement()) {
                            reader.next();
                        }
                        // Step out of this element
                        reader.next();
                        // Step to next element event.
                        while (!reader.isStartElement() && !reader.isEndElement()) {
                            reader.next();
                        }
                        if (reader.isEndElement()) {
                            //two continuous end elements means we are exiting the xml structure
                            loopDone1 = true;
                        } else {
                            if (new javax.xml.namespace.QName(
                                    "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "SignalMessage")
                                    .equals(reader.getName())) {
                                list1.add(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage.Factory
                                                  .parse(reader));

                            } else {
                                loopDone1 = true;
                            }
                        }
                    }
                    // call the converter utility  to convert and set the array

                    object.setSignalMessage(
                            (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage[]) org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToArray(
                                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage.class,
                                            list1));

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "UserMessage").equals(reader.getName())) {


                    // Process the array and step past its final element's end.
                    list2.add(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage.Factory.parse(reader));

                    //loop until we find a start element that is not part of this array
                    boolean loopDone2 = false;
                    while (!loopDone2) {
                        // We should be at the end element, but make sure
                        while (!reader.isEndElement()) {
                            reader.next();
                        }
                        // Step out of this element
                        reader.next();
                        // Step to next element event.
                        while (!reader.isStartElement() && !reader.isEndElement()) {
                            reader.next();
                        }
                        if (reader.isEndElement()) {
                            //two continuous end elements means we are exiting the xml structure
                            loopDone2 = true;
                        } else {
                            if (new javax.xml.namespace.QName(
                                    "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "UserMessage")
                                    .equals(reader.getName())) {
                                list2.add(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage.Factory
                                                  .parse(reader));

                            } else {
                                loopDone2 = true;
                            }
                        }
                    }
                    // call the converter utility  to convert and set the array

                    object.setUserMessage(
                            (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage[]) org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToArray(
                                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage.class,
                                            list2));

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()) {


                    // Process the array and step past its final element's end.

                    boolean loopDone3 = false;

                    while (!loopDone3) {
                        event = reader.getEventType();
                        if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event) {

                            // We need to wrap the reader so that it produces a fake START_DOCUEMENT event
                            final org.apache.axis2.databinding.utils.NamedStaxOMBuilder builder3 =
                                    new org.apache.axis2.databinding.utils.NamedStaxOMBuilder(
                                            new org.apache.axis2.util.StreamWrapper(reader), reader.getName());

                            list3.add(builder3.getOMElement());
                            reader.next();
                            if (reader.isEndElement()) {
                                // we have two countinuos end elements
                                loopDone3 = true;
                            }

                        } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event) {
                            loopDone3 = true;
                        } else {
                            reader.next();
                        }

                    }


                    object.setExtraElement(
                            (org.apache.axiom.om.OMElement[]) org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToArray(org.apache.axiom.om.OMElement.class, list3));

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
           
    