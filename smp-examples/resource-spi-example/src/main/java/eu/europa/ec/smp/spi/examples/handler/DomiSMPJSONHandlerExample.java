/*-
 * #START_LICENSE#
 * resource-spi-example
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
package eu.europa.ec.smp.spi.examples.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.security.utils.CertificateKeyType;
import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;

@Component
public class DomiSMPJSONHandlerExample extends AbstractHandler {


    private static final Logger LOG = LoggerFactory.getLogger(DomiSMPJSONHandlerExample.class);


    final SmpDataServiceApi smpDataApi;
    final SmpIdentifierServiceApi smpIdentifierApi;

    final SmpXmlSignatureApi signatureApi;


    public DomiSMPJSONHandlerExample(SmpDataServiceApi smpDataApi,
                                     SmpIdentifierServiceApi smpIdentifierApi,
                                     SmpXmlSignatureApi signatureApi) {
        this.smpDataApi = smpDataApi;
        this.smpIdentifierApi = smpIdentifierApi;
        this.signatureApi = signatureApi;
    }

    public void generateResource(RequestData resourceData, ResponseData responseData, List<String> fields) throws ResourceException {

        ResourceIdentifier identifier = getResourceIdentifier(resourceData);


        try {
            String identifierString = smpIdentifierApi.formatResourceIdentifier(
                    resourceData.getDomainCode(),
                    identifier);

            ExampleEntityRo exampleEntityRo = new ExampleEntityRo();
            exampleEntityRo.setIdentifier(identifierString);
            exampleEntityRo.setUrl("http://example.local/test");
            exampleEntityRo.setEmail("test.address@example.local");
            X509Certificate cert = createX509Certificate("CN=" + identifierString + ",O=edelivery,C=EU");
            exampleEntityRo.setCertificate(Base64.getEncoder().encodeToString(cert.getEncoded()));

            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(responseData.getOutputStream(), exampleEntityRo);

        } catch (IOException | CertificateEncodingException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal properties: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    public static X509Certificate createX509Certificate(String subject) throws ResourceException {
        try {
            KeyPair key = X509CertificateUtils.generateKeyPair(CertificateKeyType.RSA_2048);
            return X509CertificateUtils.generateCertificate(
                    BigInteger.TEN, key.getPublic(), subject, OffsetDateTime.now().minusDays(1),
                    OffsetDateTime.now().plusYears(1), subject,
                    key.getPrivate(), false, -1, null,
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        } catch (NoSuchProviderException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException | IOException |
                 CertificateException | OperatorCreationException e) {
            throw new ResourceException(INTERNAL_ERROR, "Error occurred at sample certificate generation!", e);
        }

    }


    @Override
    public void readResource(RequestData resourceData, ResponseData responseData) throws ResourceException {

        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        if (resourceData.getResourceInputStream() == null) {
            LOG.warn("Empty document input stream for service-group [{}]!", identifier);
            return;
        }

        InputStream inputStream = resourceData.getResourceInputStream();
        // reading resource multiple time make sure it can be rest
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(Integer.MAX_VALUE - 2);

        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Can not reset input stream", e);
        }

        try {
            StreamUtils.copy(inputStream, responseData.getOutputStream());
        } catch (IOException e) {
            throw new ResourceException(PROCESS_ERROR, "Error occurred while signing the cpp documen!: ["
                    + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

    }

    @Override
    public void storeResource(RequestData resourceData, ResponseData responseData) throws ResourceException {
        InputStream inputStream = resourceData.getResourceInputStream();
        // reading resource multiple time make sure it can be rest
        ByteArrayInputStream bios;
        try {
            bios = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));
        } catch (IOException e) {
            throw new ResourceException(ResourceException.ErrorCode.PROCESS_ERROR, ExceptionUtils.getRootCauseMessage(e), e);
        }
        inputStream.mark(Integer.MAX_VALUE - 2);


        validateAndParse(bios, getResourceIdentifier(resourceData), resourceData.getDomainCode());
        try {
            bios.reset();
            StreamUtils.copy(bios, responseData.getOutputStream());
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Error occurred while storing the resource", e);
        }
    }

    /**
     * Method validates service group
     *
     * @param resourceData the resource data
     */
    @Override
    public void validateResource(RequestData resourceData) throws ResourceException {
        validateAndParse(resourceData);
    }

    public ExampleEntityRo validateAndParse(RequestData resourceData) throws ResourceException {
        // get service group identifier
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        return validateAndParse(resourceData.getResourceInputStream(), identifier, resourceData.getDomainCode());
    }

    public ExampleEntityRo validateAndParse(InputStream inputStream, ResourceIdentifier identifier, String domainCode) throws ResourceException {
        // get service group identifier

        // validate by schema
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ExampleEntityRo entityRo;
        try {
            entityRo = mapper.readValue(inputStream, ExampleEntityRo.class);
        } catch (IOException ex) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while reading example property document: [" + identifier + "] with error: " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }

        if (StringUtils.isBlank(entityRo.getIdentifier())) {
            throw new ResourceException(INVALID_RESOURCE, "Missing  property [identifier]!");
        }

        if (StringUtils.isBlank(entityRo.getUrl())) {
            throw new ResourceException(INVALID_RESOURCE, "Missing  property [url]!");
        }

        if (StringUtils.isBlank(entityRo.getEmail())) {
            throw new ResourceException(INVALID_RESOURCE, "Missing  property [email]!");
        }

        if (StringUtils.isBlank(entityRo.getCertificate())) {
            throw new ResourceException(INVALID_RESOURCE, "Missing  property [certificate]");
        }

        String identifierString = smpIdentifierApi.formatResourceIdentifier(domainCode, identifier);
        if (!StringUtils.equalsIgnoreCase(entityRo.getIdentifier(), identifierString)) {
            throw new ResourceException(INVALID_RESOURCE, "Property: [identifier] does not match value for the resource [" + identifierString + "]");
        }

        try {
            new URL(entityRo.getUrl());
        } catch (MalformedURLException e) {
            throw new ResourceException(INVALID_RESOURCE, "Bad property value: [url]!. Value [" + entityRo.getUrl() + "]  is not URL");
        }
        return entityRo;
    }

}
