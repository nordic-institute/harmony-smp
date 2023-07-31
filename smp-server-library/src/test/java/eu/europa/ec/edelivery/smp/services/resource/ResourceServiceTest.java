package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.MalformedIdentifierException;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.servlet.ResourceResponse;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ResourceServiceTest extends AbstractJunit5BaseDao {
    @Autowired
    ResourceService testInstance;

    ResourceRequest resourceRequest = Mockito.mock(ResourceRequest.class);
    ResolvedData resolvedData = Mockito.mock(ResolvedData.class);
    ResourceResponse resourceResponse = Mockito.mock(ResourceResponse.class);
    SMPUserDetails user = Mockito.mock(SMPUserDetails.class);


    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();
    }

    @Test
    void handleRequestFail() {
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.handleRequest(user, resourceRequest, resourceResponse));

        assertThat(result.getMessage(), containsString("Invalid request"));
    }

    @ParameterizedTest
    @CsvSource({
            ", 'Location vector coordinates must not be null!'",
            "1/2/3/4/5/6/7, 'More than max. count (5) of Resource Location vector coordinates!'",
            TestConstants.TEST_DOMAIN_CODE_1 + ", 'Not enough path parameters to locate resource'",
            TestConstants.TEST_DOMAIN_CODE_1 + "/" + TestConstants.TEST_RESOURCE_DEF_CPP + ", 'Not enough path parameters to locate resource'",
            "badIdentifier, 'Malformed identifier, scheme and id should be delimited by double colon: badidentifier'",
            "doc-type/badIdentifier, 'Malformed identifier, scheme and id should be delimited by double colon: doc-type'",
            "domain/doc-type/badIdentifier, 'Malformed identifier, scheme and id should be delimited by double colon: domain'",
    })
    void handleRequestFailBadPath(String path, String errorMessage) {
        when(resourceRequest.getUrlPathParameters()).thenReturn(path == null ? null : Arrays.asList(path.split("/")));
        when(resourceRequest.getAuthorizedDomain()).thenReturn(testUtilsDao.getD1());

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.handleRequest(user, resourceRequest, resourceResponse));

        assertThat(result.getMessage(), containsString(errorMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "'', 'Can not parse empty identifier value!'"
    }
    )
    void handleRequestFailBadIdentifier(String path, String errorMessage) {
        when(resourceRequest.getUrlPathParameters()).thenReturn(path == null ? null : Arrays.asList(path.split("/")));
        when(resourceRequest.getAuthorizedDomain()).thenReturn(testUtilsDao.getD1());

        MalformedIdentifierException result = assertThrows(MalformedIdentifierException.class,
                () -> testInstance.handleRequest(user, resourceRequest, resourceResponse));

        assertThat(result.getMessage(), containsString(errorMessage));
    }

    @Test
    void handleRequestReadOK() {
        when(resourceRequest.getUrlPathParameters()).thenReturn(Arrays.asList(TestConstants.TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_SCHEMA_1 + "::" + TestConstants.TEST_SG_ID_1));
        when(resourceRequest.getAuthorizedDomain()).thenReturn(testUtilsDao.getD1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.READ);
        when(resourceRequest.getResolvedData()).thenReturn(resolvedData);
        when(resolvedData.getResourceDef()).thenReturn(testUtilsDao.getResourceDefSmp());
        when(resolvedData.getResource()).thenReturn(testUtilsDao.getResourceD1G1RD1());
        when(resolvedData.getDomain()).thenReturn(testUtilsDao.getD1());
        testInstance.handleRequest(user, resourceRequest, resourceResponse);
    }

    @Test
    void handleRequestCreateOK() {
        when(user.getUser()).thenReturn(testUtilsDao.getUser1());
        when(resourceRequest.getUrlPathParameters()).thenReturn(Arrays.asList(TestConstants.TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_SCHEMA_1 + "::0007:001:utest"));
        when(resourceRequest.getAuthorizedDomain()).thenReturn(testUtilsDao.getD1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        when(resourceRequest.getResolvedData()).thenReturn(resolvedData);
        when(resolvedData.getResourceDef()).thenReturn(testUtilsDao.getResourceDefSmp());
        when(resolvedData.getResource()).thenReturn(testUtilsDao.getResourceD1G1RD1());
        when(resolvedData.getDomain()).thenReturn(testUtilsDao.getD1());
        when(resourceRequest.getInputStream()).thenReturn(ResourceResolverServiceTest.class.getResourceAsStream("/examples/oasis-smp-1.0/ServiceGroupOK.xml"));
        testInstance.handleRequest(user, resourceRequest, resourceResponse);
    }

    @Test
    void handleRequestDeleteOK() {
        when(user.getUser()).thenReturn(testUtilsDao.getUser1());
        when(resourceRequest.getUrlPathParameters()).thenReturn(Arrays.asList(TestConstants.TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_SCHEMA_1 + "::" + TestConstants.TEST_SG_ID_1));
        when(resourceRequest.getAuthorizedDomain()).thenReturn(testUtilsDao.getD1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.DELETE);
        when(resourceRequest.getResolvedData()).thenReturn(resolvedData);
        when(resolvedData.getResourceDef()).thenReturn(testUtilsDao.getResourceDefSmp());
        when(resolvedData.getResource()).thenReturn(testUtilsDao.getResourceD1G1RD1());
        when(resolvedData.getDomain()).thenReturn(testUtilsDao.getD1());

        testInstance.handleRequest(user, resourceRequest, resourceResponse);
    }

    @Test
    void handleRequestCreateNotAuthorized() {

        when(resourceRequest.getUrlPathParameters()).thenReturn(Arrays.asList(TestConstants.TEST_DOMAIN_CODE_1, TestConstants.TEST_SG_SCHEMA_1 + "::0007:001:utest"));
        when(resourceRequest.getAuthorizedDomain()).thenReturn(testUtilsDao.getD1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        when(resourceRequest.getResolvedData()).thenReturn(resolvedData);
        when(resolvedData.getResourceDef()).thenReturn(testUtilsDao.getResourceDefSmp());
        when(resolvedData.getResource()).thenReturn(testUtilsDao.getResourceD1G1RD1());
        when(resolvedData.getDomain()).thenReturn(testUtilsDao.getD1());
        when(resourceRequest.getInputStream()).thenReturn(ResourceResolverServiceTest.class.getResourceAsStream("/examples/oasis-smp-1.0/ServiceGroupOK.xml"));
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.handleRequest(user, resourceRequest, resourceResponse));

        assertThat(result.getMessage(), containsString("User not authorized"));
    }


    @Test
    void testFindOwnerOK() {
        DBUser user = testInstance.findOwner(testUtilsDao.getUser1().getUsername());
        assertNotNull(user);
        assertEquals(testUtilsDao.getUser1().getUsername(), user.getUsername());
    }

    @Test
    void testFindOwnerNotExists() {
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.findOwner("CN=User not exists,OU=Test Users,O=Test Domain,C=BE:1234567890"));
        assertThat(result.getMessage(), containsString("Invalid owner id"));
    }


    @Test
    void testSplitSerialFromSubject() {
        String[] values =
                ResourceService.splitSerialFromSubject("CN=Test User 1,OU=Test Users,O=Test Domain,C=BE:1234567890");
        assertEquals(2, values.length);
        assertEquals("CN=Test User 1,OU=Test Users,O=Test Domain,C=BE", values[0]);
        assertEquals("1234567890", values[1]);
    }

}
