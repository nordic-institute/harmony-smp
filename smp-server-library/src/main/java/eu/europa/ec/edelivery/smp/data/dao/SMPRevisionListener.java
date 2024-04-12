/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBRevisionLog;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;

/**
 * The purpose of the SMPRevisionListener is to update the revision log with currently logged-in username
 *
 * @author Joze Rihtarsic
 * @since 4.0
 */
public class SMPRevisionListener implements RevisionListener {
    private static final String ANONYMOUS_USER = "anonymous";

    private static final Logger LOG = LoggerFactory.getLogger(SMPRevisionListener.class);

    @Override
    public void newRevision(Object revisionEntity) {
        DBRevisionLog rev = (DBRevisionLog) revisionEntity;
        String username = getSessionUserName();
        rev.setRevisionDate(OffsetDateTime.now());
        if (StringUtils.isEmpty(username)) {
            LOG.warn("Update database revision [{}] without session - authenticated user!", rev);
            rev.setUserName(ANONYMOUS_USER);
        } else {
            rev.setUserName(getSessionUserName());
        }
    }

    public String getSessionUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
