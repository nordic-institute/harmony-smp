/*-
 * #%L
 * smp-webapp
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
