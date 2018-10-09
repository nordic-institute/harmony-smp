/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.error;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode.*;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Created by migueti on 06/01/2017.
 */
public class ErrorResponseBuilderTest {

    private final Pattern PATTERN = Pattern.compile(".*?(:).*?(:).*?(:)([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


    private final ErrorBusinessCode DEFAULT_BUSINESS_CODE = TECHNICAL;
    private final String DEFAULT_ERROR_DESCRIPTION = "Unexpected technical error occurred.";

    @Test
    public void testDifferentErrorIds() throws ParserConfigurationException, IOException, SAXException {
        ResponseEntity result1 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).build();
        ResponseEntity result2 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).build();

        ErrorResponse entity1 = (ErrorResponse) result1.getBody();
        ErrorResponse entity2 = (ErrorResponse) result2.getBody();

        // test result 1
        String errorUniqueId1 = checkXmlError(entity1, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        // test result 2
        String errorUniqueId2 = checkXmlError(entity2, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId1);
        assertNotNull(errorUniqueId2);
        assertNotEquals(errorUniqueId1, errorUniqueId2);

    }

    @Test
    public void testDifferentErrorIdsWithBusinessCodeAndErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        ResponseEntity result1 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).businessCode(OTHER_ERROR).errorDescription("Business Error Description").build();
        ResponseEntity result2 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).businessCode(OTHER_ERROR).errorDescription("Business Error Description").build();

        ErrorResponse entity1 = (ErrorResponse) result1.getBody();
        ErrorResponse entity2 = (ErrorResponse) result2.getBody();

        final ErrorBusinessCode BC_BUSINESS_CODE = OTHER_ERROR;
        final String STR_ERROR_DESCRIPTION = "Business Error Description";

        // test result 1
        String errorUniqueId1 = checkXmlError(entity1, BC_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        // test result 2
        String errorUniqueId2 = checkXmlError(entity2, BC_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId1);
        assertNotNull(errorUniqueId2);
        assertNotEquals(errorUniqueId1, errorUniqueId2);
    }

    @Test
    public void testBusinessCode() throws ParserConfigurationException, IOException, SAXException {
        final ErrorBusinessCode BC_BUSINESS_CODE = MISSING_FIELD;
        ResponseEntity result = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).businessCode(BC_BUSINESS_CODE).build();

        ErrorResponse entity = (ErrorResponse) result.getBody();

        String errorUniqueId = checkXmlError(entity, BC_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testErrorDescription() throws ParserConfigurationException, IOException, SAXException {
        final String STR_ERROR_DESCRIPTION = "Business Error Description";
        ResponseEntity result = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).errorDescription(STR_ERROR_DESCRIPTION).build();

        ErrorResponse entity = (ErrorResponse) result.getBody();

        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, STR_ERROR_DESCRIPTION);

        assertNotNull(errorUniqueId);
    }

    @Test
    public void testBuildWithStatus() throws IOException, SAXException, ParserConfigurationException {
        ResponseEntity result = ErrorResponseBuilder.status(BAD_REQUEST).build();

        assertNotNull(result);
        ErrorResponse entity = (ErrorResponse) result.getBody();
        String errorUniqueId = checkXmlError(entity, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId);
        assertEquals(BAD_REQUEST.value(), result.getStatusCodeValue());
    }

    @Test
    public void testBuildTwoInstancesWithStatus() throws IOException, SAXException, ParserConfigurationException {
        ResponseEntity result1 = ErrorResponseBuilder.status(BAD_REQUEST).build();
        ResponseEntity result2 = ErrorResponseBuilder.status(INTERNAL_SERVER_ERROR).build();

        assertNotNull(result1);
        ErrorResponse entity1 = (ErrorResponse) result1.getBody();
        String errorUniqueId1 = checkXmlError(entity1, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId1);
        assertEquals(BAD_REQUEST.value(), result1.getStatusCodeValue());

        assertNotNull(result2);
        ErrorResponse entity2 = (ErrorResponse) result2.getBody();
        String errorUniqueId2 = checkXmlError(entity2, DEFAULT_BUSINESS_CODE, DEFAULT_ERROR_DESCRIPTION);
        assertNotNull(errorUniqueId2);
        assertEquals(INTERNAL_SERVER_ERROR.value(), result2.getStatusCodeValue());
    }

    private String checkXmlError(ErrorResponse errorResponse, ErrorBusinessCode errorBusinessCode, String errorDescription) throws ParserConfigurationException, IOException, SAXException {
        assertNotNull(errorResponse);

        assertEquals(errorBusinessCode.toString(), errorResponse.getBusinessCode());
        assertEquals(errorDescription, errorResponse.getErrorDescription());

        String errorUniqueId = null;
        Matcher matcher = PATTERN.matcher(errorResponse.getErrorUniqueId());
        if (matcher.find()) {
            errorUniqueId = matcher.group(4);
        }
        return errorUniqueId;
    }

}
