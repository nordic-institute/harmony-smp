package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceGroupService;
import eu.europa.ec.edelivery.smp.ui.DomainResource;
import eu.europa.ec.edelivery.smp.validation.ServiceMetadataValidator;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/servicemetadata")
public class ServiceMetadataResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceMetadataResource.class);

    @Autowired
    ServiceMetadataValidator serviceMetadataValidator;

    @Autowired
    private UIServiceGroupService uiServiceGroupService;


    @ResponseBody
    @PutMapping(produces = {"application/json"})
    @RequestMapping(method = RequestMethod.GET, path = "{serviceMetadataId}")
    public ServiceGroupRO getServiceGroupById(@PathVariable Long serviceMetadataId) {
        return uiServiceGroupService.getServiceGroupById(serviceMetadataId);
    }

    private void validateServiceMetadata(ServiceMetadataRO val){
/*
        try {
           // serviceMetadataValidator.validate();
            //BdxSmpOasisValidator.validateXSD(xml.getBytes());


        } catch (XmlInvalidAgainstSchemaException e) {
            e.printStackTrace();
        }
*/
    }
}

