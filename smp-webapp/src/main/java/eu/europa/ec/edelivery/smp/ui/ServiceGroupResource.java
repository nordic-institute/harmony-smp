package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceGroupService;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation .PostConstruct;
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

    @PostConstruct
    protected void init() {

    }

    @PutMapping(produces = {"application/json"})
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ServiceResult<ServiceGroupRO> getServiceGroupList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "participantId", required = false) String participantIdentifier,
            @RequestParam(value = "participantSchema", required = false) String participantScheme,
            @RequestParam(value = "domain", required = false) String domain
    ) {
        LOG.info("Search for page: {}, page size: {}, part. id: {}, part sch: {}, domain {}",page, pageSize, participantIdentifier, participantScheme, domain );
        ServiceGroupFilter sgf = new ServiceGroupFilter();
        sgf.setParticipantIdentifierLike(participantIdentifier);
        sgf.setParticipantSchemeLike(participantScheme);

        return uiServiceGroupService.getTableList(page,pageSize, orderBy, orderType, sgf, domain);
    }

    @ResponseBody
    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET, path = "{serviceGroupId}")
    public ServiceGroupRO getServiceGroupById(@PathVariable Long serviceGroupId) {


        return uiServiceGroupService.getServiceGroupById(serviceGroupId);
    }

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.PUT)
    public void updateDomainList(@RequestBody(required = true) ServiceGroupRO[] updateEntities ){
        LOG.info("GOT LIST OF ServiceGroupRO to UPDATE: " + updateEntities.length);
        uiServiceGroupService.updateServiceGroupList(Arrays.asList(updateEntities));
    }
}

