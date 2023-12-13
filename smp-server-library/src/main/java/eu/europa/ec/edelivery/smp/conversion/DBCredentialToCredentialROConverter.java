/*-
 * #%L
 * smp-server-library
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.ui.CredentialRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Objects;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBCredentialToCredentialROConverter implements Converter<DBCredential, CredentialRO> {

    private final ConfigurationService configurationService;

    public DBCredentialToCredentialROConverter(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public CredentialRO convert(DBCredential source) {

        CredentialRO target = new CredentialRO();
        target.setCredentialId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        target.setName(source.getName());
        target.setActive(source.isActive());
        target.setCredentialType(source.getCredentialType());
        target.setDescription(source.getDescription());
        target.setSequentialLoginFailureCount(source.getSequentialLoginFailureCount());
        target.setLastFailedLoginAttempt(source.getLastFailedLoginAttempt());
        target.setActiveFrom(source.getActiveFrom());
        target.setExpireOn(source.getExpireOn());
        target.setUpdatedOn(source.getChangedOn());
        target.setSequentialLoginFailureCount(source.getSequentialLoginFailureCount());
        target.setSuspendedUtil(getSuspensionUtilDate(source));
        target.setExpired(isCredentialExpired(source));
        return target;
    }

    public OffsetDateTime getSuspensionUtilDate(DBCredential credential) {
        Integer suspensionTime = null;
        Integer maxAllowedAttempts = null;
        if (Objects.requireNonNull(credential.getCredentialType()) == CredentialType.USERNAME_PASSWORD) {
            suspensionTime = configurationService.getLoginSuspensionTimeInSeconds();
            maxAllowedAttempts = configurationService.getLoginMaxAttempts();
        } else if (credential.getCredentialType() == CredentialType.ACCESS_TOKEN) {
            suspensionTime = configurationService.getAccessTokenLoginSuspensionTimeInSeconds();
            maxAllowedAttempts = configurationService.getAccessTokenLoginMaxAttempts();
        }

        return getSuspensionUntilDate(credential.getLastFailedLoginAttempt(),
                credential.getSequentialLoginFailureCount(),
                suspensionTime,
                maxAllowedAttempts);
    }


    public OffsetDateTime getSuspensionUntilDate(OffsetDateTime lastAttempt, Integer currentCount,
                                                 Integer suspendedForSec, Integer suspendedFromCount) {
        if (lastAttempt == null || currentCount == null || suspendedForSec == null || suspendedFromCount == null) {
            return null;
        }
        if (currentCount < suspendedFromCount) {
            return null;
        }
        OffsetDateTime suspendedUtil = lastAttempt.plusSeconds(suspendedForSec);
        if (suspendedUtil.isBefore(OffsetDateTime.now())) {
            return null;
        }
        return suspendedUtil;
    }

    private boolean isCredentialExpired(DBCredential source) {
        return (source.getExpireOn() == null
                || OffsetDateTime.now().isAfter(source.getExpireOn()));
    }
}
