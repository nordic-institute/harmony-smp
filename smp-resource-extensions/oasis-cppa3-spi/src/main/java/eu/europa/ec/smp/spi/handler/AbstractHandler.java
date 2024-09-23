/*-
 * #START_LICENSE#
 * oasis-cppa3-spi
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.CPPARuntimeException;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import gen.eu.europa.ec.ddc.api.cppa.CPP;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * The abstract class with common methods for implementation of the  ResourceHandlerSpi.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public abstract class AbstractHandler implements ResourceHandlerSpi {

    static final Logger LOG = LoggerFactory.getLogger(AbstractHandler.class);

    private static final String DISALLOW_DOCTYPE_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final ThreadLocal<DocumentBuilder> threadLocalDocumentBuilder = ThreadLocal.withInitial(() -> createDocumentBuilder());

    public static DocumentBuilder createDocumentBuilder() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        enableFeature(factory, DISALLOW_DOCTYPE_FEATURE);
        enableFeature(factory, XMLConstants.FEATURE_SECURE_PROCESSING);

        try {
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new CPPARuntimeException(CPPARuntimeException.ErrorCode.INITIALIZE_ERROR, "Can not create new XML Document builder! Error: [" + ExceptionUtils.getRootCauseMessage(ex) + "]", ex);
        }
    }

    private static boolean enableFeature(DocumentBuilderFactory factory, String feature) {
        try {
            factory.setFeature(feature, true);
            return true;
        } catch (ParserConfigurationException e) {
            LOG.warn("DocumentBuilderFactory initialization error. The feature [{}] is not supported by current factory. The feature is ignored.", feature);
            return false;
        }
    }

    private static final ThreadLocal<Unmarshaller> jaxbUnmarshaller = ThreadLocal.withInitial(() -> {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CPP.class);
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            LOG.error("Error occurred while initializing JAXBContext for ServiceGroup. Cause message:" + ex, ex);
        }
        return null;
    });

    private static final ThreadLocal<Marshaller> jaxbMarshaller = ThreadLocal.withInitial(() -> {
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(CPP.class);
            return jaxbContext.createMarshaller();
        } catch (JAXBException ex) {
            LOG.error("Error occurred while initializing JAXBContext for ServiceGroup. Cause message:" + ex, ex);
        }
        return null;
    });

    private static final ThreadLocal<Validator> oasisCPPAValidator = ThreadLocal.withInitial(() -> {
        URL xsdFilePath = AbstractHandler.class.getResource("/xsd/cppa3.xsd");
        return generateValidatorForSchema(xsdFilePath);
    });

    protected static Validator getOasisCPPAValidator() {
        return oasisCPPAValidator.get();
    }

    public Unmarshaller getUnmarshaller() {
        return jaxbUnmarshaller.get();
    }

    public Marshaller getMarshaller() {
        return jaxbMarshaller.get();
    }

    /**
     * Removes the current thread's ServiceGroup Unmarshaller for this thread-local variable. If this thread-local variable
     * is subsequently read by the current thread, its value will be reinitialized by invoking its initialValue method.
     */
    public void destroyUnmarshaller() {
        jaxbUnmarshaller.remove();
    }

    public void destroyMarshaller() {
        jaxbMarshaller.remove();
    }

    public byte[] readFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private static Validator generateValidatorForSchema(URL xsdFilePath) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(xsdFilePath);
            Validator vaInstance = schema.newValidator();
            vaInstance.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            vaInstance.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return vaInstance;
        } catch (SAXException e) {
            throw new IllegalStateException("Unable to initialize BDX CPPA OASIS XSD schema validator.", e);
        }
    }

    public Document parse(InputStream inputStream) throws IOException, SAXException {
        DocumentBuilder builder = getDocumentBuilder();
        try {
            return builder.parse(inputStream);
        } finally {
            builder.reset();
        }
    }


    public CPP parseNative(Document document) {
        try {
            return (CPP) jaxbUnmarshaller.get().unmarshal(document);
        } catch (JAXBException ex) {
            throw new CPPARuntimeException(CPPARuntimeException.ErrorCode.PARSE_ERROR, "Can not parse XML Document ! Error: [" + ExceptionUtils.getRootCauseMessage(ex) + "]", ex);
        }
    }


    public CPP parseNative(InputStream inputStream) {
        try {
            DocumentBuilder db = createDocumentBuilder();
            // just to validate DISALLOW_DOCTYPE_FEATURE parse to Document
            Document document = db.parse(inputStream);
            return parseNative(document);
        } catch (SAXException | IOException ex) {
            throw new CPPARuntimeException(CPPARuntimeException.ErrorCode.PARSE_ERROR, "Can not parse XML Document ! Error: [" + ExceptionUtils.getRootCauseMessage(ex) + "]", ex);
        }
    }


    public void serializeNative(Object jaxbObject, OutputStream outputStream, boolean prettyPrint) {
        if (jaxbObject == null) {
            return;
        }
        Marshaller jaxbMarsh = getMarshaller();

        // Pretty Print XML
        try {
            jaxbMarsh.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            jaxbMarsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint ? Boolean.TRUE : Boolean.FALSE);

            // to remove xmlDeclaration
            jaxbMarsh.marshal(jaxbObject, outputStream);
        } catch (JAXBException ex) {
            throw new CPPARuntimeException(CPPARuntimeException.ErrorCode.PARSE_ERROR, "Error occurred while serializing the CPP document! Error: [" + ExceptionUtils.getRootCauseMessage(ex) + "]", ex);
        }
    }

    protected DocumentBuilder getDocumentBuilder() {
        return threadLocalDocumentBuilder.get();
    }


    public QName getRootElementQName(Document document) {
        Element element = document.getDocumentElement();
        String namespace = element.getNamespaceURI();
        return new QName(namespace, element.getTagName());
    }


    public ResourceIdentifier getResourceIdentifier(RequestData resourceData) throws ResourceException {
        if (resourceData == null || resourceData.getResourceIdentifier() == null || StringUtils.isEmpty(resourceData.getResourceIdentifier().getValue())) {
            throw new ResourceException(ResourceException.ErrorCode.INVALID_PARAMETERS, "Missing resource identifier for the resource CPP ");
        }
        return resourceData.getResourceIdentifier();
    }

    public ResourceIdentifier getSubresourceIdentifier(RequestData resourceData) throws ResourceException {
        if (resourceData == null || resourceData.getSubresourceIdentifier() == null || StringUtils.isEmpty(resourceData.getSubresourceIdentifier().getValue())) {
            throw new ResourceException(ResourceException.ErrorCode.INVALID_PARAMETERS, "Missing sub-resource identifier for the resource service metadata!");
        }
        return resourceData.getSubresourceIdentifier();
    }
}
