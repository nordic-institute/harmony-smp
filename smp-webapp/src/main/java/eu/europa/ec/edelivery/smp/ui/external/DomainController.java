/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.data.ui.DomainPublicRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainEditService;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the DomainResource is to provide public method to retrieve configured domains in SMP.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_PUBLIC_DOMAIN)
public class DomainController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainController.class);

    private final UIDomainEditService uiDomainService;


    public DomainController(UIDomainEditService uiDomainService) {
        this.uiDomainService = uiDomainService;

    }

    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<DomainPublicRO> getDomainList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_QUERY_USER, required = false) String user) {
        LOG.info("Search for page: {}, page size: {}, user: {}", page, pageSize, user);
        return uiDomainService.getUserPermittedDomains(page, pageSize);
    }
}
