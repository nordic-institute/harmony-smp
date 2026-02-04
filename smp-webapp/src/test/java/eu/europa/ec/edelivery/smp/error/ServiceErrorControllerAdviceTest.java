/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.error;

import eu.europa.ec.dynamicdiscovery.exception.MalformedIdentifierException;
import eu.europa.ec.edelivery.smp.error.xml.ErrorResponse;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import eu.europa.ec.edelivery.smp.services.SMPLanguageResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

class ServiceErrorControllerAdviceTest {

    File localeFolder = new File("target/locales");
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    SMPLanguageResourceService smpLanguageResourceService = new SMPLanguageResourceService(configurationService, resourcePatternResolver);
    SMPExceptionLanguageService smpExceptionLanguageService = new SMPExceptionLanguageService(smpLanguageResourceService);

    ServiceErrorControllerAdvice testIntance = new ServiceErrorControllerAdvice(smpExceptionLanguageService);

    @BeforeEach
    public void setup() {
        Mockito.when(configurationService.getLocaleFolder()).thenReturn(localeFolder);
    }

    @Test
    void handleRuntimeException() {
        ResponseEntity re = testIntance.handleRuntimeException(new RuntimeException("RuntimeExceptionMessage"));

        assertEquals(INTERNAL_SERVER_ERROR, re.getStatusCode());
        assertEquals(ErrorBusinessCode.TECHNICAL.toString(), ((ErrorResponse) re.getBody()).getBusinessCode());
    }

    @Test
    void handleBadRequestException() {

        ResponseEntity re = testIntance.handleBadRequestException(new BadRequestException(ErrorMessageType.INVALID_REQUEST_PARAMETER).
                addParam(ErrorMessageArgument.ERROR, "BadRequestExceptionMessage"));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.WRONG_FIELD.toString(), ((ErrorResponse) re.getBody()).getBusinessCode());
    }


    @Test
    void handleMalformedIdentifierException() {
        ResponseEntity re = testIntance.handleMalformedIdentifierException(new MalformedIdentifierException("MalformedIdentifierExceptionMessage", null));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.FORMAT_ERROR.toString(), ((ErrorResponse) re.getBody()).getBusinessCode());
    }

    @Test
    void handleAuthenticationException() {

        ResponseEntity re = testIntance.handleRuntimeException(new AuthenticationException("AuthenticationException") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        });

        assertEquals(UNAUTHORIZED, re.getStatusCode());
        assertEquals(ErrorBusinessCode.UNAUTHORIZED.toString(), ((ErrorResponse) re.getBody()).getBusinessCode());
    }

    @Test
    void handleAccessDeniedException() {
        ResponseEntity re = testIntance.handleAccessDeniedException(new AccessDeniedException("AccessDeniedExceptionMessage"));

        assertEquals(UNAUTHORIZED, re.getStatusCode());
        assertEquals(ErrorBusinessCode.UNAUTHORIZED.toString(), ((ErrorResponse) re.getBody()).getBusinessCode());
    }

/*
    @Test
    void handleXmlInvalidAgainstSchemaException() {
        ResponseEntity re = testIntance.handleXmlInvalidAgainstSchemaException(
                new XmlInvalidAgainstSchemaException("XmlInvalidAgainstSchemaExceptionMessage", null));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.XSD_INVALID.toString(), ((ErrorResponse)re.getBody()).getBusinessCode());
    }

 */
}
