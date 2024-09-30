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
package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SML_MANAGE_MAX_COUNT;


/**
 * Service for domain
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Service
public class DomainSMLIntegrationService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainSMLIntegrationService.class);

    public static final Pattern DOMAIN_ID_PATTERN = Pattern.compile("[a-zA-Z0-9]{1,50}");

    private final SMLIntegrationService smlIntegrationService;
    private final ResourceDao resourceDao;
    private final DomainDao domainDao;
    private final ConfigurationService configurationService;

    public DomainSMLIntegrationService(SMLIntegrationService smlIntegrationService,
                                       ResourceDao resourceDao,
                                       DomainDao domainDao,
                                       ConfigurationService configurationService) {
        this.smlIntegrationService = smlIntegrationService;
        this.resourceDao = resourceDao;
        this.domainDao = domainDao;
        this.configurationService = configurationService;
    }

    /**
     * If domain is not yet registered and SML integration is on, it tries to register a domain and all participants
     * on that domain. If integration is disabled, it returns a configuration exception. The participants are registered
     * not registered if count of participants is higher than defined in  max bdmsl.participants.manage.max-count.
     * In this case they must be registered manually.
     * <p>
     * Method is not in transaction - but sub-methods are. if registering domain or particular serviceGroup succeed
     * then the database flag (SML_REGISTERED) is turned on ( if method fails
     * while execution the SML_REGISTERED reflect the real status in SML). Running the method again updates only
     * serviceGroup which are not yet registered.
     *
     * @param domainId domain id
     */
    @Transactional
    public void registerDomainAndParticipants(Long domainId) {

        DBDomain dbDomain = domainDao.find(domainId);
        LOG.info("Start registerDomainAndParticipants for domain: [{}]", dbDomain);
        DBResourceFilter filter = DBResourceFilter.createBuilder().domain(dbDomain).build();
        smlIntegrationService.registerDomain(dbDomain);

        Long resourceCnt = resourceDao.getResourcesForFilterCount(filter);
        int maxSMLRecordCount = configurationService.getManageMaxSMLRecordCount();
        if (resourceCnt > maxSMLRecordCount) {
            LOG.warn("Too many resources to register for the domain [{}]. Count: [{}], max. allowed [{}]!" +
                            "For details check the configuration option [{}]!",
                    dbDomain, resourceCnt, maxSMLRecordCount, SML_MANAGE_MAX_COUNT.getProperty());
            return;
        }

        List<DBResource> resources = resourceDao.getResourcesForFilter(-1, -1, filter);
        List<DBResource> processed = new ArrayList<>();
        try {
            for (DBResource resource : resources) {
                smlIntegrationService.registerParticipant(resource, dbDomain);
                processed.add(resource);
            }
        } catch (SMPRuntimeException exc) {
            // rollback dns records
            for (DBResource resource : processed) {
                smlIntegrationService.unregisterParticipant(resource, dbDomain);
            }
            throw exc;
        }
    }

    /**
     * If domain is registered and SML integration is on, it tries to unregister a domain and all its participants
     * The participants are not unregistered if count of participants is higher than defined in  max bdmsl.participants.manage.max-count.
     * In this case they must be unregistered manually.
     *
     * @param domainId domain id
     */
    @Transactional
    public void unregisterDomainAndParticipantsFromSml(Long domainId) {
        DBDomain dbDomain = domainDao.find(domainId);
        LOG.info("Start unregisterDomainAndParticipants for domain: [{}]", dbDomain);
        DBResourceFilter filter = DBResourceFilter.createBuilder().domain(dbDomain).build();
        Long resourceCnt = resourceDao.getResourcesForFilterCount(filter);
        int maxSMLRecordCount = configurationService.getManageMaxSMLRecordCount();
        if (resourceCnt > maxSMLRecordCount) {
            LOG.warn("Too many resources to unregister for the domain [{}]. Count: [{}], max. allowed [{}]!" +
                            "For details check the configuration option [{}]!",
                    dbDomain, resourceCnt, maxSMLRecordCount, SML_MANAGE_MAX_COUNT.getProperty());
            return;
        }

        List<DBResource> resources = resourceDao.getResourcesForFilter(-1, -1, filter);
        List<DBResource> processed = new ArrayList<>();
        try {
            for (DBResource resource : resources) {
                smlIntegrationService.unregisterParticipant(resource, dbDomain);
                processed.add(resource);
            }
        } catch (SMPRuntimeException exc) {
            // rollback dns records
            for (DBResource resource : processed) {
                smlIntegrationService.registerParticipant(resource, dbDomain);
            }
            throw exc;
        }
        smlIntegrationService.unRegisterDomain(dbDomain);
    }

}
