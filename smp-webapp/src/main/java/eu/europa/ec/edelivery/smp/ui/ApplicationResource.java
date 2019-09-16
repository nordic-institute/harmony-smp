package eu.europa.ec.edelivery.smp.ui;


import eu.europa.ec.edelivery.smp.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.data.ui.SmpConfigRO;
import eu.europa.ec.edelivery.smp.data.ui.SmpInfoRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimeZone;

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
    ConfigurationService configurationService;

    @Value("${smp.artifact.name:eDelivery SMP}")
    String artifactName;
    @Value("${smp.artifact.version:}")
    String artifactVersion;
    @Value("${smp.artifact.build.time:}")
    String buildTime;


    @RequestMapping(method = RequestMethod.GET, path = "name")
    public String getName() {
        return artifactName;
    }

    @RequestMapping(method = RequestMethod.GET, path = "rootContext")
    public String getRootContext() {
        return env.getProperty("server.contextPath", "/");
    }

    @RequestMapping(method = RequestMethod.GET, path = "info")
    public SmpInfoRO getApplicationInfo() {
        SmpInfoRO info = new SmpInfoRO();
        info.setVersion(getDisplayVersion());
        info.setSmlIntegrationOn(configurationService.isSMLIntegrationEnabled());
        info.setSmlParticipantMultiDomainOn(configurationService.isSMLMultiDomainEnabled());
        info.setContextPath(getRootContext());
        return info;
    }

    @RequestMapping(method = RequestMethod.GET, path = "config")
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN,SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN,
            SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    public SmpConfigRO getApplicationConfig() {
        SmpConfigRO info = new SmpConfigRO();

        info.setSmlIntegrationOn(configurationService.isSMLIntegrationEnabled());
        info.setSmlParticipantMultiDomainOn(configurationService.isSMLMultiDomainEnabled());
        info.setParticipantSchemaRegExp(configurationService.getParticipantIdentifierSchemeRexExpPattern());
        info.setParticipantSchemaRegExpMessage(configurationService.getParticipantIdentifierSchemeRexExpMessage());

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
