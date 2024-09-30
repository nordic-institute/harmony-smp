/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.auth.cas;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.utils.StringNamedSubstitutor;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The purpose of the class is to retrieve Spring Security UserDetails object for the CAS ticket validation request (CasAssertionAuthenticationToken).
 * The User object is mapped to local authorization object via AttributePrincipal name value.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Component
public class SMPCasUserService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
    enum MappingData {
        EMAIL("${email}"),
        FULL_NAME("${firstName} ${lastName}");

        private final String defaultValue;

        MappingData(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(SMPCasUserService.class);

    private final UIUserService uiUserService;
    private final ConfigurationService configurationService;

    @Autowired
    public SMPCasUserService(UIUserService uiUserService,
                             ConfigurationService configurationService) {
        this.uiUserService = uiUserService;
        this.configurationService = configurationService;
    }

    /**
     * @param token The pre-authenticated authentication token from the cas SMPCas20ServiceTicketValidator
     * @return UserDetails for the given authentication token, never null.
     * @throws UsernameNotFoundException if no user details can be found for the given authentication token
     */
    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {

        AttributePrincipal principal = token.getAssertion().getPrincipal();
        // the cas id must match with username

        String username = principal.getName();
        LOG.info("Got CAS user with principal name: [{}]", username);
        DBUser dbuser;
        try {
            dbuser = uiUserService.findUserByUsername(username);
        } catch (SMPRuntimeException ex) {
            if (configurationService.isCasAutomaticRegistrationEnabledForUserAuthentication()) {
                dbuser = registerNewCasUser(principal);
            } else {
                throw new UsernameNotFoundException("User with the username [" + username + "] is not registered in SMP", ex);
            }
        }

        SMPAuthority authority = SMPAuthority.getAuthorityByApplicationRole(dbuser.getApplicationRole());
        // generate secret for the session
        SMPUserDetails smpUserDetails = new SMPUserDetails(dbuser, SecurityUtils.generatePrivateSymmetricKey(true), Collections.singletonList(authority));
        smpUserDetails.setCasAuthenticated(true);
        LOG.info("Return authenticated user details for username: [{}]", username);
        return smpUserDetails;
    }

    public DBUser registerNewCasUser(AttributePrincipal principal) {
        // convert user attributes to map of string
        Map<String, Object> princAttr = principal.getAttributes();
        Map<String, String> attributes = princAttr == null ? Collections.emptyMap() :
                princAttr.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> String.valueOf(entry.getValue())
                        ));

        Map<String, String> attributesMap = configurationService.getCasAutomaticRegistrationDataMapping();

        DBUser dbUser = new DBUser();
        // update user data by admin
        dbUser.setUsername(principal.getName());
        dbUser.setApplicationRole(ApplicationRoleType.USER);
        dbUser.setActive(!configurationService.isCasAutomaticRegistrationConfirmation());
        dbUser.setEmailAddress(getValueFromCasPrincipal(MappingData.EMAIL, attributes, attributesMap));
        dbUser.setFullName(getValueFromCasPrincipal(MappingData.FULL_NAME, attributes, attributesMap));
        uiUserService.createDBUser(dbUser);
        return dbUser;
    }

    protected String getValueFromCasPrincipal(MappingData data, Map<String, String> attributes, Map<String, String> mapping) {
        String template = mapping.getOrDefault(data.name(), data.getDefaultValue());
        return StringNamedSubstitutor.resolve(template, attributes);
    }
}
