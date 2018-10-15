package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/application")
public class ApplicationResource {

    @Autowired
    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);


    @RequestMapping(method = RequestMethod.GET, path = "name")
    public  String getName() {
        return "SMP TEST";
    }

    @RequestMapping(method = RequestMethod.GET, path = "rootContext")
    public  String getRootContext() {
        return env  .getProperty("server.contextPath", "/");
    }
}
