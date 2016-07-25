package eu.europa.ec.cipa.smp.server.util;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to help converting XML string to XML objects (and viceversa).
 * It works with both MySQL and Oracle DB.
 *
 * @author Martini Federico
 */
public class XMLUtils {

    public static final QName EXT_TYPE_QNAME = new QName("http://busdox.org/serviceMetadata/publishing/1.0/", "ExtensionType");

    static final Pattern pat = Pattern.compile("-?[0-9a-fA-F]+");

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
        } catch (JAXBException e) {
            e.printStackTrace();
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
        } catch (JAXBException e) {
            e.printStackTrace();
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
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isHexBinary(String value) {
        Matcher m = pat.matcher(value);
        return m.matches();
    }
}
