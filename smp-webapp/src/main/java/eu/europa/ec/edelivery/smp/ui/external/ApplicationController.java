package eu.europa.ec.edelivery.smp.ui.external;


import eu.europa.ec.edelivery.smp.auth.enums.SMPUserAuthenticationTypes;
import eu.europa.ec.edelivery.smp.data.ui.SmpConfigRO;
import eu.europa.ec.edelivery.smp.data.ui.SmpInfoRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(path = ResourceConstants.CONTEXT_PATH_PUBLIC_APPLICATION)
public class ApplicationController {

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
        List<String> authTypes = configurationService.getUIAuthenticationTypes();
        // set default password
        authTypes = authTypes ==null || authTypes.isEmpty()?
                Collections.singletonList(SMPUserAuthenticationTypes.PASSWORD.name()):authTypes;
        info.addAuthTypes(authTypes);
        if (authTypes.contains(SMPUserAuthenticationTypes.SSO.name())){
            info.setSsoAuthenticationLabel(configurationService.getCasUILabel());
            info.setSsoAuthenticationURI(configurationService.getCasSMPLoginRelativePath());
        }
        info.setContextPath(getRootContext());
        return info;
    }

    protected String getDisplayVersion() {
        return artifactName +
                " Version [" +
                artifactVersion +
                "] Build-Time [" +
                buildTime + "|" + TimeZone.getDefault().getDisplayName() +
                "]";
    }

    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_USER})
    @GetMapping(path = "config")
    public SmpConfigRO getApplicationConfig() {
        SmpConfigRO info = new SmpConfigRO();
        info.setSmlIntegrationOn(configurationService.isSMLIntegrationEnabled());
        info.setSmlParticipantMultiDomainOn(configurationService.isSMLMultiDomainEnabled());
        info.setParticipantSchemaRegExp(configurationService.getParticipantIdentifierSchemeRexExpPattern());
        info.setParticipantSchemaRegExpMessage(configurationService.getParticipantIdentifierSchemeRexExpMessage());
        info.setConcatEBCorePartyId(false);
        info.setPartyIDSchemeMandatory(configurationService.getParticipantSchemeMandatory());

        info.setPasswordValidationRegExp(configurationService.getPasswordPolicyRexExpPattern());
        info.setPasswordValidationRegExpMessage(configurationService.getPasswordPolicyValidationMessage());
        info.addWebServiceAuthTypes(configurationService.getAutomationAuthenticationTypes());
        return info;
    }
}
