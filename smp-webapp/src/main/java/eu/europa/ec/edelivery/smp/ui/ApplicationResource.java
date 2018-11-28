package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.SmpInfoRO;
import eu.europa.ec.edelivery.smp.services.SMLIntegrationService;
import eu.europa.ec.edelivery.smp.services.ui.UIServiceGroupService;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.util.TimeZone;
import java.util.jar.Manifest;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/ui/rest/application")
public class ApplicationResource {

    @Autowired
    private Environment env;

    @Autowired
    SmlConnector smlConnector;

    @Value("${smp.artifact.name:eDelivery SMP}")
    String artifactName;
    @Value("${smp.artifact.version:}")
    String artifactVersion;
    @Value("${smp.artifact.build.time:}")
    String buildTime;


    @RequestMapping(method = RequestMethod.GET, path = "name")
    public  String getName() {
        return artifactName;
    }

    @RequestMapping(method = RequestMethod.GET, path = "rootContext")
    public  String getRootContext() {
        return env.getProperty("server.contextPath", "/");
    }

    @RequestMapping(method = RequestMethod.GET, path = "info")
    public SmpInfoRO getApplicationInfo() {
        SmpInfoRO info = new SmpInfoRO();
        info.setVersion(getDisplayVersion());
        info.setSmlIntegrationOn(smlConnector.isSmlIntegrationEnabled());
        info.setContextPath(getRootContext());
        return info;
    }


    public String getDisplayVersion() {
        StringBuilder display = new StringBuilder();
        display.append(artifactName);
        display.append(" Version [");
        display.append(artifactVersion);
        display.append("] Build-Time [");
        display.append(buildTime + "|" + TimeZone.getDefault().getDisplayName());
        display.append("]");
        return display.toString();
    }
}
