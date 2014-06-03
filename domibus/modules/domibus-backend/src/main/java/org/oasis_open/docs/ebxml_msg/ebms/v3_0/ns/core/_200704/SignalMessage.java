/**
 * SignalMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */


package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;


/**
 * SignalMessage bean class
 */
@SuppressWarnings({"unchecked", "unused"})

public class SignalMessage implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = SignalMessage
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
     * field for PullRequest
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest localPullRequest;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localPullRequestTracker = false;

    public boolean isPullRequestSpecified() {
        return localPullRequestTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest getPullRequest() {
        return localPullRequest;
    }


    /**
     * Auto generated setter method
     *
     * @param param PullRequest
     */
    public void setPullRequest(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest param) {
        localPullRequestTracker = param != null;

        this.localPullRequest = param;


    }


    /**
     * field for Receipt
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Receipt localReceipt;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localReceiptTracker = false;

    public boolean isReceiptSpecified() {
        return localReceiptTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Receipt
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Receipt getReceipt() {
        return localReceipt;
    }


    /**
     * Auto generated setter method
     *
     * @param param Receipt
     */
    public void setReceipt(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Receipt param) {
        localReceiptTracker = param != null;

        this.localReceipt = param;


    }


    /**
     * field for Error
     * This was an Array!
     */


    protected org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[] localError;

    /*  This tracker boolean wil be used to detect whether the user called the set method
   *   for this attribute. It will be used to determine whether to include this field
    *   in the serialized XML
    */
    protected boolean localErrorTracker = false;

    public boolean isErrorSpecified() {
        return localErrorTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[]
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[] getError() {
        return localError;
    }


    /**
     * validate the array for Error
     */
    protected void validateError(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param Error
     */
    public void setError(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[] param) {

        validateError(param);

        localErrorTracker = param != null;

        this.localError = param;
    }


    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error
     */
    public void addError(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error param) {
        if (localError == null) {
            localError = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[]{};
        }


        //update the setting tracker
        localErrorTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localError);
        list.add(param);
        this.localError = (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[]) list
                .toArray(new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[list.size()]);

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
        return localExtraElementTracker;
    }


    /**
     * Auto generated getter method
     *
     * @return org.apache.axiom.om.OMElement[]
     */
    public org.apache.axiom.om.OMElement[] getExtraElement() {
        return localExtraElement;
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

        localExtraElementTracker = param != null;

        this.localExtraElement = param;
    }


    /**
     * Auto generated add method for the array for convenience
     *
     * @param param org.apache.axiom.om.OMElement
     */
    public void addExtraElement(final org.apache.axiom.om.OMElement param) {
        if (localExtraElement == null) {
            localExtraElement = new org.apache.axiom.om.OMElement[]{};
        }


        //update the setting tracker
        localExtraElementTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localExtraElement);
        list.add(param);
        this.localExtraElement =
                (org.apache.axiom.om.OMElement[]) list.toArray(new org.apache.axiom.om.OMElement[list.size()]);

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
                               namespacePrefix + ":SignalMessage", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "SignalMessage", xmlWriter);
            }


        }
        if (localMessageInfoTracker) {
            if (localMessageInfo == null) {
                throw new org.apache.axis2.databinding.ADBException("MessageInfo cannot be null!!");
            }
            localMessageInfo.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "MessageInfo"), xmlWriter);
        }
        if (localPullRequestTracker) {
            if (localPullRequest == null) {
                throw new org.apache.axis2.databinding.ADBException("PullRequest cannot be null!!");
            }
            localPullRequest.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "PullRequest"), xmlWriter);
        }
        if (localReceiptTracker) {
            if (localReceipt == null) {
                throw new org.apache.axis2.databinding.ADBException("Receipt cannot be null!!");
            }
            localReceipt.serialize(
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "Receipt"), xmlWriter);
        }
        if (localErrorTracker) {
            if (localError != null) {
                for (int i = 0; i < localError.length; i++) {
                    if (localError[i] != null) {
                        localError[i].serialize(new javax.xml.namespace.QName(
                                "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "Error"), xmlWriter);
                    } else {

                        // we don't have to do any thing since minOccures is zero

                    }

                }
            } else {

                throw new org.apache.axis2.databinding.ADBException("Error cannot be null!!");

            }
        }
        if (localExtraElementTracker) {

            if (localExtraElement != null) {
                for (int i = 0; i < localExtraElement.length; i++) {
                    if (localExtraElement[i] != null) {
                        localExtraElement[i].serialize(xmlWriter);
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
        if (localPullRequestTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "PullRequest"));


            if (localPullRequest == null) {
                throw new org.apache.axis2.databinding.ADBException("PullRequest cannot be null!!");
            }
            elementList.add(localPullRequest);
        }
        if (localReceiptTracker) {
            elementList
                    .add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                       "Receipt"));


            if (localReceipt == null) {
                throw new org.apache.axis2.databinding.ADBException("Receipt cannot be null!!");
            }
            elementList.add(localReceipt);
        }
        if (localErrorTracker) {
            if (localError != null) {
                for (int i = 0; i < localError.length; i++) {

                    if (localError[i] != null) {
                        elementList.add(new javax.xml.namespace.QName(
                                "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "Error"));
                        elementList.add(localError[i]);
                    } else {

                        // nothing to do

                    }

                }
            } else {

                throw new org.apache.axis2.databinding.ADBException("Error cannot be null!!");

            }

        }
        if (localExtraElementTracker) {
            if (localExtraElement != null) {
                for (int i = 0; i < localExtraElement.length; i++) {
                    if (localExtraElement[i] != null) {
                        elementList.add(new javax.xml.namespace.QName("", "extraElement"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                                                                          .convertToString(localExtraElement[i]));
                    } else {

                        // have to do nothing

                    }

                }
            } else {
                throw new org.apache.axis2.databinding.ADBException("extraElement cannot be null!!");
            }
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
        public static SignalMessage parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
            final SignalMessage object = new SignalMessage();

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

                        if (!"SignalMessage".equals(type)) {
                            //find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (SignalMessage) backend.ecodex.org.ExtensionMapper
                                                                     .getTypeObject(nsUri, type, reader);
                        }


                    }


                }


                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();


                reader.next();

                final java.util.ArrayList list4 = new java.util.ArrayList();

                final java.util.ArrayList list5 = new java.util.ArrayList();


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
                                                  "PullRequest").equals(reader.getName())) {

                    object.setPullRequest(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "Receipt").equals(reader.getName())) {

                    object.setReceipt(
                            org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Receipt.Factory.parse(reader));

                    reader.next();

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() &&
                    new javax.xml.namespace.QName("http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/",
                                                  "Error").equals(reader.getName())) {


                    // Process the array and step past its final element's end.
                    list4.add(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error.Factory.parse(reader));

                    //loop until we find a start element that is not part of this array
                    boolean loopDone4 = false;
                    while (!loopDone4) {
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
                            loopDone4 = true;
                        } else {
                            if (new javax.xml.namespace.QName(
                                    "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/", "Error")
                                    .equals(reader.getName())) {
                                list4.add(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error.Factory
                                                                                                       .parse(reader));

                            } else {
                                loopDone4 = true;
                            }
                        }
                    }
                    // call the converter utility  to convert and set the array

                    object.setError((org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error[]) org.apache.axis2
                            .databinding.utils.ConverterUtil.convertToArray(
                                    org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error.class, list4));

                }  // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()) {


                    // Process the array and step past its final element's end.

                    boolean loopDone5 = false;

                    while (!loopDone5) {
                        event = reader.getEventType();
                        if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event) {

                            // We need to wrap the reader so that it produces a fake START_DOCUEMENT event
                            final org.apache.axis2.databinding.utils.NamedStaxOMBuilder builder5 =
                                    new org.apache.axis2.databinding.utils.NamedStaxOMBuilder(
                                            new org.apache.axis2.util.StreamWrapper(reader), reader.getName());

                            list5.add(builder5.getOMElement());
                            reader.next();
                            if (reader.isEndElement()) {
                                // we have two countinuos end elements
                                loopDone5 = true;
                            }

                        } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event) {
                            loopDone5 = true;
                        } else {
                            reader.next();
                        }

                    }


                    object.setExtraElement(
                            (org.apache.axiom.om.OMElement[]) org.apache.axis2.databinding.utils.ConverterUtil
                                                                                                .convertToArray(
                                                                                                        org.apache.axiom.om.OMElement.class,
                                                                                                        list5));

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
           
    