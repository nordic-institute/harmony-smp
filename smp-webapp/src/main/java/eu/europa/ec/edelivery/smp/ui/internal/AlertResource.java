package eu.europa.ec.edelivery.smp.ui.internal;


import eu.europa.ec.edelivery.smp.data.ui.AlertRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIAlertService;
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
public class AlertResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(AlertResource.class);

    final UIAlertService uiAlertService;

    public AlertResource(UIAlertService uiAlertService) {
        this.uiAlertService = uiAlertService;
    }

    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<AlertRO> geDomainList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, defaultValue = "id", required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "desc", required = false) String orderType
    ) {
        LOG.info("Search for page: {}, page size: {}", page, pageSize);
        return uiAlertService.getTableList(page, pageSize, orderBy, orderType, null);
    }
}
