package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.data.ui.SmpInfoRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimeZone;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(path = ResourceConstants.CONTEXT_PATH_PUBLIC_APPLICATION)
public class ApplicationResource {

    @Autowired
    private Environment env;

    @Autowired
    ConfigurationService configurationService;

    @Value("${smp.artifact.name:eDelivery SMP}")
    String artifactName;
    @Value("${smp.artifact.version:}")
    String artifactVersion;
    @Value("${smp.artifact.build.time:}")
    String buildTime;


    @GetMapping(path = "name")
    public String getName() {
        return artifactName;
    }


    protected String getRootContext() {
        return env.getProperty("server.contextPath", "/");
    }

    @GetMapping(path = "info")
    public SmpInfoRO getApplicationInfo() {
        SmpInfoRO info = new SmpInfoRO();
        info.setVersion(getDisplayVersion());
        info.setAuthTypes(configurationService.getUIAuthenticationTypes());
        info.setSsoAuthenticationLabel(configurationService.getCasUILabel());
        info.setContextPath(getRootContext());
        return info;
    }

    protected String getDisplayVersion() {
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
