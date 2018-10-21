/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.apache.cxf.staxutils.PrettyPrintXMLStreamWriter;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by migueti on 13/02/2017.
 */
public class ExtensionConverter {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupConverter.class);
   // private static final String WRAPPED_FORMAT = "<ExtensionsWrapper xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">%s</ExtensionsWrapper>";
    private static final byte[] WRAPPED_FORMAT_START = "<ExtensionsWrapper xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">".getBytes();
    private static final byte[] WRAPPED_FORMAT_END = "</ExtensionsWrapper>".getBytes();
    private static final byte[] WRAPPED_SERVICE_GROUP_START = "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"> <ParticipantIdentifier scheme=\"schema\">value</ParticipantIdentifier><ServiceMetadataReferenceCollection/>".getBytes();
    private static final byte[] WRAPPED_SERVICE_GROUP_END = "</ServiceGroup>".getBytes();
    private static final QName EXT_TYPE_QNAME = new QName("http://docs.oasis-open.org/bdxr/ns/SMP/2016/05", "Extension");

    /**
     * Create root extension wrapper to made marshal and unmarshal easier.
     */
    @XmlRootElement(name = "ExtensionsWrapper")
    private static class ExtensionsWrapper {
        @XmlElement(name = "Extension")
        List<ExtensionType> extensions;
    }

    /**
     * Create static thread safe umarshaller.
     */
    private static final ThreadLocal<Unmarshaller> extensionUnmarshaller = ThreadLocal.withInitial( () -> {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ExtensionsWrapper.class, ExtensionType.class);
            return jaxbContext.createUnmarshaller();
        }catch(JAXBException ex) {
            LOG.error("Error occured while initializing JAXBContext for ServiceMetadata. Cause message:", ex);
        }
        return null;
    });

    private static Unmarshaller getUnmarshaller() {
        return extensionUnmarshaller.get();
    }

    public static byte[] marshalExtensions(List<ExtensionType> extensions) throws JAXBException, XMLStreamException, IOException {
        return marshalExtensions(extensions, false);
    }




    public static  byte[] marshalExtensions(List<ExtensionType> extensions, boolean prettyPrint ) throws JAXBException, XMLStreamException, IOException {
        if (extensions == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
     //   StringBuilder stringBuilder = new StringBuilder();
        for (ExtensionType aExtension : extensions) {
            baos.write(ExtensionConverter.marshalExtension(aExtension, prettyPrint));
       //     stringBuilder.append(ExtensionConverter.marshalExtension(aExtension, prettyPrint));
        }
        return baos.toByteArray();
    }

    private static byte[] marshalExtension(ExtensionType extension, boolean prettyPrint ) throws JAXBException, XMLStreamException {
        if (extension == null) {
            return null;
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(ExtensionType.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        JAXBElement jaxbElement = new JAXBElement(EXT_TYPE_QNAME, ExtensionType.class, extension);
        jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLStreamWriter xmlStreamWriter = null;
        PrettyPrintXMLStreamWriter xsw = null;
        try {
            xmlStreamWriter =  xof.createXMLStreamWriter(baos);
            if (prettyPrint) {
                xsw = new PrettyPrintXMLStreamWriter(xmlStreamWriter, 4);
            }
            jaxbMarshaller.marshal(jaxbElement,prettyPrint?xsw: xmlStreamWriter);
        } finally {
            if (xmlStreamWriter != null) {
                xmlStreamWriter.close();
            }
            if (xsw != null) {
                xsw.close();
            }
        }
        //return baos.toString(UTF_8.name());
        return baos.toByteArray();
    }

    public static List<ExtensionType> unmarshalExtensions(byte[] xml) throws JAXBException {


        InputStream inStream = new ByteArrayInputStream(concatByteArrays(WRAPPED_FORMAT_START,xml,WRAPPED_FORMAT_END  ));

        Unmarshaller jaxbUnmarshaller = getUnmarshaller();
        JAXBElement<ExtensionsWrapper> wrappedExtensions = jaxbUnmarshaller.unmarshal(new StreamSource(inStream), ExtensionsWrapper.class);
        if (wrappedExtensions.getValue() != null && wrappedExtensions.getValue().extensions != null) {
            return wrappedExtensions.getValue().extensions;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Method validates extension by schema In order to do that wraps the content to simple servicegroup.
     *
     * @param xml
     */
    public static  void validateExtensionBySchema(byte[] xml) throws XmlInvalidAgainstSchemaException {
        byte[] buff = concatByteArrays(WRAPPED_SERVICE_GROUP_START,xml,WRAPPED_SERVICE_GROUP_END);
        BdxSmpOasisValidator.validateXSD(buff);
    }

    /**
     * Method concat the bytearrays to one array
     *
     *
     * https://stackoverflow.com/questions/5513152/easy-way-to-concatenate-two-byte-arrays
     * - Use varargs (...) to be called with any number of byte[].
     * - Use System.arraycopy() that is implemented with machine specific native code, to ensure high speed operation.
     * - Create a new byte[] with the exact size that is need it.
     * - Allocate little less int variables by reusing the i and len variables.

     * @param inputs - byte arrays
     * @return
     */
    public static byte[] concatByteArrays(byte[]... inputs) {
        int i, len = 0;
        for (i = 0; i < inputs.length; i++) {
            len += inputs[i].length;
        }
        byte[] r = new byte[len];
        len = 0;
        for (i = 0; i < inputs.length; i++) {
            System.arraycopy(inputs[i], 0, r, len, inputs[i].length);
            len += inputs[i].length;
        }
        return r;
    }
}
