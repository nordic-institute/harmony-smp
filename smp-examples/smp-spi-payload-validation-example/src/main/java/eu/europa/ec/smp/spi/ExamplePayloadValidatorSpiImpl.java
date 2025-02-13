/*-
 * #START_LICENSE#
 * smp-spi-payload-validation-example
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
package eu.europa.ec.smp.spi;

import eu.europa.ec.smp.spi.exceptions.PayloadValidatorSpiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 * <p>
 * Example of the SMP Service provider interface (SPI) for uploaded payload validation.
 */
@Service
public class ExamplePayloadValidatorSpiImpl implements PayloadValidatorSpi {
    private static final Logger LOG = LoggerFactory.getLogger(ExamplePayloadValidatorSpiImpl.class);

    /**
     * Example methods logs the byte size and the mime type
     *
     * @param payload  The payload data to be validated
     * @param mimeType The payload mime type
     * @throws PayloadValidatorSpiException in case the validation does not pass
     */
    public void validatePayload(InputStream payload, String mimeType) throws PayloadValidatorSpiException {
        try {
            LOG.info("*********************************************************************");
            LOG.info("* Validate payload with size [{}] and mime type [{}]!", payload.available(), mimeType);
            LOG.info("**********************************************************************");
            if (payload.available() > 0 ) {
                int firstChar = payload.read();
                // For the test if payload starts with an E throws and error
                if (firstChar == (int)'E') {
                    throw new PayloadValidatorSpiException("This is invalid payload starting with E");
                }
            }
        } catch (IOException e) {
            throw new PayloadValidatorSpiException("Can not read payload", e);
        }
    }
}
