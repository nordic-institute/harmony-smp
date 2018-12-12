package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupSearchRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceGroupSearchService;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.URLDecoder;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.DOMAIN_NOT_EXISTS;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/search")
public class SearchResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SearchResource.class);

    @Autowired
    private UIServiceGroupSearchService uiServiceGroupService;
    @Autowired
    private DomainDao domainDao;


    @PutMapping(produces = {"application/json"})
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ServiceResult<ServiceGroupSearchRO> getServiceGroupList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "participantIdentifier", required = false) String participantIdentifier,
            @RequestParam(value = "participantScheme", required = false) String participantScheme,
            @RequestParam(value = "domain", required = false) String domainCode
    ) {

        String participantIdentifierDecoded =StringUtils.isBlank(participantIdentifier)?null:URLDecoder.decode(participantIdentifier);
        String participantSchemeDecoded = StringUtils.isBlank(participantScheme)?null:URLDecoder.decode(participantScheme);
        String domainCodeDecoded = StringUtils.isBlank(domainCode)?null:URLDecoder.decode(domainCode);

        LOG.info("Search for page: {}, page size: {}, part. id: {}, part sch: {}, domain {}", page, pageSize, participantIdentifierDecoded,
                participantSchemeDecoded, domainCodeDecoded);

        ServiceGroupFilter sgf = new ServiceGroupFilter();
        sgf.setParticipantIdentifierLike(participantIdentifierDecoded);
        sgf.setParticipantSchemeLike(participantSchemeDecoded);
        // add domain search parameter
        sgf.setDomain(domainDao.validateDomainCode(domainCodeDecoded));

        return uiServiceGroupService.getTableList(page, pageSize, orderBy, orderType, sgf);
    }
}
