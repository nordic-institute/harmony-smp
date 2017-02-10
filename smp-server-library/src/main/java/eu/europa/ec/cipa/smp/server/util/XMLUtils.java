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

    public static final QName EXT_TYPE_QNAME = new QName("http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/", "ExtensionType");

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

}
