/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.validation.ServiceMetadata20Validator;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

class OasisSMPServiceMetadata20HandlerTest extends AbstractHandlerTest {


    ResourceIdentifier resourceIdentifier = new ResourceIdentifier( "9915:123456789", "iso6523-actorid-upis");
    ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1", "bdx-docid-qns");

    @Override
    public AbstractOasisSMPHandler getTestInstance() {
        return new OasisSMPServiceMetadata20Handler(mockSmpDataApi, mockSmpIdentifierServiceApi, mockSignatureApi, new ServiceMetadata20Validator(mockSmpIdentifierServiceApi) );
    }

    @Test
    void testGenerateResource() throws ResourceException {

        generateResourceAction(resourceIdentifier, subResourceIdentifier);
    }

    @Test
    void validateResourceOK() throws ResourceException {

        // validate
        validateResourceAction("/examples/oasis-smp-2.0/service_metadata_unsigned_valid_iso6523.xml", resourceIdentifier, subResourceIdentifier);
    }

    @Test
    void validateResourceDisallowedDocType() {
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-2.0/service_metadata_unsigned_invalid_iso6523_DTD.xml", resourceIdentifier, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("accessExternalDTD"));
    }

    @Test
    void validateResourceInvalidIdentifier() {
        ResourceIdentifier resourceIdentifierInvalid = new ResourceIdentifier("urn:poland:ncpb:wrongIdentifier", "ehealth-actorid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-2.0/service_metadata_unsigned_valid_iso6523.xml", resourceIdentifierInvalid, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Participant identifiers don't match"));
    }

    @Test
    void validateResourceInvalidDocumentIdentifier() {

        ResourceIdentifier subResourceIdentifier = new ResourceIdentifier("urn::epsos##services:extended:epsos::101:invalidIdentifeir", "ehealth-resid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-2.0/service_metadata_unsigned_valid_iso6523.xml", resourceIdentifier, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Document identifiers don't match"));
    }

    @Test
    void validateResourceInvalidScheme() {

        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-2.0/service_metadata_unsigned_invalid_iso6523.xml", resourceIdentifier, subResourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("SAXParseException"));
    }

    @Test
    void readResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-2.0/service_metadata_unsigned_valid_iso6523.xml";

        readResourceAction(resourceName, resourceIdentifier, subResourceIdentifier);
    }

    @Test
    void storeResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-2.0/service_metadata_unsigned_valid_iso6523.xml";
        storeResourceAction(resourceName, resourceIdentifier, subResourceIdentifier);
    }


}
