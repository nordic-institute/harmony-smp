package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.ServiceUIData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/servicegroup")
public class ServiceGroupResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceGroupResource.class);

    @Autowired
    private ServiceUIData serviceUIData;

    @PostConstruct
    protected void init() {

    }

    @PutMapping(produces = {"application/json"})
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public  ServiceResult<ServiceGroupRO> getServiceGroupList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "participantId", required = false) String participantId,
            @RequestParam(value = "participantSchema", required = false) String participantSchema,
            @RequestParam(value = "domain", required = false) String domainwe
            ) {


        return serviceUIData.getServiceGroupList(page,pageSize, orderBy, orderType );
    }
}
