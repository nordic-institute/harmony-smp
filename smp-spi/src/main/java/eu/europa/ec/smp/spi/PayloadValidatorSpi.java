/*-
 * #%L
 * smp-spi
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
package eu.europa.ec.smp.spi;

import eu.europa.ec.smp.spi.exceptions.PayloadValidatorSpiException;

import java.io.InputStream;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 *
 * SMP Service provider interface (SPI) for uploaded payload validation.
 * This SPI interface is intended to allow antivirus validation using third-party antivirus software.
 */
public interface PayloadValidatorSpi {

    /**
     * Validates the SMP payload. If the payload is invalid it throws  PayloadValidatorSpiException
     *
     * @param payload The payload data to be validated
     * @param mimeType The payload mime type
     * @throws PayloadValidatorSpiException in case the validation does not pass
     */
    void validatePayload(InputStream payload, String mimeType) throws PayloadValidatorSpiException;
}
