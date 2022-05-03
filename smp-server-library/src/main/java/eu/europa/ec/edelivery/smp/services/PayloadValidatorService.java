package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.spi.PayloadValidatorSpi;
import eu.europa.ec.smp.spi.exceptions.PayloadValidatorSpiException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * @author Joze Rihtarsic
 * @since 4.2
 * <p>
 * PayloadValidatorService delegates validation to external (SPI) PayloadValidatorSpi implementations.
 * This SPI interface is intended to allow antivirus validation using third-party antivirus software.
 */
@Service
public class PayloadValidatorService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(PayloadValidatorService.class);
    List<PayloadValidatorSpi> payloadValidatorSpiList;

    public PayloadValidatorService(Optional<List<PayloadValidatorSpi>> optPayloadValidatorSpiList) {
        this.payloadValidatorSpiList = optPayloadValidatorSpiList.isPresent() ? optPayloadValidatorSpiList.get() : Collections.emptyList();
    }

    /**
     * Validates the SMP payload with the registered PayloadValidatorSpi validators.
     *
     * @param payload  The payload data to be validated
     * @param mimeType The payload mime type
     * @throws PayloadValidatorSpiException in case the validation does not pass
     */
    public void validateUploadedContent(InputStream payload, String mimeType) {
        if (payloadValidatorSpiList.isEmpty()) {
            LOG.debug("No PayloadValidatorSpi registered. Skip validation!");
            return;
        }
        LOG.debug("Validate uploaded content");
        try {
            for (PayloadValidatorSpi validatorSpi : payloadValidatorSpiList) {
                LOG.debug("Validate payload with spi: [{}]!", validatorSpi);
                validatorSpi.validatePayload(payload, mimeType);
            }
        } catch (PayloadValidatorSpiException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Content validation failed", ExceptionUtils.getRootCauseMessage(e),e);
        }
    }

    ;

}
