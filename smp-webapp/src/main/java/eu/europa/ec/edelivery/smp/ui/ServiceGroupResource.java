package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPRole;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceGroupService;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/servicegroup")
public class ServiceGroupResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupResource.class);

    @Autowired
    private UIServiceGroupService uiServiceGroupService;
    @Autowired
    private DomainDao domainDao;

    @PostConstruct
    protected void init() {

    }

    @PutMapping(produces = {"application/json"})
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceResult<ServiceGroupRO> getServiceGroupList(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "participantIdentifier", required = false) String participantIdentifier,
            @RequestParam(value = "participantScheme", required = false) String participantScheme,
            @RequestParam(value = "domain", required = false) String domainCode
    ) {

        String participantIdentifierDecoded =decodeUrlToUTF8(participantIdentifier);
        String participantSchemeDecoded = decodeUrlToUTF8(participantScheme);
        String domainCodeDecoded = decodeUrlToUTF8(domainCode);

        LOG.info("Search for page: {}, page size: {}, part. id: {}, part sch: {}, domain {}",page, pageSize, participantIdentifierDecoded,
                participantSchemeDecoded, domainCodeDecoded );
        ServiceGroupFilter sgf = new ServiceGroupFilter();
        sgf.setParticipantIdentifierLike(participantIdentifierDecoded);
        sgf.setParticipantSchemeLike(participantSchemeDecoded);
        // add domain search parameter
        sgf.setDomain(domainDao.validateDomainCode(domainCodeDecoded));

        // check if logged user is ServiceGroup admin if yes return only his servicegroups
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // show all service groups only for SMP Admin
        // SMP admin can edit all service groups. For others return only services groups they own.
        if (!request.isUserInRole(SMPRole.SMP_ADMIN.getCode())){
            SMPAuthenticationToken authToken = (SMPAuthenticationToken) authentication;
            DBUser user = authToken.getUser();
            sgf.setOwner(user);
        }
        return uiServiceGroupService.getTableList(page,pageSize, orderBy, orderType, sgf);
    }

    @ResponseBody
    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET, path = "{serviceGroupId}")
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceGroupRO getServiceGroupById(@PathVariable Long serviceGroupId) {
        return uiServiceGroupService.getServiceGroupById(serviceGroupId);
    }

    @ResponseBody
    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET, path = "extension/{serviceGroupId}")
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceGroupValidationRO getExtensionServiceGroupById(@PathVariable Long serviceGroupId) {
        return uiServiceGroupService.getServiceGroupExtensionById(serviceGroupId);
    }
    @RequestMapping(path = "extension/validate", method = RequestMethod.POST)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceGroupValidationRO getExtensionServiceGroupById(@RequestBody(required = true) ServiceGroupValidationRO sg) {
        return uiServiceGroupService.validateServiceGroup(sg);
    }

    @RequestMapping(path = "extension/format", method = RequestMethod.POST)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public ServiceGroupValidationRO formatExtension(@RequestBody(required = true) ServiceGroupValidationRO sg) {
        return uiServiceGroupService.formatExtension(sg);
    }


    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.PUT)
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public void updateDomainList(@RequestBody(required = true) ServiceGroupRO[] updateEntities ){
        LOG.info("Update ServiceGroupRO count: " + updateEntities.length);
        uiServiceGroupService.updateServiceGroupList(Arrays.asList(updateEntities));
    }

    private String decodeUrlToUTF8(String value){
        if (StringUtils.isBlank(value)){
            return null;
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex){
            LOG.error("Unsupported UTF-8 encoding while converting: " + value, ex);
        }
        return value;
    }
}

