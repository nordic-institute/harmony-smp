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

import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.def.OasisSMPResource10;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

abstract class AbstractHandlerTest {
    protected SmpDataServiceApi mockSmpDataApi = Mockito.mock(SmpDataServiceApi.class);
    protected SmpIdentifierServiceApi mockSmpIdentifierServiceApi = Mockito.mock(SmpIdentifierServiceApi.class);
    protected SmpXmlSignatureApi mockSignatureApi = Mockito.mock(SmpXmlSignatureApi.class);


    protected RequestData requestData = Mockito.mock(RequestData.class);
    protected ResponseData responseData = Mockito.mock(ResponseData.class);

    void readResourceAction(String resourceName, ResourceIdentifier resourceIdentifier) throws ResourceException {
        readResourceAction(resourceName, resourceIdentifier, null);
    }

    void readResourceAction(String resourceName, ResourceIdentifier resourceIdentifier, ResourceIdentifier subresourceIdentifier) throws ResourceException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(baos).when(responseData).getOutputStream();
        Mockito.doReturn(OasisSMPResource10.class.getResourceAsStream(resourceName)).when(requestData).getResourceInputStream();
        Mockito.doReturn(resourceIdentifier).when(requestData).getResourceIdentifier();
        if (subresourceIdentifier != null) {
            Mockito.doReturn(subresourceIdentifier).when(requestData).getSubresourceIdentifier();
        }

        Mockito.when(mockSmpIdentifierServiceApi.normalizeResourceIdentifier(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> new ResourceIdentifier((String) i.getArguments()[0], (String) i.getArguments()[1]));
        getTestInstance().readResource(requestData, responseData);

        assertTrue(baos.size() > 0);
    }

    void storeResourceAction(String resourceName, ResourceIdentifier resourceIdentifier) throws ResourceException {
        storeResourceAction(resourceName, resourceIdentifier, null);
    }

    void storeResourceAction(String resourceName, ResourceIdentifier resourceIdentifier, ResourceIdentifier subresourceIdentifier) throws ResourceException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(baos).when(responseData).getOutputStream();
        Mockito.doReturn(OasisSMPResource10.class.getResourceAsStream(resourceName)).when(requestData).getResourceInputStream();
        Mockito.doReturn(resourceIdentifier).when(requestData).getResourceIdentifier();
        if (subresourceIdentifier != null) {
            Mockito.doReturn(subresourceIdentifier).when(requestData).getSubresourceIdentifier();
            Mockito.when(mockSmpIdentifierServiceApi.normalizeSubresourceIdentifier(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> new ResourceIdentifier((String) i.getArguments()[0], (String) i.getArguments()[1]));
        }
        Mockito.when(mockSmpIdentifierServiceApi.normalizeResourceIdentifier(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> new ResourceIdentifier((String) i.getArguments()[0], (String) i.getArguments()[1]));

        getTestInstance().storeResource(requestData, responseData);
    }

    void validateResourceAction(String resourceName, ResourceIdentifier resourceIdentifier) throws ResourceException {
        validateResourceAction(resourceName, resourceIdentifier, null);
    }

    void validateResourceAction(String resourceName, ResourceIdentifier resourceIdentifier, ResourceIdentifier subresourceIdentifier) throws ResourceException {
        // validate
        if (subresourceIdentifier != null) {
            Mockito.doReturn(subresourceIdentifier).when(requestData).getSubresourceIdentifier();
            Mockito.when(mockSmpIdentifierServiceApi.normalizeSubresourceIdentifier(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> new ResourceIdentifier((String) i.getArguments()[0], (String) i.getArguments()[1]));
        }
        Mockito.doReturn(OasisSMPResource10.class.getResourceAsStream(resourceName)).when(requestData).getResourceInputStream();
        Mockito.doReturn(resourceIdentifier).when(requestData).getResourceIdentifier();
        Mockito.when(mockSmpIdentifierServiceApi.normalizeResourceIdentifier(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> new ResourceIdentifier((String) i.getArguments()[0], (String) i.getArguments()[1]));



        getTestInstance().validateResource(requestData);
    }


    void generateResourceAction(ResourceIdentifier resourceIdentifier) throws ResourceException {
        generateResourceAction(resourceIdentifier, null);
    }

    void generateResourceAction(ResourceIdentifier resourceIdentifier, ResourceIdentifier subresourceIdentifier) throws ResourceException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(resourceIdentifier).when(requestData).getResourceIdentifier();
        if (subresourceIdentifier != null) {
            Mockito.doReturn(subresourceIdentifier).when(requestData).getSubresourceIdentifier();
            Mockito.when(mockSmpIdentifierServiceApi.normalizeSubresourceIdentifier(Mockito.anyString(), Mockito.anyString())).thenAnswer(i -> new ResourceIdentifier((String) i.getArguments()[0], (String) i.getArguments()[1]));

        }
        Mockito.doReturn(baos).when(responseData).getOutputStream();

        getTestInstance().generateResource(requestData, responseData, Collections.emptyList());
        assertTrue(baos.size() > 0);

        // The generated resource should be valid
        ByteArrayInputStream bios = new ByteArrayInputStream(baos.toByteArray());
        Mockito.doReturn(bios).when(requestData).getResourceInputStream();
        Mockito.doReturn(resourceIdentifier).when(mockSmpIdentifierServiceApi).normalizeResourceIdentifier(Mockito.anyString(), Mockito.anyString());

        getTestInstance().validateResource(requestData);
    }

    abstract AbstractOasisSMPHandler getTestInstance();
}
