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

package eu.europa.ec.smp.spi.converter;


import eu.europa.ec.smp.spi.exceptions.ResourceException;
import gen.eu.europa.ec.ddc.api.smp10.ExtensionType;
import gen.eu.europa.ec.ddc.api.smp10.ServiceGroup;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.List;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.INVALID_RESOURCE;
import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.PARSE_ERROR;

/**
 *  Purpose of class is to test ServiceGroupService base methods
 *
 * @author migueti
 * @since 3.0.0
 */
public class ServiceGroupConverter {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceGroupConverter.class);

    /**
     * Class has only static members.
     */
    private ServiceGroupConverter() {

    }

    private static final String PARSER_DISALLOW_DTD_PARSING_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";


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
     * Method unmarshal ServiceGroup from xml bytearray
     *
     * @param serviceGroupXml
     * @return
     */
    public static ServiceGroup unmarshal(byte[] serviceGroupXml) throws ResourceException {

        try {
            Document serviceGroupDoc = parse(serviceGroupXml);
            return getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
        } catch (ParserConfigurationException | IOException | SAXException | JAXBException ex) {
            throw new ResourceException(PARSE_ERROR, "Error occurred while parsing resource: " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
    }

    public static ServiceGroup unmarshal(InputStream inputStream) throws ResourceException {

        try {
            Document serviceGroupDoc = parse(inputStream);
            return getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
        } catch (ParserConfigurationException | IOException | SAXException | JAXBException ex) {
            throw new ResourceException(PARSE_ERROR, "Error occurred while parsing resource: " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
    }

    private static Document parse(byte[] serviceGroupXml) throws ParserConfigurationException, IOException, SAXException {
        return parse(new ByteArrayInputStream(serviceGroupXml));
    }

    private static Document parse(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        return getDocumentBuilder().parse(inputStream);
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setFeature(PARSER_DISALLOW_DTD_PARSING_FEATURE, true);
        return documentBuilderFactory.newDocumentBuilder();
    }

    public static byte[] extractExtensionsPayload(ServiceGroup sg) throws ResourceException {
        try {
            return ExtensionConverter.marshalExtensions(sg.getExtensions());
        } catch (JAXBException | XMLStreamException | IOException e) {
            throw new ResourceException(INVALID_RESOURCE, "Invalid extension with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    public static void marshalToOutputStream(ServiceGroup serviceGroup, OutputStream outputStream) throws JAXBException {
       marshalToOutputStream(serviceGroup, false, outputStream);
    }

    private static void marshalToOutputStream(ServiceGroup serviceGroup, boolean prettyPrint, OutputStream outputStream) throws JAXBException {
        if (serviceGroup == null) {
            return;
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceGroup.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // Pretty Print XML
        if (prettyPrint) {
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);
        }
        // to remove xmlDeclaration
        jaxbMarshaller.marshal(serviceGroup, outputStream);
    }
}
