package eu.europa.ec.cipa.smp.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to help converting XML string to XML objects (and viceversa).
 * It works with both MySQL and Oracle DB.
 *
 * @author Martini Federico
 */
public class XMLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);

    private static final Pattern pat = Pattern.compile("-?[0-9a-fA-F]+");

    public static final QName EXT_TYPE_QNAME = new QName("http://busdox.org/serviceMetadata/publishing/1.0/", "ExtensionType");

    /**
     * Converts an XML object to a string.
     * Null is returned in case of null parameters or an exception is raised.
     *
     * @param xmlObject
     * @param clazz
     * @param qName
     * @return
     */
    public static String marshallObject(Object xmlObject, Class clazz, QName qName) {
        if (xmlObject == null || clazz == null || qName == null) return null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            JAXBElement aJaxbElement = new JAXBElement(qName, clazz, xmlObject);
            StringWriter buf = new StringWriter();
            jaxbMarshaller.marshal(aJaxbElement, buf);
            return buf.toString();
        } catch (JAXBException jEx) {
            LOGGER.error(jEx.getMessage());
        }
        return null;
    }


    /**
     * Reads an XML string and returns a corresponding XML object of the type indicated.
     * Null is returned in case of null parameters or an exception is raised.
     *
     * @param xml
     * @param clazz
     * @return
     */
    public static Object unmarshallObject(String xml, Class clazz) {
        if (xml == null || clazz == null) return null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            InputStream inStream = new ByteArrayInputStream(xml.getBytes());
            return jaxbUnmarshaller.unmarshal(inStream);
        } catch (JAXBException jEx) {
            LOGGER.error(jEx.getMessage());
        }
        return null;
    }

    /**
     * Reads an XML string which represents a BLOB and returns a corresponding XML object of the type indicated.
     * Null is returned in case of null parameters or an exception is raised.
     *
     * @param xml
     * @param clazz
     * @return
     */
    public static Object unmarshallBLOB(String xml, Class clazz) {

        if (xml == null || clazz == null) return null;

        InputStream inStream;
        if (isHexBinary(xml)) {
            inStream = new ByteArrayInputStream(DatatypeConverter.parseHexBinary(xml));
        } else {
            inStream = new ByteArrayInputStream(xml.getBytes());
        }
        // Unmarshalling
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<?> root = jaxbUnmarshaller.unmarshal(new StreamSource(inStream), clazz);
            return root.getValue();
        } catch (JAXBException jEx) {
            LOGGER.error(jEx.getMessage());
        }
        return null;
    }

    public static boolean isHexBinary(String value) {
        Matcher m = pat.matcher(value);
        return m.matches();
    }


    /**
     * Converts an XML object to a string. Namespaces are set to empty strings.
     * Null is returned in case of null parameters or an exception is raised.
     *
     * @param xmlObject
     * @param clazz
     * @param qName
     * @return
     */
    public static String marshallObjectNoNameSpaces(Object xmlObject, Class clazz, QName qName) {
        if (xmlObject == null || clazz == null || qName == null) return null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            JAXBElement aJaxbElement = new JAXBElement(qName, clazz, xmlObject);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLOutputFactory xof = XMLOutputFactory.newFactory();
            XMLStreamWriter xsw = xof.createXMLStreamWriter(baos);
            xsw = new MyXMLStreamWriter(xsw);
            jaxbMarshaller.marshal(aJaxbElement, xsw);
            xsw.close();
            return baos.toString();
        } catch (JAXBException jEx) {
            LOGGER.error(jEx.getMessage());
        } catch (XMLStreamException xSex) {
            LOGGER.error(xSex.getMessage());
        }
        return null;
    }


    public static class MyXMLStreamWriter implements XMLStreamWriter {

        private XMLStreamWriter xsw;
        private MyNamespaceContext nc = new MyNamespaceContext();

        public MyXMLStreamWriter(XMLStreamWriter xsw) throws XMLStreamException {
            this.xsw = xsw;
            xsw.setNamespaceContext(nc);
        }

        public void close() throws XMLStreamException {
            xsw.close();
        }

        public void flush() throws XMLStreamException {
            xsw.flush();
        }

        public javax.xml.namespace.NamespaceContext getNamespaceContext() {
            return xsw.getNamespaceContext();
        }

        public String getPrefix(String arg0) throws XMLStreamException {
            return xsw.getPrefix(arg0);
        }

        public Object getProperty(String arg0) throws IllegalArgumentException {
            return xsw.getProperty(arg0);
        }

        public void setDefaultNamespace(String arg0) throws XMLStreamException {
            xsw.setDefaultNamespace(arg0);
        }

        public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException {
        }

        public void setPrefix(String arg0, String arg1) throws XMLStreamException {
            xsw.setPrefix(arg0, arg1);
        }

        public void writeAttribute(String arg0, String arg1) throws XMLStreamException {
            xsw.writeAttribute(arg0, arg1);
        }

        public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException {
            xsw.writeAttribute(arg0, arg1, arg2);
        }

        public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException {
            xsw.writeAttribute(arg0, arg1, arg2, arg3);
        }

        public void writeCData(String arg0) throws XMLStreamException {
            xsw.writeCData(arg0);
        }

        public void writeCharacters(String arg0) throws XMLStreamException {
            xsw.writeCharacters(arg0);
        }

        public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
            xsw.writeCharacters(arg0, arg1, arg2);
        }

        public void writeComment(String arg0) throws XMLStreamException {
            xsw.writeComment(arg0);
        }

        public void writeDTD(String arg0) throws XMLStreamException {
            xsw.writeDTD(arg0);
        }

        public void writeDefaultNamespace(String arg0) throws XMLStreamException {
            xsw.writeDefaultNamespace(arg0);
        }

        public void writeEmptyElement(String arg0) throws XMLStreamException {
            xsw.writeEmptyElement(arg0);
        }

        public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
            xsw.writeEmptyElement(arg0, arg1);
        }

        public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            xsw.writeEmptyElement(arg0, arg1, arg2);
        }

        public void writeEndDocument() throws XMLStreamException {
            xsw.writeEndDocument();
        }

        public void writeEndElement() throws XMLStreamException {
            xsw.writeEndElement();
        }

        public void writeEntityRef(String arg0) throws XMLStreamException {
            xsw.writeEntityRef(arg0);
        }

        public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
        }

        public void writeProcessingInstruction(String arg0) throws XMLStreamException {
            xsw.writeProcessingInstruction(arg0);
        }

        public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
            xsw.writeProcessingInstruction(arg0, arg1);
        }

        public void writeStartDocument() throws XMLStreamException {
            xsw.writeStartDocument();
        }

        public void writeStartDocument(String arg0) throws XMLStreamException {
            xsw.writeStartDocument(arg0);
        }

        public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
            xsw.writeStartDocument(arg0, arg1);
        }

        public void writeStartElement(String arg0) throws XMLStreamException {
            xsw.writeStartElement(arg0);
        }

        public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
            xsw.writeStartElement(arg0, arg1);
        }

        public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            xsw.writeStartElement("", arg1, arg2);
            if (null != arg2 || arg2.length() > 0) {
                String currentDefaultNS = nc.getNamespaceURI("");
                if (!arg2.equals(currentDefaultNS)) {
                    writeDefaultNamespace(arg2);
                    nc.setDefaultNS(arg2);
                }
            }
        }
    }

    private static class MyNamespaceContext implements NamespaceContext {

        private String defaultNS = "";

        public void setDefaultNS(String ns) {
            defaultNS = ns;
        }

        public String getNamespaceURI(String arg0) {
            if ("".equals(arg0)) {
                return defaultNS;
            }
            return null;
        }

        public String getPrefix(String arg0) {
            return "";
        }

        public Iterator getPrefixes(String arg0) {
            return null;
        }

    }

}
