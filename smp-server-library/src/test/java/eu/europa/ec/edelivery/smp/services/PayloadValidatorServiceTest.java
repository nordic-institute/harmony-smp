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
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.smp.spi.PayloadValidatorSpi;
import eu.europa.ec.smp.spi.exceptions.PayloadValidatorSpiException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.util.MimeTypeUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PayloadValidatorServiceTest {

    @Test
    void validateUploadedContentNoValidatorsMostNotFail() {
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.empty());
        InputStream inputStream = Mockito.mock(InputStream.class);

        testInstance.validateUploadedContent(inputStream, MimeTypeUtils.APPLICATION_JSON.getType());
        // no error should accrue
    }

    @Test
    void validateUploadedContentNoValidatorsMostNotFailEmpty() {
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.of(Collections.emptyList()));
        InputStream inputStream = Mockito.mock(InputStream.class);

        testInstance.validateUploadedContent(inputStream, MimeTypeUtils.APPLICATION_JSON.getType());
        // no error should accrue
    }

    @Test
    void validateUploadedContent() throws PayloadValidatorSpiException {
        PayloadValidatorSpi validatorSpi1 = Mockito.mock(PayloadValidatorSpi.class);
        PayloadValidatorSpi validatorSpi2 = Mockito.mock(PayloadValidatorSpi.class);
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.of(Arrays.asList(validatorSpi1, validatorSpi2)));
        InputStream inputStream = Mockito.mock(InputStream.class);
        String mimeType = MimeTypeUtils.APPLICATION_JSON.getType();


        testInstance.validateUploadedContent(inputStream, mimeType);
        // no error should accrue
        ArgumentCaptor<InputStream> streamCapture1 = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<String> mimeTypeCapture1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> streamCapture2 = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<String> mimeTypeCapture2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(validatorSpi1, Mockito.times(1)).validatePayload(streamCapture1.capture(), mimeTypeCapture1.capture());
        Mockito.verify(validatorSpi2, Mockito.times(1)).validatePayload(streamCapture2.capture(), mimeTypeCapture2.capture());

        assertEquals(inputStream, streamCapture1.getValue());
        assertEquals(inputStream, streamCapture2.getValue());
        assertEquals(mimeType, mimeTypeCapture1.getValue());
        assertEquals(mimeType, mimeTypeCapture2.getValue());
    }

    @Test
    void validateUploadedContentThrowException() throws PayloadValidatorSpiException {
        PayloadValidatorSpi validatorSpi1 = Mockito.mock(PayloadValidatorSpi.class);
        PayloadValidatorService testInstance = new PayloadValidatorService(Optional.of(Collections.singletonList(validatorSpi1)));
        InputStream inputStream = Mockito.mock(InputStream.class);
        String mimeType = MimeTypeUtils.APPLICATION_JSON.getType();
        PayloadValidatorSpiException spiException = new PayloadValidatorSpiException("TestError");
        Mockito.doThrow(spiException).when(validatorSpi1).validatePayload(Mockito.any(), Mockito.any());


        SMPRuntimeException smpRuntimeException =
                assertThrows(SMPRuntimeException.class, () -> testInstance.validateUploadedContent(inputStream, mimeType));

        assertEquals(ErrorCode.INVALID_REQUEST, smpRuntimeException.getErrorCode());
        // generic error
        assertEquals("Invalid request [Upload payload]. Error: Content validation failed!", smpRuntimeException.getMessage());

    }
}
