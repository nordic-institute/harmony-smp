package eu.europa.ec.edelivery.smp.ui.internal;


import eu.europa.ec.edelivery.smp.data.ui.SmpConfigRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RestController
@RequestMapping(path = ResourceConstants.CONTEXT_PATH_INTERNAL_APPLICATION)
public class ApplicationAdminResource {

    final ConfigurationService configurationService;

    public ApplicationAdminResource(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN,
            SMPAuthority.S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN})
    @GetMapping(path = "config")
    public SmpConfigRO getApplicationConfig() {
        SmpConfigRO info = new SmpConfigRO();
        info.setSmlIntegrationOn(configurationService.isSMLIntegrationEnabled());
        info.setSmlParticipantMultiDomainOn(configurationService.isSMLMultiDomainEnabled());
        info.setParticipantSchemaRegExp(configurationService.getParticipantIdentifierSchemeRexExpPattern());
        info.setParticipantSchemaRegExpMessage(configurationService.getParticipantIdentifierSchemeRexExpMessage());
        info.setConcatEBCorePartyId(configurationService.getForceConcatenateEBCorePartyId());
        info.setPartyIDSchemeMandatory(configurationService.getParticipantSchemeMandatory());

        info.setPasswordValidationRegExp(configurationService.getPasswordPolicyRexExpPattern());
        info.setPasswordValidationRegExpMessage(configurationService.getPasswordPolicyValidationMessage());
        return info;
    }
}
