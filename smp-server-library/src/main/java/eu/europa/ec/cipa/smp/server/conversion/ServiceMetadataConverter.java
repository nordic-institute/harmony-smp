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

package eu.europa.ec.cipa.smp.server.conversion;

import com.helger.commons.xml.transform.XMLTransformerFactory;
import eu.europa.ec.cipa.smp.server.errors.exceptions.XmlParsingException;
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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by gutowpa on 05/01/2017.
 */
public class ServiceMetadataConverter {

    private static final String NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String DOC_SIGNED_SERVICE_METADATA_EMPTY = "<SignedServiceMetadata xmlns=\""+NS+"\"/>";
    private static final String PARSER_DISALLOW_DTD_PARSING_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

    static Unmarshaller jaxbUnmarshaller;

    private static Unmarshaller getUnmarshaller() throws JAXBException {
        if (jaxbUnmarshaller != null) {
            return jaxbUnmarshaller;
        }
        synchronized (ServiceMetadataConverter.class) {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceMetadata.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return jaxbUnmarshaller;
        }
    }

    public static Document toSignedServiceMetadatadaDocument(String serviceMetadataXml)  {
        try {
            Document docServiceMetadata = parse(serviceMetadataXml);
            Document root = parse(DOC_SIGNED_SERVICE_METADATA_EMPTY);
            Node imported = root.importNode(docServiceMetadata.getDocumentElement(), true);
            root.getDocumentElement().appendChild(imported);
            return root;
        }catch(ParserConfigurationException | SAXException | IOException e){
            throw new XmlParsingException(e);
        }
    }


    public static ServiceMetadata unmarshal(String serviceMetadataXml){
        try {
            Document serviceMetadataDoc = parse(serviceMetadataXml);
            ServiceMetadata serviceMetadata = getUnmarshaller().unmarshal(serviceMetadataDoc, ServiceMetadata.class).getValue();
            return serviceMetadata;
        } catch (SAXException | IOException | ParserConfigurationException | JAXBException e) {
            throw new XmlParsingException(e);
        }
    }

    private static Document parse(String serviceMetadataXml) throws SAXException, IOException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(serviceMetadataXml.getBytes());
        return getDocumentBuilder().parse(inputStream);
    }

    public static String toString(Document doc) throws TransformerException, UnsupportedEncodingException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature(PARSER_DISALLOW_DTD_PARSING_FEATURE, true);
        return dbf.newDocumentBuilder();
    }

}
