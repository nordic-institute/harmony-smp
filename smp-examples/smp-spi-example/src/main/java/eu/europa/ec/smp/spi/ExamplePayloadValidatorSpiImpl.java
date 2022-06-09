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
        } catch (IOException e) {
            throw new PayloadValidatorSpiException("Can not read payload", e);
        }
    }
}