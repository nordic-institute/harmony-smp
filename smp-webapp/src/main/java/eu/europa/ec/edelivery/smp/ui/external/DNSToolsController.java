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


import eu.europa.ec.edelivery.smp.data.ui.DNSQueryRO;
import eu.europa.ec.edelivery.smp.data.ui.DNSQueryRequestRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDynamicDiscoveryTools;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_DNS_TOOLS;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.PATH_ACTION_GENERATE_DNS_QUERY;

/**
 * Controller for the DNS tools. The dns tools help users to test, debug and troubleshoot DNS issues.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_PUBLIC_DNS_TOOLS)
public class DNSToolsController {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DNSToolsController.class);

    UIDynamicDiscoveryTools uiDynamicDiscoveryTools;

    public DNSToolsController(UIDynamicDiscoveryTools uiDynamicDiscoveryTools) {
        this.uiDynamicDiscoveryTools = uiDynamicDiscoveryTools;
    }

    @PostMapping(path = PATH_ACTION_GENERATE_DNS_QUERY,
            produces = {MimeTypeUtils.APPLICATION_JSON_VALUE},
            consumes = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public List<DNSQueryRO> getDnsQueryList(@RequestBody DNSQueryRequestRO resourceRO) {
        LOG.debug("Received request to generate DNS queries: {}", resourceRO);
        return uiDynamicDiscoveryTools.createDnsQueries(resourceRO);
    }
}
