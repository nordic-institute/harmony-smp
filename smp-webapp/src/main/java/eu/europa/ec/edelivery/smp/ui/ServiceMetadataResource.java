package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
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
@RequestMapping(value = "/ui/servicemetadata")
public class ServiceMetadataResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetadataResource.class);

    @Autowired
    private ServiceUIData serviceUIData;

    @PostConstruct
    protected void init() {

    }

    @PutMapping(produces = {"application/json"})
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public  ServiceResult<ServiceMetadataRO> getServiceMetadataList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderType", defaultValue = "asc", required = false) String orderType,
            @RequestParam(value = "participantId", required = false) String participantId,
            @RequestParam(value = "participantSchema", required = false) String participantSchema
            ) {


        return serviceUIData.getServiceMetadataList(page,pageSize, orderBy, orderType );
    }
}
