package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/domain")
public class DomainResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainResource.class);

    @Autowired
    private UIDomainService uiDomainService;

    @PostConstruct
    protected void init() {

    }

    @PutMapping(produces = {"application/json"})
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ServiceResult<DomainRO> geDomainList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "user", required = false) String user
            ) {
        return uiDomainService.getTableList(page,pageSize, orderBy, orderType, null );
    }

    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.PUT)
    public void updateDomainList(@RequestBody(required = true) DomainRO [] updateEntities ){
        LOG.info("GOT LIST OF DomainRO to UPDATE: " + updateEntities.length);
        uiDomainService.updateDomainList(Arrays.asList(updateEntities));
    }
}
