package eu.europa.ec.edelivery.smp.security;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DomainGuardTest extends AbstractJunit5BaseDao {

    @Autowired
    DomainGuard testInstance;

    ResourceRequest resourceRequest = Mockito.mock(ResourceRequest.class);
    SMPUserDetails user = Mockito.mock(SMPUserDetails.class);

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.creatDomainMemberships();
        testUtilsDao.createGroupMemberships();
        testUtilsDao.createResourceMemberships();
    }

    @Test
    void testResolveAndAuthorizeForDomainInvalidRequestMissingAction() {

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, user));

        assertThat(result.getMessage(), containsString("Invalid request"));
    }

    @Test
    void testResolveAndAuthorizeForDomainDeleteInvalidRequestNotUser() {
        when(resourceRequest.getAction()).thenReturn(ResourceAction.DELETE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, user));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    void testResolveAndAuthorizeForDomainDeleteInvalidRequestUserNotAuthorized() {
        when(user.getUser()).thenReturn(testUtilsDao.getUser3());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.DELETE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, user));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    void testResolveAndAuthorizeForDomainCreateInvalidRequestUserNotAuthorized() {
        when(user.getUser()).thenReturn(testUtilsDao.getUser3());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, user));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    void testResolveAndAuthorizeForDomainDeleteUserAuthorized() {
        when(user.getUser()).thenReturn(testUtilsDao.getUser1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.DELETE);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, user);
        assertNotNull(domain);
    }

    @Test
    void testResolveAndAuthorizeForDomainDeleteCreateAuthorized() {
        when(user.getUser()).thenReturn(testUtilsDao.getUser1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, user);
        assertNotNull(domain);
    }

    @Test
    void testResolveAndAuthorizeForDomainCreateInvalidRequestNotUser() {
        when(resourceRequest.getAction()).thenReturn(ResourceAction.CREATE_UPDATE);
        AuthenticationServiceException result = assertThrows(AuthenticationServiceException.class,
                () -> testInstance.resolveAndAuthorizeForDomain(resourceRequest, user));

        assertThat(result.getMessage(), containsString("User is not authorized for the domain!"));
    }

    @Test
    void testResolveAndAuthorizeForDomainNoUserOK() {
        when(resourceRequest.getAction()).thenReturn(ResourceAction.READ);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, user);
        assertNotNull(domain);
    }

    @Test
    void testResolveAndAuthorizeForDomain() {
        when(user.getUser()).thenReturn(testUtilsDao.getUser1());
        when(resourceRequest.getAction()).thenReturn(ResourceAction.READ);
        DBDomain domain = testInstance.resolveAndAuthorizeForDomain(resourceRequest, user);
        assertNotNull(domain);
    }

}
