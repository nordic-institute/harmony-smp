/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.util;

import org.apache.cxf.staxutils.PrettyPrintXMLStreamWriter;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by migueti on 13/02/2017.
 */
public class ExtensionUtils {

    private static final String WRAPPED_FORMAT = "<ExtensionsWrapper xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">%s</ExtensionsWrapper>";

    @XmlRootElement(name = "ExtensionsWrapper")
    private static class ExtensionsWrapper {
        @XmlElement(name = "Extension")
        List<ExtensionType> extensions;
    }

    private static final QName EXT_TYPE_QNAME = new QName("http://docs.oasis-open.org/bdxr/ns/SMP/2016/05", "Extension");

    public static String marshalExtensions(List<ExtensionType> extensions) throws JAXBException, XMLStreamException {
        if (extensions == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (ExtensionType aExtension : extensions) {
            stringBuilder.append(ExtensionUtils.marshalExtension(aExtension));
        }
        return stringBuilder.toString();
    }

    private static String marshalExtension(ExtensionType extension) throws JAXBException, XMLStreamException {
        if(extension == null) {
            return null;
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(ExtensionType.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        JAXBElement aJaxbElement = new JAXBElement(EXT_TYPE_QNAME, ExtensionType.class, extension);
        jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLStreamWriter xmlStreamWriter = null;
        PrettyPrintXMLStreamWriter xsw = null;
        try {
            xmlStreamWriter = xof.createXMLStreamWriter(baos);
            xsw = new PrettyPrintXMLStreamWriter(xmlStreamWriter, 4);
            jaxbMarshaller.marshal(aJaxbElement, xsw);
        } finally {
            if(xmlStreamWriter != null) {
                xmlStreamWriter.close();
            }
            if (xsw != null) {
                xsw.close();
            }
        }
        return baos.toString();
    }

    public static List<ExtensionType> unmarshalExtensions(String xml) throws JAXBException {
        String wrappedExtensionsStr = String.format(WRAPPED_FORMAT, xml);
        InputStream inStream = new ByteArrayInputStream(wrappedExtensionsStr.getBytes());
        JAXBContext jaxbContext = JAXBContext.newInstance(ExtensionsWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<ExtensionsWrapper> wrappedExtensions = jaxbUnmarshaller.unmarshal(new StreamSource(inStream), ExtensionsWrapper.class);
        if (wrappedExtensions.getValue() != null && wrappedExtensions.getValue().extensions != null) {
            return wrappedExtensions.getValue().extensions;
        } else {
            return Collections.emptyList();
        }
    }
}