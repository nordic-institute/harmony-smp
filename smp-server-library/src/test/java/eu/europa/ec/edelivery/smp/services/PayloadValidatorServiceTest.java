package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.smp.spi.PayloadValidatorSpi;
import eu.europa.ec.smp.spi.exceptions.PayloadValidatorSpiException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.util.MimeTypeUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

public class PayloadValidatorServiceTest {

    @Test
    public void validateUploadedContentNoValidatorsMostNotFail() {
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.empty());
        InputStream inputStream = Mockito.mock(InputStream.class);

        testInstance.validateUploadedContent(inputStream, MimeTypeUtils.APPLICATION_JSON.getType());
        // no error should accrue
    }

    @Test
    public void validateUploadedContentNoValidatorsMostNotFailEmpty() {
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.of(Collections.emptyList()));
        InputStream inputStream = Mockito.mock(InputStream.class);

        testInstance.validateUploadedContent(inputStream, MimeTypeUtils.APPLICATION_JSON.getType());
        // no error should accrue
    }

    @Test
    public void validateUploadedContent() throws PayloadValidatorSpiException {
        PayloadValidatorSpi validatorSpi1  = Mockito.mock(PayloadValidatorSpi.class);
        PayloadValidatorSpi validatorSpi2  = Mockito.mock(PayloadValidatorSpi.class);
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.of(Arrays.asList(validatorSpi1,validatorSpi2)));
        InputStream inputStream = Mockito.mock(InputStream.class);
        String mimeType = MimeTypeUtils.APPLICATION_JSON.getType();


        testInstance.validateUploadedContent(inputStream, mimeType);
        // no error should accrue
        ArgumentCaptor<InputStream> streamCapture1 = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<String> mimeTypeCapture1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> streamCapture2 = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<String> mimeTypeCapture2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(validatorSpi1, Mockito.times(1)).validatePayload(streamCapture1.capture(), mimeTypeCapture1.capture());
        Mockito.verify(validatorSpi2,Mockito.times(1)).validatePayload(streamCapture2.capture(), mimeTypeCapture2.capture());

        assertEquals(inputStream, streamCapture1.getValue());
        assertEquals(inputStream, streamCapture2.getValue());
        assertEquals(mimeType, mimeTypeCapture1.getValue());
        assertEquals(mimeType, mimeTypeCapture2.getValue());
    }

    @Test
    public void validateUploadedContentThrowException() throws PayloadValidatorSpiException {
        PayloadValidatorSpi validatorSpi1  = Mockito.mock(PayloadValidatorSpi.class);
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.of(Arrays.asList(validatorSpi1)));
        InputStream inputStream = Mockito.mock(InputStream.class);
        String mimeType = MimeTypeUtils.APPLICATION_JSON.getType();
        PayloadValidatorSpiException spiException = new PayloadValidatorSpiException("TestError");
        Mockito.doThrow(spiException).when(validatorSpi1).validatePayload(Mockito.any(),Mockito.any());


        SMPRuntimeException smpRuntimeException =
                assertThrows(SMPRuntimeException.class, () -> testInstance.validateUploadedContent(inputStream, mimeType));

        assertEquals(ErrorCode.INVALID_REQUEST, smpRuntimeException.getErrorCode());
        // generic error
        assertEquals("Invalid request [Upload payload]. Error: Content validation failed!", smpRuntimeException.getMessage());

    }
}