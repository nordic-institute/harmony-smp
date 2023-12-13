/*-
 * #%L
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.PayloadValidatorService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_PUBLIC_TRUSTSTORE)
public class TruststoreController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(TruststoreController.class);

    private final UITruststoreService uiTruststoreService;
    private final PayloadValidatorService payloadValidatorService;

    public TruststoreController(UITruststoreService uiTruststoreService, PayloadValidatorService payloadValidatorService) {
        this.uiTruststoreService = uiTruststoreService;
        this.payloadValidatorService = payloadValidatorService;
    }

    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#userId)")
    @PostMapping(path = "/{user-id}/validate-certificate", consumes = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public CertificateRO validateCertificate(@PathVariable("user-id") String userId, @RequestBody byte[] data) {
        LOG.info("Got certificate data size: {}", data.length);
        // validate uploaded content
        payloadValidatorService.validateUploadedContent(new ByteArrayInputStream(data), MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
        return uiTruststoreService.getCertificateData(data, true, true);
    }

}
