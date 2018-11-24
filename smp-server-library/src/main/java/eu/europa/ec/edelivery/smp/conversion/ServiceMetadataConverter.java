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

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_SMD_XML;

;

/**
 * Created by gutowpa on 05/01/2017.
 */
public class ServiceMetadataConverter {

    /**
     * Class has only static members. Is not ment to create instances  - also SONAR warning.
     */
    private  ServiceMetadataConverter() {

    }

    private static final String NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String DOC_SIGNED_SERVICE_METADATA_EMPTY = "<SignedServiceMetadata xmlns=\""+NS+"\"/>";
    private static final String PARSER_DISALLOW_DTD_PARSING_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceMetadataConverter.class);


    private static final ThreadLocal<Unmarshaller> jaxbUnmarshaller = ThreadLocal.withInitial( () -> {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceMetadata.class);
            return jaxbContext.createUnmarshaller();
        }catch(JAXBException ex) {
            LOG.error("Error occurred while initializing JAXBContext for ServiceMetadata. Root Error:" +
                    ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return null;
    } );

    private static Unmarshaller getUnmarshaller() {
        return jaxbUnmarshaller.get();
    }

    /**
     * Method parses serviceMetadata XML and envelopes it to SignedServiceMetadata.
     * @param serviceMetadataXml
     * @return w3d dom element
     */
    public static Document toSignedServiceMetadatadaDocument(byte[] serviceMetadataXml)  {
        try {
            Document docServiceMetadata = parse(serviceMetadataXml);
            Document root = parse(DOC_SIGNED_SERVICE_METADATA_EMPTY.getBytes());
            Node imported = root.importNode(docServiceMetadata.getDocumentElement(), true);
            root.getDocumentElement().appendChild(imported);
            return root;
        }catch(ParserConfigurationException | SAXException | IOException ex){
            throw new SMPRuntimeException(INVALID_SMD_XML, ex, ExceptionUtils.getRootCauseMessage(ex));
        }
    }


    public static ServiceMetadata unmarshal(byte[] serviceMetadataXml){
        try {
            Document serviceMetadataDoc = parse(serviceMetadataXml);
            ServiceMetadata serviceMetadata = getUnmarshaller().unmarshal(serviceMetadataDoc, ServiceMetadata.class).getValue();
            return serviceMetadata;
        } catch (SAXException | IOException | ParserConfigurationException | JAXBException ex) {
            throw new SMPRuntimeException(INVALID_SMD_XML, ex, ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    private static Document parse(byte[] serviceMetadataXml) throws SAXException, IOException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(serviceMetadataXml);
        return getDocumentBuilder().parse(inputStream);
    }

    public static String toString(Document doc) throws TransformerException, UnsupportedEncodingException {
        Transformer transformer = createNewSecureTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    public static byte[] toByteArray(Document doc) throws TransformerException, UnsupportedEncodingException {
        Transformer transformer = createNewSecureTransformer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(stream));
        return stream.toByteArray();
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature(PARSER_DISALLOW_DTD_PARSING_FEATURE, true);
        return dbf.newDocumentBuilder();
    }

    private static Transformer createNewSecureTransformer() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        return factory.newTransformer();
    }




}
