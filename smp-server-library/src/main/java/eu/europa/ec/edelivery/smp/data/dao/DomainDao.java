/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainDeleteValidation;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.DOMAIN_NOT_EXISTS;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY;

/**
 * @author gutowpa
 * @since 3.0
 */
@Repository
public class DomainDao extends BaseDao<DBDomain> {



    /**
     * Returns the only single record from smp_domain table.
     * Returns Optional.empty() if there is more than 1 record present.
     *
     * @return the only single record from smp_domain table
     * @throws IllegalStateException if no domain is configured
     */
    public Optional<DBDomain> getTheOnlyDomain() {
        try {
            // expected is only one domain,
            TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_ALL, DBDomain.class);
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            return Optional.empty();
        } catch (NoResultException e) {
            throw new IllegalStateException(ErrorCode.NO_DOMAIN.getMessage());
        }
    }

    /**
     * Returns domain records from smp_domain table.
     *
     * @return the list of domain records from smp_domain table
     * @throws IllegalStateException if no domain is configured
     */
    public List<DBDomain> getAllDomains() {
        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_ALL, DBDomain.class);

        return query.getResultList();
    }


    public Optional<DBDomain> getFirstDomain() {
        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_ALL, DBDomain.class);
        query.setMaxResults(1);
        try {
            // expected is only one domain,
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the domain by code.
     * Returns the domain or Optional.empty() if there is no domain.
     *
     * @return the only single record for domain code from smp_domain table or empty value
     * @throws IllegalStateException if more than one domain is not configured for the code!
     */
    public Optional<DBDomain> getDomainByCode(String domainCode) {
        if (StringUtils.isEmpty(domainCode)) {
            return Optional.empty();
        }
        try {
            TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_CODE, DBDomain.class);
            query.setParameter(PARAM_DOMAIN_CODE, domainCode);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY.getMessage(domainCode));
        }
    }

    /**
     * Check if domain for domain code exists. If not SMPRuntimeException with DOMAIN_NOT_EXISTS is thrown.
     * If code is null or blank - then null is returned.
     *
     * @param domainCode
     * @return
     */
    public DBDomain validateDomainCode(String domainCode) {
        DBDomain domain = null;
        if (!StringUtils.isBlank(domainCode)) {
            Optional<DBDomain> od = getDomainByCode(domainCode);
            if (od.isPresent()) {
                domain = od.get();
            } else {
                throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, domainCode);
            }
        }
        return domain;
    }

    /**
     * Removes Entity by given domain code
     *
     * @return true if entity existed before and was removed in this call.
     * False if entity did not exist, so nothing was changed
     */
    @Transactional
    public boolean removeByDomainCode(String domainCode) {
        Optional<DBDomain> optd = getDomainByCode(domainCode);
        if (optd.isPresent()) {
            memEManager.remove(optd.get());
            return true;
        }
        return false;
    }

    /**
     * Validation report for domain which are used by  service groups from list of domain ids..
     *
     * @param domainIds
     * @return
     */
    public List<DBDomainDeleteValidation> validateDomainsForDelete(List<Long> domainIds) {
        TypedQuery<DBDomainDeleteValidation> query = memEManager.createNamedQuery("DBDomainDeleteValidation.validateDomainUsage",
                DBDomainDeleteValidation.class);
        query.setParameter("domainIds", domainIds);
        return query.getResultList();
    }

}
