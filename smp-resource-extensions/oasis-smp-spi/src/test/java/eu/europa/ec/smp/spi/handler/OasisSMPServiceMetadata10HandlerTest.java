/*-
 * #%L
 * oasis-smp-spi
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.validation.ServiceMetadata10Validator;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

class OasisSMPServiceMetadata10HandlerTest extends AbstractHandlerTest {


    @Override
    public AbstractOasisSMPHandler getTestInstance() {
        return new OasisSMPServiceMetadata10Handler(mockSmpDataApi, mockSmpIdentifierServiceApi, mockSignatureApi, new ServiceMetadata10Validator(mockSmpIdentifierServiceApi));
    }

    @Test
    void testGenerateResource() throws ResourceException {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("test-subidentifier", "test-test-test");

        generateResourceAction(resourceIdentifier, subResourceIdentifier);
    }

    @Test
    void validateResourceOK() throws ResourceException {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::107", "ehealth-resid-qns");
        // validate
        validateResourceAction("/examples/oasis-smp-1.0/ServiceMetadataWithServiceOk.xml", resourceIdentifier, subResourceIdentifier);
    }

    @Test
    void validateResourceDisallowedDocType() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:poland:ncpb", "ehealth-actorid-qns");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::107", "ehealth-resid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-1.0/ServiceMetadataWithDOCTYPE.xml", resourceIdentifier, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("DOCTYPE is disallowed"));
    }

    @Test
    void validateResourceInvalidIdentifier() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:poland:ncpb:wrongIdentifier", "ehealth-actorid-qns");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::101", "ehealth-resid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-1.0/ServiceMetadataWithServiceOk.xml", resourceIdentifier, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Participant identifiers don't match"));
    }

    @Test
    void validateResourceInvalidDocumentIdentifier() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::101:invalidIdentifeir", "ehealth-resid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-1.0/ServiceMetadataWithServiceOk.xml", resourceIdentifier, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Document identifiers don't match"));
    }

    @Test
    void validateResourceInvalidScheme() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:poland:ncpb", "ehealth-actorid-qns");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::10", "ehealth-resid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-1.0/ServiceMetadataMissingMandatoryFields.xml", resourceIdentifier, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("SAXParseException"));
    }

    @Test
    void readResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-1.0/ServiceMetadataWithServiceOk.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::10", "ehealth-resid-qns");

        readResourceAction(resourceName, resourceIdentifier, subResourceIdentifier);
    }


    @Test
    void storeResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-1.0/ServiceMetadataWithServiceOk.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");
        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::107", "ehealth-resid-qns");

        storeResourceAction(resourceName, resourceIdentifier, subResourceIdentifier);
    }


}
