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

package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupId;
import eu.europa.ec.edelivery.smp.exceptions.ConversionException;
import eu.europa.ec.edelivery.smp.exceptions.XmlParsingException;
import eu.europa.ec.cipa.smp.server.util.ExtensionUtils;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
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
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by migueti on 26/01/2017.
 */
public class ServiceGroupConverter {

    private static final String PARSER_DISALLOW_DTD_PARSING_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

    static Unmarshaller jaxbUnmarshaller;

    private static Unmarshaller getUnmarshaller() throws JAXBException {
        if(jaxbUnmarshaller != null) {
            return jaxbUnmarshaller;
        }

        synchronized (ServiceGroupConverter.class) {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceGroup.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return jaxbUnmarshaller;
        }
    }

    public static ServiceGroup unmarshal(String serviceGroupXml) {
        try {
            Document serviceGroupDoc = parse(serviceGroupXml);
            return getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
        } catch (ParserConfigurationException | IOException | SAXException | JAXBException e) {
            throw new XmlParsingException(e);
        }
    }

    public static ServiceGroup toServiceGroup(DBServiceGroup dbServiceGroup){
        ServiceGroup serviceGroup = new ServiceGroup();
        ParticipantIdentifierType identifier = new ParticipantIdentifierType(dbServiceGroup.getId().getBusinessIdentifier(), dbServiceGroup.getId().getBusinessIdentifierScheme());
        serviceGroup.setParticipantIdentifier(identifier);
        try {
            List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(dbServiceGroup.getExtension());
            serviceGroup.getExtensions().addAll(extensions);
        } catch (JAXBException e) {
            throw new ConversionException(e);
        }
        serviceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType(new ArrayList()));
        return serviceGroup;
    }

    private static Document parse(String serviceGroupXml) throws ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(serviceGroupXml.getBytes(UTF_8));
        return getDocumentBuilder().parse(inputStream);
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setFeature(PARSER_DISALLOW_DTD_PARSING_FEATURE, true);
        return documentBuilderFactory.newDocumentBuilder();
    }

    public static String extractExtensionsPayload(ServiceGroup serviceGroup) {
        try {
            return ExtensionUtils.marshalExtensions(serviceGroup.getExtensions());
        } catch (JAXBException | XMLStreamException e) {
            throw new ConversionException(e);
        }
    }

    public static DBServiceGroupId toDbModel(ParticipantIdentifierType serviceGroupId){
        return new DBServiceGroupId(serviceGroupId.getScheme(), serviceGroupId.getValue());
    }
}
