package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPRole;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceGroupService;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = ResourceConstants.CONTEXT_PATH_PUBLIC_SERVICE_GROUP)
public class ServiceGroupResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupResource.class);

    @Autowired
    private UIServiceGroupService uiServiceGroupService;
    @Autowired
    private DomainDao domainDao;

    @GetMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceResult<ServiceGroupRO> getServiceGroupList(
            HttpServletRequest request,
            @RequestParam(value = PARAM_PAGINATION_PAGE, defaultValue = "0") int page,
            @RequestParam(value = PARAM_PAGINATION_PAGE_SIZE, defaultValue = "10") int pageSize,
            @RequestParam(value = PARAM_PAGINATION_ORDER_BY, required = false) String orderBy,
            @RequestParam(value = PARAM_PAGINATION_ORDER_TYPE, defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = PARAM_QUERY_PARTC_ID, required = false) String participantIdentifier,
            @RequestParam(value = PARAM_QUERY_PARTC_SCHEME, required = false) String participantScheme,
            @RequestParam(value = PARAM_QUERY_DOMAIN_CODE, required = false) String domainCode) {

        String participantIdentifierDecoded = decodeUrlToUTF8(participantIdentifier);
        String participantSchemeDecoded = decodeUrlToUTF8(participantScheme);
        String domainCodeDecoded = decodeUrlToUTF8(domainCode);

        LOG.info("Search for page: {}, page size: {}, part. id: {}, part sch: {}, domain {}", page, pageSize, participantIdentifierDecoded,
                participantSchemeDecoded, domainCodeDecoded);
        ServiceGroupFilter sgf = new ServiceGroupFilter();
        sgf.setParticipantIdentifierLike(participantIdentifierDecoded);
        sgf.setParticipantSchemeLike(participantSchemeDecoded);
        // add domain search parameter
        sgf.setDomain(domainDao.validateDomainCode(domainCodeDecoded));

        // check if logged user is ServiceGroup admin if yes return only his servicegroups
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // show all service groups only for SMP Admin
        // SMP admin can edit all service groups. For others return only services groups they own.
        if (!request.isUserInRole(SMPRole.SMP_ADMIN.getCode())) {
            SMPAuthenticationToken authToken = (SMPAuthenticationToken) authentication;
            DBUser user = authToken.getUser();
            sgf.setOwner(user);
        }
        return uiServiceGroupService.getTableList(page, pageSize, orderBy, orderType, sgf);
    }

    @GetMapping( path = "{serviceGroupId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceGroupRO getServiceGroupById(@PathVariable Long serviceGroupId) {
        LOG.info("Get service group [{}]", serviceGroupId);
        return uiServiceGroupService.getServiceGroupById(serviceGroupId);
    }

    @GetMapping(path = "{service-group-id}/extension", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceGroupValidationRO getExtensionServiceGroupById(@PathVariable("service-group-id") Long sgId) {
        LOG.info("Get service group extension [{}]", sgId);
        return uiServiceGroupService.getServiceGroupExtensionById(sgId);
    }

    @PostMapping(path = "extension/validate", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceGroupValidationRO getValidateExtensionService(@RequestBody ServiceGroupValidationRO sg) {
        LOG.info("Validate service group extension");
        LOG.debug("Extension: [{}]", sg.getExtension());
        return uiServiceGroupService.validateServiceGroup(sg);
    }

    @PutMapping(produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public void updateServiceGroupList(@RequestBody ServiceGroupRO[] updateEntities) {
        LOG.info("Update ServiceGroupRO count: " + updateEntities.length);
        uiServiceGroupService.updateServiceGroupList(Arrays.asList(updateEntities));
    }

    private String decodeUrlToUTF8(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Unsupported UTF-8 encoding while converting: " + value, ex);
        }
        return value;
    }
}

