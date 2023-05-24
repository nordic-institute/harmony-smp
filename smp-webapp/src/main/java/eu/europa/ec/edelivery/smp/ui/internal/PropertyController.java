package eu.europa.ec.edelivery.smp.ui.internal;


import eu.europa.ec.edelivery.smp.data.ui.PropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.PropertyValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UIPropertyService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
@RestController
@RequestMapping(value = CONTEXT_PATH_INTERNAL_PROPERTY)
public class PropertyController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(PropertyController.class);

    final UIPropertyService uiPropertyService;
    final ConfigurationService configurationService;

    public PropertyController(UIPropertyService uiPropertyService, ConfigurationService configurationService) {
        this.uiPropertyService = uiPropertyService;
        this.configurationService = configurationService;
    }

    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<PropertyRO> gePropertyList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = PARAM_QUERY_PROPERTY) String property
    ) {
        LOG.info("Search for page: {}, page size: {}", page, pageSize);
        return uiPropertyService.getTableList(page, pageSize, orderBy, orderType, property);
    }

    @PutMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public void updateUserList(@RequestBody PropertyRO[] updateEntities) {
        LOG.info("Update property list, count: {}", updateEntities.length);
        // Pass the users and mark the passwords of the ones being updated as expired by passing the passwordChange as null
        uiPropertyService.updatePropertyList(Arrays.asList(updateEntities));
    }

    @PostMapping(path = "/validate", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN})
    public PropertyValidationRO validateProperty(@RequestBody PropertyRO propertyRO) {
        LOG.info("Validate property: [{}]", propertyRO.getProperty());
        return uiPropertyService.validateProperty(propertyRO);
    }

}
