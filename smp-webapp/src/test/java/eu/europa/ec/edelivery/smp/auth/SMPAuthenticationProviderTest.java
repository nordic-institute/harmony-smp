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
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialTargetType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Calendar;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPAuthenticationProviderTest {


    CredentialDao mockCredentialDao = Mockito.mock(CredentialDao.class);
    ConversionService mockConversionService = Mockito.mock(ConversionService.class);
    CRLVerifierService mockCrlVerifierService = Mockito.mock(CRLVerifierService.class);
    UITruststoreService mockTruststoreService = Mockito.mock(UITruststoreService.class);
    ConfigurationService mockConfigurationService = Mockito.mock(ConfigurationService.class);
    CredentialsAlertService mocAlertService = Mockito.mock(CredentialsAlertService.class);
    UserDao mockUserDao = Mockito.mock(UserDao.class);


    CredentialService mockCredentialService = new CredentialService(mockUserDao, mockCredentialDao, mockConversionService, mockCrlVerifierService, mockTruststoreService, mockConfigurationService, mocAlertService);
    SMPAuthenticationProvider testInstance = new SMPAuthenticationProvider(mockCredentialService);


    // response time for existing and nonexistent user should be "approx. equal"
    @Test
    public void authenticateByAccessTokenResponseTime() {

        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken("User", "User");
        int count = 10;
        DBUser user = new DBUser();
        user.setId(1L);
        user.setApplicationRole(ApplicationRoleType.USER);
        DBCredential credential = new DBCredential();
        credential.setValue(BCrypt.hashpw("InvalidPassword", BCrypt.gensalt()));
        credential.setName("User");
        credential.setUser(user);
        credential.setCredentialType(CredentialType.ACCESS_TOKEN);
        credential.setCredentialTarget(CredentialTargetType.REST_API);

        doReturn(1000).when(mockConfigurationService).getAccessTokenLoginFailDelayInMilliSeconds();
        doReturn(count + 5).when(mockConfigurationService).getAccessTokenLoginMaxAttempts();

        doReturn(Optional.of(credential)).when(mockCredentialDao).findAccessTokenCredentialForAPI(any());

        long averageExists = 0;
        long averageNotExist = 0;
        for (int i = 0; i < count; i++) {
            long userExistTime = Calendar.getInstance().getTimeInMillis();
            try {
                testInstance.authenticateByAuthenticationToken(userToken);
            } catch (BadCredentialsException ignore) {
            }
            averageExists += Calendar.getInstance().getTimeInMillis() - userExistTime;
        }

        doReturn(Optional.empty()).when(mockUserDao).findUserByIdentifier(any());
        for (int i = 0; i < count; i++) {
            long userExistTime = Calendar.getInstance().getTimeInMillis();
            try {
                testInstance.authenticateByAuthenticationToken(userToken);
            } catch (AuthenticationServiceException | BadCredentialsException ignore) {
            }
            averageNotExist += Calendar.getInstance().getTimeInMillis() - userExistTime;
        }

        // the average should be the same!
        assertThat("average difference between failed login must be less than 10ms", Math.abs(averageExists - averageNotExist),
                Matchers.lessThan(50L));
    }
}
