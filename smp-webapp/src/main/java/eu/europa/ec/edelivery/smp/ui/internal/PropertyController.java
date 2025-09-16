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
package eu.europa.ec.edelivery.smp.ui.internal;


import eu.europa.ec.edelivery.smp.data.ui.PropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.PropertyValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UIPropertyService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * Get paginated property list
     *
     * @param page       - page number (0..N)
     * @param pageSize   - page size default 10
     * @param orderBy    - order by field
     * @param orderType  - order type (asc, desc)
     * @param filterValue - filter value (searches in property name and value)
     * @return - paginated property list
     */
    @GetMapping(produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    @PreAuthorize("@smpAuthorizationService.isSystemAdministrator")
    public ServiceResult<PropertyRO> getPropertyList(
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = PARAM_QUERY_PROPERTY, required = false) String filterValue
    ) {
        LOG.info("Search for page: [{}], page size: [{}] with filter: [{}]", page, pageSize, filterValue);
        return uiPropertyService.getTableList(page, pageSize, orderBy, orderType, filterValue);
    }

    @PutMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isSystemAdministrator")
    public void updatePropertyList(@RequestBody PropertyRO[] updateEntities) {
        LOG.info("Update property list, count: {}", updateEntities.length);
        // Pass the users and mark the passwords of the ones being updated as expired by passing the passwordChange as null
        uiPropertyService.updatePropertyList(Arrays.asList(updateEntities));
    }

    @PostMapping(path = "/validate", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE, produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @PreAuthorize("@smpAuthorizationService.isSystemAdministrator")
    public PropertyValidationRO validateProperty(@RequestBody PropertyRO propertyRO) {
        LOG.info("Validate property: [{}]", propertyRO.getProperty());
        return uiPropertyService.validateProperty(propertyRO);
    }

}
