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
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY;

/**
 * The group of resources with shared resource management rights. The user with group admin has rights to create/delete
 * resources for the group.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class GroupDao extends BaseDao<DBGroup> {

    /**
     * Returns domain records from smp_domain table.
     *
     * @return the list of domain records from smp_domain table
     * @throws IllegalStateException if no domain is configured
     */
    public List<DBGroup> getAllGroups() {
        TypedQuery<DBGroup> query = memEManager.createNamedQuery(QUERY_GROUP_ALL, DBGroup.class);
        return query.getResultList();
    }

    /**
     * Get group list for domains
     *
     * @param domain
     * @return
     */
    public List<DBGroup> getAllGroupsForDomain(DBDomain domain) {
        TypedQuery<DBGroup> query = memEManager.createNamedQuery(QUERY_GROUP_BY_DOMAIN, DBGroup.class);
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getResultList();
    }

    /**
     * Returns the group or Optional.empty() if there is no group for name and domain.
     *
     * @param name is the group name
     * @param domain where the group is registered
     *
     * @return the only single record for name  from smp_group table or empty value
     * @throws IllegalStateException if no group is not configured
     */
    public Optional<DBGroup> getGroupByNameAndDomain(String name, DBDomain domain) {
        try {
            TypedQuery<DBGroup> query = memEManager.createNamedQuery(QUERY_GROUP_BY_NAME_DOMAIN, DBGroup.class);
            query.setParameter(PARAM_NAME, name);
            query.setParameter(PARAM_DOMAIN_ID, domain.getId());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY.getMessage(name, domain.getDomainCode()));
        }
    }

    /**
     * Returns the group or Optional.empty() if there is no group for name and domain code
     *
     * @param name is the group name
     * @param domainCode where the group is registered
     *
     * @return the only single record for name  from smp_group table or empty value
     * @throws IllegalStateException if no group is not configured
     */
    public Optional<DBGroup> getGroupByNameAndDomainCode(String name, String domainCode) {
        try {
            TypedQuery<DBGroup> query = memEManager.createNamedQuery(QUERY_GROUP_BY_NAME_DOMAIN_CODE, DBGroup.class);
            query.setParameter(PARAM_NAME, name);
            query.setParameter(PARAM_DOMAIN_CODE, domainCode);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY.getMessage(name,domainCode));
        }
    }
    /**
     * Removes Entity by given domain code
     *
     * @return true if entity existed before and was removed in this call.
     * False if entity did not exist, so nothing was changed
     */
    @Transactional
    public boolean removeByNameAndDomain(String domainCode, DBDomain domain) {
        Optional<DBGroup> optd = getGroupByNameAndDomain(domainCode, domain);
        if (optd.isPresent()) {
            memEManager.remove(optd.get());
            return true;
        }
        return false;
    }

}
