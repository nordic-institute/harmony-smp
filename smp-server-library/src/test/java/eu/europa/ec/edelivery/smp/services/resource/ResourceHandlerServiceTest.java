/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.servlet.ResourceResponse;
import eu.europa.ec.smp.spi.def.OasisSMPResource10;
import eu.europa.ec.smp.spi.def.OasisSMPSubresource10;
import eu.europa.ec.smp.spi.handler.OasisSMPResource10Handler;
import eu.europa.ec.smp.spi.handler.OasisSMPSubresource10Handler;
import eu.europa.ec.smp.spi.validation.Subresource10Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;


// add SPI examples to the context
@ContextConfiguration(classes = {OasisSMPResource10.class,
        OasisSMPSubresource10.class,
        OasisSMPResource10Handler.class,
        OasisSMPSubresource10Handler.class,
        Subresource10Validator.class})
class ResourceHandlerServiceTest extends AbstractJunit5BaseDao {

    @Autowired
    private ConfigurationDao configurationDao;

    @Autowired
    ResourceHandlerService testInstance;

    protected ResourceRequest requestData = Mockito.mock(ResourceRequest.class);
    protected ResolvedData resolvedData = Mockito.mock(ResolvedData.class);
    protected ResourceResponse responseData = Mockito.mock(ResourceResponse.class);


    @BeforeEach
    public void prepareDatabase() throws IOException {


        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
        testUtilsDao.createResourceMemberships();
        resetKeystore();
        configurationDao.reloadPropertiesFromDatabase();

        // for reading the resource Oasis SMP 1.0
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/").when(request).getContextPath();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    }

    @Test
    void createResource() {
    }

    @Test
    void testReadResource() {
        Mockito.doReturn(resolvedData).when(requestData).getResolvedData();
        Mockito.doReturn(testUtilsDao.getResourceDefSmp()).when(resolvedData).getResourceDef();
        Mockito.doReturn(testUtilsDao.getD1()).when(resolvedData).getDomain();
        Mockito.doReturn(testUtilsDao.getResourceD1G1RD1()).when(resolvedData).getResource();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(baos).when(responseData).getOutputStream();

        testInstance.readResource(requestData, responseData);
        assertTrue(baos.size() > 0);
    }

    @Test
    void testReadSubresource() {

        Mockito.doReturn(resolvedData).when(requestData).getResolvedData();
        Mockito.doReturn(testUtilsDao.getResourceDefSmp()).when(resolvedData).getResourceDef();
        Mockito.doReturn(testUtilsDao.getSubresourceDefSmpMetadata()).when(resolvedData).getSubResourceDef();
        Mockito.doReturn(testUtilsDao.getD1()).when(resolvedData).getDomain();
        Mockito.doReturn(testUtilsDao.getResourceD1G1RD1()).when(resolvedData).getResource();
        Mockito.doReturn(testUtilsDao.getSubresourceD2G1RD1_S1()).when(resolvedData).getSubresource();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(baos).when(responseData).getOutputStream();

        testInstance.readSubresource(requestData, responseData);
        assertTrue(baos.size() > 0);
    }

    @Test
    void testCreateResource() {
        Mockito.doReturn(resolvedData).when(requestData).getResolvedData();
        Mockito.doReturn(ResourceHandlerService.class.getResourceAsStream("/examples/oasis-smp-1.0/ServiceGroupOK.xml"))
                .when(requestData).getInputStream();

        Mockito.doReturn(testUtilsDao.getResourceDefSmp()).when(resolvedData).getResourceDef();
        Mockito.doReturn(testUtilsDao.getD1()).when(resolvedData).getDomain();
        Mockito.doReturn(testUtilsDao.getResourceD1G1RD1()).when(resolvedData).getResource();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(baos).when(responseData).getOutputStream();

        testInstance.createResource(Collections.singletonList(testUtilsDao.getUser1()), requestData, responseData);
    }

    @Test
    void testCreateSubResource() {
        Mockito.doReturn(resolvedData).when(requestData).getResolvedData();
        Mockito.doReturn(ResourceHandlerService.class.getResourceAsStream("/examples/oasis-smp-1.0/ServiceMetadataOK.xml"))
                .when(requestData).getInputStream();

        Mockito.doReturn(testUtilsDao.getResourceDefSmp()).when(resolvedData).getResourceDef();
        Mockito.doReturn(testUtilsDao.getSubresourceDefSmpMetadata()).when(resolvedData).getSubResourceDef();
        Mockito.doReturn(testUtilsDao.getD1()).when(resolvedData).getDomain();
        Mockito.doReturn(testUtilsDao.getResourceD1G1RD1()).when(resolvedData).getResource();
        Mockito.doReturn(testUtilsDao.getSubresourceD2G1RD1_S1()).when(resolvedData).getSubresource();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(baos).when(responseData).getOutputStream();

        testInstance.createSubresource(requestData, responseData);
    }

}
