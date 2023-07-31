package eu.europa.ec.edelivery.smp.monitor;

import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MonitorControllerTest unit tests
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
class MonitorControllerTest {

    DomainDao mockDomainDao = Mockito.mock(DomainDao.class);
    MonitorController testInstance = new MonitorController(mockDomainDao);

    // mock security context and authentication
    @BeforeAll
    public static void before() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(Mockito.mock(Authentication.class));
    }

    @AfterAll
    public static void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isAliveOK() {
        // given
        Mockito.when(mockDomainDao.getAllDomains()).thenReturn(Collections.singletonList(new DBDomain()));
        // when
        ResponseEntity result = testInstance.isAlive();
        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void isAliveNotConfigured() {
        // given
        Mockito.when(mockDomainDao.getAllDomains()).thenReturn(Collections.emptyList());
        // when
        ResponseEntity result = testInstance.isAlive();
        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void isAliveDatabaseRuntimeError() {
        // given
        Mockito.when(mockDomainDao.getAllDomains()).thenThrow(Mockito.mock(RuntimeException.class));
        // when
        ResponseEntity result = testInstance.isAlive();
        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void testDatabaseEmpty() {
        // when
        Mockito.when(mockDomainDao.getAllDomains()).thenReturn(Collections.emptyList());
        boolean result = testInstance.testDatabase();

        assertFalse(result);
    }

    @Test
    void testDatabaseNotEmpty() {
        // when
        Mockito.when(mockDomainDao.getAllDomains()).thenReturn(Collections.singletonList(new DBDomain()));
        boolean result = testInstance.testDatabase();

        assertTrue(result);
    }
}
