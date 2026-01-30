/*-
 * #START_LICENSE#
 * oasis-smp-spi
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
package eu.europa.ec.smp.peppol.handler;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.peppol.handler.AbstractPeppolSMPHandler;
import eu.europa.ec.smp.spi.peppol.handler.PeppolSMPResourceHandler;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


class PeppolSMPResourceHandlerTest extends AbstractHandlerTest {
    @Override
    public AbstractPeppolSMPHandler getTestInstance() {
        return new PeppolSMPResourceHandler(mockSmpDataApi, mockSmpIdentifierServiceApi);
    }

    @Test
    void testGenerateResource() throws ResourceException {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178r", "iso6523-actorid-upis");

        generateResourceAction(resourceIdentifier);
    }

    @Test
    void validateResourceOK() throws ResourceException {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178", "iso6523-actorid-upis");
        // validate
        validateResourceAction("/examples/peppol/ResourceOK.xml", resourceIdentifier);
    }

    @Test
    void validateResourceDisallowedDocType() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178", "iso6523-actorid-upis");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/peppol/ResourceWithDOCTYPE.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("DOCTYPE is disallowed"));
    }

    @Test
    void validateResourceInvalidIdentifier() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:INVALID", "iso6523-actorid-upis");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/peppol/ResourceOK.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Participant identifiers don't match"));
    }

    @Test
    void validateResourceInvalidScheme() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178", "iso6523-actorid-upis");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/peppol/ResourceInvalidScheme.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("SAXParseException"));
    }

    @Test
    void readResourceOK() throws ResourceException {
        String resourceName = "/examples/peppol/ResourceOK.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178", "iso6523-actorid-upis");

        readResourceAction(resourceName, resourceIdentifier);
    }

    @Test
    void readResourceBusinessCardOK() throws ResourceException {
        String resourceName = "/examples/peppol/ResourceWithBusinessCardOK.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178", "iso6523-actorid-upis");

        readResourceAction("businesscard", resourceName, resourceIdentifier);
    }

    @Test
    void storeResourceOK() throws ResourceException {
        String resourceName = "/examples/peppol/ResourceOK.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178", "iso6523-actorid-upis");

        storeResourceAction(resourceName, resourceIdentifier);
    }


}
