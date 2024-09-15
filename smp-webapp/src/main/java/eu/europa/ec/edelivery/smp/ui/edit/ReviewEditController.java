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
package eu.europa.ec.edelivery.smp.ui.edit;


import eu.europa.ec.edelivery.smp.data.ui.ReviewDocumentVersionRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDocumentService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * Purpose of the DomainResource is to provide search method to retrieve configured domains in SMP.
 * base path for the resource includes two variables user who is editing and domain for the group
 * /ui/edit/rest/[user-id]/domain/[domain-id]/group/[group-id]/resource
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_EDIT_REVIEW)
public class ReviewEditController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ReviewEditController.class);
    private final UIDocumentService uiDocumentService;

    public ReviewEditController(UIDocumentService uiDocumentService) {
        this.uiDocumentService = uiDocumentService;
    }

    /**
     * Method returns Users list of alerts. To access the list user must be logged in.
     * <p>
     *
     * @param encUserId - encrypted user id (from session) - used for authorization check
     * @param page      - page number (0..n)
     * @param pageSize  - page size (0..n) - number of results on page/max number of returned results.
     * @param orderBy   - order by field
     * @param orderType - order type (asc, desc)
     * @return ServiceResult<AlertRO> - list of alerts
     */
    @PreAuthorize("@smpAuthorizationService.isCurrentlyLoggedIn(#encUserId)")
    @GetMapping(path = "/", produces = {MimeTypeUtils.APPLICATION_JSON_VALUE})
    public ServiceResult<ReviewDocumentVersionRO> getUserReviewTasks(
            @PathVariable("user-id") String encUserId,
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, defaultValue = "id", required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "desc", required = false) String orderType,
            @RequestParam(value = PARAM_PAGINATION_FILTER, required = false) Object filter
    ) {
        LOG.info("Search for page: {}, page size: {}", page, pageSize);
        Long userId = SessionSecurityUtils.decryptEntityId(encUserId);
        // set filter to current user
        return uiDocumentService.getDocumentReviewListForUser(userId, page, pageSize, orderBy, orderType, filter);
    }
}

