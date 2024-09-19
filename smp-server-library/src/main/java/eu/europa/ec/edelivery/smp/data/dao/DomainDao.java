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

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType.toList;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.DOMAIN_NOT_EXISTS;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY;

/**
 * @author gutowpa
 * @since 3.0
 */
@Repository
public class DomainDao extends BaseDao<DBDomain> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(DomainDao.class);

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
        return getDomainByQueryWithParam(domainCode, QUERY_DOMAIN_CODE, PARAM_DOMAIN_CODE);
    }

    public Optional<DBDomain> getDomainBySmlSmpId(String smlSmpId) {
        return getDomainByQueryWithParam(smlSmpId, QUERY_DOMAIN_SMP_SML_ID, PARAM_DOMAIN_SML_SMP_ID);
    }

    /**
     * Returns the Optional DBDomain from database. The domain is searched by domain parameter and queryDomainCode.
     *
     * @param domainParameter - parameter value to search for
     * @param queryName       - The named DBDomain query
     * @param queryParamName  the parameter name in the query
     * @return Optional DBDomain
     */
    private Optional<DBDomain> getDomainByQueryWithParam(String domainParameter, String queryName, String queryParamName) {
        if (StringUtils.isEmpty(domainParameter)) {
            return Optional.empty();
        }
        try {
            TypedQuery<DBDomain> query = memEManager.createNamedQuery(queryName, DBDomain.class);
            query.setParameter(queryParamName, domainParameter);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY.getMessage(domainParameter));
        }
    }


    public Long getResourceCountForDomain(Long domainId) {
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCES_BY_DOMAIN_ID_COUNT, Long.class);

        query.setParameter(PARAM_DOMAIN_ID, domainId);
        return query.getSingleResult();
    }

    public Long getDomainsByUserIdAndDomainRolesCount(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_ROLES_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getSingleResult();
    }

    public List<DBDomain> getDomainsByUserIdAndDomainRoles(Long userId, MembershipRoleType... roleTypes) {
        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_ROLES, DBDomain.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getResultList();
    }

    public Long getDomainsByUserIdAndGroupRolesCount(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_GROUP_ROLES_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getSingleResult();
    }

    public List<DBDomain> getDomainsByUserIdAndGroupRoles(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_GROUP_ROLES, DBDomain.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getResultList();
    }

    public Long getDomainsByUserIdAndResourceRolesCount(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_RESOURCE_ROLES_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getSingleResult();
    }

    public List<DBDomain> getDomainsByUserIdAndResourceRoles(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_RESOURCE_ROLES, DBDomain.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getResultList();
    }

    /**
     * Check if domain for domain code exists. If not SMPRuntimeException with DOMAIN_NOT_EXISTS is thrown.
     * If code is null or blank - then null is returned.
     *
     * @param domainCode  - domain code to be validated
     * @return DBDomain - domain if exists
     * @throws SMPRuntimeException if domain does not exist
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
     * Method returns all public domains with all domains where user is direct or indirect member.
     * and have some resources assigned. See the EDELIVERY-13793
     * If user is null then only public domains are returned.
     *
     * @param user - user to search for
     *             if null only public domains are returned
     * @return list of domains
     * @Param page - page number
     * @Param pageSize - page size
     */
    public List<DBDomain> getAllDomainsForUser(DBUser user, int page, int pageSize) {
        TypedQuery<DBDomain> query = createAllDomainsForUserQuery(DBDomain.class, user);
        setPaginationParametersToQuery(query, page, pageSize);
        return query.getResultList();
    }

    public long getAllDomainsForUserCount(DBUser user) {
        TypedQuery<Long> query = createAllDomainsForUserQuery(Long.class, user);
        return query.getSingleResult();
    }

    private <T> TypedQuery<T> createAllDomainsForUserQuery(Class<T> resultClass, DBUser user) {
        String queryName = resultClass == Long.class ? QUERY_DOMAIN_FOR_USER_COUNT :
                QUERY_DOMAIN_FOR_USER;
        LOG.debug("Create search query [{}] for all users domain", queryName);
        TypedQuery<T> query = memEManager.createNamedQuery(queryName, resultClass);
        query.setParameter(PARAM_USER_ID, user != null ? user.getId() : null);
        query.setParameter(PARAM_DOMAIN_VISIBILITY, VisibilityType.PUBLIC);
        return query;
    }
}
