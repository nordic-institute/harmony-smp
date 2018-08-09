package eu.europa.ec.edelivery.smp.error;

import ec.services.smp._1.ErrorResponse;
import eu.europa.ec.edelivery.smp.error.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.NotFoundException;
import eu.europa.ec.edelivery.smp.exceptions.UnknownUserException;
import eu.europa.ec.edelivery.smp.exceptions.WrongInputFieldException;
import eu.europa.ec.edelivery.smp.exceptions.XmlParsingException;
import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.*;

public class ErrorMappingControllerAdviceTest {

    ErrorMappingControllerAdvice testIntance = new ErrorMappingControllerAdvice();

    @Test
    public void handleRuntimeException() {
        ResponseEntity re = testIntance.handleRuntimeException(new RuntimeException("RuntimeExceptionMessage"));

        assertEquals(INTERNAL_SERVER_ERROR, re.getStatusCode());
        assertEquals(ErrorBusinessCode.TECHNICAL, ((ErrorResponse)re.getBody()).getBusinessCode());


    }

    @Test
    public void handleBadRequestException() {

        ResponseEntity re = testIntance.handleBadRequestException(new BadRequestException(ErrorBusinessCode.WRONG_FIELD, "BadRequestExceptionMessage"));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.WRONG_FIELD, ((ErrorResponse)re.getBody()).getBusinessCode());
    }


    @Test
    public void handleMalformedIdentifierException() {
        ResponseEntity re = testIntance.handleMalformedIdentifierException(new MalformedIdentifierException("MalformedIdentifierExceptionMessage", null));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.FORMAT_ERROR, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleWrongInputFieldException() {

        ResponseEntity re = testIntance.handleWrongInputFieldException(new WrongInputFieldException("WrongInputFieldExceptionMessage"));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.WRONG_FIELD, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleNotFoundException() {
        ResponseEntity re = testIntance.handleNotFoundException(new NotFoundException("NotFoundExceptionMessage"));

        assertEquals(NOT_FOUND, re.getStatusCode());
        assertEquals(ErrorBusinessCode.NOT_FOUND, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleAuthenticationException() {

        ResponseEntity re = testIntance.handleAuthenticationException(new AuthenticationException("AuthenticationException") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        });

        assertEquals(UNAUTHORIZED, re.getStatusCode());
        assertEquals(ErrorBusinessCode.UNAUTHORIZED, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleAccessDeniedException() {
        ResponseEntity re = testIntance.handleAccessDeniedException(new AccessDeniedException("AccessDeniedExceptionMessage"));

        assertEquals(UNAUTHORIZED, re.getStatusCode());
        assertEquals(ErrorBusinessCode.UNAUTHORIZED, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleUnknownUserException() {
        ResponseEntity re = testIntance.handleUnknownUserException(new UnknownUserException("UnknownUserExceptionMessage"));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.USER_NOT_FOUND, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleInvalidOwnerException() {
        ResponseEntity re = testIntance.handleUnknownUserException(new UnknownUserException("UnknownUserExceptionMessage"));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.UNAUTHORIZED, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleXmlParsingException() {

        ResponseEntity re = testIntance.handleXmlParsingException(new XmlParsingException(null));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.XSD_INVALID, ((ErrorResponse)re.getBody()).getBusinessCode());
    }

    @Test
    public void handleXmlInvalidAgainstSchemaException() {
        ResponseEntity re = testIntance.handleXmlInvalidAgainstSchemaException(
                new XmlInvalidAgainstSchemaException("XmlInvalidAgainstSchemaExceptionMessage", null));

        assertEquals(BAD_REQUEST, re.getStatusCode());
        assertEquals(ErrorBusinessCode.XSD_INVALID, ((ErrorResponse)re.getBody()).getBusinessCode());
    }
}