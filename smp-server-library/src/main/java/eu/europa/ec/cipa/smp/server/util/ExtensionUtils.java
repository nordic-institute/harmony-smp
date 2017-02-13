package eu.europa.ec.cipa.smp.server.util;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by migueti on 13/02/2017.
 */
public class ExtensionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionUtils.class);

    public static final QName EXT_TYPE_QNAME = new QName("http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/", "ExtensionType");

    public static String marshalExtension(ExtensionType extension, QName qName) {
        if (extension == null || qName == null) {
            return null;
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ExtensionType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            JAXBElement aJaxbElement = new JAXBElement(qName, ExtensionType.class, extension);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLOutputFactory xof = XMLOutputFactory.newFactory();
            XMLStreamWriter xsw = xof.createXMLStreamWriter(baos);
            jaxbMarshaller.marshal(aJaxbElement, xsw);
            xsw.close();
            return baos.toString();
        } catch (JAXBException | XMLStreamException jEx) {
            LOGGER.error(jEx.getMessage());
        }
        return null;
    }

    public static List<ExtensionType> unmarshalExtensions(String xml) {
        InputStream inStream;
        List<ExtensionType> result = new ArrayList<>();
        List<String> listExtensions = parseXml(xml);
        for(String extension : listExtensions) {
            if (isHexBinary(xml)) {
                inStream = new ByteArrayInputStream(DatatypeConverter.parseHexBinary(extension));
            } else {
                inStream = new ByteArrayInputStream(extension.getBytes());
            }
            JAXBElement element = null;
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ExtensionType.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                element = jaxbUnmarshaller.unmarshal(new StreamSource(inStream), ExtensionType.class);
            } catch (JAXBException e) {
                LOGGER.error(e.getMessage());
            }
            if (element != null) {
                result.add((ExtensionType) element.getValue());
            }
        }
        return result;
    }

    private static List<String> parseXml(String xml) {
        List<String> result = new ArrayList<>();
        if(xml != null) {
            Pattern p = Pattern.compile("(<)(\\?)(xml)( )(version)(=).*?(\\?)(>)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            String[] extensions =  xml.split(p.pattern());
            for(String extension : extensions) {
                if(!extension.isEmpty()) {
                    result.add(extension);
                }
            }
        }
        return result;
    }

    private static boolean isHexBinary(String value) {
        final Pattern pat = Pattern.compile("-?[0-9a-fA-F]+");
        Matcher m = pat.matcher(value);
        return m.matches();
    }
}
