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
