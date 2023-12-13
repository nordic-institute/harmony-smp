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
package eu.europa.ec.edelivery.smp.ui.internal;


import eu.europa.ec.edelivery.smp.data.ui.AlertRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIAlertService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_INTERNAL_ALERT)
public class AlertController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AlertController.class);

    final UIAlertService uiAlertService;

    public AlertController(UIAlertService uiAlertService) {
        this.uiAlertService = uiAlertService;
    }

    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public ServiceResult<AlertRO> getAlertList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, defaultValue = "id", required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "desc", required = false) String orderType
    ) {
        LOG.info("Search for page: {}, page size: {}", page, pageSize);
        return uiAlertService.getTableList(page, pageSize, orderBy, orderType, null);
    }
}
