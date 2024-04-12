/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.ui.SubresourceRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = UIDomainPublicService.class)
class UISubresourceServiceTest extends AbstractJunit5BaseDao {

    @Autowired
    UISubresourceService testInstance;

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();
    }

    @Test
    void testGetSubResourcesForResourceOK() {
        List<SubresourceRO> result = testInstance.getSubResourcesForResource(testUtilsDao.getResourceD1G1RD1().getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getSubresourceD1G1RD1_S1().getIdentifierValue(), result.get(0).getIdentifierValue());
        assertEquals(testUtilsDao.getSubresourceD1G1RD1_S1().getIdentifierScheme(), result.get(0).getIdentifierScheme());
    }

    @Test
    void testGetSubResourcesForResourceEmpty() {
        List<SubresourceRO> result = testInstance.getSubResourcesForResource(-1000L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
/*
    @ParameterizedTest
    @CsvSource({
            "1, -1, 'Resource does not exist'",
            "-1, 1, 'Subresource does not exist!'",
            "-1, 1, 'Subresource does not belong to the resource!",
            })*/
    @Test
    void testDeleteSubresourceFromResourceFailedResourceNotExists() {
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.deleteSubresourceFromResource(testUtilsDao.getSubresourceD1G1RD1_S1().getId(), -1L));

        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Resource does not exist"));
    }

    @Test
    void testDeleteSubresourceFromResourceFailedSubResourceNotExists() {
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.deleteSubresourceFromResource(-1L, testUtilsDao.getResourceD1G1RD1().getId()));

        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Subresource does not exist!"));
    }

    @Test
    void testDeleteSubresourceFromResourceFailedSubResourceNotBelong() {
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.deleteSubresourceFromResource(testUtilsDao.getSubresourceD2G1RD1_S1().getId(),
                        testUtilsDao.getResourceD1G1RD1().getId()));

        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Subresource does not belong to the resource!"));
    }

    @Test
    void testDeleteSubresourceFromResourceFailedSubResourceOK() {

        SubresourceRO result = testInstance.deleteSubresourceFromResource(
                    testUtilsDao.getSubresourceD1G1RD1_S1().getId(),
                      testUtilsDao.getResourceD1G1RD1().getId());

        assertNotNull(result);
        assertEquals(testUtilsDao.getSubresourceD1G1RD1_S1().getIdentifierValue(), result.getIdentifierValue());
        assertEquals(testUtilsDao.getSubresourceD1G1RD1_S1().getIdentifierScheme(), result.getIdentifierScheme());
    }

    @Test
    void createSubresourceForResource() {
        SubresourceRO subresourceRO = new SubresourceRO();
        subresourceRO.setIdentifierScheme("scheme");
        subresourceRO.setIdentifierValue(UUID.randomUUID().toString());
        subresourceRO.setSubresourceTypeIdentifier(testUtilsDao.getSubresourceDefSmpMetadata().getIdentifier());
        int count = testInstance.getSubResourcesForResource(testUtilsDao.getResourceD1G1RD1().getId()).size();
        SubresourceRO result = testInstance.createSubresourceForResource(subresourceRO,
                testUtilsDao.getResourceD1G1RD1().getId());

        assertNotNull(result);
        assertEquals(subresourceRO.getIdentifierValue(), result.getIdentifierValue());
        assertEquals(subresourceRO.getIdentifierScheme(), result.getIdentifierScheme());
        assertEquals(count + 1, testInstance.getSubResourcesForResource(testUtilsDao.getResourceD1G1RD1().getId()).size());
    }

    @Test
    void createSubresourceForResourceFailResourceNotExists() {
        SubresourceRO subresourceRO = Mockito.mock(SubresourceRO.class);

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.createSubresourceForResource(subresourceRO, -1L));

        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Resource does not exist"));
    }

    @Test
    void createSubresourceForResourceFailDefinitionNotExists() {
        String def = UUID.randomUUID().toString();
        SubresourceRO subresourceRO = Mockito.mock(SubresourceRO.class);
        Mockito.when(subresourceRO.getSubresourceTypeIdentifier()).thenReturn(def);

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.createSubresourceForResource(subresourceRO, testUtilsDao.getResourceD1G1RD1().getId()));

        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Subresource definition ["+def+"] does not exist"));
    }
}
