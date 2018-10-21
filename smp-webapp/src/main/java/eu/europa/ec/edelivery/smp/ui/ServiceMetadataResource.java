package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/servicemetadata")
public class ServiceMetadataResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceMetadataResource.class);

    @Autowired
    private UIServiceMetadataService uiServiceMetadataService;


    @ResponseBody
    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET, path = "{serviceMetadataId}")
    public ServiceMetadataRO getServiceGroupById(@PathVariable Long serviceMetadataId) {
        return uiServiceMetadataService.getServiceMetadataXMLById(serviceMetadataId);
    }

    @RequestMapping(path = "validate", method = RequestMethod.POST)
    public ServiceMetadataValidationRO validateServiceMetadata(@RequestBody(required = true) ServiceMetadataValidationRO serviceMetadataValidationRO) {
        return uiServiceMetadataService.validateServiceMetadata(serviceMetadataValidationRO);
    }
}

