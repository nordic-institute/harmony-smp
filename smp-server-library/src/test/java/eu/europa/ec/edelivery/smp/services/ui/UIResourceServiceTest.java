/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.TEST_SG_SCHEMA_1;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {UIResourceService.class, ConversionTestConfig.class})
public class UIResourceServiceTest extends AbstractJunit5BaseDao {
    @Autowired
    protected UIResourceService testInstance;

    @Autowired
    ResourceDao resourceDao;
    @Autowired
    ResourceMemberDao resourceMemberDao;
    @Autowired
    ConversionService conversionService;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourceMemberships();
    }

    @Test
    public void testGetGroupResources() {
        ServiceResult<ResourceRO> result = testInstance.getGroupResources(testUtilsDao.getGroupD1G1().getId(), -1, -1, null);
        // one resource is expected  - see the data in testUtilsDao.createResources()
        assertNotNull(result);
        assertEquals(1, result.getCount().intValue());
    }

    @Test
    public void testGetResourcesForUserAndGroup() {
        ServiceResult<ResourceRO> resultAdmin = testInstance.getResourcesForUserAndGroup(testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN,
                testUtilsDao.getGroupD1G1().getId(), -1, -1, null);

        ServiceResult<ResourceRO> resultViewer = testInstance.getResourcesForUserAndGroup(testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER,
                testUtilsDao.getGroupD1G1().getId(), -1, -1, null);
        // see the data in testUtilsDao.createResourceMemberships()
        assertEquals(1, resultAdmin.getCount().intValue());
        assertEquals(0, resultViewer.getCount().intValue());
    }

    @Test
    public void testCreateResourceForGroup() {
        // given
        ResourceRO testResource = TestROUtils.createResource(UUID.randomUUID().toString(), TEST_SG_SCHEMA_1,
                testUtilsDao.getDomainResourceDefD1R1().getResourceDef().getIdentifier());

        // when
        ResourceRO result = testInstance.createResourceForGroup(testResource, testUtilsDao.getGroupD1G1().getId(),
                testUtilsDao.getD1().getId(), testUtilsDao.getUser1().getId());
        // then
        assertNotNull(result);
        assertEquals(testResource.getIdentifierValue(), result.getIdentifierValue());
        assertEquals(testResource.getIdentifierScheme(), result.getIdentifierScheme());
    }

    @Test
    public void testCreateResourceForGroupFailsGroupNotExists() {
        // given
        ResourceRO testResource = TestROUtils.createResource(UUID.randomUUID().toString(), TEST_SG_SCHEMA_1,
                testUtilsDao.getDomainResourceDefD1R1().getResourceDef().getIdentifier());

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.createResourceForGroup(testResource, -100L,
                        testUtilsDao.getD1().getId(), testUtilsDao.getUser1().getId()));
        // then
        assertNotNull(result);
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Group does not exist"));
    }

    @Test
    public void testCreateResourceForGroupFailsGroupNotForDomain() {
        // given
        ResourceRO testResource = TestROUtils.createResource(UUID.randomUUID().toString(), TEST_SG_SCHEMA_1,
                testUtilsDao.getDomainResourceDefD1R1().getResourceDef().getIdentifier());

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.createResourceForGroup(testResource, testUtilsDao.getGroupD1G1().getId(), -100L,
                        testUtilsDao.getUser1().getId()));
        // then
        assertNotNull(result);
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Group does not belong to the given domain"));
    }

    @Test
    public void testCreateResourceForGroupFailInvalidResourceDef() {
        // given
        ResourceRO testResource = TestROUtils.createResource(UUID.randomUUID().toString(), TEST_SG_SCHEMA_1,
                UUID.randomUUID().toString());

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.createResourceForGroup(testResource, testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId(),
                        testUtilsDao.getUser1().getId()));
        // then
        assertNotNull(result);
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Resource definition [" + testResource.getResourceTypeIdentifier() + "] does not exist!"));
    }

    @Test
    public void testCreateResourceForGroupFailAlreadyExists() {
        // given
        DBResource dbResource = testUtilsDao.getResourceD1G1RD1();
        ResourceRO testResource = TestROUtils.createResource(dbResource.getIdentifierValue(), dbResource.getIdentifierScheme(),
                testUtilsDao.getDomainResourceDefD1R1().getResourceDef().getIdentifier());

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.createResourceForGroup(testResource, testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId(),
                        testUtilsDao.getUser1().getId()));
        // then
        assertNotNull(result);
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Resource [val:" + testResource.getIdentifierValue() + " scheme:" + testResource.getIdentifierScheme() + "] already exists for domain!"));
    }

    @Test
    public void testUpdateResourceForGroup() {
        // given
        DBResource dbResource = testUtilsDao.getResourceD1G1RD1();
        ResourceRO testResource = TestROUtils.createResource(dbResource.getIdentifierValue(), dbResource.getIdentifierScheme(), dbResource.getDomainResourceDef().getResourceDef().getIdentifier());
        assertNotEquals(dbResource.getVisibility(), VisibilityType.PRIVATE);
        testResource.setVisibility(VisibilityType.PRIVATE);

        // when
        ResourceRO result = testInstance.updateResourceForGroup(testResource, dbResource.getId(),
                testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId());
        // then
        assertNotNull(result);
        assertEquals(testResource.getIdentifierValue(), result.getIdentifierValue());
        assertEquals(testResource.getIdentifierScheme(), result.getIdentifierScheme());
        assertEquals(VisibilityType.PRIVATE, result.getVisibility());
    }


    @Test
    public void testUpdateResourceForGroupGroupNotFound() {
        // given
        DBResource dbResource = testUtilsDao.getResourceD1G1RD1();
        ResourceRO testResource = TestROUtils.createResource(dbResource.getIdentifierValue(), dbResource.getIdentifierScheme(), dbResource.getDomainResourceDef().getResourceDef().getIdentifier());
        assertNotEquals(dbResource.getVisibility(), VisibilityType.PRIVATE);
        testResource.setVisibility(VisibilityType.PRIVATE);

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                ()-> testInstance.updateResourceForGroup(testResource, dbResource.getId(),
                        -1000L, testUtilsDao.getD1().getId()));
        // then
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Group does not exist!"));
    }

    @Test
    public void testUpdateResourceForGroupNotBelongToDomain() {
        // given
        DBResource dbResource = testUtilsDao.getResourceD1G1RD1();
        ResourceRO testResource = TestROUtils.createResource(dbResource.getIdentifierValue(), dbResource.getIdentifierScheme(), dbResource.getDomainResourceDef().getResourceDef().getIdentifier());
        assertNotEquals(dbResource.getVisibility(), VisibilityType.PRIVATE);
        testResource.setVisibility(VisibilityType.PRIVATE);

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                ()-> testInstance.updateResourceForGroup(testResource, dbResource.getId(),
                        testUtilsDao.getGroupD2G1().getId(), testUtilsDao.getD1().getId()));
        // then
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Group does not belong to the given domain!"));
    }


    @Test
    public void testUpdateResourceForGroupResourceDefNotFound() {
        // given
        String defIdNotExist = UUID.randomUUID().toString();
        DBResource dbResource = testUtilsDao.getResourceD1G1RD1();
        ResourceRO testResource = TestROUtils.createResource(dbResource.getIdentifierValue(), dbResource.getIdentifierScheme(), defIdNotExist);
        assertNotEquals(dbResource.getVisibility(), VisibilityType.PRIVATE);
        testResource.setVisibility(VisibilityType.PRIVATE);

        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                ()-> testInstance.updateResourceForGroup(testResource, dbResource.getId(),
                testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId()));
        // then
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Resource definition ["+defIdNotExist+"] does not exist!"));
    }

    @Test
    public void testDeleteResourceFromGroupOK() {
        // given
        ResourceRO testResource = TestROUtils.createResource(UUID.randomUUID().toString(), TEST_SG_SCHEMA_1,
                testUtilsDao.getDomainResourceDefD1R1().getResourceDef().getIdentifier());
        ResourceRO result = testInstance.createResourceForGroup(testResource, testUtilsDao.getGroupD1G1().getId(),
                testUtilsDao.getD1().getId(), testUtilsDao.getUser1().getId());
        Long resourceId = Long.valueOf(result.getResourceId());
        assertNotNull(resourceDao.find(resourceId));
        // when
        testInstance.deleteResourceFromGroup(resourceId, testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId());
        // then
        assertNull(resourceDao.find(resourceId));
    }


    @Test
    public void testDeleteResourceFromGroupResourceNotExists() {
        // given
        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                ()-> testInstance.deleteResourceFromGroup(-1000L, testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId()));
        // then
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Resource does not exist!"));
    }


    @Test
    public void testDeleteResourceFromGroupResourceNotBelongToGroup() {
        // given
        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                ()-> testInstance.deleteResourceFromGroup(testUtilsDao.getResourceD2G1RD1().getId(), testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId()));
        // then
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Resource does not belong to the group!"));
    }

    @Test
    public void testDeleteResourceFromGroup() {
        // given
        ResourceRO testResource = TestROUtils.createResource(UUID.randomUUID().toString(), TEST_SG_SCHEMA_1,
                testUtilsDao.getDomainResourceDefD1R1().getResourceDef().getIdentifier());
        ResourceRO resourceRO = testInstance.createResourceForGroup(testResource, testUtilsDao.getGroupD1G1().getId(),
                testUtilsDao.getD1().getId(), testUtilsDao.getUser1().getId());
        Long resourceId = Long.valueOf(resourceRO.getResourceId());
        assertNotNull(resourceDao.find(resourceId));
        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                ()-> testInstance.deleteResourceFromGroup(resourceId, testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD2().getId()));
        // then
        assertEquals(ErrorCode.INVALID_REQUEST, result.getErrorCode());
        assertThat(result.getMessage(), containsString("Group does not belong to the given domain!"));
    }

    @Test
    public void testGetResourceMembers() {
        // given
        // see the data in testUtilsDao.createResourceMemberships()
        // when
        ServiceResult<MemberRO> resourceMembers = testInstance.getResourceMembers(testUtilsDao.getResourceD1G1RD1().getId(), testUtilsDao.getGroupD1G1().getId(), -1, -1, null);
        // then
        assertNotNull(resourceMembers);
        assertEquals(1, resourceMembers.getCount().intValue());
        assertEquals(testUtilsDao.getUser1().getUsername(), resourceMembers.getServiceEntities().get(0).getUsername());
    }

    @Test
    public void testAddUpdateMemberToResourceUpdate() {
        // given
        // see the data in testUtilsDao.createResourceMemberships()
        DBResourceMember resourceMember = testUtilsDao.getResourceMemberU1R1_D2G1RD1_Admin();
        MemberRO member = conversionService.convert(resourceMember, MemberRO.class);
        member.setRoleType(MembershipRoleType.VIEWER);
        // when
        testInstance.addUpdateMemberToResource(testUtilsDao.getResourceD2G1RD1().getId(), testUtilsDao.getGroupD2G1().getId(), member, resourceMember.getId());
        // then
        DBResourceMember result = resourceMemberDao.find(resourceMember.getId());
        assertNotNull(result);
        assertEquals(MembershipRoleType.VIEWER, result.getRole());
    }

    @Test
    public void testAddUpdateMemberToResourceUAdd() {
        // given
        int memberCount = testInstance.getResourceMembers(testUtilsDao.getResourceD1G1RD1().getId(), testUtilsDao.getGroupD1G1().getId(), -1, -1, null).getCount().intValue();
        DBResourceMember dbMember = new DBResourceMember();
        dbMember.setRole(MembershipRoleType.VIEWER);
        dbMember.setUser(testUtilsDao.getUser2());
        dbMember.setResource(testUtilsDao.getResourceD1G1RD1());
        MemberRO member = conversionService.convert(dbMember, MemberRO.class);

        // when
        testInstance.addUpdateMemberToResource(testUtilsDao.getResourceD1G1RD1().getId(), testUtilsDao.getGroupD1G1().getId(), member, null);
        // then
        ServiceResult<MemberRO> resourceMembers = testInstance.getResourceMembers(testUtilsDao.getResourceD1G1RD1().getId(), testUtilsDao.getGroupD1G1().getId(), -1, -1, null);
        assertEquals(memberCount + 1, resourceMembers.getCount().intValue());
    }
}
