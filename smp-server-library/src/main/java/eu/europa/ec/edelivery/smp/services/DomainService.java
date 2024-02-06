/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;


/**
 * Service for domain
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Service
public class DomainService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainService.class);

    public static final Pattern DOMAIN_ID_PATTERN = Pattern.compile("[a-zA-Z0-9]{1,50}");

    @Autowired
    private SMLIntegrationService smlIntegrationService;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private DomainDao domainDao;


    /**
     * Method checks if domain is in right format. Domain must contains only alphanomeric chars and it must
     * not be longer than 50 chars.
     *
     * @param domain
     * @return
     */
    @NotNull
    public DBDomain getDomain(final String domain) {
        if (StringUtils.isBlank(domain)) {
            Optional<DBDomain> res = domainDao.getTheOnlyDomain();
            if (!res.isPresent()) {
                throw new SMPRuntimeException(MISSING_DOMAIN);
            }
            return res.get();
        }
        // else test if domain is ok.
        if (!DOMAIN_ID_PATTERN.matcher(domain).matches()) {
            throw new SMPRuntimeException(INVALID_DOMAIN_CODE, domain, DOMAIN_ID_PATTERN);
        }
        // get domain by code
        Optional<DBDomain> domEntity = domainDao.getDomainByCode(domain);
        if (!domEntity.isPresent()) {
            throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, domain);
        }
        return domEntity.get();
    }

    /**
     * If domain is not yet registered and SML integration is on, it tries to register a domain and all participants
     * on that domain. If integration is off, it returns a configuration exception.
     * <p>
     * Method is not in transaction - but sub-methods are. if registering domain or particular serviceGroup succeed
     * then the database flag (SML_REGISTERED) is turned on ( if method fails
     * while execution the SML_REGISTERED reflect the real status in SML). Running the method again updates only
     * serviceGroup which are not yet registered.
     *
     * @param domain
     */

    @Transactional
    public void registerDomainAndParticipants(DBDomain domain) {
        LOG.info("Start registerDomainAndParticipants for domain:" + domain.getDomainCode());
        smlIntegrationService.registerDomain(domain);

        DBResourceFilter filter = DBResourceFilter.createBuilder().domain(domain).build();
        List<DBResource> resources = resourceDao.getResourcesForFilter(-1, -1, filter);
        List<DBResource> processed = new ArrayList<>();
        try {
            for (DBResource resource : resources) {
                smlIntegrationService.registerParticipant(resource, domain);
                processed.add(resource);
            }
        } catch (SMPRuntimeException exc) {
            // rollback dns records
            for (DBResource resource : processed) {
                smlIntegrationService.unregisterParticipant(resource, domain);
            }
            throw exc;
        }
    }

    @Transactional
    public void unregisterDomainAndParticipantsFromSml(DBDomain domain) {

        DBResourceFilter filter = DBResourceFilter.createBuilder().domain(domain).build();
        List<DBResource> resources = resourceDao.getResourcesForFilter(-1, -1, filter);
        List<DBResource> processed = new ArrayList<>();
        try {
            for (DBResource resource : resources) {
                smlIntegrationService.unregisterParticipant(resource, domain);
                processed.add(resource);
            }
        } catch (SMPRuntimeException exc) {
            // rollback dns records
            for (DBResource resource : processed) {
                smlIntegrationService.registerParticipant(resource, domain);
            }
            throw exc;
        }
        smlIntegrationService.unRegisterDomain(domain);
    }

}
