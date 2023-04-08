package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.data.ui.DomainPublicRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainPublicService;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the DomainResource is to provide search method to retrieve configured domains in SMP.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_PUBLIC_DOMAIN)
public class DomainResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainResource.class);

    private UIDomainPublicService uiDomainService;

    public DomainResource(UIDomainPublicService uiDomainService) {
        this.uiDomainService = uiDomainService;
    }

    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<DomainPublicRO> geDomainList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = PARAM_QUERY_USER, required = false) String user) {

        LOG.info("Search for page: {}, page size: {}, user: {}", page, pageSize, user);
        ServiceResult<DomainPublicRO> result = uiDomainService.getTableList(page, pageSize, orderBy, orderType, null);
        return result;
    }

}
