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

import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.api.Identifiers;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceCollectionType;
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

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_EXTENSION_FOR_SG;
import static eu.europa.ec.smp.api.Identifiers.EBCORE_IDENTIFIER_PREFIX;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by migueti on 26/01/2017.
 */
public class ServiceGroupConverter {

    /**
     * Class has only static members.
     */
    private  ServiceGroupConverter() {

    }

    private static final String PARSER_DISALLOW_DTD_PARSING_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupConverter.class);

    private static final ThreadLocal<Unmarshaller> jaxbUnmarshaller = ThreadLocal.withInitial( () -> {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceGroup.class);
            return jaxbContext.createUnmarshaller();
        }catch(JAXBException ex) {
            LOG.error("Error occurred while initializing JAXBContext for ServiceMetadata. Cause message:", ex);
        }
        return null;
    } );


    private static Unmarshaller getUnmarshaller() {
        return jaxbUnmarshaller.get();
    }

    /**
     * Method umarshal ServiceGroup from xml string
     * @param serviceGroupXml
     * @return
     */
    public static ServiceGroup unmarshal(String serviceGroupXml) {
        try {
            Document serviceGroupDoc = parse(serviceGroupXml);
            return getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
        } catch (ParserConfigurationException | IOException | SAXException | JAXBException ex) {
            throw new SMPRuntimeException(ErrorCode.XML_PARSE_EXCEPTION,ex,ServiceGroup.class.getName(), ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    /**
     * Method umarshal ServiceGroup from xml bytearraz
     * @param serviceGroupXml
     * @return
     */
    public static ServiceGroup unmarshal(byte[] serviceGroupXml) {
        try {
            System.out.println("UNMARSHAL SERVICE GROUP " + new String(serviceGroupXml));
            Document serviceGroupDoc = parse(serviceGroupXml);
            ServiceGroup serviceGroup =  getUnmarshaller().unmarshal(serviceGroupDoc, ServiceGroup.class).getValue();
            if (serviceGroup!=null && serviceGroup.getParticipantIdentifier()!=null
            && StringUtils.isBlank(serviceGroup.getParticipantIdentifier().getScheme())
            && StringUtils.startsWithAny(serviceGroup.getParticipantIdentifier().getValue(),
                    EBCORE_IDENTIFIER_PREFIX,
                    "::"+ EBCORE_IDENTIFIER_PREFIX)){
                // normalize participant identifier
                LOG.info("Normalize ebCore identifier: " + serviceGroup.getParticipantIdentifier().getValue());
                ParticipantIdentifierType participantIdentifierType = Identifiers.asParticipantId(serviceGroup.getParticipantIdentifier().getValue());
                serviceGroup.setParticipantIdentifier(participantIdentifierType);
            }
            return serviceGroup;
        } catch (ParserConfigurationException | IOException | SAXException | JAXBException ex) {
            throw new SMPRuntimeException(ErrorCode.XML_PARSE_EXCEPTION,ex,ServiceGroup.class.getName(), ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    /**
     * Method returns Oasis ServiceGroup entity with  extension and
     * empty ServiceMetadataReferenceCollectionType. If extension can not be converted to jaxb object than
     * ConversionException is thrown.
     *
     * @param dsg - database service group entity
     * @return Oasis ServiceGroup entity or null if parameter is null
     */
    public static ServiceGroup toServiceGroup(DBServiceGroup dsg, boolean concatenateEBCoreID){

        if (dsg==null){
            return null;
        }

        ServiceGroup serviceGroup = new ServiceGroup();
        String schema  = dsg.getParticipantScheme();
        String value  = dsg.getParticipantIdentifier();
        if (concatenateEBCoreID && StringUtils.startsWithIgnoreCase(schema, EBCORE_IDENTIFIER_PREFIX) ){
            value = schema + ":" + value;
            schema = null;
        }
        ParticipantIdentifierType identifier = new ParticipantIdentifierType(value, schema);
        serviceGroup.setParticipantIdentifier(identifier);
        if (dsg.getExtension()!=null){
            try {
                List<ExtensionType> extensions = ExtensionConverter.unmarshalExtensions(dsg.getExtension());
                serviceGroup.getExtensions().addAll(extensions);
            } catch (JAXBException e) {
                 throw new SMPRuntimeException(INVALID_EXTENSION_FOR_SG, e, dsg.getParticipantIdentifier(),
                         dsg.getParticipantScheme(),ExceptionUtils.getRootCauseMessage(e));
            }
        }
        serviceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType(new ArrayList()));
        return serviceGroup;
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
        } catch (JAXBException | XMLStreamException | IOException  e) {
            throw new SMPRuntimeException(INVALID_EXTENSION_FOR_SG, e,
                    sg.getParticipantIdentifier().getValue(), sg.getParticipantIdentifier().getScheme(),
                    ExceptionUtils.getRootCauseMessage(e));
        }
    }

}
