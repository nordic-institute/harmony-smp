/*-
 * #%L
 * smp-server-library
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

import static eu.europa.ec.edelivery.smp.logging.SMPLogger.SECURITY_MARKER;


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
            LOG.error(SECURITY_MARKER, "Content validation failed: [" + ExceptionUtils.getRootCauseMessage(e) + "]", e);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Upload payload", "Content validation failed");
        }
    }
}
