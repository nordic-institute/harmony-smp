/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
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
                buildTime +
                "]";
    }

    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_USER})
    @GetMapping(path = "config")
    public SmpConfigRO getApplicationConfig() {
        SmpConfigRO info = new SmpConfigRO();
        info.setSmlIntegrationOn(configurationService.isSMLIntegrationEnabled());
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
