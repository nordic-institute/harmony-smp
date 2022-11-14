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