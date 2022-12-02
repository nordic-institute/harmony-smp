/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_EXTENSION_FOR_SG;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *  Purpose of class is to test ServiceGroupService base methods
 *
 * @author migueti
 * @since 3.0.0
 */
public class ServiceGroupConverter {

    /**
     * Class has only static members.
     */
    private ServiceGroupConverter() {

    }

    private static final String PARSER_DISALLOW_DTD_PARSING_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupConverter.class);

    private static final ThreadLocal<Unmarshaller> jaxbUnmarshaller = ThreadLocal.withInitial(() -> {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceGroup.class);
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            LOG.error("Error occurred while initializing JAXBContext for ServiceMetadata. Cause message:", ex);
        }
        return null;
    });


    private static Unmarshaller getUnmarshaller() {
        return jaxbUnmarshaller.get();
    }

    /**
     * Method unmarshal ServiceGroup from xml string
     *
     * @param serviceGroupXml service group xml
     * @return java object Object
     */
    public static ServiceGroup unmarshal(String serviceGroupXml) {
        try {
            Document serviceGroupDoc = parse(serviceGroupXml);
            return getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
        } catch (ParserConfigurationException | IOException | SAXException | JAXBException ex) {
            throw new SMPRuntimeException(ErrorCode.XML_PARSE_EXCEPTION, ex, ServiceGroup.class.getName(), ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    /**
     * Method unmarshal ServiceGroup from xml bytearray
     *
     * @param serviceGroupXml
     * @return
     */
    public static ServiceGroup unmarshal(byte[] serviceGroupXml) {

        try {
            Document serviceGroupDoc = parse(serviceGroupXml);
            ServiceGroup serviceGroup = getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
            return serviceGroup;
        } catch (ParserConfigurationException | IOException | SAXException | JAXBException ex) {
            throw new SMPRuntimeException(ErrorCode.XML_PARSE_EXCEPTION, ex, ServiceGroup.class.getName(), ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    private static Document parse(String serviceGroupXml) throws ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(serviceGroupXml.getBytes(UTF_8));
        return getDocumentBuilder().parse(inputStream);
    }

    private static Document parse(byte[] serviceGroupXml) throws ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(serviceGroupXml);
        return getDocumentBuilder().parse(inputStream);
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setFeature(PARSER_DISALLOW_DTD_PARSING_FEATURE, true);
        return documentBuilderFactory.newDocumentBuilder();
    }

    public static byte[] extractExtensionsPayload(ServiceGroup sg) {
        try {
            return ExtensionConverter.marshalExtensions(sg.getExtensions());
        } catch (JAXBException | XMLStreamException | IOException e) {
            throw new SMPRuntimeException(INVALID_EXTENSION_FOR_SG, e,
                    sg.getParticipantIdentifier().getValue(), sg.getParticipantIdentifier().getScheme(),
                    ExceptionUtils.getRootCauseMessage(e));
        }
    }
}
