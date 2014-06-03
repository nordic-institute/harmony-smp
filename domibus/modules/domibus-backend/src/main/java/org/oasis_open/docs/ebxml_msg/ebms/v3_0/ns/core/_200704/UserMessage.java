/**
 * UserMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;


/**
 * UserMessage bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class UserMessage implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = UserMessage
                Namespace URI = http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/
                Namespace Prefix = ns4
                */


    /**
     * field for MessageInfo
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo localMessageInfo;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localMessageInfoTracker = false;

    public boolean isMessageInfoSpecified() {
        return localMessageInfoTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo getMessageInfo() {
        return localMessageInfo;
    }


    /**
     * Auto generated setter method
     *
     * @param param MessageInfo
     */
    public void setMessageInfo(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo param) {
        localMessageInfoTracker = param != null;

        this.localMessageInfo = param;


    }


    /**
     * field for PartyInfo
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo localPartyInfo;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localPartyInfoTracker = false;

    public boolean isPartyInfoSpecified() {
        return localPartyInfoTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo getPartyInfo() {
        return localPartyInfo;
    }


    /**
     * Auto generated setter method
     *
     * @param param PartyInfo
     */
    public void setPartyInfo(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo param) {
        localPartyInfoTracker = param != null;

        this.localPartyInfo = param;


    }


    /**
     * field for CollaborationInfo
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo localCollaborationInfo;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localCollaborationInfoTracker = false;

    public boolean isCollaborationInfoSpecified() {
        return localCollaborationInfoTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo getCollaborationInfo() {
        return localCollaborationInfo;
    }


    /**
     * Auto generated setter method
     *
     * @param param CollaborationInfo
     */
    public void setCollaborationInfo(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo param) {
        localCollaborationInfoTracker = param != null;

        this.localCollaborationInfo = param;


    }


    /**
     * field for MessageProperties
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties localMessageProperties;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localMessagePropertiesTracker = false;

    public boolean isMessagePropertiesSpecified() {
        return localMessagePropertiesTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties getMessageProperties() {
        return localMessageProperties;
    }


    /**
     * Auto generated setter method
     *
     * @param param MessageProperties
     */
    public void setMessageProperties(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties param) {
        localMessagePropertiesTracker = param != null;

        this.localMessageProperties = param;


    }


    /**
     * field for PayloadInfo
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo localPayloadInfo;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localPayloadInfoTracker = false;

    public boolean isPayloadInfoSpecified() {
        return localPayloadInfoTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo getPayloadInfo() {
        return localPayloadInfo;
    }


    /**
     * Auto generated setter method
     *
     * @param param PayloadInfo
     */
    public void setPayloadInfo(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo param) {
        localPayloadInfoTracker = param != null;

        this.localPayloadInfo = param;


    }


    /**
     * field for Mpc
     * This was an Attribute!
     */


    protected org.apache.axis2.databinding.types.URI localMpc;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.databinding.types.URI
     */
    public org.apache.axis2.databinding.types.URI getMpc() {
        return localMpc;
    }


    /**
     * Auto generated setter method
     *
     * @param param Mpc
     */
    public void setMpc(final org.apache.axis2.databinding.types.URI param) {

        this.localMpc = param;


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


            final java.lang.String namespacePrefix =
                    registerPrefix(xmlWriter, "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/");
            if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                               namespacePrefix + ":UserMessage", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "UserMessage", xmlWriter);
            }


        }

        if (localMpc != null) {

            writeAttribute("", "mpc", org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMpc),
                           xmlWriter);


        }
        if (localMessageInfoTracker) {
            if (localMessageInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("MessageInfo cannot be null!!");
            }
            localMessageInfo.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "MessageInfo"), xmlWriter);
        }
        if (localPartyInfoTracker) {
            if (localPartyInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("PartyInfo cannot be null!!");
            }
            localPartyInfo.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "PartyInfo"), xmlWriter);
        }
        if (localCollaborationInfoTracker) {
            if (localCollaborationInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("CollaborationInfo cannot be null!!");
            }
            localCollaborationInfo.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "CollaborationInfo"), xmlWriter);
        }
        if (localMessagePropertiesTracker) {
            if (localMessageProperties == null) {
                throw new org.apache.axis2.databinding.ADBException("MessageProperties cannot be null!!");
            }
            localMessageProperties.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "MessageProperties"), xmlWriter);
        }
        if (localPayloadInfoTracker) {
            if (localPayloadInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("PayloadInfo cannot be null!!");
            }
            localPayloadInfo.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "PayloadInfo"), xmlWriter);
        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/")) {
            return "ns4";
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

        if (localMessageInfoTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "MessageInfo"));


            if (localMessageInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("MessageInfo cannot be null!!");
            }
            elementList.add(localMessageInfo);
        }
        if (localPartyInfoTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "PartyInfo"));


            if (localPartyInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("PartyInfo cannot be null!!");
            }
            elementList.add(localPartyInfo);
        }
        if (localCollaborationInfoTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "CollaborationInfo"));


            if (localCollaborationInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("CollaborationInfo cannot be null!!");
            }
            elementList.add(localCollaborationInfo);
        }
        if (localMessagePropertiesTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "MessageProperties"));


            if (localMessageProperties == null) {
                throw new org.apache.axis2.databinding.ADBException("MessageProperties cannot be null!!");
            }
            elementList.add(localMessageProperties);
        }
        if (localPayloadInfoTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "PayloadInfo"));


            if (localPayloadInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("PayloadInfo cannot be null!!");
            }
            elementList.add(localPayloadInfo);
        }
        attribList.add(new javax.xml.namespace.QName("", "mpc"));

        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMpc));


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
        public static UserMessage parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final UserMessage object = new UserMessage();

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

                        if (!"UserMessage".equals(type)) {
                            //find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (UserMessage) backend.ecodex.org.ExtensionMapper.getTypeObject(nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


                // handle attribute "mpc"
                final java.lang.String tempAttribMpc =

                        reader.getAttributeValue(null, "mpc");

                if (tempAttribMpc != null) {
                    final java.lang.String content = tempAttribMpc;

                    object.setMpc(org.apache.axis2.databinding.utils.ConverterUtil.convertToAnyURI(tempAttribMpc));

                } else {

                }
                handledAttributes.add("mpc");


                reader.next();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "MessageInfo").equals(reader.getName())) {

                    object.setMessageInfo(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "PartyInfo").equals(reader.getName())) {

                    object.setPartyInfo(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "CollaborationInfo").equals(reader.getName())) {

                    object.setCollaborationInfo(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo.Factory
                                                                                                     .parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "MessageProperties").equals(reader.getName())) {

                    object.setMessageProperties(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties.Factory
                                                                                                     .parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "PayloadInfo").equals(reader.getName())) {

                    object.setPayloadInfo(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo.Factory.parse(reader));

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
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }//end of factory class


}
           
    